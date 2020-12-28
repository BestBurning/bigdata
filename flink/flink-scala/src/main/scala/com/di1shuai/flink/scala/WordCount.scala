package com.di1shuai.flink.scala

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._

/**
 * @author: Shea
 * @date: 2020/6/24
 * @description:
 */
object WordCount {

  def main(args: Array[String]): Unit = {
    //获取环境
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    val inputPath: String = "/Users/shuai/Documents/GitRepo/mine/bigdata/bigdata/flink/flink-scala/src/main/resources/words.txt"
    val inputDataSet: DataSet[String] = env.readTextFile(inputPath)
    val resultDataSet: DataSet[(String, Int)] = inputDataSet
      .flatMap(_.toLowerCase.split("\\W+")) //每行打散
      .map((_, 1))            //(word,1)
      .groupBy(0)   //以第一个元素word作为分组依据
      .sum(1)        //对所有分组数据的第二个元素count求和
    //打印
    resultDataSet.print()

  }

}
