import akka.actor.ActorSystem

/**
  * Created by xsobolx on 11.12.2015.
  */
trait SystemInfo {
  implicit val system = ActorSystem("TODO")
}
