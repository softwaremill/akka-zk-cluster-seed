package akka.cluster.client

import akka.actor.{ActorSystem, Address, Props}
import akka.cluster.seed.{ZookeeperClient, ZookeeperClusterSeedSettings}
import com.typesafe.config.ConfigValueFactory

object ZookeeperClusterClientSettings {

  def apply(system: ActorSystem): ClusterClientSettings = {
    val config = system.settings.config.getConfig("akka.cluster.client")

    val settings = new ZookeeperClusterSeedSettings(system, "akka.cluster.client.zookeeper")

    val systemName = config.getString("akka.cluster.client.zookeeper.name")

    val address    : Address = if (settings.host.nonEmpty && settings.port.nonEmpty) {
      system.log.info(s"host:port read from environment variables=${settings.host}:${settings.port}")
      Address("http", systemName, settings.host, settings.port)
    } else throw new RuntimeException("I need an address")

    val client = new ZookeeperClient(settings, address, systemName)
    config.withValue("initial-contacts", ConfigValueFactory.fromAnyRef(""))
    ClusterClientSettings(config)
  }

}

object ZookeeperClusterClientProps {
  def apply(system: ActorSystem): Props = ClusterClient.props(ZookeeperClusterClientSettings(system))
}
