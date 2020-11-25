package com.lulj.base.cache.redis.springTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import redis.clients.jedis.Protocol;
import redis.clients.util.SafeEncoder;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author lu
 */
public class RedisTemplateUtil {

    /**
     * redis操作类
     *
     * @Autowired
     */
    private static RedisTemplate<String, Object> redisTemplate;

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(RedisTemplateUtil.class);

    /**
     * 设置redis[key-value]类型的缓存
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param timeOut  失效时间 0为永不超时
     * @param timeUnit 时间单位
     * @return true or false
     */
    public static Boolean set(String key, Object value, long timeOut, TimeUnit timeUnit) {
        boolean flag = false;
        try {
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            if (0 == timeOut) {
                valueOperations.set(key, value);
                logger.info("Insert into redis(" + key + ") success");
            } else {
                valueOperations.set(key, value, timeOut, timeUnit);
                logger.info("Insert into redis(" + key + ", timeout=" + timeOut + ", TimeUnit=" + TimeUnit.values() + ")success.");
            }
            flag = true;
        } catch (Exception e) {
            logger.error("An exception occurred when adding data to redis，key=" + key + ",exception info:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return flag;
    }

    /**
     * 获取redis[key-value]类型对应key的缓存
     *
     * @param key 缓存键
     * @return key对应缓存值
     * @throws Exception
     */
    public static Object get(String key) {
        Object result = null;
        try {
            if (checkKeyIsExist(key)) {
                ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                Object value = valueOperations.get(key);
                return value;
            }
            logger.info("redis(" + key + ") not exist!");
        } catch (Exception e) {
            logger.error("An exception occurs when obtaining redis data,key=" + key + ". Exception information:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return result;
    }


    /**
     * @param key
     * @param value
     * @return
     */
    public static Object getAndSet(String key, Object value) {
        Object object = null;
        try {
            object = redisTemplate.opsForValue().getAndSet(key, value);
        } catch (Exception e) {
            logger.error("An exception occurs when obtaining redis data,key=" + key + ". Exception information:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return object;
    }

    /**
     * 设置redis[hash]缓存
     *
     * @param key     缓存键
     * @param hashKey hash缓存键
     * @param value   hash缓存值
     * @param timeOut 失效时间(秒) 0为永不超时
     * @return true or false
     * @throws Exception
     */
    public static Boolean putHash(String key, String hashKey, Object value, long timeOut) {
        boolean flag = false;
        try {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            hashOperations.put(key, hashKey, value);
            if (0 != timeOut) {
                redisTemplate.expireAt(key, new Date(System.currentTimeMillis() + (timeOut * 1000)));
            }
            flag = true;
            logger.info("Insert into redis-hash(" + key + ":" + hashKey + ")success");
        } catch (Exception e) {
            logger.error("Add data exception to redis,[hash](key=" + key + ":" + hashKey + ", exception information:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return flag;
    }

    /**
     * 设置redis[hash]缓存
     *
     * @param key     缓存键
     * @param hash    hash内容[key-vavle]
     * @param timeOut 失效时间(秒) 0为永不超时
     * @return true or false
     * @throws Exception
     */
    public static Boolean putHashAll(String key, Map<String, Object> hash, long timeOut) {
        boolean flag = false;
        try {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            hashOperations.putAll(key, hash);
            if (0 != timeOut) {
                redisTemplate.expireAt(key, new Date(System.currentTimeMillis() + (timeOut * 1000)));
            }
            flag = true;
            logger.info("Insert into redis-hash(" + key + ")success");
        } catch (Exception e) {
            logger.error("Add data exception to redis([hash](key=" + key + "), exception information:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
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
     */
    public static Object getHash(String key, String hashKey) {
        try {
            if (checkKeyIsExist(key)) {
                HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
                Object value = hashOperations.get(key, hashKey);
                return value;
            }
            logger.info("redis[hash](" + key + ") not exist!");
        } catch (Exception e) {
            logger.error("Add data exception to redis,[hash](key=" + key + ":" + hashKey + ", exception information:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return null;
    }

    /**
     * 删除对应key的缓存
     *
     * @param key 缓存键
     * @return true or false
     * @throws Exception
     */
    public static Boolean del(String key) {
        boolean flag = false;
        try {
            if (checkKeyIsExist(key)) {
                redisTemplate.delete(key);
                logger.info("Delete redis(" + key + ")success!");
            }
            flag = true;
        } catch (Exception e) {
            logger.error("An exception occurred while deleting redis data(key=(" + key + ")). Exception information:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return flag;
    }

    /**
     * 删除对应key的缓存
     *
     * @param keys 缓存键集合
     * @return true or false
     * @throws Exception
     */
    public static Boolean del(Collection<String> keys) {
        boolean flag = false;
        try {
            redisTemplate.delete(keys);
            flag = true;
        } catch (Exception e) {
            logger.error("An exception occurred while deleting redis data(keys=(" + keys + ")). Exception information:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return flag;
    }

    /**
     * 设置redis[list]类型数据缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @return true or false
     * @throws Exception
     */
    public static Boolean setList(String key, List<?> valueList) {
        boolean flag = false;
        try {
            if (!del(key)) {
                return flag;
            }
            ListOperations<String, Object> listOperations = redisTemplate.opsForList();
            listOperations.rightPushAll(key, valueList.toArray());
            flag = true;
        } catch (Exception e) {
            logger.error("An exception occurred to redis data{[list](key=" + key + ")}. Exception information:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return flag;
    }

    /**
     * 获取redis[list]类型对应key全部缓存
     *
     * @param key 缓存键
     * @return List 缓存值
     * @throws Exception
     */
    public static List<?> getListAll(String key) {
        return getList(key, 0, -1);
    }

    /**
     * 获取redis[list]类型对应key范围缓存
     *
     * @param key        缓存键
     * @param startIndex 开始位置 从0开始
     * @param endIndex   结束位置 -1代表全部
     * @return List 缓存值
     * @throws Exception
     */
    public static List<?> getList(String key, int startIndex, int endIndex) {
        List<?> list = null;
        try {
            list = redisTemplate.opsForList().range(key, startIndex, endIndex);
        } catch (Exception e) {
            logger.error("getList data{(key=" + key + ")}, exception information to redis:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return list;
    }

    /**
     * 获取缓存key的有效时间
     *
     * @param key
     * @return (- 1 为永久有效)
     * @throws Exception
     */
    public static long expireKey(String key) {
        Long expire = null;
        try {
            expire = redisTemplate.getExpire(key);
        } catch (Exception e) {
            logger.error("expireKey data{(key=" + key + ")}, exception information to redis:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return expire;
    }

    /**
     * 设置redis[key-value]类型的缓存，自增
     *
     * @param key   缓存键
     * @param delta 自增值
     * @return true or false
     */
    public static Boolean setIncr(String key, long delta) {
        boolean flag = false;
        try {
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            valueOperations.increment(key, delta);
            logger.info("Insert into redis(" + key + ")success");
            flag = true;
        } catch (Exception e) {
            logger.error("setIncr data{(key=" + key + ")}, exception information to redis:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return flag;
    }

    /**
     * 检测redis对应key是否存在
     *
     * @param key 缓存键
     * @return exist:true, not exist:false
     * @throws Exception
     */
    public static Boolean checkKeyIsExist(String key) {
        boolean isFlag = false;
        try {
            isFlag = redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error("checkKeyIsExist data{(key=" + key + ")}, exception information to redis:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return isFlag;
    }

    /**
     * @param pattern
     * @return
     * @throws Exception
     * @desc 获取某一前缀下，所有的key的值key 值
     */
    public static Set<String> getKeysByPrefix(String pattern) {

        Set<String> keysByPrefix = null;
        try {
            keysByPrefix = redisTemplate.keys(pattern);
        } catch (Exception e) {
            logger.error("getKeysByPrefix data{(pattern=" + pattern + ")}, exception information to redis:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return keysByPrefix;
    }

    /**
     * 模糊匹配删除
     *
     * @param pattern
     * @throws Exception
     */
    public static void delKeysByPrefix(String pattern) {
        try {
            redisTemplate.delete(getKeysByPrefix(pattern));
        } catch (Exception e) {
            logger.error("delKeysByPrefix data{(pattern=" + pattern + ")}, exception information to redis:", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
    }

    /**
     * @param keys
     * @return
     * @throws Exception
     */
    public static List<String> mGetRedisValue(final List<String> keys) {
        try {
            return redisTemplate.execute((RedisCallback<List<String>>) connection -> {
                byte[][] bKeys = new byte[keys.size()][];
                for (int i = 0; i < keys.size(); i++) {
                    byte[] bKey = redisTemplate.getStringSerializer().serialize(keys.get(i));
                    bKeys[i] = bKey;
                }
                List<String> values = new ArrayList<>();
                List<byte[]> bValues = connection.mGet(bKeys);
                for (int i = 0; i < bValues.size(); i++) {
                    byte[] bValue = bValues.get(i);
                    String value = null;
                    if (bValue != null && bValue.length > 0) {
                        value = redisTemplate.getStringSerializer().deserialize(bValue);
                    }
                    values.add(value);
                }
                logger.debug("getRedisValue key: {} value: {}", keys, values);
                return values;
            });
        } catch (Exception e) {
            logger.error("redis#mGetRedisValue#exception:{}", e);
            return null;
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
    }


    /**
     * @param key
     * @param value
     * @param expire
     * @throws Exception
     */
    public static void LPush(final String key, final String value, final Long expire) {
        try {
            redisTemplate.execute((RedisCallback<Object>) connection -> {
                byte[] bkey = redisTemplate.getStringSerializer().serialize(key);
                byte[] bvalue = redisTemplate.getStringSerializer().serialize(value);
                connection.lRem(bkey, 0, bvalue);
                connection.lPush(bkey, bvalue);
                if (expire != null) {
                    connection.expire(bkey, expire);
                }
                logger.info("Lpush key: " + key + " value: " + value);
                return null;
            });
        } catch (Exception e) {
            logger.error("redis#LPush#exception:{}", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
    }


    public static void lRem(final String key, final int i, final String value) {
        try {
            redisTemplate.execute((RedisCallback<Object>) connection -> {
                byte[] bkey = redisTemplate.getStringSerializer().serialize(key);
                byte[] bvalue = redisTemplate.getStringSerializer().serialize(value);
                connection.lRem(bkey, i, bvalue);
                logger.info("lRem key: " + key + " value: " + value);
                return null;
            });
        } catch (Exception e) {
            logger.error("redis#lRem#exception:{}", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }

    }

    /**
     * @param key
     * @return
     * @throws Exception
     */
    public static Long LLen(final String key) {
        try {
            return redisTemplate.execute((RedisCallback<Long>) connection -> {
                byte[] bkey = redisTemplate.getStringSerializer().serialize(key);
                if (connection.exists(bkey)) {
                    Long bvalue = connection.lLen(bkey);
                    logger.debug("getRedislLen key: " + key + " value: " + bvalue);
                    return bvalue;
                } else {
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("redis#LLen#exception:{}", e);
            return null;
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
    }

    public static List<String> lRange(final String key, final Long minLen, final Long maxLen) {
        try {
            return redisTemplate.execute((RedisCallback<List<String>>) connection -> {
                byte[] bkey = redisTemplate.getStringSerializer().serialize(key);
                if (connection.exists(bkey)) {
                    List<byte[]> bvalueList = connection.lRange(bkey, minLen, maxLen);
                    List<String> sidList = null;
                    if (bvalueList != null && bvalueList.size() > 0) {
                        sidList = Collections.synchronizedList(new ArrayList<String>());
                        for (byte[] bValue : bvalueList) {
                            String value = null;
                            if (bValue != null && bValue.length > 0) {
                                value = redisTemplate.getStringSerializer().deserialize(bValue);
                                sidList.add(value);
                                logger.debug("lRange key: " + key + " value: " + value);
                            }
                        }
                    }

                    return sidList;
                } else {
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("redis#lRange#exception:{}", e);
            return null;
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
    }

    public static long getExpireTimeType(final String key, final TimeUnit timeUnit) {
        Long expireTimeType = null;
        try {
            expireTimeType = redisTemplate.getExpire(key, timeUnit);
        } catch (Exception e) {
            logger.error("redis#getExpireTimeType#exception:{}", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return expireTimeType;
    }


    public static boolean setIfAbsent(final String key, final Serializable value) {
        boolean isFlag = false;
        try {
            isFlag = redisTemplate.opsForValue().setIfAbsent(key, value);
        } catch (Exception e) {
            logger.error("redis#setIfAbsent#exception:{}", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return isFlag;
    }

    public static Long incrRedis(final String key) {
        Long increment = null;
        try {
            RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
            increment = entityIdCounter.getAndIncrement();
        } catch (Exception e) {
            logger.error("redis#incrRedis#exception:{}", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
        return increment;
    }

    public static void expire(final String key, final long timeout, final TimeUnit unit) {
        try {
            redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            logger.error("redis#expire#exception:{}", e);
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
    }

    public static boolean setIfAbsent(final String key, final Serializable value, final long exptime) {
        try {
            Boolean b = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                Object obj = connection.execute("set", keySerializer.serialize(key),
                        valueSerializer.serialize(value),
                        SafeEncoder.encode("NX"),
                        SafeEncoder.encode("EX"),
                        Protocol.toByteArray(exptime));
                return obj != null;
            });
            return b;
        } catch (Exception e) {
            logger.error("redis#setIfAbsent#exception:{}", e);
            return false;
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
    }


    public static Cursor<String> scan(String pattern, int limit) {
        try {
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(limit).build();
            RedisSerializer<String> redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
            return redisTemplate.executeWithStickyConnection(redisConnection -> new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize));
        } catch (Exception e) {
            logger.error("redis#scan#exception:{}", e);
            return null;
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }
    }


    public static Set<Object> scanSet(String pattern, int limit) {
        try {
            return redisTemplate.execute((RedisCallback<Set<Object>>) connection -> {
                Set<Object> binaryKeys = new HashSet<>();
                Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(pattern).count(limit).build());
                while (cursor.hasNext()) {
                    binaryKeys.add(new String(cursor.next()));
                }
                return binaryKeys;
            });
        } catch (Exception e) {
            logger.error("redis#scanSet#exception:{}", e);
            return null;
        } finally {
            RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
        }

    }

    public static RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public static void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisTemplateUtil.redisTemplate = redisTemplate;
    }

}
