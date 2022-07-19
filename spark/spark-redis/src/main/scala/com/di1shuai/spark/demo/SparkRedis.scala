package com.di1shuai.spark.demo

//import com.redislabs.provider.redis.toRedisContext
import com.di1shuai.spark.redis.RedisPool
import org.apache.spark.sql.SparkSession

/**
  * @Author: Bruce Zhu
  * @Description:
  * @Date: Created in 2022-07-15
  * @Midified By:
  */
object SparkRedis {
  def main(args: Array[String]): Unit = {
    val prefix = "permission:"
    val spark = SparkSession.builder()
      .appName("SparkRedis")
      .master("local[*]")
      .getOrCreate()

    val userSeq = Seq(
      TargetAuth("u1", "/path1"),
      TargetAuth("u1", "/path2"),
      TargetAuth("u1", "/path3"),
      TargetAuth("u2", "/path1"),
      TargetAuth("u2", "/path3"),
      TargetAuth("u3", "/path4"),
      TargetAuth("u4", "/path4"),
      TargetAuth("u5", "/path4"),
      TargetAuth("u6", "/path4"),
      TargetAuth("u7", "/path4"),
      TargetAuth("u8", "/path4"),
      TargetAuth("u9", "/path4")
    )
    val data = spark.createDataFrame(userSeq)

    val expireTime = 86400;

    data.rdd.repartition(10).foreachPartition(partition => {
      val jedis = RedisPool.getJedis()
      partition.foreach(v => {
        val redisKey = v(0).toString
        val redisValue = v(1).toString
        if (redisKey.length > 0) {
          jedis.sadd(prefix+redisKey,redisValue)
          jedis.expire(prefix+redisKey, expireTime)
        } else {
          println("no data")
        }
      })
    })

  }
}

case class TargetAuth(clientId: String, path: String)