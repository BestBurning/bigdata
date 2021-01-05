package com.di1shuai.flink.scala

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._

/**
 * @author: Shea
 * @date: 2021/1/4
 * @description:
 */
object SinkDemo {

  def main(args: Array[String]): Unit = {
    val inputPath = "/Users/shuai/Documents/GitRepo/mine/bigdata/bigdata/flink/flink-scala/src/main/resources/sensor.txt"
    val outputPath = "/Users/shuai/Documents/GitRepo/mine/bigdata/bigdata/flink/flink-scala/src/main/resources/out"

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val dataStream = env.readTextFile(inputPath)
    val dataResult = dataStream.map(s => {
      val values = s.split(",")
      SensorReader(values(0),values(1).toLong,values(2).toDouble)
    })


    dataResult.print()
    dataResult.writeAsCsv(outputPath)

    env.execute("Sink Demo")
  }


}
