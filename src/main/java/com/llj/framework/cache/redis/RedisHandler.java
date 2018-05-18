package com.llj.framework.cache.redis;

import redis.clients.jedis.ShardedJedis;

/**
 * @authorlu
 * @desc
 */
public interface RedisHandler<T> {

	public T handler(ShardedJedis shardJedis);
}
