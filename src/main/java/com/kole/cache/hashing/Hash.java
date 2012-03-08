package com.kole.cache.hashing;

public enum Hash {
	SHA1 ("SHA1");
	
	private final String algorithm;
	
	private Hash(String AlgorithmName) {
		this.algorithm = AlgorithmName;
	}
	
	public String toStirng() {
		return algorithm; 
	}
}
