/**
 *  * Copyright (c) 2013 RareMile Technologies. 
 * All rights reserved. 
 * 
 * No part of this document may be reproduced or transmitted in any form or by 
 * any means, electronic or mechanical, whether now known or later invented, 
 * for any purpose without the prior and express written consent. 
 *
 */
package com.raremile.prd.inferlytics.database;

import org.apache.commons.pool.impl.GenericObjectPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author pratyusha
 * @created 02-Aug-2013
 * 
 * TODO: Write a quick description of what the class is supposed to do.
 * 
 */
public class RedisConnector {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(RedisConnector.class);

	
	private static JedisPool jedisPool;
	
	static
	{
		initializeRedisPool();
	}
	
	private static void initializeRedisPool()
	{
		JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(7999);
        config.setTestOnBorrow(true);
        config.setMaxWait(1000);
        config.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);
        jedisPool = new JedisPool(config, "localhost", 6379);
        
	}

	public static Jedis getConnection() {
		Jedis jedisConnection = jedisPool.getResource();  // Jedis("localhost", 6379);
		jedisConnection.connect();
		return jedisConnection;
	}

	public static void returnResource(Jedis jedis) {
		jedisPool.returnResource(jedis);
		
	}


}
