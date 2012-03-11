package com.kole.cache.cluster.jgroup;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import net.sf.ehcache.Element;

import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kole.cache.cluster.UnsupportedCacheElementException;
import com.kole.cache.hashing.ConsistentHashing;
import com.kole.cache.hashing.Node;

public class JGroupCacheReceiver implements Receiver {
	private static final Logger LOG = LoggerFactory.getLogger(JGroupCacheReceiver.class);
	private final JGroupClusterNode owner;
	private final ConsistentHashing hashing;
	private View currentView;

	public JGroupCacheReceiver(JGroupClusterNode node) {
		hashing = node.getHashingAlgorithm();
		this.owner = node;
	}

	public void getState(OutputStream arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	public void receive(Message message) {
		// TODO need to differentiate broadcast message and check against hashing
		if (message.getObject() instanceof Element) {
			owner.getCache().put((Element) message.getObject());
		} else {
			new UnsupportedCacheElementException("Cannot cast message object into Element: " + message.getObject().getClass());
		}

	}

	public void setState(InputStream arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	public void block() {
		// TODO Auto-generated method stub

	}

	public void suspect(Address arg0) {
		// TODO Auto-generated method stub

	}

	public void unblock() {
		// TODO Auto-generated method stub

	}

	public void viewAccepted(View view) {
		boolean updateRequired = false;
		if (currentView == null) {
			currentView = view.copy();
			List<Address> addresses = view.getMembers();
			for (final Address address : addresses) {
				if (!address.equals(view.getCreator()))
					hashing.addNode(new Node() {

						public String getName() {
							return address.toString();
						}
					});
			}
		} else {
			// add new node
			if (currentView.size() < view.size()) {
				for (final Address address : view.getMembers()) {
					if (!currentView.containsMember(address)) {
						hashing.addNode(new Node() {

							public String getName() {
								return address.toString();
							}
						});
					}
				}
				owner.evaluateCache();
			} else {
				// delete node
				for (final Address address : currentView.getMembers()) {
					if (!view.containsMember(address)) {
						hashing.deleteNode(new Node() {

							public String getName() {
								return address.toString();
							}
						});
					}
				}
			}
		}
	}
}
