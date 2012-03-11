package com.kole.cache.cluster.jgroup;

import java.net.URL;

import net.sf.ehcache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kole.cache.cluster.ClusterInitializationException;

public class DefaultJGroupClusterFactory {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultJGroupClusterFactory.class);

	public static final String DEFAULT_CLUSTER = "Distributed-Cluster-Cache";
	public static final String DEFAULT_CACHE = "Default-Cache";
	private static final String DEFAULT_NODE_NAME = "Ehcache-Node";
	private static final String DEFAULT_JGROUP_CONFIG = "/default-jgroup.xml";
	private static final String DEFAULT_EHCACHE_CONFIG = "/default-ehcache.xml";

	private JGroupClusterNode node;

	private static DefaultJGroupClusterFactory factory;

	private DefaultJGroupClusterFactory() {
		try {
			URL clusterConfig = this.getClass().getResource(DEFAULT_JGROUP_CONFIG);
			URL cacheConfig = this.getClass().getResource(DEFAULT_EHCACHE_CONFIG);
			node = new JGroupClusterNode();
			node.setCache(CacheManager.create(cacheConfig).getCache(DEFAULT_CACHE));
			node.initialize(DEFAULT_CLUSTER, DEFAULT_NODE_NAME + Math.random(), clusterConfig);
		} catch (ClusterInitializationException e) {
			// should never happen
			LOG.error("Unable to create cluster node", e);
		}

	}

	public static synchronized DefaultJGroupClusterFactory getInstance() {
		if (factory == null) {
			factory = new DefaultJGroupClusterFactory();
		}

		return factory;
	}

	public JGroupClusterNode createNode() {
		return node;
	}
}
