package akka.cluster.client

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigValueFactory

object ZookeeperClusterClientSettings {

  def apply(system: ActorSystem): ClusterClientSettings = {
    val config = system.settings.config.getConfig("akka.cluster.client")

    config.withValue("initial-contacts", ConfigValueFactory.fromAnyRef(""))
    ClusterClientSettings(config)
  }

}

object ZookeeperClusterClientProps {
  def apply(system: ActorSystem): Props = ClusterClient.props(ZookeeperClusterClientSettings(system))
}
