package com.llj.framework.cache.redis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;

/**
 * @author lu
 * @desc
 */
public interface JedisClient {

	// #####################String操作########################
	// public boolean isAlive();
	public boolean expire(final String key, final int seconds);

	public boolean expire(final byte[] key, final int seconds);

	public Boolean set(final String key, final String value);

	// public Boolean set(final String key, final Number value);
	public Boolean set(byte[] key, byte[] value);

	// public <T extends Serializable> Boolean set(final String key, final T
	// value);
	public Long setnx(final String key, final String value);

	public String get(final String key);

	public byte[] get(final byte[] key);

	public List<String> mget(final String... keys) throws Exception;

	public List<String> mset(final Map<String, String> map) throws Exception;

	public Boolean exists(String key);

	public Long del(final String keys);

	public Long del(final List<String> keys);

	public Long incr(final String key);

	public Long incrBy(final String key, long value);

	public Long decr(final String key);

	public Long decrBy(final String key, final long value);

	public Set<byte[]> keys(final byte[] pattern);

	public Set<String> keys(final String pattern);

	// public Boolean exists(final byte[] key);
	// public Boolean exists(String key);
	// public Long del(final byte[]... keys);
	// public Long del(final String... keys);
	public Long ttl(String key);

	// #####################List操作########################
	public Long rpush(final byte[] key, final byte[] value);

	public Long rpush(final String key, final String value);

	public Long rpush(final String key, final String[] value);

	public Long lpush(final byte[] key, final byte[] value);

	public Long lpush(final String key, final String value);

	public Long lpush(final String key, final String[] value);

	public Long lrem(String key, Long count, String value);

	public String lpop(final String key);

	public String rpop(String key);

	// 这2个接口目前有bug 不建议使用
	public List<String> blpop(String key);

	public List<String> brpop(String key);

	public List<String> blpop(String key, int timeout);

	public List<String> brpop(String key, int timeout);

	public String lindex(String key, long index);

	public List<String> lrange(String key, long start, long end);

	public Long llen(final String key);

	public String lset(String key, long index, String value);

	// #####################Hash表操作########################
	public Long hset(String key, String field, String value);

	public Long hset(byte[] key, byte[] field, byte[] value);

	public Long hsetnx(String key, String field, String value);

	public String hget(String key, String value);

	public byte[] hget(byte[] key, byte[] field);

	public String hmset(String key, Map<String, String> map);

	public List<String> hmget(String key, String... fields);

	public Boolean hexists(String key, String field);

	public Long hincrby(String key, String field, Long value);

	public Map<String, String> hgetall(String key);

	public Long hdel(String key, String... fields);

	public Long hdel(byte[] key, byte[] fields);

	public Long hlen(String key);

	public Set<String> hkeys(String key);

	public Set<byte[]> hkeys(byte[] key);

	public List<String> hvals(String key);

	public <T extends Serializable> List<T> hvalsToObject(String key);

	// #####################Set操作########################
	public Long sadd(String key, String... member);

	public Long srem(String key, String... members);

	public Boolean sismember(String key, String member);

	public String spop(String key);

	public Set<String> spop(String key, long count);

	public Long scard(String key);

	public Set<String> smembers(String key);

	// #####################SortedSet操作########################
	public Long zadd(String key, long score, String member);

	public Long zadd(String key, Map<String, Double> scoreMembers);

	public Set<String> zrange(String key, long start, long end);

	public Set<String> zrevrange(String key, long start, long end);

	public Set<String> zrangeByScore(String key, double min, double max);

	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count);

	public Set<String> zrevrangeByScore(String key, double max, double min);

	public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count);

	public Long zrem(String key, String... members);

	public Long zremrangeByScore(String key, String start, String end);

	public Long zcount(String key, double min, double max);

	public Long zcard(String key);

	public Double zscore(String key, String member);

	public Long zrank(String key, String member);

	public Long zrevrank(String key, String member);

	// #####################pipeline操作########################
	public ShardedJedisPipeline getPipeline();

	public ShardedJedis getShardedJedis();

	public void releaseShardedJedis(ShardedJedis shardJedis, Boolean hasException);
}
