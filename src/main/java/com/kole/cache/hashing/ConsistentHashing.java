package com.kole.cache.hashing;


/**
 * 
 * @author edwardlee
 * 
 */
public interface ConsistentHashing {
	void addNode(Node node);

	void deleteNode(Node node);

	boolean isNodeExisted(Node node);

	Node findNode(String key);

	int size();

	String hashing(String key);
}
