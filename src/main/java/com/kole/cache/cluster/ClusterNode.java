package com.kole.cache.cluster;

import java.net.URL;

import net.sf.ehcache.Cache;

import com.kole.cache.hashing.Node;

public interface ClusterNode extends Node {
	void initialize(String clusterName, String nodeName, URL configFile) throws ClusterInitializationException;

	void setCache(Cache cache);

	Cache getCache();

	ClusterInfo getClusterInfo();
}
