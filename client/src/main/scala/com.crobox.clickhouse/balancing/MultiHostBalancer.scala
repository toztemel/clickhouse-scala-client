package com.crobox.clickhouse.balancing

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.Uri
import akka.pattern.ask
import akka.util.Timeout
import akka.util.Timeout.durationToTimeout
import com.crobox.clickhouse.balancing.discovery.ConnectionManagerActor
import com.crobox.clickhouse.balancing.discovery.ConnectionManagerActor.GetConnection
import com.crobox.clickhouse.internal.ClickhouseHostBuilder

import scala.concurrent.Future
import scala.concurrent.duration._

case class MultiHostBalancer(hosts: Set[Uri], manager: ActorRef)(implicit system: ActorSystem)
    extends HostBalancer
    with ClickhouseHostBuilder {

  private implicit val timeout: Timeout = durationToTimeout(5.seconds)

  manager ! ConnectionManagerActor.Connections(hosts)

  override def nextHost: Future[Uri] = (manager ? GetConnection()).mapTo[Uri]
}
