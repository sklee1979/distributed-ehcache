package com.kole.cache.cluster.jgroup.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.junit.Before;
import org.junit.Test;

import com.kole.cache.cluster.ClusterInitializationException;
import com.kole.cache.cluster.jgroup.JGroupClusterNode;

public class JGroupClusterNodeTest {
	private static final String clusterName = "c1";
	private static final String nodeName = "n1";
	private URL clusterConfig;
	private URL cacheConfig;

	@Before
	public void setUp() {
		clusterConfig = this.getClass().getResource("/default-jgroup.xml");
		cacheConfig = this.getClass().getResource("/default-ehcache.xml");
	}

	@Test
	public void testInitialize() {
		JGroupClusterNode node = new JGroupClusterNode();
		try {
			Cache cache = CacheManager.create(cacheConfig).getCache("Default-Cache");
			node.setCache(cache);
			node.initialize(clusterName, nodeName, clusterConfig);
			assertTrue("Cluster name doesn't match", clusterName.equals(node.getClusterName()));
			assertTrue("Node name doesn't match", nodeName.equals(node.getName()));
		} catch (ClusterInitializationException e) {
			fail("Failed to initialise" + e.getMessage());
		}
	}

	@Test
	public void testMultiNodes() {
		try {
			JGroupClusterNode node1 = new JGroupClusterNode();
			Cache cache = CacheManager.create(cacheConfig).getCache("Default-Cache");
			node1.setCache(cache);
			node1.initialize(clusterName, nodeName, clusterConfig);
			assertTrue("Cluster name doesn't match", clusterName.equals(node1.getClusterName()));
			assertTrue("Node name doesn't match", nodeName.equals(node1.getName()));

			JGroupClusterNode node2 = new JGroupClusterNode();
			node2.setCache(cache);
			node2.initialize(clusterName, "n2", clusterConfig);
			assertTrue("Cluster name doesn't match", clusterName.equals(node2.getClusterName()));
			assertTrue("Node name doesn't match", "n2".equals(node2.getName()));

			assertTrue("clusterInfo mismatch", node1.getClusterInfo().getSize() == node2.getClusterInfo().getSize());
			assertTrue("clusterInfo mismatch - size:" + node1.getClusterInfo().getSize(), node1.getClusterInfo().getSize() == 2);
			System.out.println("Cluster nodes - from node1: " + node1.getClusterInfo().getNodes());
			System.out.println("Cluster nodes - from node2: " + node2.getClusterInfo().getNodes());
		} catch (ClusterInitializationException e) {
			fail("Failed to initialise" + e.getMessage());
		}
	}

	@Test
	public void testSetCache() {
		JGroupClusterNode node = new JGroupClusterNode();
		try {
			node.initialize(clusterName, nodeName, clusterConfig);
			fail("No cache setup");
		} catch (ClusterInitializationException e) {
			// should end up here
			System.out.println(e.getMessage());
		}
	}
}
