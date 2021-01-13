package com.di1shuai.flink.scala.api

import com.di1shuai.flink.scala.SensorReader
import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.{OutputTag, StreamExecutionEnvironment}
import org.apache.flink.util.Collector
import org.apache.flink.streaming.api.scala._
/**
 * @author: Shea
 * @date: 2020/12/28
 * @description:
 */
object Transformations {


  def main(args: Array[String]): Unit = {
    //获取环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    val inputPath: String = "/Users/shuai/Documents/GitRepo/mine/bigdata/bigdata/flink/flink-scala/src/main/resources/sensor.txt"
    val inputStream = env.readTextFile(inputPath)
    // -----------------

    //1 map 转换为样例类
    val dataStream = inputStream
      .map(
        data => {
          val array = data.split(",")
          SensorReader(array(0), array(1).toLong, array(2).toDouble)
        }
      )

    // --------------------

    //2 aggregation 分组聚合，计算每个传感器当前的最小温度
    val aggStream = dataStream
      .keyBy(_.id) // 根据ID分组
      //      .min("temperature")    // 温度最小的一组数据
      .minBy("temperature") //只保持温度最小,不影响时间的数据

    //--------------------------

    //3 reduce 实现温度最小，但时间戳最新
    val reduceStream = dataStream
      .keyBy(_.id)
      //3.1 lambda
      //      .reduce( (current,next) => {
      //        SensorReader(
      //          current.id,
      //          current.timestamp.max(next.timestamp),
      //          current.temperature.min(next.temperature)
      //        )
      //      })
      //3.2 自定义ReduceFunction
      .reduce(new MaxTimeMinTemp)

    // ------------------------------

    //4 Side Outputs 实现分流，按温度>37 和<=37 分为高温、低温流
    val highTag = OutputTag[SensorReader]("high-temperature")
    val normalTag = OutputTag[SensorReader]("normal-temperature")
    // 4.1 lambda
    val allStream = dataStream.process(
      (value, ctx: ProcessFunction[SensorReader, SensorReader]#Context, out: Collector[SensorReader]) => {
        out.collect(value)
        if (value.temperature > 37) {
          ctx.output(highTag, value)
        } else {
          ctx.output(normalTag, value)
        }
      }
    )
    // 4.2 自定义ProcessFunction
    //    val processStream = dataStream.process(new TemperatureTagProcess)

    val highStream = allStream.getSideOutput(highTag)
    val normalStream = allStream.getSideOutput(normalTag)

    //----------------------

    // 5. Connect 合流 可以不同类型 但只能两个
    val warnStream = highStream.map(data => (data.id, data.temperature))
    val connectedStream = warnStream.connect(normalStream)
    val coMapResultStream = connectedStream
      .map(
        warnData => (warnData._1, warnData._2, "warning"),
        normalData => (normalData.id, "healthy")
      )

    // 6 Union 合并 必须同类型，但可以多个
    val unionStream = highStream.union(normalStream)



    //-------------------
    //打印
    //    aggStream.print("agg")
    //    reduceStream.print("reduce")
    //    highStream.print("high")
    //    normalStream.print("normal")
    //    allStream.print("all")
    //    coMapResultStream.print("coMap")
    unionStream.print("union")


    env.execute("Transformations Demo")

  }

  class MaxTimeMinTemp() extends ReduceFunction[SensorReader] {
    override def reduce(value1: SensorReader, value2: SensorReader): SensorReader = SensorReader(
      value1.id,
      value1.timestamp.max(value2.timestamp),
      value1.temperature.min(value2.temperature)
    )
  }

  class TemperatureTagProcess() extends ProcessFunction[SensorReader, SensorReader] {
    val highTag = OutputTag[SensorReader]("high-temperature")
    val normalTag = OutputTag[SensorReader]("normal-temperature")

    override def processElement(value: SensorReader, ctx: ProcessFunction[SensorReader, SensorReader]#Context, out: Collector[SensorReader]): Unit = {
      out.collect(value)
      if (value.temperature > 37) {
        ctx.output(highTag, value)
      } else {
        ctx.output(normalTag, value)
      }
    }
  }

}
