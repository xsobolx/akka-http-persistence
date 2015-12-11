
import TodoPersistentActor.TodoAdd
import akka.actor._
import akka.event.Logging

/**
  * Created by xsobolx on 10.12.2015.
  */

trait TodoManagerProvider{
  implicit val system: ActorSystem
  lazy val todoActorRef: ActorRef = system.actorOf(Props(new TodoManger))
}

object TodoManger{
  case object Get
  case class Get(id: Int)
  case class Add(todo: String)
  case class UpdateTitle(id: Int, title: String)
  case class SetAchieved(id: Int, isAchieved: Int)
  case class Delete(id: Int)
}

class TodoManger extends Actor with SystemInfo{
  import TodoManger._
  val log = Logging(context.system, this)

  val persistenceActor = system.actorOf(Props(new TodoPersistentActor))

   def receive = {
    case Get =>
      sender() ! DataBase.findAllTodos()
    case Get(id) =>
      sender() ! DataBase.findTodoById(id).getOrElse(Status.Failure(new IllegalStateException("ID not found")))
    case Add(newTodo: String) =>
      val todo = Todo(newTodo)
      log.info("Added todo " + todo.id + ": " + todo.title + " " + todo.isAchieved.toString)
      DataBase.addTodo(todo.id, newTodo)
      persistenceActor ! TodoAdd(todo.id, todo.title)
      sender() ! todo
    case UpdateTitle(id, title) =>
      DataBase.updateTodoTitle(id, title)
      log.info("Update title of " + id + " todo")
      persistenceActor ! UpdateTitle(id, title)
      self.forward(Get(id))
    case SetAchieved(id, isAchieved) =>
      DataBase.updateTodoAchieved(id, isAchieved)
      log.info("Switched achieved of " + id + " to " + isAchieved.toString)
      persistenceActor ! SetAchieved(id, isAchieved)
      self.forward(Get(id))
    case Delete(id) =>
      DataBase.deleteTodo(id)
      log.info("Deleted todo " + id)
      sender() ! Status.Success()
  }
}
