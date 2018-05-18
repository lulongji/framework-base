package com.llj.framework.cache.redis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.llj.framework.log.BaseResourceLog;
import com.llj.framework.log.LogFoot;
import com.llj.framework.log.LogTypeEnum;
import com.llj.framework.utils.SerializeUtil;
import com.llj.framework.utils.data.CastUtil;
import com.llj.framework.utils.data.MyStringUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author lu
 * @desc
 */
@SuppressWarnings("deprecation")
public class ShardedJedisClient extends BaseResourceLog implements JedisClient {

	private static final Logger log = LogManager.getLogger(ShardedJedisClient.class);

	public static Long error_num = -2L;
	public static String error_str = "error";
	// redis的配置文件名字，默认redis.properties
	private String config_path = "";
	private final Properties properties = new Properties();
	// shardedJedis池
	private ShardedJedisPool shardPool;
	// 高可用方案中 指定master或slave
	private String HA_flag = null;

	static final String REDIS_RET_OK = "OK";

	// 应用程序根据指定的redis配置文件来初始化池配置
	public ShardedJedisClient(String config_name) throws FileNotFoundException, IOException {
		this.config_path = config_name;
		if (shardPool == null) {
			initial();
		}
	}

	public ShardedJedisClient(String config_name, String HA_flag) throws FileNotFoundException, IOException {
		this.config_path = config_name;
		this.HA_flag = HA_flag;
		if (shardPool == null) {
			initial();
		}
	}

	private void initial() throws IOException {

		File file = new File(config_path);
		Reader reader = null;
		if (file.exists()) {
			try {
				reader = new FileReader(config_path);
			} catch (FileNotFoundException e) {
				reader = new InputStreamReader(ShardedJedisClient.class.getResourceAsStream("/" + config_path));
			}
		} else {
			reader = new InputStreamReader(ShardedJedisClient.class.getResourceAsStream("/" + config_path));
		}

		// 加载redis配置文件
		properties.load(reader);
		reader.close();
		// 创建jedis池配置实例
		JedisPoolConfig config = new JedisPoolConfig();
		// 设置池配置项值
		// config.setTimeBetweenEvictionRunsMillis(Integer.valueOf(properties.getProperty("redis.pool.maxActive")));
		if (!MyStringUtil.isEmpty(properties.getProperty("redis.pool.maxActive")))
			config.setMaxTotal(Integer.valueOf(properties.getProperty("redis.pool.maxActive")));
		if (!MyStringUtil.isEmpty(properties.getProperty("redis.pool.maxIdle")))
			config.setMaxIdle(Integer.valueOf(properties.getProperty("redis.pool.maxIdle")));
		if (!MyStringUtil.isEmpty(properties.getProperty("redis.pool.minIdle")))
			config.setMinIdle(Integer.valueOf(properties.getProperty("redis.pool.minIdle")));
		if (!MyStringUtil.isEmpty(properties.getProperty("redis.pool.maxWait")))
			config.setMaxWaitMillis(Long.valueOf(properties.getProperty("redis.pool.maxWait")));
		if (!MyStringUtil.isEmpty(properties.getProperty("redis.pool.testOnBorrow")))
			config.setTestOnBorrow(Boolean.valueOf(properties.getProperty("redis.pool.testOnBorrow")));
		if (!MyStringUtil.isEmpty(properties.getProperty("redis.pool.testOnReturn")))
			config.setTestOnReturn(Boolean.valueOf(properties.getProperty("redis.pool.testOnReturn")));

		// 根据配置创建多个redis共享服务
		int redis_num = Integer.valueOf(properties.getProperty("redis.num"));
		List<JedisShardInfo> list = new LinkedList<JedisShardInfo>();
		if (redis_num > 0) {
			for (int i = 0; i < redis_num; i++) {
				if (MyStringUtil.isEmpty(HA_flag)) {
					JedisShardInfo jedisShardInfo = new JedisShardInfo(properties.getProperty("redis" + i + ".ip"),
							Integer.valueOf(properties.getProperty("redis" + i + ".port")));
					jedisShardInfo.setPassword(CastUtil.castString(properties.getProperty("redis" + i + ".pwd")));
					list.add(jedisShardInfo);
				} else {
					JedisShardInfo jedisShardInfo = new JedisShardInfo(properties.getProperty(HA_flag + "_" + "redis" + i + ".ip"),
							Integer.valueOf(properties.getProperty(HA_flag + "_" + "redis" + i + ".port")));
					jedisShardInfo.setPassword(CastUtil.castString(properties.getProperty(HA_flag + "_" + "redis" + i + ".port")));
					list.add(jedisShardInfo);
				}
			}
		}

		// 根据配置文件,创建shared池实例
		shardPool = new ShardedJedisPool(config, list);
	}

