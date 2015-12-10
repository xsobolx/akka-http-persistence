
import akka.actor._
import akka.event.Logging

/**
  * Created by xsobolx on 10.12.2015.
  */

trait ITodoActor{
  implicit val system: ActorSystem

  lazy val todoActorRef: ActorRef = system.actorOf(Props(new TodoActor))
}

object TodoActor{
  case object Get
  case class Get(id: Int)
  case class Add(todo: String)
  case class UpdateTitle(id: Int, title: String)
  case class SetAchieved(id: Int, isAchieved: Int)
  case class Delete(id: Int)
}

class TodoActor extends Actor{
  import TodoActor._
  val log = Logging(context.system, this)

   def receive = {
    case Get =>
      sender() ! DataBase.findAllTodos()
    case Get(id) =>
      sender() ! DataBase.findTodoById(id).getOrElse(Status.Failure(new IllegalStateException("ID not found")))
    case Add(newTodo: String) =>
      val todo = Todo(newTodo)
      log.info("Added todo " + todo.id + ": " + todo.title + " " + todo.isAchieved.toString)
      DataBase.addTodo(todo.id, newTodo)
      sender() ! todo
    case UpdateTitle(id, title) =>
      DataBase.updateTodoTitle(id, title)
      log.info("Update title of " + id + " todo")
      self.forward(Get(id))
    case SetAchieved(id, isAchieved) =>
      DataBase.updateTodoAchieved(id, isAchieved)
      log.info("Switched achieved of " + id + " to " + isAchieved.toString)
      self.forward(Get(id))
    case Delete(id) =>
      DataBase.deleteTodo(id)
      log.info("Deleted todo " + id)
      sender() ! Status.Success()
  }
}
