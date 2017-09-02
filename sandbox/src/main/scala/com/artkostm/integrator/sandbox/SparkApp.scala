package com.artkostm.integrator.sandbox

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by artsiom.chuiko on 04/07/2017.
  */

object SparkApp {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("Example app")
      .setSparkHome("localhost")
      .setMaster("local[*]")
    val sc = new SparkContext(conf)

    val file = "/Users/arttsiom.chuiko/git/Integrator/src/main/resources/application.conf"
    val fileData = sc.textFile(file, 2).cache()

    val numAs = fileData.filter(line => line.contains("a")).count()
    val numBs = fileData.filter(line => line.contains("m")).count()
    println(s"Lines with a: $numAs, Lines with m: $numBs")

    Thread.sleep(60000)
    sc.stop()
  }
}