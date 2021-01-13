package com.di1shuai.flink.scala

import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._

/**
 * @author: Shea
 * @date: 2021/1/4
 * @description:
 */
object SinkDemo {

  def main(args: Array[String]): Unit = {
    val basePath = "/Users/shuai/Documents/GitRepo/mine/bigdata/bigdata/flink/flink-scala/src/main/resources/"
    val inputPath = basePath + "sensor.txt"
    val outputCSVPath = basePath + "out-csv"
    val outputSinkPath = basePath + "out-sink"

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val dataStream = env.readTextFile(inputPath)
    val dataResult = dataStream.map(s => {
      val values = s.split(",")
      SensorReader(values(0), values(1).toLong, values(2).toDouble)
    })


    dataResult.print()
    dataResult.writeAsCsv(outputCSVPath)
    dataResult.addSink(StreamingFileSink.forRowFormat(
        new Path(outputSinkPath),
        new SimpleStringEncoder[SensorReader]()
      ).build()
    )

    env.execute("Sink Demo")
  }


}
