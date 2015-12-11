import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.{Await, ExecutionContext}



/**
  * Created by xsobolx on 09.12.2015.
  */
object Boot extends App with SystemInfo
    with TodoManagerProvider
    with TodoRouteProvider
    {


  implicit val materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher


  DataBase.run()
  Http(system).bindAndHandle(routes, "localhost", 8080)
    .foreach(binding => system.log.info("Bound to " + binding.localAddress))


}
