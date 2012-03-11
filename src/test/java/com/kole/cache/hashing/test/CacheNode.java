package com.kole.cache.hashing.test;

import com.kole.cache.hashing.Node;

public class CacheNode implements Node {
	private final String name;

	public CacheNode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
