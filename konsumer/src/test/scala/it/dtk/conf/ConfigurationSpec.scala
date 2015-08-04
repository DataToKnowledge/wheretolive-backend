package it.dtk.conf

import org.scalatest.FlatSpec
import com.typesafe.config.{ConfigFactory, Config}

/**
 * Created by gigitsu on 04/08/15.
 */
class ConfigurationSpec extends FlatSpec {
  "A kafka consumer" should "read the private configuration file" in {
    val config = ConfigFactory.load().getConfig("konsumer")
    assert(config.getString("name") == "konsumer")
    assert(config.getString("kafka.topic") == ConfigFactory.load().getString("twitter-monitor.kafka.topic"))
    assert(config.getString("zookeeper.quorum") == ConfigFactory.load().getString("twitter-monitor.kafka.zookeeper.quorum"))
  }
}
