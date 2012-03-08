package com.kole.cache.hashing.test;

import com.kole.cache.hashing.Node;

public class CacheNode implements Node {
	private final String name;
	
	public CacheNode(String cacheName) {
		this.name = cacheName;
	}

	public String getName() {
		return this.name;
	}

}
