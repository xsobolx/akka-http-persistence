
import akka.actor.{Props, ActorRef, ActorSystem}
import akka.event.Logging
import akka.persistence._
/**
  * Created by xsobolx on 10.12.2015.
  */

object TodoPersistentActor{
  trait State
  case object Uninitialized extends State
  case class TodoState(id: Int, title: String, isAchieved: Int) extends State

  sealed trait Command
  case class TodoAdd(id: Int, title: String) extends Command
  case class UpdateTitle(id: Int, title: String) extends Command
  case class ChangeAchieved(id:Int, isAchieved: Int) extends Command
  case class Delete(id: Int) extends Command

  sealed trait Event
  case class TodoAdded(id: Int, title: String) extends Event
  case class TitleUpdated(title: String) extends Event
  case class AchieveChanged(isAchieved: Int) extends Event
  case class Deleted(id: Int) extends Event

  val eventsPerSnapshot = 5
}

class TodoPersistentActor extends PersistentActor{
  import TodoPersistentActor._

  override def persistenceId = "todo-persistence-id"

  var eventsSinceLastSnapshot = 0

  protected var state: State = Uninitialized

  val log = Logging(context.system, this)

  def afterEventPersist(event: Event): Unit = {
    eventsSinceLastSnapshot += 1
    if (eventsSinceLastSnapshot >= eventsPerSnapshot) {
      log.debug(s"$eventsSinceLastSnapshot events reached, saving snapshot")
      saveSnapshot(state)
      eventsSinceLastSnapshot = 0
    }
    updateState(event)
  }

  def updateState(event: Event): Unit = event match {
    case TodoAdded(id, title) =>
      state = TodoState(id, title, 0)
    case TitleUpdated(newTitle) =>
      state match {
        case t: TodoState => state = t.copy(title = newTitle)
        case _            => //
      }
    case AchieveChanged(newIsAchieved) =>
      state match {
        case t: TodoState => state = t.copy(isAchieved = newIsAchieved)
      }
  }


  private def publish(event: Event): Unit =
    context.system.eventStream.publish(event)


  val receiveRecover: Receive = {
    case evt: Event =>
      eventsSinceLastSnapshot += 1
      updateState(evt)
    case SnapshotOffer(_, snapshot: TodoState) => state = snapshot
  }

  val receiveCommand: Receive = {
    case TodoAdd(id, title) =>
      persist(TodoAdded(id, title)){ event =>
        afterEventPersist(event)
        publish(event)
        log.info(s"Todo adding ${id}: ${title}  persisted")
      }
    case UpdateTitle(id, title) =>
      persist(TitleUpdated(title)){ event =>
        afterEventPersist(event)
        publish(event)
        log.info(s"Todo ${id} update title to  ${title} persisted")
      }
    case ChangeAchieved(id, isAchieved) =>
      persist(AchieveChanged(isAchieved)){ event =>
        afterEventPersist(event)
        log.info(s"Todo ${id} change isAchievd to ${isAchieved} persisted")
        publish(event)
      }
    case "snap" => saveSnapshot(state)
    case "print" => println(state)
  }
}

















































































