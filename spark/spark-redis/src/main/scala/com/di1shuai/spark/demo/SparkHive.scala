package com.di1shuai.spark.demo

import org.apache.spark.sql.SparkSession

/**
  * @Author: Bruce Zhu
  * @Description:
  * @Date: Created in 9:09 2018/2/5
  * @Midified By:
  */
object SparkHive {
  def main(args: Array[String]): Unit = {
    //    val conf = new SparkConf().setAppName("ForeachDemo").setMaster("local")
    //    val sc = new SparkContext(conf)

    val spark = SparkSession.builder()
      .appName("SparkHive")
      .enableHiveSupport()
      .getOrCreate()
    val df = spark.sql("select * from `xxxx`")
    df.foreach(println(_))
    spark.stop()
  }
}
