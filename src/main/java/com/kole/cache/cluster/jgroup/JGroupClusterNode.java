package com.kole.cache.cluster.jgroup;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kole.cache.cluster.ClusterInfo;
import com.kole.cache.cluster.ClusterInitializationException;
import com.kole.cache.cluster.ClusterNode;
import com.kole.cache.hashing.ConsistentHashing;
import com.kole.cache.hashing.ConsistentHashingImpl;
import com.kole.cache.hashing.Node;

public class JGroupClusterNode implements ClusterNode {
	private static final Logger LOG = LoggerFactory.getLogger(JGroupClusterNode.class);
	private JChannel channel;
	private Cache cache;
	private ConsistentHashing hashing;

	public JGroupClusterNode() {
		try {
			hashing = ConsistentHashingImpl.getInstance(new ArrayList<Node>());
		} catch (NoSuchAlgorithmException e) {
			// should not happen
			LOG.error("Failed to initialise JGroupCacheReceiver, No SHA1", e);
		}
	}

	public void initialize(final String clusterName, final String nodeName, URL configFile) throws ClusterInitializationException {
		if (cache == null) {
			throw new ClusterInitializationException("Cache is null. Please set a cache first");
		}
		try {
			channel = new JChannel(configFile);
			channel.setName(nodeName);
			channel.setReceiver(new JGroupCacheReceiver(this));
			channel.connect(clusterName);
		} catch (Exception e) {
			throw new ClusterInitializationException(e);
		}
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public Cache getCache() {
		return cache;
	}

	public String getName() {
		return channel.getName();
	}

	public ClusterInfo getClusterInfo() {
		ClusterInfo info = new ClusterInfo();
		info.setClusterName(channel.getClusterName());
		info.setSize(channel.getView().size() - 1);
		List<String> nodes = new ArrayList<String>();
		Address creator = channel.getView().getCreator();
		for (Address addr : channel.getView().getMembers()) {
			if (!creator.equals(addr))
				nodes.add(addr.toString());
		}
		info.setNodes(nodes);

		return info;
	}

	public String getClusterName() {
		return channel.getClusterName();
	}

	public int getSize() {
		return channel.getView().size();
	}

	public void evaluateCache() {
		List<String> keys = cache.getKeys();
		Address creator = channel.getView().getCreator();
		for (String key : keys) {
			String nodeName = hashing.findNode(key).getName();
			if (!nodeName.equals(getName())) {
				// need to public the element to the new location
				for (Address address : channel.getView().getMembers()) {
					if (!address.equals(creator)) {
						if (address.toString().equals(nodeName)) {
							try {
								channel.send(address, cache.get(key));
							} catch (IllegalStateException e) {
								LOG.error("Cannot get the cache element", e);
							} catch (CacheException e) {
								LOG.error("Cannot get the cache element", e);
							} catch (Exception e) {
								LOG.error("Cannot send message", e);
							}
							break;
						}
					}
				}
			}
		}
	}

	public void publishCache() {
		List<String> keys = cache.getKeys();
		for (String key : keys) {
			try {
				channel.send(null, cache.get(key));
			} catch (IllegalStateException e) {
				LOG.error("Cannot get the cache element", e);
			} catch (CacheException e) {
				LOG.error("Cannot get the cache element", e);
			} catch (Exception e) {
				LOG.error("Cannot send message", e);
			}

		}
	}

	public ConsistentHashing getHashingAlgorithm() {
		return hashing;
	}
}
