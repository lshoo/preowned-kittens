package ddata

import akka.actor.Actor
import akka.cluster.Cluster
import akka.cluster.ddata.Replicator.{ReplicaCount, GetReplicaCount}
import akka.remote.testconductor.RoleName
import akka.remote.testkit._
import akka.testkit._
import com.typesafe.config.ConfigFactory
import ddaata.ServiceRegistry
import akka.cluster.ddata._
import org.scalatest.{WordSpecLike, Matchers, BeforeAndAfterAll}

import scala.concurrent.duration._

trait STMultiNodeSpec extends MultiNodeSpecCallbacks
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def beforeAll() = multiNodeSpecBeforeAll()

  override def afterAll() = multiNodeSpecAfterAll()
}

object ServiceRegistrySpec extends MultiNodeConfig {
  val node1 = role("node-1")
  val node2 = role("node-2")
  val node3 = role("node-3")

  commonConfig(
    ConfigFactory.parseString(
      """
        |akka.log.level = INFO
        |akka.actor.provider = "akka.cluster.ClusterActorRefProvider"
        |akka.log-dead-letters-during-shutdown = off
      """.stripMargin)
  )

  class Service extends Actor {
    def receive = {
      case s: String => sender() ! self.path.name + ":" + s
    }
  }
}

class ServiceRegistrySpecMultiJvmNode1 extends  ServiceRegistrySpec
class ServiceRegistrySpecMultiJvmNode2 extends  ServiceRegistrySpec
class ServiceRegistrySpecMultiJvmNode3 extends ServiceRegistrySpec

class ServiceRegistrySpec extends MultiNodeSpec(ServiceRegistrySpec) with STMultiNodeSpec with ImplicitSender {
  import ServiceRegistrySpec._
  import ServiceRegistry._

  override def initialParticipants = roles.size

  val cluster = Cluster(system)
  val registry = system.actorOf(ServiceRegistry.props)

  def join(from: RoleName, to: RoleName): Unit = {
    runOn(from) {
      cluster join node(to).address
    }
    enterBarrier(from.name + "-joined")
  }

  "Demo of a replicated service registry" must {
    "join cluster" in within(20.seconds) {
      join(node1, node1)
      join(node2, node1)
      join(node3, node1)
    }

    awaitAssert {
      DistributedData(system).replicator ! GetReplicaCount
      expectMsg(ReplicaCount(roles.size))
    }
    enterBarrier("after-1")
  }
}


