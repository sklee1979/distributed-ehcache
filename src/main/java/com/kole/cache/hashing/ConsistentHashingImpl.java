package com.kole.cache.hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ConsistentHashingImpl implements ConsistentHashing {
	private final MessageDigest digest;
	public static final int REPLICA = 10000;
	private final TreeMap<String, Node> hashMap = new TreeMap<String, Node>();

	private ConsistentHashingImpl(List<Node> nodes, Hash algorithm)
			throws NoSuchAlgorithmException {
		Security.addProvider(new BouncyCastleProvider());
		this.digest = MessageDigest.getInstance(algorithm.toStirng());
		for (Node node : nodes) {
			for (int i = 0; i < REPLICA; i++) {
				hashMap.put(this.hashing(node.getName() + "-" + i), node);
			}
		}
	}

	public static ConsistentHashingImpl getInstance(List<Node> nodes,
			Hash algorithm) throws NoSuchAlgorithmException {
		return new ConsistentHashingImpl(nodes, algorithm);
	}

	public static ConsistentHashingImpl getInstance(List<Node> nodes)
			throws NoSuchAlgorithmException {
		return new ConsistentHashingImpl(nodes, Hash.SHA1);
	}

	public void addNode(Node node) {
		for (int i = 0; i < REPLICA; i++) {
			hashMap.put(this.hashing(node.getName() + "-" + i), node);
		}
	}

	public void deleteNode(Node node) {
		for (int i = 0; i < REPLICA; i++) {
			hashMap.remove(this.hashing(node.getName() + "-" + i));
		}
	}

	public Node findNode(byte[] key) {
		String location = Base64.encodeBase64String(this.digest.digest(key));
		SortedMap<String, Node> nodes = hashMap.tailMap(location);
		if (nodes.size() == 0) {
			return hashMap.firstEntry().getValue();
		} else {
			return nodes.get(nodes.firstKey());
		}

	}

	public int size() {
		return hashMap.size();
	}

	public String hashing(String key) {
		return Base64.encodeBase64String(this.digest.digest(key.getBytes()));
	}

}
