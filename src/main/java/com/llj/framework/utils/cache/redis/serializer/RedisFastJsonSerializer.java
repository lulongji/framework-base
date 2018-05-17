package com.llj.framework.utils.cache.redis.serializer;

import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Redis
 * 
 * @author lu
 *
 * @param <T>
 */
public class RedisFastJsonSerializer<T> implements RedisSerializer<T> {

	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	public byte[] serialize(T t) throws SerializationException {
		try {
			if (t == null) {
				return new byte[0];
			}
			return JSON.toJSONBytes(t, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
		} catch (Exception ex) {
			throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
		}

	}

	@SuppressWarnings("unchecked")
	public T deserialize(byte[] bytes) throws SerializationException {
		try {
			if (bytes == null || bytes.length == 0) {
				return null;
			}
			return (T) JSON.parse(bytes, Feature.AllowSingleQuotes);
		} catch (Exception ex) {
			throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
		}
	}
}
