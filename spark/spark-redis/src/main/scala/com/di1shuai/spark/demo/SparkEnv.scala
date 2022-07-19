package com.di1shuai.spark.demo

import org.apache.spark.sql.SparkSession

/**
 * @Author: Bruce Zhu
 * @Description:
 * @Date: 2022-07-18
 * @Midified By:
 */
object SparkEnv {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("SparkEnv")
      .enableHiveSupport()
      .getOrCreate()
    val db = args(0)
    val table = args(1)
    println(s"======db -> $db=======================")
    println(s"======table -> $table=======================")

    val df = spark.sql(s"select * from `$db`.`$table`")

    df.foreach(println(_))

    spark.stop()
  }
}