	// 应用程序根据指定的redis配置文件来初始化池配置
	public ShardedJedisClient(Reader reader) throws IOException {
		if (shardPool == null) {
			initFromReader(reader);
		}
	}

	private void initFromReader(Reader reader) throws IOException {
		// 加载redis配置文件
		properties.load(reader);
		reader.close();
		// 创建jedis池配置实例
		JedisPoolConfig config = new JedisPoolConfig();
		// 设置池配置项值
		// config.setMaxActive(Integer.valueOf(properties
		// .getProperty("redis.pool.maxActive")));
		config.setMaxIdle(Integer.valueOf(properties.getProperty("redis.pool.maxIdle")));
		config.setMaxWaitMillis(Long.valueOf(properties.getProperty("redis.pool.maxWait")));
		config.setTestOnBorrow(Boolean.valueOf(properties.getProperty("redis.pool.testOnBorrow")));
		config.setTestOnReturn(Boolean.valueOf(properties.getProperty("redis.pool.testOnReturn")));

		// 根据配置创建多个redis共享服务
		int redis_num = Integer.valueOf(properties.getProperty("redis.num"));
		List<JedisShardInfo> list = new LinkedList<JedisShardInfo>();
		if (redis_num > 0) {
			for (int i = 0; i < redis_num; i++) {
				JedisShardInfo jedisShardInfo = new JedisShardInfo(properties.getProperty("redis" + i + ".ip"),
						Integer.valueOf(properties.getProperty("redis" + i + ".port")));
				list.add(jedisShardInfo);
			}
		}

		// 根据配置文件,创建shared池实例
		shardPool = new ShardedJedisPool(config, list);
	}

	// private ShardedJedis getJedis(){
	// // 从shard池中获取shardJedis实例
	// ShardedJedis shardJedis = shardPool.getResource();
	// return shardJedis;
	// }

