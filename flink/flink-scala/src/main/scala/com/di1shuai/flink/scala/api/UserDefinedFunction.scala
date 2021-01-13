package com.di1shuai.flink.scala.api

import org.apache.flink.api.common.functions.{FilterFunction, MapFunction, RichMapFunction}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._


import scala.util.Random

/**
 * @author: Shea
 * @date: 2021/1/4
 * @description:
 */
object UserDefinedFunction {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val dataStream = env.addSource(new UDFSource())
    dataStream
      .map(new UDFMap())
      .filter(new UDFFilter())

    dataStream.print("UDF")

    env.execute("UDF")
  }


  class UDFSource() extends SourceFunction[String] {
    var isRunning = true;
    val random = Random


    override def run(ctx: SourceFunction.SourceContext[String]): Unit = {
      while (isRunning) {
        ctx.collect(
          random.nextInt(10000).toString
        )
        Thread.sleep(500)
      }
    }

    override def cancel(): Unit = isRunning = false
  }


  class UDFMap() extends MapFunction[String, Int] {

    override def map(value: String): Int = value.toInt
  }

  class UDFRichMap() extends RichMapFunction[String, Int] {

    override def open(parameters: Configuration): Unit = super.open(parameters)

    override def close(): Unit = super.close()

    override def map(value: String): Int = value.toInt


  }


  class UDFFilter() extends FilterFunction[Int] {
    override def filter(value: Int): Boolean = value % 2 == 0
  }

}
