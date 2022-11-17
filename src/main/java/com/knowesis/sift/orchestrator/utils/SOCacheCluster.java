package com.knowesis.sift.orchestrator.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.camel.PropertyInject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.knowesis.sift.orchestrator.domain.Constants;
import com.knowesis.sift.orchestrator.exception.CacheKeyNotFoundException;
import com.knowesis.sift.orchestrator.exception.CacheOperationNotSupported;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * This util class is used to perform cache operations
 */
public class SOCacheCluster {
	/**
	 * This variable is configurable in properties. It is the default TTL for key in seconds 
	 */
	@PropertyInject("{{so.cache.key.default.ttl}}")
	private int defaultTTL;

	private final Logger log = LoggerFactory.getLogger(getClass());
	private JedisCluster jc;


	@PostConstruct
	/**
	 * Initialises JedisCluster for SOCache operations. Cache hosts are fetched from environment variable SO_CACHE_HOSTS
	 */
	public void onPostConstruct() {

		/*  sample value for cluster nodes:
        localhost:30001,localhost:30002,localhost:30003,localhost:30004,localhost:30005,localhost:30006 */
		Set<HostAndPort> nodes = new HashSet<>();
		String clusterNodes = System.getenv(Constants.SO_CACHE_HOSTS);
		String[] cNodes = clusterNodes.split(",");
		for (String cNode : cNodes) {
			String[] hostAndPort = cNode.split(":");
			nodes.add(new HostAndPort(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
		}
		jc = new JedisCluster(nodes);
	}

	/**
	 * The method supports the following operations<br>
	 * <ol>
	 * <li><b>SET</b>: set key, value pair with default TTL configured in the properties <br></li> 
	 * <li><b>SETANDEXPIRE</b>: set key, value pair with TTL specified by SOCache.TTL header. Invokes <br></li>
	 * <li><b>SETANDEXPIREAT</b>:  set key, value pair with expiry time specified by SOCache.ExpireAt header. Invokes <br></li>
	 * <li><b>MSET</b>: set multiple key, value pairs with default TTL configured in the properties. Invokes <br></li>
	 * <li><b>MSETANDEXPIRE</b>: set multiple key, value pairs with TTL specified by SOCache.TTL header. Invokes <br></li>
	 * <li><b>MSETANDEXPIREAT</b>:  set key, value pair with expiry time specified by SOCache.ExpireAt header. Invokes <br></li>
	 * <li><b>GET</b>: get value for the key specified by SOCache.Key header. Invokes <br></li>
	 * <li><b>MGET</b>:  get value for the key specified by SOCache.Keys header. Invokes <br></li>
	 * </ol>
	 * @param headers[SOCache.Command] This specified the cache operation to be performed
	 * @throws CacheOperationNotSupported when the SOCache.Command is not supported
	 * @throws CacheKeyNotFoundException when key in cache is not found
	 * @return Object In case of GET returned value is a string while in case of MGET returned value is a List of String 
	 */
	public Object doCacheOperations(@Body Object body, @Headers Map<String,Object> headers) throws CacheOperationNotSupported, CacheKeyNotFoundException	{

		String command=(String) headers.get(Constants.SOCache_dot_Command);
		log.info("The command is {}",command);
		
		switch(command)
		{
		case "SET" :
			cacheSet(headers);
			return body; 
		case "SETANDEXPIRE" :
			cacheSetExpire(headers);
			return body; 
		case "SETANDEXPIREAT" :
			cacheSetExpireAt(headers);
			return body; 
		case "MSET" :
			cacheMset(headers);
			return body; 
		case "MSETANDEXPIRE" :
			cacheMsetExpire(headers);
			return body; 
		case "MSETANDEXPIREAT" :
			cacheMsetExpireAt(headers);
			return body; 
		case "GET" :
			return cacheGet(headers);
		case "MGET" :
			return cacheMget(headers);
		default : 
			throw new CacheOperationNotSupported("Command is not supported : " + command);
		}
	}

	/**
	 * Set key and value in cache with default TTL configured in properties. <br>
	 * @param headers[SOCache.Key] - Mandatory - this is the cache key
	 * @param headers[SOCache.Value] - Mandatory - this is the value that is set in cache
	 * 
	 */
	private void cacheSet(Map<String, Object> headers) {
		jc.set((String)headers.get(Constants.SOCache_dot_Key),(String)headers.get(Constants.SOCache_dot_Value));
		jc.expire((String)headers.get(Constants.SOCache_dot_Key), defaultTTL);
		log.debug("key {}, write in Cache.", (String)headers.get(Constants.SOCache_dot_Key));
	}
	
	/**
	 * Set key and value in cache with TTL specified by <code>SOCache.TTL</code> header
	 * @param headers[SOCache.Key] - Mandatory - this is the cache key
	 * @param headers[SOCache.Value] - Mandatory - this is the value that is set in cache
	 * @param headers[SOCache.TTL] - Mandatory - this is use to set the TTL(seconds) for <code>SOCache.Key</code><br>
	 * If any of the 3 headers is not found an exception will be thrown 
	 */
	private void cacheSetExpire(Map<String, Object> headers) {
		jc.set((String)headers.get(Constants.SOCache_dot_Key),(String)headers.get(Constants.SOCache_dot_Value));
		jc.expire((String)headers.get(Constants.SOCache_dot_Key), (int)headers.get(Constants.SOCache_dot_TTL));
	}

	/**
	 * Set key and value in cache with TTL specified by <code>SOCache.TTL</code> header
	 * @param headers[SOCache.Key] - Mandatory - this is the cache key
	 * @param headers[SOCache.Value] - Mandatory - this is the value that is set in cache
	 * @param headers[SOCache.ExpireAt] - Mandatory - this is use to set the TTL for <code>SOCache.Key</code><br>
	 * <blockquote>If any of the 3 headers is not found an exception will be thrown</blockquote> 
	 */
	private void cacheSetExpireAt(Map<String, Object> headers) {
		jc.set((String)headers.get(Constants.SOCache_dot_Key),(String)headers.get(Constants.SOCache_dot_Value));
		jc.expireAt((String)headers.get(Constants.SOCache_dot_Key), (long)headers.get(Constants.SOCache_dot_ExpireAt) );

	}
	/**
	 * Sets multiple keys and values with default TTL configured in properties.
	 * @param headers[SOCache.Values] - Mandatory - Map of key value pairs that need to be set in cache with default TTL
	 */
	private void cacheMset(Map<String, Object> headers) {
		Map<String,Object> hashdehash=(Map<String, Object>) headers.get(Constants.SOCache_dot_Values);
		for (Map.Entry<String,Object> entry : hashdehash.entrySet()) {
			String hash = (String) entry.getKey();// Get hashed key
			String dehash = (String) entry.getValue();//Get dehashed Value
			jc.set(hash, dehash);
			jc.expire(hash, defaultTTL);
		}
		log.debug("hash-dehash pairs {}, write in Cache.", hashdehash);
	}
	
	/**
	 * Sets multiple keys and values with default TTL configured in properties.
	 * @param headers[SOCache.Values] - Mandatory - Map of key value pairs that need to be set in cache with default TTL
	 * @param headers[SOCache.TTL] - Mandatory - this is the TTL applied to all the keys set in cache
	 */
	private void cacheMsetExpire(Map<String, Object> headers) {
		Map<String,Object> hashdehash=(Map<String, Object>) headers.get(Constants.SOCache_dot_Values);
		for (Map.Entry<String,Object> entry : hashdehash.entrySet()) {
			String hash = (String) entry.getKey();// Get hashed key
			String dehash = (String) entry.getValue();//Get dehashed Value
			jc.set(hash,dehash);
			jc.expire(hash, (int)headers.get(Constants.SOCache_dot_TTL));
		}
	}
	
	/**
	 * Sets multiple keys and values with expiry time specified in the <code>SOCache.ExpireAt</code> header
	 * @param headers[SOCache.Values] - Mandatory - Map of key value pairs that need to be set in cache with default TTL
	 * @param headers[SOCache.TTL] - Mandatory - this is the TTL applied to all the keys set in cache
	 */
	private void cacheMsetExpireAt(Map<String, Object> headers) {
		Map<String,Object> hashdehash=(Map<String, Object>) headers.get(Constants.SOCache_dot_Values);
		for (Map.Entry<String,Object> entry : hashdehash.entrySet()) {
			String hash = (String) entry.getKey();// Get hashed key
			String dehash = (String) entry.getValue();//Get dehashed Value
			jc.set(hash,dehash);
			jc.expireAt(hash, (long)headers.get(Constants.SOCache_dot_ExpireAt));
		}

	}

	/**
	 * Get multiple keys and values from cache. Requires "SOCache.Keys" headers to be set with a list of keys to fetch from cache.
	 * @param headers Exchange headers as Map
	 * @return Returns a list of values from cache in the same order as the SOCache.Keys
	 */
	private List<String> cacheMget(Map<String,Object> headers) {
		log.debug("keys:{}",headers.get(Constants.SOCache_dot_Keys));
		LinkedList<String> cacheKeysList = (LinkedList<String>) headers.get(Constants.SOCache_dot_Keys);
		ArrayList<String> values = new ArrayList<String>();
		cacheKeysList.forEach((temp) -> {
			values.add(jc.get(temp));
		});
		log.debug("Retrieved multiple keys and values from cache {} ",values);
		return values;
	}

	/**
	 * Gets the value for the key specified by <code>SOCache.Key</code> header
	 * @param headers Exchange headers as Map
	 * @return value String value from SO cache for the key specified by <code>SOCache.Key</code> header
	 * @throws CacheKeyNotFoundException If the key is not found CacheKeyNotFoundException is thrown
	 */
	private String cacheGet(Map<String,Object> headers) throws CacheKeyNotFoundException {
		log.debug("Received cache read request in readCacheKey method : cache key -> {}",headers.get(Constants.SOCache_dot_Key));
		String soCacheValue = jc.get((String) headers.get(Constants.SOCache_dot_Key));
		if(soCacheValue == null) 
			throw new CacheKeyNotFoundException("Key not found");
		return soCacheValue;
	}
}
