package com.di1shuai.flink.scala

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer.Semantic



/**
 * @author: Shea
 * @date: 2020/12/27
 * @description:
 */
object KafkaStream {

  def main(args: Array[String]): Unit = {
    val consumerProperties = new Properties()
    consumerProperties.setProperty("bootstrap.servers", "kafka1:9092")
    consumerProperties.setProperty("group.id", "flink-stream")

    val producerProperties = new Properties
    producerProperties.setProperty("bootstrap.servers", "kafka1:9092")

    val topicIn  = "flink-stream-in"
    val topicOut  = "flink-stream-out"

    val kafkaSource = new FlinkKafkaConsumer(topicIn, new SimpleStringSchema(), consumerProperties);
    val kafkaSink = new FlinkKafkaProducer(
      topicOut,                  // target topic
      new SimpleStringSchema(),    // serialization schema
      producerProperties)
    //,  //                 producer config FlinkKafkaProducer.Semantic.EXACTLY_ONCE)

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val dataStream = env.addSource(kafkaSource);
    dataStream.print()
    dataStream.addSink(kafkaSink)



    env.execute()

  }

}


