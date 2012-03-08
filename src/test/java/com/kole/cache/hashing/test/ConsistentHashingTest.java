package com.kole.cache.hashing.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.kole.cache.hashing.ConsistentHashing;
import com.kole.cache.hashing.ConsistentHashingImpl;
import com.kole.cache.hashing.Hash;
import com.kole.cache.hashing.Node;

public class ConsistentHashingTest {
	List<Node> nodes;

	@Before
	public void setUpNodes() {
		nodes = new ArrayList<Node>();
		for (int i = 0; i < 10; i++) {
			nodes.add(new CacheNode("Node" + i));
		}
	}

	@Test
	public void testGetInstanceListOfNodeHash() {
		try {
			ConsistentHashing hashing = ConsistentHashingImpl.getInstance(nodes, Hash.SHA1);
			assertTrue("Size incorrect: " + hashing.size(), hashing.size() == 10 * ConsistentHashingImpl.REPLICA);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error in constructor");
		}
	}

	@Test
	public void testGetInstanceListOfNode() {
		try {
			ConsistentHashing hashing = ConsistentHashingImpl.getInstance(nodes);
			assertTrue("Size incorrect: " + hashing.size(), hashing.size() == 10 * ConsistentHashingImpl.REPLICA);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error in constructor");
		}
	}

	@Test
	public void testAddNode() {
		try {
			ConsistentHashing hashing = ConsistentHashingImpl.getInstance(nodes);
			hashing.addNode(new CacheNode("Node11"));
			assertTrue("Size incorrect: " + hashing.size(), hashing.size() == 11 * ConsistentHashingImpl.REPLICA);
			Node node = hashing.findNode("Node11-3234".getBytes());
			assertTrue("Get the wrong node back: " + node.getName(), node.getName().equals("Node11"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error in constructor");
		}

	}

	@Test
	public void testDeleteNode() {
		try {
			ConsistentHashing hashing = ConsistentHashingImpl.getInstance(nodes);
			hashing.deleteNode(new CacheNode("Node11"));
			assertTrue("Size incorrect: " + hashing.size(), hashing.size() == 10 * ConsistentHashingImpl.REPLICA);
			Node node = hashing.findNode("Node11-5474".getBytes());
			assertTrue("Get the wrong node back: " + node.getName(), !node.getName().startsWith("Node11"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error in constructor");
		}
	}
}
