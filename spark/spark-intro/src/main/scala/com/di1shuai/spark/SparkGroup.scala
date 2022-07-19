package com.di1shuai.spark

import org.apache.spark.sql.functions.collect_list
import org.apache.spark.sql.SparkSession

import scala.collection.mutable

/**
  * @Author: Bruce Zhu
  * @Description:
  * @Date: Created in 2022-07-15
  * @Midified By:
  */
object SparkGroup {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("SparkRedis")
      .master("local[*]")
      .getOrCreate()
    spark.implicits
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
    /**
      * c1 p1
      * c1 p2
      * c2 p2
      * ...
      */
    val data = spark.createDataFrame(userSeq)

    /**
      * c1 [p1,p2,p3]
      * c2 [p2,p3]
      */
    data.groupBy("clientId")
      .agg( collect_list("path").as("paths"))
      .rdd
      .repartition(1).foreachPartition(partition => {
          partition.foreach(v => {
            val redisKey = v(0).toString
            val redisValue = v.getAs[mutable.WrappedArray[String]]("paths")
            println(s"== key -> $redisKey ==")
            if (redisKey.length > 0) {
              redisValue.foreach( path => {
                println(path)
              })
            } else {
              println("no data")
            }
          })
        })
    spark.stop()
  }
}

case class TargetAuth(clientId: String, path: String)