	/**
	 * 暂时用于扩展接口之外的redis方法，不建议使用
	 * 
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	public <T> T handler(RedisHandler<T> handler) throws Exception {
		ShardedJedis shardJedis = shardPool.getResource();
		T result = null;
		try {
			result = handler.handler(shardJedis);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis handler error!" + e);
			throw new Exception("redis handler error!" + e);
		} finally {
			shardPool.returnResource(shardJedis);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#expire(java.lang.String,
	 * int)
	 */
	@Override
	public boolean expire(String key, int seconds) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.expire(key, seconds);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis expire error!" + e);
			doAferService(logFoot, "expire", false, new Object[]{key, seconds}, e);
			return false;
		}
		shardPool.returnResource(shardJedis);
		if (result == null || result != 1) {
			doAferService(logFoot, "expire", false, new Object[]{key, seconds}, false);
			return false;
		}
		doAferService(logFoot, "expire", true, new Object[]{key, seconds}, true);
		return true;
	}

	@Override
	public boolean expire(final byte[] key, final int seconds) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.expire(key, seconds);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis expire error!" + e);
			doAferService(logFoot, "expire", false, new Object[]{key, seconds}, e);
			return false;
		}
		shardPool.returnResource(shardJedis);
		if (result == null || result != 1) {
			doAferService(logFoot, "expire", false, new Object[]{key, seconds}, false);
			return false;
		}
		doAferService(logFoot, "expire", true, new Object[]{key, seconds}, true);
		return true;
	}

	@Override
	public Long ttl(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.ttl(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis ttl error!" + e);
			doAferService(logFoot, "ttl", false, new Object[]{key}, e);
			return -2L;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "ttl", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public Long del(final String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.del(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis del error!" + e);
			doAferService(logFoot, "del", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "del", true, new Object[]{key}, result);
		return result;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Long del(final List<String> keys) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = 0L;
		Map<Jedis, Set<String>> shardmap = new HashMap<Jedis, Set<String>>();
		try {
			for (String key : keys) {
				Jedis jedis = shardJedis.getShard(key);
				Set<String> keyList = shardmap.get(jedis);
				if (keyList == null) {
					keyList = new HashSet();
					keyList.add(key);
				} else {
					keyList.add(key);
				}
				shardmap.put(jedis, keyList);
			}
			Collection<Jedis> jedisList = shardJedis.getAllShards();
			for (Jedis jedis : jedisList) {
				Set<String> keySet = shardmap.get(jedis);
				if (keySet == null)
					continue;
				String[] keyArr = new String[keySet.size()];
				Long succkey = jedis.del(keySet.toArray(keyArr));
				result += succkey;
			}
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis del error!" + e);
			doAferService(logFoot, "del", false, new Object[]{keys}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "del", true, new Object[]{keys}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#setnx(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Long setnx(String key, String value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.setnx(key, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis setnx error!" + e);
			doAferService(logFoot, "setnx", false, new Object[]{key, value}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "setnx", true, new Object[]{key, value}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#set(byte[], byte[])
	 */
	@Override
	public Boolean set(byte[] key, byte[] value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		String result = null;
		try {
			result = shardJedis.set(key, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis set error!" + e);
			doAferService(logFoot, "set", false, new Object[]{key, value}, e);
			return false;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "set", true, new Object[]{key, value}, result);
		return REDIS_RET_OK.equalsIgnoreCase(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#set(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Boolean set(String key, String value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		String result = null;
		try {
			result = shardJedis.set(key, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis set error!" + e);
			doAferService(logFoot, "set", false, new Object[]{key, value}, e);
			return false;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "set", true, new Object[]{key, value}, result);
		return REDIS_RET_OK.equalsIgnoreCase(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#get(java.lang.String)
	 */
	@Override
	public String get(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		String result = null;
		try {
			result = shardJedis.get(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis get error!" + e);
			doAferService(logFoot, "get", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "get", true, new Object[]{key}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#get(java.lang.String)
	 */
	@Override
	public byte[] get(byte[] key) {
		String keyStr = new String(key);
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		byte[] result = null;
		try {
			result = shardJedis.get(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis get error!" + e);
			doAferService(logFoot, "get", false, new Object[]{keyStr}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "get", true, new Object[]{keyStr}, result == null ? "" : new String(result));
		return result;
	}

	@Override
	public List<String> mget(final String... keys) throws Exception {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		List<String> list = new ArrayList<String>();
		try {
			ShardedJedisPipeline pipeline = shardJedis.pipelined();
			for (String key : keys)
				pipeline.get(key);
			List<Object> results = pipeline.syncAndReturnAll();
			for (Object o : results) {
				list.add((String) o);
			}
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis mget error!" + e);
			doAferService(logFoot, "mget", false, new Object[]{keys}, e);
			throw new Exception(e);
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "mget", true, new Object[]{keys}, list);
		return list;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<String> mset(final Map<String, String> map) throws Exception {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		List<String> list = new ArrayList<String>();
		try {
			ShardedJedisPipeline pipeline = shardJedis.pipelined();
			for (Entry entry : map.entrySet())
				pipeline.set((String) entry.getKey(), (String) entry.getValue());
			List<Object> results = pipeline.syncAndReturnAll();
			for (Object o : results) {
				list.add((String) o);
			}
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis mset error!" + e);
			doAferService(logFoot, "mset", false, new Object[]{map}, e);
			throw new Exception(e);
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "mset", true, new Object[]{map}, list);
		return list;
	}

	@Override
	public Long incr(final String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.incr(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis incr error!" + e);
			doAferService(logFoot, "incr", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "incr", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public Long incrBy(String key, long value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.incrBy(key, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis incrBy error!" + e);
			doAferService(logFoot, "incrBy", false, new Object[]{key, value}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "incrBy", true, new Object[]{key, value}, result);
		return result;
	}

	@Override
	public Long decrBy(String key, long value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.decrBy(key, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis decrBy error!" + e);
			doAferService(logFoot, "decrBy", false, new Object[]{key, value}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "decrBy", true, new Object[]{key, value}, result);
		return result;
	}

	@Override
	public Long decr(final String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.decr(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis decr error!" + e);
			doAferService(logFoot, "decr", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "decr", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public Boolean exists(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Boolean result = null;
		try {
			result = shardJedis.exists(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis exists error!" + e);
			doAferService(logFoot, "exists", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "exists", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public Set<byte[]> keys(final byte[] pattern) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		String patternStr = new String(pattern);
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Set<byte[]> result = new HashSet<byte[]>();
		try {
			Collection<Jedis> allredis = shardJedis.getAllShards();
			for (Jedis jedis : allredis) {
				Set<byte[]> newset = jedis.keys(pattern);
				result.addAll(newset);
			}
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis keys error!" + e);
			doAferService(logFoot, "keys", false, new Object[]{patternStr}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "keys", true, new Object[]{patternStr}, result);
		return result;
	}

	@Override
	public Set<String> keys(String pattern) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Set<String> result = new HashSet<String>();
		try {
			Collection<Jedis> allredis = shardJedis.getAllShards();
			for (Jedis jedis : allredis) {
				Set<String> newset = jedis.keys(pattern);
				result.addAll(newset);
			}
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis keys error!" + e);
			doAferService(logFoot, "keys", false, new Object[]{pattern}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "keys", true, new Object[]{pattern}, result);
		return result;
	}

	@Override
	public String lindex(String key, long index) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		String result = null;
		try {
			result = shardJedis.lindex(key, index);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis lindex error!" + e);
			doAferService(logFoot, "lindex", false, new Object[]{key, index}, e);
			return error_str;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "lindex", true, new Object[]{key, index}, result);
		return result;
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		List<String> result = null;
		try {
			result = shardJedis.lrange(key, start, end);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis lrange error!" + e);
			doAferService(logFoot, "lrange", false, new Object[]{key, start, end}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "lrange", true, new Object[]{key, start, end}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#rpush(byte[], byte[])
	 */
	@Override
	public Long rpush(byte[] key, byte[] value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.rpush(key, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis rpush error!" + e);
			doAferService(logFoot, "rpush", false, new Object[]{new String(key), new String(value)}, e);
			return error_num;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "rpush", true, new Object[]{new String(key), new String(value)}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#rpush(java.lang.String,
	 * byte[])
	 */
	@Override
	public Long rpush(String key, String value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.rpush(key, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis rpush error!" + e);
			doAferService(logFoot, "rpush", false, new Object[]{key, value}, e);
			return error_num;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "rpush", true, new Object[]{key, value}, result);
		return result;
	}

	@Override
	public Long rpush(String key, String[] value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.rpush(key, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis rpush error!" + e);
			doAferService(logFoot, "rpush", false, new Object[]{key, value}, e);
			return error_num;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "rpush", true, new Object[]{key, value}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#lpop(java.lang.String)
	 */
	@Override
	public String rpop(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		String result = null;
		try {
			result = shardJedis.rpop(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis rpop error!" + e);
			doAferService(logFoot, "rpop", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "rpop", true, new Object[]{key}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#lpop(java.lang.String)
	 */
	@Override
	public String lpop(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		String result = null;
		try {
			result = shardJedis.lpop(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis lpop error!" + e);
			doAferService(logFoot, "lpop", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "lpop", true, new Object[]{key}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#lpop(java.lang.String)
	 */
	@Override
	public List<String> brpop(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		List<String> result = new ArrayList<String>();
		try {
			result = shardJedis.brpop(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis brpop error!" + e);
			doAferService(logFoot, "brpop", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "brpop", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public List<String> brpop(String key, int timeout) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		List<String> result = new ArrayList<String>();
		try {
			result = shardJedis.brpop(timeout, key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis brpop error!" + e);
			doAferService(logFoot, "brpop", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "brpop", true, new Object[]{key}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#lpop(java.lang.String)
	 */
	@Override
	public List<String> blpop(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		List<String> result = new ArrayList<String>();
		try {
			result = shardJedis.blpop(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis lpop error!" + e);
			doAferService(logFoot, "blpop", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "blpop", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public List<String> blpop(String key, int timeout) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		List<String> result = new ArrayList<String>();
		try {
			result = shardJedis.blpop(timeout, key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis blpop error!" + e);
			doAferService(logFoot, "blpop", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "blpop", true, new Object[]{key}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#lpush(byte[], byte[])
	 */
	@Override
	public Long lpush(byte[] key, byte[] value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		String keyStr = "", valueStr = "";
		if (key != null)
			keyStr = new String(key);
		if (value != null)
			valueStr = new String(value);
		try {
			result = shardJedis.lpush(key, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis lpush error!" + e);
			doAferService(logFoot, "lpush", false, new Object[]{keyStr, valueStr}, e);
			return error_num;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "lpush", true, new Object[]{keyStr, valueStr}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.qding.framework.common.redis.JedisClient#lpush(java.lang.String,
	 * byte[])
	 */
	@Override
	public Long lpush(String key, String value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.lpush(key, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis lpush error!" + e);
			doAferService(logFoot, "lpush", false, new Object[]{key, value}, e);
			return error_num;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "lpush", true, new Object[]{key, value}, result);
		return result;
	}

	@Override
	public Long lpush(String key, String[] value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.lpush(key, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis lpush error!" + e);
			doAferService(logFoot, "lpush", false, new Object[]{key, value}, e);
			return error_num;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "lpush", true, new Object[]{key, value}, result);
		return result;
	}

	@Override
	public Long lrem(String key, Long count, String value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.lrem(key, count, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis lrem error!" + e);
			doAferService(logFoot, "lrem", false, new Object[]{key, value}, e);
			return error_num;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "lrem", true, new Object[]{key, value}, result);
		return result;
	}

	@Override
	public Long llen(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.llen(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis llen error!" + e);
			doAferService(logFoot, "llen", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "llen", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public String lset(String key, long index, String value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		String result = null;
		try {
			result = shardJedis.lset(key, index, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis lset error!" + e);
			doAferService(logFoot, "lset", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "lset", true, new Object[]{key}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qding.framework.common.redis.JedisClient#hset()
	 */
	@Override
	public Long hset(String key, String field, String value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.hset(key, field, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hset error!" + e);
			doAferService(logFoot, "hset", false, new Object[]{key, field, value}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hset", true, new Object[]{key, field, value}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qding.framework.common.redis.JedisClient#hset()
	 */
	@Override
	public Long hsetnx(String key, String field, String value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.hsetnx(key, field, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hsetnx error!" + e);
			doAferService(logFoot, "hsetnx", false, new Object[]{key, field, value}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hsetnx", true, new Object[]{key, field, value}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qding.framework.common.redis.JedisClient#hset()
	 */
	@Override
	public Long hset(byte[] key, byte[] field, byte[] value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		String keyStr = "", filedStr = "", valueStr = "";
		if (key != null)
			keyStr = new String(key);
		if (field != null)
			filedStr = new String(field);
		if (valueStr != null)
			valueStr = new String(value);
		try {
			result = shardJedis.hset(key, field, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hset error!" + e);
			doAferService(logFoot, "hset", false, new Object[]{keyStr, filedStr, valueStr}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hset", true, new Object[]{keyStr, filedStr, valueStr}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qding.framework.common.redis.JedisClient#hget()
	 */
	@Override
	public String hget(String key, String field) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		String result = null;
		try {
			result = shardJedis.hget(key, field);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hget error!" + e);
			doAferService(logFoot, "hget", false, new Object[]{key, field}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hget", true, new Object[]{key, field}, result);
		return result;
	}

	@Override
	public byte[] hget(byte[] key, byte[] field) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		byte[] result = null;
		String keyStr = "", fieldStr = "";
		if (key != null)
			keyStr = new String(key);
		if (field != null)
			fieldStr = new String(field);
		try {
			result = shardJedis.hget(key, field);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hget error!" + e);
			doAferService(logFoot, "hget", false, new Object[]{keyStr, fieldStr}, e);
			return null;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hget", true, new Object[]{keyStr, fieldStr}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qding.framework.common.redis.JedisClient#hmset()
	 */
	@Override
	public String hmset(String key, Map<String, String> map) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		String result = null;
		try {
			result = shardJedis.hmset(key, map);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hmset error!" + e);
			doAferService(logFoot, "hmset", false, new Object[]{key, map}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hmset", true, new Object[]{key, map}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qding.framework.common.redis.JedisClient#hmget()
	 */
	@Override
	public List<String> hmget(String key, String... fields) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		List<String> result = null;
		try {
			result = shardJedis.hmget(key, fields);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hmget error!" + e);
			doAferService(logFoot, "hmget", false, new Object[]{key, fields}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hmget", true, new Object[]{key, fields}, result);
		return result;
	}

	@Override
	public Boolean hexists(String key, String field) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Boolean result = null;
		try {
			result = shardJedis.hexists(key, field);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hexists error!" + e);
			doAferService(logFoot, "hexists", false, new Object[]{key, field}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hexists", true, new Object[]{key, field}, result);
		return result;
	}

	@Override
	public Map<String, String> hgetall(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Map<String, String> result = null;
		try {
			result = shardJedis.hgetAll(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hgetall error!" + e);
			doAferService(logFoot, "hgetall", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hgetall", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public Long hincrby(String key, String field, Long value) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.hincrBy(key, field, value);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hincrby error!" + e);
			doAferService(logFoot, "hincrby", false, new Object[]{key, field, value}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hincrby", true, new Object[]{key, field, value}, result);
		return result;
	}

	@Override
	public Long hdel(String key, String... fields) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.hdel(key, fields);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hdel error!" + e);
			doAferService(logFoot, "hdel", false, new Object[]{key, fields}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hdel", true, new Object[]{key, fields}, result);
		return result;
	}

	@Override
	public Long hdel(byte[] key, byte[] fields) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		String keyStr = "", fieldStr = "";
		if (key != null)
			keyStr = new String(key);
		if (fields != null)
			fieldStr = new String(fields);
		try {
			result = shardJedis.hdel(key, fields);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hdel bytes error!" + e);
			doAferService(logFoot, "hdel", false, new Object[]{keyStr, fieldStr}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hdel", true, new Object[]{keyStr, fieldStr}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qding.framework.common.redis.JedisClient#hlen(java.lang.String)
	 */
	@Override
	public Long hlen(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.hlen(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hlen error!" + e);
			doAferService(logFoot, "hlen", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hlen", true, new Object[]{key}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qding.framework.common.redis.JedisClient#hkeys(java.lang.String)
	 */
	@Override
	public Set<String> hkeys(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Set<String> result = null;
		try {
			result = shardJedis.hkeys(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hkeys error!" + e);
			doAferService(logFoot, "hkeys", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hkeys", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public Set<byte[]> hkeys(byte[] key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Set<byte[]> result = null;
		String keyStr = "";
		if (key != null)
			keyStr = new String(key);
		try {
			result = shardJedis.hkeys(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hkeys bytes error!" + e);
			doAferService(logFoot, "hkeys", false, new Object[]{keyStr}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hkeys", true, new Object[]{keyStr}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qding.framework.common.redis.JedisClient#hvals(java.lang.String)
	 */
	@Override
	public List<String> hvals(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		List<String> result = null;
		try {
			result = shardJedis.hvals(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hvals error!" + e);
			doAferService(logFoot, "hvals", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "hvals", true, new Object[]{key}, result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qding.framework.common.redis.JedisClient#hvals(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> List<T> hvalsToObject(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		List<T> list = new ArrayList<T>();
		ShardedJedis shardJedis = shardPool.getResource();
		Collection<byte[]> result = null;
		try {
			result = shardJedis.hvals(key.getBytes());
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis hvalsToObject error!" + e);
			doAferService(logFoot, "hvalsToObject", false, new Object[]{key}, e);
			return null;
		}
		shardPool.returnResource(shardJedis);
		Iterator<byte[]> it = result.iterator();
		while (it.hasNext()) {
			list.add((T) SerializeUtil.decode(it.next()));
		}
		doAferService(logFoot, "hvalsToObject", true, new Object[]{key}, list);
		return list;
	}

	@Override
	public Long sadd(String key, String... member) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.sadd(key, member);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis sadd error!" + e);
			doAferService(logFoot, "sadd", false, new Object[]{key, member}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "sadd", true, new Object[]{key, member}, result);
		return result;
	}

	@Override
	public Long srem(String key, String... members) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.srem(key, members);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis srem error!" + e);
			doAferService(logFoot, "srem", false, new Object[]{key, members}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "srem", true, new Object[]{key, members}, result);
		return result;
	}

	@Override
	public Boolean sismember(String key, String member) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Boolean result = null;
		try {
			result = shardJedis.sismember(key, member);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis sismember error!" + e);
			doAferService(logFoot, "sismember", false, new Object[]{key, member}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "sismember", true, new Object[]{key, member}, result);
		return result;
	}

	@Override
	public String spop(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		String result = null;
		try {
			result = shardJedis.spop(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis spop error!" + e);
			doAferService(logFoot, "spop", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "spop", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public Set<String> spop(String key, long count) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Set<String> result = new HashSet<String>();
		try {
			for (int i = 0; i < count; i++) {
				String resultString = shardJedis.spop(key);
				if (StringUtils.isBlank(resultString)) {
					break;
				}
				result.add(resultString);
			}
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis spop error!" + e);
			doAferService(logFoot, "spop", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "spop", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public Set<String> smembers(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Set<String> result = new HashSet<String>();
		try {
			result = shardJedis.smembers(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis spop error!" + e);
			doAferService(logFoot, "spop", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "spop", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public Long scard(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = 0l;
		try {
			result = shardJedis.scard(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis scard error!" + e);
			doAferService(logFoot, "scard", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "scard", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public Long zadd(String key, long score, String member) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.zadd(key, score, member);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zadd error!" + e);
			doAferService(logFoot, "zadd", false, new Object[]{key, score, member}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zadd", true, new Object[]{key, score, member}, result);
		return result;
	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.zadd(key, scoreMembers);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zadd error!" + e);
			doAferService(logFoot, "zadd", false, new Object[]{key, scoreMembers}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zadd", true, new Object[]{key, scoreMembers}, result);
		return result;
	}

	@Override
	public Set<String> zrange(String key, long start, long end) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Set<String> result = null;
		try {
			result = shardJedis.zrange(key, start, end);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zrange error!" + e);
			doAferService(logFoot, "zrange", false, new Object[]{key, start, end}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zrange", true, new Object[]{key, start, end}, result);
		return result;
	}

	@Override
	public Set<String> zrevrange(String key, long start, long end) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Set<String> result = null;
		try {
			result = shardJedis.zrevrange(key, start, end);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zrevrange error!" + e);
			doAferService(logFoot, "zrevrange", false, new Object[]{key, start, end}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zrevrange", true, new Object[]{key, start, end}, result);
		return result;
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Set<String> result = null;
		try {
			result = shardJedis.zrangeByScore(key, min, max);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zrangeByScore error!" + e);
			doAferService(logFoot, "zrangeByScore", false, new Object[]{key, min, max}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zrangeByScore", true, new Object[]{key, min, max}, result);
		return result;
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Set<String> result = null;
		try {
			result = shardJedis.zrangeByScore(key, min, max, offset, count);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zrangeByScore error!" + e);
			doAferService(logFoot, "zrangeByScore", false, new Object[]{key, min, max, offset, count}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zrangeByScore", true, new Object[]{key, min, max, offset, count}, result);
		return result;
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Set<String> result = null;
		try {
			result = shardJedis.zrevrangeByScore(key, max, min);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zrevrangeByScore error!" + e);
			doAferService(logFoot, "zrevrangeByScore", false, new Object[]{key, max, min}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zrevrangeByScore", true, new Object[]{key, max, min}, result);
		return result;
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Set<String> result = null;
		try {
			result = shardJedis.zrevrangeByScore(key, max, min, offset, count);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zrevrangeByScore error!" + e);
			doAferService(logFoot, "zrevrangeByScore", false, new Object[]{key, max, min, offset, count}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zrevrangeByScore", true, new Object[]{key, max, min, offset, count}, result);
		return result;
	}

	@Override
	public Long zrem(String key, String... members) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.zrem(key, members);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zrem error!" + e);
			doAferService(logFoot, "zrem", false, new Object[]{key, members}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zrem", true, new Object[]{key, members}, result);
		return result;
	}

	@Override
	public Long zremrangeByScore(String key, String start, String end) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.zremrangeByScore(key, start, end);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zremrangeByScore error!" + e);
			doAferService(logFoot, "zremrangeByScore", false, new Object[]{key, start, end}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zremrangeByScore", true, new Object[]{key, start, end}, result);
		return result;
	}

	@Override
	public Long zcount(String key, double min, double max) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.zcount(key, min, max);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zcount error!" + e);
			doAferService(logFoot, "zcard", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zcard", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public Long zcard(String key) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.zcard(key);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zcard error!" + e);
			doAferService(logFoot, "zcard", false, new Object[]{key}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zcard", true, new Object[]{key}, result);
		return result;
	}

	@Override
	public Double zscore(String key, String member) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Double result = null;
		try {
			result = shardJedis.zscore(key, member);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zscore error!" + e);
			doAferService(logFoot, "zscore", false, new Object[]{key, member}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zscore", true, new Object[]{key, member}, result);
		return result;
	}

	@Override
	public Long zrank(String key, String member) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.zrank(key, member);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zrank error!" + e);
			doAferService(logFoot, "zrank", false, new Object[]{key, member}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zrank", true, new Object[]{key, member}, result);
		return result;
	}

	@Override
	public Long zrevrank(String key, String member) {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		Long result = null;
		try {
			result = shardJedis.zrevrank(key, member);
		} catch (Exception e) {
			shardPool.returnBrokenResource(shardJedis);
			log.error("redis zrevrank error!" + e);
			doAferService(logFoot, "zrevrank", false, new Object[]{key, member}, e);
			return result;
		}
		shardPool.returnResource(shardJedis);
		doAferService(logFoot, "zrevrank", true, new Object[]{key, member}, result);
		return result;
	}

	@Override
	@Deprecated
	public ShardedJedisPipeline getPipeline() {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		ShardedJedisPipeline pipeline = shardJedis.pipelined();
		return pipeline;
	}

	@Override
	public ShardedJedis getShardedJedis() {
		LogFoot logFoot = new LogFoot(LogTypeEnum.RESOURCE_REDIS.getValue());
		doBeforeService(logFoot);
		ShardedJedis shardJedis = shardPool.getResource();
		return shardJedis;
	}

	@Override
	public void releaseShardedJedis(ShardedJedis shardJedis, Boolean hasException) {
		if (shardJedis == null)
			log.error("[releaseShardedJedis] get null");
		if (hasException) {
			shardPool.returnBrokenResource(shardJedis);
		} else {
			shardPool.returnResource(shardJedis);
		}
	}
}
