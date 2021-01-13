package com.di1shuai.flink.scala.sink

import com.di1shuai.flink.scala.SensorReader
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

/**
 * @author: Shea
 * @date: 2021/1/13
 * @description:
 */
object RedisSink {

  def main(args: Array[String]): Unit = {
    val basePath = "/Users/shuai/Documents/GitRepo/mine/bigdata/bigdata/flink/flink-scala/src/main/resources/"
    val inputPath = basePath + "sensor.txt"
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val dataStream = env.readTextFile(inputPath)
    val stream = dataStream
      .map(s => {
        val strings = s.split(",")
        SensorReader(strings(0), strings(1).toLong, strings(2).toDouble)
      })
      .map(s => {
        (s.id,s.temperature+"-"+s.timestamp)
      })

    val conf = new FlinkJedisPoolConfig.Builder().setHost("di1shuai").build()

    stream.addSink(new RedisSink[(String, String)](conf, new RedisExampleMapper))
    stream.print("Redis Sink")

    env.execute("Redis Sink")
  }


  class RedisExampleMapper extends RedisMapper[(String, String)] {
    override def getCommandDescription: RedisCommandDescription = {
      new RedisCommandDescription(RedisCommand.HSET, "HASH_NAME")
    }

    override def getKeyFromData(data: (String, String)): String = data._1

    override def getValueFromData(data: (String, String)): String = data._2
  }

}
