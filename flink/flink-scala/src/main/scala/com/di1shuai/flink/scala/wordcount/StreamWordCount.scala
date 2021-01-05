package com.di1shuai.flink.scala.wordcount

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.scala._
/**
 * @author: Shea
 * @date: 2020/6/24
 * @description:
 */
object StreamWordCount {

  def main(args: Array[String]): Unit = {
    // 获取执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    //从参数中获取 host port    e.g.  --host localhost --port 9000
    val paramTool = ParameterTool.fromArgs(args)
    val host: String = paramTool.get("host")
    val port: Int = paramTool.getInt("port")

    // 并行度
    //    env.setParallelism(8)

    // 接受一个socket流
    val inputDataStream: DataStream[String] = env.socketTextStream(host, port);
    val resultDataStream: DataStream[(String, Int)] = inputDataStream
      .flatMap(_.toLowerCase.split("\\W+"))
      .map((_, 1))
      .keyBy(_._1)
      .sum(1)
    resultDataStream.print().setParallelism(1)

    // 启动任务
    env.execute("Stream Word Count")


    // nc -lk 9000
  }

}
