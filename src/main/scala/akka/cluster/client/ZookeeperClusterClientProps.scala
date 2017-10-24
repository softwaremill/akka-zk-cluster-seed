package akka.cluster.client

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.cluster.seed.{ZookeeperClient, ZookeeperClusterSeedSettings}
import com.typesafe.config.ConfigValueFactory

import scala.collection.JavaConverters._
import scala.collection.immutable

object ZookeeperClusterClientSettings {

  def apply(system: ActorSystem): ClusterClientSettings = {
    val config = system.settings.config.getConfig("akka.cluster.client")

    val settings = new ZookeeperClusterSeedSettings(system, "akka.cluster.client.zookeeper")

    val systemName = config.getString("zookeeper.name")

    val actorPath = config.getString("zookeeper.actor-path")

    val client = new ZookeeperClient(settings, UUID.randomUUID().toString, systemName, system.log)

    client.createPathIfNeeded()
    client.start()

    val contacts = client.latch.getParticipants.iterator().asScala.filterNot(_.getId == client.myId).map {
      node => node.getId + actorPath
    }.toList
    system.log.warning("component=zookeeper-cluster-client at=find-initial-contacts contacts={}", contacts)

    client.close()

    ClusterClientSettings(
      config.withValue(
        "initial-contacts",
        ConfigValueFactory.fromIterable(immutable.List(contacts: _*).asJava)
      )
    )
  }

}

object ZookeeperClusterClientProps {
  def apply(system: ActorSystem): Props = ClusterClient.props(ZookeeperClusterClientSettings(system))
}
