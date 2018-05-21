package com.llj.framework.cache.redis.springTemplate;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * redis操作工具类
 * 
 * @author lu
 */
public class RedisTemplateUtil {

	/**
	 * redis操作类
	 * 
	 * @Autowired
	 */
	private static RedisTemplate<String, Object> redisTemplate;

	/** 日志 */
	private static Logger logger = LogManager.getLogger(RedisTemplateUtil.class.getName());

	/**
	 * 设置redis[key-value]类型的缓存
	 * 
	 * @param key
	 *            缓存键
	 * @param value
	 *            缓存值
	 * @param timeOut
	 *            失效时间 0为永不超时
	 * @param timeUnit
	 *            时间单位
	 * @return true or false
	 * @author dubl
	 */
	public static Boolean set(String key, Object value, long timeOut, TimeUnit timeUnit) throws Exception {
		boolean flag = false;
		try {
			ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
			if (0 == timeOut) {
				valueOperations.set(key, value);
				logger.info("插入redis(" + key + ")成功");
			} else {
				valueOperations.set(key, value, timeOut, timeUnit);
				logger.info("插入redis(" + key + ", timeout=" + timeOut + ", TimeUnit=" + TimeUnit.values() + ")成功");
			}
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("向redis(key=" + key + ")中加入数据出现异常,异常信息:" + e.getMessage());
		}
		return flag;
	}

