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
			Node node = hashing.findNode("Node11-3234");
			assertTrue("Get the wrong node back: " + node.getName(), node.getName().equals("Node11"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			fail("Error in constructor");
		}

	}

	@Test
	public void testDeleteNode() {
		try {
			ConsistentHashing hashing = ConsistentHashingImpl.getInstance(nodes);
			hashing.deleteNode(new CacheNode("Node1"));
			assertTrue("Size incorrect: " + hashing.size(), hashing.size() == 9 * ConsistentHashingImpl.REPLICA);
			Node node = hashing.findNode("Node1-5474");
			assertTrue("Get the wrong node back: " + node.getName(), !node.getName().startsWith("Node1-"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			fail("Error in constructor");
		}
	}

	@Test
	public void testIsNodeExisted() {
		try {
			ConsistentHashing hashing = ConsistentHashingImpl.getInstance(nodes);
			boolean existed = hashing.isNodeExisted(new CacheNode("Node9"));
			assertTrue("isNodeExisted check failed", existed);
			hashing.addNode(new CacheNode("Node9"));
			assertTrue("Size incorrect: " + hashing.size(), hashing.size() == 10 * ConsistentHashingImpl.REPLICA);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			fail("Error in constructor");
		}
	}
}
