package com.llj.framework.cache.redis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class JedisMSClient {
	private static String MASETER_FLAG = "master";
	private static String SLAVE_FLAG = "slave";
	private JedisClient master;
	private JedisClient slave;

	public JedisMSClient(String config_name) throws FileNotFoundException, IOException {
		master = new ShardedJedisClient(config_name, MASETER_FLAG);
		slave = new ShardedJedisClient(config_name, SLAVE_FLAG);
	}

	// #####################List########################

	/**
	 * 写操作，等master失败写入slave操作
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Long rpush(final byte[] key, final byte[] value) throws Exception {
		Long result = master.rpush(key, value);
		if (result == ShardedJedisClient.error_num) {
			result = slave.rpush(key, value);
			if (result == ShardedJedisClient.error_num) {
				throw new Exception("rpush master and slave all error!");
			} ;
		}
		return result;
	}

	/**
	 * 写操作，等master失败写入slave操作
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Long rpush(final String key, final String value) throws Exception {
		Long result = master.rpush(key, value);
		if (result == ShardedJedisClient.error_num) {
			result = slave.rpush(key, value);
			if (result == ShardedJedisClient.error_num) {
				throw new Exception("rpush master and slave all error!");
			} ;
		}
		return result;
	}

	/**
	 * 写操作，等master失败写入slave操作
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Long rpush(final String key, final String[] value) throws Exception {
		Long result = master.rpush(key, value);
		if (result == ShardedJedisClient.error_num) {
			result = slave.rpush(key, value);
			if (result == ShardedJedisClient.error_num) {
				throw new Exception("rpush master and slave all error!");
			} ;
		}
		return result;
	}

	/**
	 * 写操作，等master失败写入slave操作
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Long lpush(final byte[] key, final byte[] value) throws Exception {
		Long result = master.lpush(key, value);
		if (result == ShardedJedisClient.error_num) {
			result = slave.lpush(key, value);
			if (result == ShardedJedisClient.error_num) {
				throw new Exception("lpush master and slave all error!");
			} ;
		}
		return result;
	}

	/**
	 * lpush操作，等master失败写入slave操作
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Long lpush(final String key, final String value) throws Exception {
		Long result = master.lpush(key, value);
		if (result == ShardedJedisClient.error_num) {
			result = slave.lpush(key, value);
			if (result == ShardedJedisClient.error_num) {
				throw new Exception("lpush master and slave all error!");
			} ;
		}
		return result;
	}

	/**
	 * lpush操作，等master失败写入slave操作
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Long lpush(final String key, final String[] value) throws Exception {
		Long result = master.lpush(key, value);
		if (result == ShardedJedisClient.error_num) {
			result = slave.lpush(key, value);
			if (result == ShardedJedisClient.error_num) {
				throw new Exception("lpush master and slave all error!");
			} ;
		}
		return result;
	}

	/**
	 * lrem，先删除master 再删除slave
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Long lrem(String key, Long count, String value) throws Exception {
		Long result = master.lrem(key, count, value);
		if (result > 0)
			return result;
		result = slave.lrem(key, count, value);
		return result;
	}

	/**
	 * lpop操作，先从master读，如果master为null 再从slave读
	 * 
	 * @param key
	 * @return
	 */
	public String lpop(final String key) throws Exception {
		String result = master.lpop(key);
		if (result == null) {
			result = slave.lpop(key);
		}
		return result;
	}

	/**
	 * rpop操作，先从master读，如果master为null 再从slave读
	 * 
	 * @param key
	 * @return
	 */
	public String rpop(String key) throws Exception {
		String result = master.rpop(key);
		if (result == null) {
			result = slave.rpop(key);
		}
		return result;
	}

	// 这2个接口目前有bug 不建议使用
	public List<String> blpop(String key) throws Exception {
		List<String> result = master.blpop(key);
		if (result.size() == 0) {
			result.add(slave.lpop(key));
		}
		return result;
	}

	public List<String> brpop(String key) throws Exception {
		List<String> result = master.brpop(key);
		if (result.size() == 0) {
			result.add(slave.rpop(key));
		}
		return result;
	}

	public List<String> blpop(String key, int timeout) throws Exception {
		List<String> result = master.blpop(key, timeout);
		if (result.size() == 0) {
			result.add(slave.lpop(key));
		}
		return result;
	}

	public List<String> brpop(String key, int timeout) throws Exception {
		List<String> result = master.brpop(key, timeout);
		if (result.size() == 0) {
			result.add(slave.rpop(key));
		}
		return result;
	}

	// **********************下列方法只针对master操作***************************
	/**
	 * lindex 只对master操作
	 * 
	 * @param key
	 * @param index
	 * @return
	 * @throws Exception
	 */
	public String lindexFromMaster(String key, long index) throws Exception {
		return master.lindex(key, index);
	}

	/**
	 * lindex 只对slave操作
	 * 
	 * @param key
	 * @param index
	 * @return
	 * @throws Exception
	 */
	public String lindexFromSlave(String key, long index) throws Exception {
		return slave.lindex(key, index);
	}

	/**
	 * lrange 只对master操作
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public List<String> lrangeFromMaster(String key, long start, long end) throws Exception {
		return master.lrange(key, start, end);
	}

	/**
	 * lrange 只对slave操作
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public List<String> lrangeFromSlave(String key, long start, long end) throws Exception {
		return slave.lrange(key, start, end);
	}

	/**
	 * llen 只对master操作
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public Long llenFromMaster(final String key) throws Exception {
		return master.llen(key);
	}

	/**
	 * llen 只对slave操作
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public Long llenFromSlave(final String key) throws Exception {
		return slave.llen(key);
	}

	/**
	 * lset 只对master操作
	 * 
	 * @param key
	 * @param index
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String lsetFromMaster(String key, long index, String value) throws Exception {
		return master.lset(key, index, value);
	}

	/**
	 * lset 只对slave操作
	 * 
	 * @param key
	 * @param index
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String lsetFromSlave(String key, long index, String value) throws Exception {
		return slave.lset(key, index, value);
	}

	public void setMaster(JedisClient master) {
		this.master = master;
	}

	public void setSlave(JedisClient slave) {
		this.slave = slave;
	}
}
