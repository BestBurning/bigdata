package com.di1shuai.spark.redis

import redis.clients.jedis.{HostAndPort, JedisCluster}

import java.util

/**
  * redis pool
  */
object RedisClusterPool {

  val host = "xxxx"
  val PORT = 6379

  private val jedisClusterNodes = new util.HashSet[HostAndPort]()
  jedisClusterNodes.add(new HostAndPort(host, PORT))

  def getJedis() = {
    lazy val jedisCluster = new JedisCluster(jedisClusterNodes)
    jedisCluster
  }
}
