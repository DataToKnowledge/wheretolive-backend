package it.dtk

import it.dtk.twitter.entities._
import it.dtk.twitter.entities.json.TwitterJsonProtocols._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.elasticsearch.spark._
import spray.json._

/**
 * Created by gigitsu on 22/06/15.
 */
object KafkaConsumer {
  def main(args: Array[String]) { consume() }

  def consume() {
    val conf = new SparkConf().setAppName("kafka-consumer")
    conf.set("es.index.auto.create", "true")
    conf.set("es.resource", "twitter/tweet")
    conf.set("es.net.http.auth.user", "wheretolive")
    conf.set("es.net.http.auth.pass", "NothingToMoney")
    conf.set("es.nodes", "http://backend-0.cloudapp.net:9200,http://backend-1.cloudapp.net:9200,http://backend-2.cloudapp.net:9200,http://backend-3.cloudapp.net:9200")

    val ssc = new StreamingContext(conf, Seconds(2))
    ssc.checkpoint("checkpoint")

    val topics = List("twitter").map((_,1)).toMap
    KafkaUtils.createStream(ssc, "backend-1.cloudapp.net:2181", "kafka-tweets-consumer", topics)
      .map(_._2)
      .flatMap(_.split("\\r?\\n"))
      .map(_.parseJson.convertTo[Tweet])
      .foreachRDD(rdd => { rdd.saveToEs("twitter/tweet", Map("es.mapping.id" -> "id")) })

    ssc.start()
    ssc.awaitTermination()
  }
}
