package com.kole.cache.hashing;

/**
 * 
 * @author edwardlee
 * 
 */
public interface ConsistentHashing {
	void addNode(Node node);

	void deleteNode(Node node);

	Node findNode(byte[] key);

	int size();

	String hashing(String key);
}
