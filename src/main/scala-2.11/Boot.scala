import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._

import scala.concurrent.{Await, ExecutionContext}
import scala.io.StdIn


/**
  * Created by xsobolx on 09.12.2015.
  */
object Boot extends App
    with ITodoActor
    with TodoRoute{

  implicit val system = ActorSystem("TODO")
  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher


  DataBase.run()
  Http(system).bindAndHandle(routes, "localhost", 8080)
    .foreach(binding => system.log.info("Bound to " + binding.localAddress))


}
