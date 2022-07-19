package com.di1shuai.spark.job

import com.di1shuai.spark.redis.RedisPool
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.collect_list

import scala.collection.mutable

/**
  * @Author: Bruce Zhu
  * @Description:
  * @Date: Created in 9:09 2022-07-18
  * @Midified By:
  */
object JobZDFPermission {

  val prefix = "zdf:permission:"
  val partitionNum = 1

  def main(args: Array[String]): Unit = {

    if (args.length < 3) {
      System.err.println("Usage: JobZDFPermission <dbName> <tableName> <redisExpireSecondsTime>")
      System.exit(-1)
    }
    val dbName = args(0)
    val tableName = args(1)
    val expireTime = args(2).toInt;

    println(s"======db -> $dbName=======================")
    println(s"======table -> $tableName=======================")
    println(s"======expireTime -> $expireTime=======================")

    val spark = SparkSession.builder()
      .appName("JobZDFPermission")
      .enableHiveSupport()
      .getOrCreate()

    val sql = s"select * from `$dbName`.`$tableName` t where t.clientId <> '' and t.path <> ''"

    /**
      * c1 p1
      * c1 p2
      * c2 p2
      * ...
      */
    val df = spark.sql(sql)


    /**
      * c1 [p1,p2,p3]
      * c2 [p2,p3]
      */
    df.groupBy("t.clientId")
      .agg(collect_list("t.path").as("paths"))
      .rdd
      .repartition(partitionNum)
      .foreachPartition(partition => {

        val jedis = RedisPool.getJedis()
        val p = jedis.pipelined()

        partition.foreach(row => {
          val redisKey = prefix + row(0).toString
          val paths = row.getAs[mutable.WrappedArray[String]]("paths")
          println(s"== Key : $redisKey ==")
          if (redisKey.length > 0) {
            p.del(redisKey, redisKey)
            paths.foreach(path => {
              println(s"$path")
              p.sadd(redisKey, path)
            })
            p.expire(redisKey, expireTime)
          } else {
            println("no data")
          }
        })

        p.sync()

      })

    spark.stop()

  }


}
