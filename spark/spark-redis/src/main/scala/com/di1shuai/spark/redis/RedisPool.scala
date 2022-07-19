package com.di1shuai.spark.redis

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.JedisPool

/**
  * redis pool
  */
object RedisPool {

  val poolConfig = new GenericObjectPoolConfig()
  poolConfig.setMaxIdle(10) // 最大连接数
  poolConfig.setMaxTotal(1000) // 最大空闲连接数
  poolConfig.setMaxWaitMillis(1000) // 最大等待时间
  poolConfig.setTestOnBorrow(true) // 检查连接可用性, 确保获取的redis实例可用

  val host = "xxxx"
  val PORT = 6379
  val PASSWORD = "xxxxx"

  private lazy val jedisPool = new JedisPool(poolConfig, host, PORT, 5000, PASSWORD)

  def getJedis() = {
    val jedis = jedisPool.getResource //获取连接池连接
    jedis
  }
}