	/**
	 * 获取redis[key-value]类型对应key的缓存
	 * 
	 * @param key
	 *            缓存键
	 * @return key对应缓存值
	 * @throws Exception
	 * @author dubl
	 */
	public static Object get(String key) throws Exception {
		Object result = null;
		try {
			if (checkKeyIsExist(key)) {
				ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
				Object value = valueOperations.get(key);
				return value;
			}
			logger.info("redis(" + key + ") not exist!");
		} catch (Exception e) {
			logger.error("获取redis(key=" + key + ")数据时出现异常,异常信息:" + e.getMessage());
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return result;
	}

	/**
	 * 设置redis[hash]缓存
	 * 
	 * @param key
	 *            缓存键
	 * @param hashKey
	 *            hash缓存键
	 * @param value
	 *            hash缓存值
	 * @param timeOut
	 *            失效时间(秒) 0为永不超时
	 * @return true or false
	 * @throws Exception
	 * @author dubl
	 */
	public static Boolean putHash(String key, String hashKey, Object value, long timeOut) throws Exception {
		boolean flag = false;
		try {
			HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
			hashOperations.put(key, hashKey, value);
			if (0 != timeOut) {
				// 当前时间缓存值+超时时间*1000(毫秒变秒) 就是过期日期
				redisTemplate.expireAt(key, new Date(System.currentTimeMillis() + (timeOut * 1000)));
			}
			flag = true;
			logger.info("插入redis-hash(" + key + ":" + hashKey + ")成功");
		} catch (Exception e) {
			logger.error("向redis[hash](key=" + key + ":" + hashKey + ")中加入数据出现异常,异常信息:" + e.getMessage());
		}
		return flag;
	}

	/**
	 * 设置redis[hash]缓存
	 * 
	 * @param key
	 *            缓存键
	 * @param hash
	 *            hash内容[key-vavle]
	 * @param timeOut
	 *            失效时间(秒) 0为永不超时
	 * @return true or false
	 * @throws Exception
	 * @author dubl
	 */
	public static Boolean putHashAll(String key, Map<String, Object> hash, long timeOut) throws Exception {
		boolean flag = false;
		try {
			HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
			hashOperations.putAll(key, hash);
			if (0 != timeOut) {
				// 当前时间缓存值+超时时间*1000(毫秒变秒) 就是过期日期
				redisTemplate.expireAt(key, new Date(System.currentTimeMillis() + (timeOut * 1000)));
			}
			flag = true;
			logger.info("插入redis-hash(" + key + ")成功");
		} catch (Exception e) {
			logger.error("向redis[hash](key=" + key + ")中加入数据出现异常,异常信息:" + e.getMessage());
		}
		return flag;
	}

	/**
	 * 获取redi[hash]类型对应key缓存
	 * 
	 * @param key
	 * @param hashKey
	 * @return
	 * @throws Exception
	 * @author dubl
	 */
	public static Object getHash(String key, String hashKey) throws Exception {
		try {
			if (checkKeyIsExist(key)) {
				HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
				Object value = hashOperations.get(key, hashKey);
				return value;
			}
			logger.info("redis[hash](" + key + ") not exist!");
		} catch (Exception e) {
			logger.error("获取redis(key=" + key + ", hashKey=" + hashKey + ")数据时出现异常,异常信息:" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 删除对应key的缓存
	 * 
	 * @param key
	 *            缓存键
	 * @return true or false
	 * @throws Exception
	 * @author dubl
	 */
	public static Boolean del(String key) throws Exception {
		boolean flag = false;
		try {
			if (checkKeyIsExist(key)) {
				redisTemplate.delete(key);
				logger.info("删除redis(" + key + ")成功!");
			}
			flag = true;
		} catch (Exception e) {
			logger.error("删除redis(" + key + ")数据时出现异常,异常信息 :" + e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 删除对应key的缓存
	 * 
	 * @param keys
	 *            缓存键集合
	 * @return true or false
	 * @throws Exception
	 * @author dubl
	 */
	public static Boolean del(Collection<String> keys) throws Exception {
		boolean flag = false;
		try {
			redisTemplate.delete(keys);
			flag = true;
		} catch (Exception e) {
			logger.error("删除redis(" + keys + ")数据时出现异常,异常信息 :" + e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 设置redis[list]类型数据缓存
	 * 
	 * @param key
	 *            缓存键
	 * @param valueList
	 *            缓存值
	 * @return true or false
	 * @throws Exception
	 * @author dubl
	 */
	public static Boolean setList(String key, List<?> valueList) throws Exception {
		boolean flag = false;
		try {
			if (!del(key)) {
				logger.info("");
				return flag;
			}
			ListOperations<String, Object> listOperations = redisTemplate.opsForList();
			listOperations.rightPushAll(key, valueList.toArray());
			// 增加list数据类型的三种方式
			/*
			 * for (Object value : valueList) { listOperations.rightPush(key,
			 * value); } //数据内容会多包一层[] listOperations.rightPushAll(key,
			 * valueList);
			 */
			flag = true;
		} catch (Exception e) {
			logger.error("向redis[list](key=" + key + ")数据时出现异常,异常信息:" + e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 获取redis[list]类型对应key全部缓存
	 * 
	 * @param key
	 *            缓存键
	 * @return List 缓存值
	 * @throws Exception
	 * @author dubl
	 */
	public static List<?> getListAll(String key) throws Exception {
		try {
			return getList(key, 0, -1);
		} catch (Exception e) {
			logger.error("获取redis[list](key=" + key + ")数据时出现异常,异常信息:" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取redis[list]类型对应key范围缓存
	 * 
	 * @param key
	 *            缓存键
	 * @param startIndex
	 *            开始位置 从0开始
	 * @param endIndex
	 *            结束位置 -1代表全部
	 * @return List 缓存值
	 * @throws Exception
	 * @author dubl
	 */
	public static List<?> getList(String key, int startIndex, int endIndex) throws Exception {
		try {
			if (checkKeyIsExist(key)) {
				ListOperations<String, Object> listOperations = redisTemplate.opsForList();
				List<?> value = listOperations.range(key, startIndex, endIndex);
				return value;
			}
			logger.info("redis(" + key + ") not exist!");
		} catch (Exception e) {
			logger.error("获取redis[list](key=" + key + ")数据时出现异常,异常信息:" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取缓存key的有效时间
	 * 
	 * @param key
	 * @return (-1 为永久有效)
	 * @throws Exception
	 * @author dubl
	 */
	public static long expireKey(String key) throws Exception {
		Long ttl = null;
		try {
			ttl = redisTemplate.getExpire(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ttl;
	}

	/**
	 * 设置redis[key-value]类型的缓存，自增
	 * 
	 * @param key
	 *            缓存键
	 * @param delta
	 *            自增值
	 * @return true or false
	 * @author dubl
	 */
	public static Boolean setIncr(String key, long delta) throws Exception {
		boolean flag = false;
		try {
			ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
			valueOperations.increment(key, delta);
			logger.info("插入redis(" + key + ")成功");
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("向redis(key=" + key + ")中加入数据,异常信息:" + e.getMessage());
		}
		return flag;
	}

	/**
	 * 检测redis对应key是否存在
	 * 
	 * @param key
	 *            缓存键
	 * @return exist:true, not exist:false
	 * @throws Exception
	 * @author dubl
	 */
	public static Boolean checkKeyIsExist(String key) throws Exception {
		Boolean flag = false;
		try {
			flag = redisTemplate.hasKey(key);
		} catch (Exception e) {

		}
		return flag;
	}

	/**
	 * @desc 获取某一前缀下，所有的key的值key 值
	 * @param pattern
	 * @return
	 * @throws Exception
	 */
	public static Set<String> getKeysByPrefix(String pattern) throws Exception {
		try {
			Set<String> keys = redisTemplate.keys(pattern);
			redisTemplate.delete(keys);
			return keys;
		} catch (Exception e) {
			logger.error("获取" + pattern + ")前缀下所有的key出现异常，异常信息:" + e.getMessage());
		}
		return null;
	}

	public static RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public static void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		RedisTemplateUtil.redisTemplate = redisTemplate;
	}

}
