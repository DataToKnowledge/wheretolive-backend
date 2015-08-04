package it.dtk

import it.dtk.twitter.entities._
import it.dtk.twitter.entities.json.TwitterJsonProtocols._
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.elasticsearch.spark._
import spray.json._
import com.typesafe.config.{Config, ConfigFactory}

/**
 * Created by gigitsu on 22/06/15.
 */
object KafkaConsumer {
  def main(args: Array[String]) { consume() }

  def consume() {
    val config = ConfigFactory.load().getConfig("konsumer")
    val spConf = new SparkConf().setAppName(config.getString("name"))
    spConf.set("es.resource", config.getString("es.resource"))
    spConf.set("es.nodes", config.getString("es.nodes"))
    spConf.set("es.index.auto.create", "true")

    val ssc = new StreamingContext(spConf, Seconds(2))
    ssc.checkpoint("checkpoint")

    val topics = List(config.getString("kafka.topic")).map((_,1)).toMap
    KafkaUtils.createStream(
        ssc,
        config.getString("zookeeper.quorum"),
        config.getString("kafka.groupid"),
        topics)
      .map(_._2)
      .flatMap(_.split("\\r?\\n"))
      .map(_.parseJson.convertTo[Tweet].toJson.compactPrint)
      .foreachRDD(rdd => { rdd.saveJsonToEs(config.getString("es.resource"), Map("es.mapping.id" -> "id_str")) })

    ssc.start()
    ssc.awaitTermination()
  }
}
