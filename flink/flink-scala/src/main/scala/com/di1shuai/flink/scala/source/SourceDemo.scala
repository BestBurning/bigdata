package com.di1shuai.flink.scala.source

import com.di1shuai.flink.scala.SensorReader
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala._

import scala.util.Random



/**
 * @author: Shea
 * @date: 2020/6/28
 * @description:
 */
object SourceDemo {


  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val now = System.currentTimeMillis()

    val dataList = List(
      SensorReader("1", now, 36.5),
      SensorReader("2", now - 30 * 1000, 39),
      SensorReader("3", now - 300 * 1000, 37.1),
      SensorReader("4", now, 36.2)
    )
    //1. 集合
    //    val dataStream = env.fromCollection(dataList)
    //2 任意类型元素
    //    val dataStream = env.fromElements(1,1.1,"hello",true)
    //3 文件
    val inputPath: String = "/Users/shuai/Documents/GitRepo/mine/bigdata/bigdata/flink/flink-scala/src/main/resources/words.txt"
    //    val dataStream = env.readTextFile(inputPath)

    //4 Kafka
//        val consumerProperties = new Properties()
//        consumerProperties.setProperty("bootstrap.servers", "kafka1:9092")
//        consumerProperties.setProperty("group.id", "flink-stream")
//        val topic = "flink-source";
//        val kafkaSource = new FlinkKafkaConsumer[String](topic,new SimpleStringSchema(),consumerProperties)
//
//        val dataStream = env.addSource(kafkaSource)


    //5 自定义Source
    val dataStream = env.addSource(new SensorReaderSource())


    dataStream.print()

    env.execute()
  }


}

// 1 场景一   测试
// 2 场景二   其他数据源
class SensorReaderSource() extends SourceFunction[SensorReader] {
  var running: Boolean = true
  val random = Random

  var currentTemp = 1.to(10).map(i => SensorReader("sensor_" + i, System.currentTimeMillis(), random.nextDouble() * 5 + 35)).toList

  def getDataList(): List[SensorReader] = {
    currentTemp.map(
      s => SensorReader(s.id,System.currentTimeMillis(),s.temperature+random.nextGaussian())
    )

  }

  override def run(ctx: SourceFunction.SourceContext[SensorReader]): Unit =
    while (running) {
      getDataList().foreach(
        data => ctx.collect(data)
      )
      Thread.sleep(1000)
    }


  override def cancel(): Unit = running = false
}