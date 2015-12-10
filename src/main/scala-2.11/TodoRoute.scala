/**
  * Created by xsobolx on 10.12.2015.
  */
import scala.concurrent.duration._

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._


import akka.http.scaladsl.server.Directives._

import akka.pattern._
import akka.util._

trait TodoRoute extends TodoMarshalling
                with ITodoActor{
  implicit val timeout: Timeout = 2 seconds

  def routes = {
    (respondWithHeaders(
      `Access-Control-Allow-Origin`.`*`,
      `Access-Control-Allow-Headers`("Accept", "Content-Type"),
      `Access-Control-Allow-Methods`(GET, HEAD, POST, DELETE, OPTIONS, PUT, PATCH)
    ) & extract(_.request.getUri())) { uri =>
      implicit val todoFormat = todoFormatFor(uri.path("/todos").toString)
        pathPrefix("todos"){
          pathEnd{
            get{
              onSuccess(todoActorRef ? TodoActor.Get){ todos =>
                complete(StatusCodes.OK, todos.asInstanceOf[Iterable[Todo]])
              }
            } ~
            post {
              entity(as[String]){ newTodo =>
                onSuccess(todoActorRef ? TodoActor.Add(newTodo)){ todo =>
                  complete(StatusCodes.OK, todo.asInstanceOf[Todo])
                }
              }
            }
          } ~ {
            path(Segment){ id =>
              get{
                onSuccess(todoActorRef ? TodoActor.Get(id.toInt)){ todo =>
                  complete(StatusCodes.OK, todo.asInstanceOf[Todo])
                }
              } ~
              put{
                entity(as[String]){newTitle =>
                  onSuccess(todoActorRef ? TodoActor.UpdateTitle(id.toInt, newTitle)){todo =>
                    complete(StatusCodes.OK, todo.asInstanceOf[Todo])
                  }
                }
              } ~
              post{
                entity(as[String]){isAchieved =>
                  onSuccess(todoActorRef ? TodoActor.SetAchieved(id.toInt, isAchieved.toInt)) { todo =>
                    complete(StatusCodes.OK, todo.asInstanceOf[Todo])
                  }
                }
              } ~
              delete{
                onSuccess(todoActorRef ? TodoActor.Delete(id.toInt)){ _ =>
                  complete(StatusCodes.OK)
                }
              }
            }
          }
        } ~
        path(""){
          get{
            complete(StatusCodes.OK)
          }
        } ~
        options{
          complete(StatusCodes.OK)
        }
    }
  }
}
































