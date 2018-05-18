package com.llj.framework.log;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/*
 * @author lu
 */
public final class IDGenerator {
	private static final int SEQUENCE_SEED = 1000000;
	private static final AtomicLong SEQUENCE = new AtomicLong(1);

	/**
	 * @return pseudo randomly generated {@code UUID}
	 */
	public final static String generate() {
		return UUID.randomUUID().toString();
	}

	/**
	 * @param salt
	 * @return name based {@code UUID}
	 */
	public final static String generateWithSalt(String salt) {
		return UUID.nameUUIDFromBytes(salt.getBytes()).toString();
	}

	/**
	 * @return (timestamp in nanoseconds) * seed + sequence
	 */
	public final static String generateWithSeq() {
		return Long.toString(System.currentTimeMillis() * SEQUENCE_SEED + SEQUENCE.incrementAndGet());
	}

}