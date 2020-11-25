package com.lulj.base.cache.Local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存服务
 * key = 字符串 | value = 字符串 | expire = 秒(有效时间)
 * <p>
 * put 添加缓存
 * get 读取缓存值
 * <p>
 * 失效策略：
 * 1. 定期删除策略：启动1个线程，每2分钟扫描一次，超时数据移除
 * 2. 懒惰淘汰策略：每次访问时校验有效性，如果失效移除
 */
public class CacheTemplate {
    private final static Logger logger = LoggerFactory.getLogger(CacheTemplate.class);

    /**
     * 启动开始后延迟5秒执行时效策略
     */
    private static final int INITIAL_DELAY_TIME = 5;
    /**
     * 执行时效策略间隔时间
     */
    private static final int PERIOD_TIME = 5;
    /**
     * 本地缓存map
     */
    private static ConcurrentHashMap<String, Cache> store;
    /**
     * 执行时效策略线程池
     */
    private final static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    /*
     * 静态代码块
     *
     * 初始化缓存map
     * 添加时效策略定时线程任务
     */
    static {
        store = new ConcurrentHashMap<>();
        executor.scheduleAtFixedRate(new Task(), INITIAL_DELAY_TIME, PERIOD_TIME, TimeUnit.SECONDS);
    }

    public static void set(String key, String value) {
        set(key, value, 0);
    }

    /**
     * 设置缓存
     *
     * @param key    唯一key
     * @param value  值
     * @param expire 超时时间-单位(s/秒)
     */
    public static void set(String key, String value, long expire) {
        logger.debug("CacheTemplate缓存策略--->添加缓存，key={}, value={}, expire={}秒", key, value, expire);
        if (expire > 0) {
            store.put(key, new Cache(value, expire));
        } else {
            store.put(key, new Cache(value));
        }
    }

    public static String get(String key) {
        Cache cache = store.get(key);
        if (cache == null) {
            return null;
        }

        if (cache.getExpire() > 0 && cache.getExpire() < System.currentTimeMillis()) {
            del(key);
            logger.debug("CacheTemplate缓存策略--->懒惰淘汰策略: 移除超时失效数据, cache={}", cache);
            return null;
        }
        return store.get(key).getValue();
    }

    public static void del(String key) {
        logger.debug("CacheTemplate缓存策略--->删除缓存，key={}", key);
        store.remove(key);

    }

    private static void removeAll() {
        logger.debug("CacheTemplate缓存策略--->定期删除策略: 开始执行, store={}", store);
        for (String key : store.keySet()) {
            Cache cache = store.get(key);
            if (cache.getExpire() > 0 && cache.getExpire() < System.currentTimeMillis()) {
                store.remove(key);
                logger.debug("CacheTemplate缓存策略--->定期删除策略: 移除超时失效数据, key={}, value={}, time={}", key, cache.getValue(), cache.getExpire());
            }
        }
    }

    /**
     * 定时移除时效数据任务
     */
    private static class Task implements Runnable {
        @Override
        public void run() {
            try {
                CacheTemplate.removeAll();
            } catch (Exception e) {
                logger.info("CacheTemplate缓存策略--->定期删除策略异常", e);
            }
        }
    }

}