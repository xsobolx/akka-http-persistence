import akka.actor._
import akka.persistence._

/**
  * Created by xsobolx on 10.12.2015.
  */
case class Cmd(data: String)
case class Evt(data: String)


case class TodoState(events: List[String] = Nil){
  def updated(evt: Evt): TodoState = copy(evt.data :: events)
  def size: Int = events.length

  override def toString: String = events.reverse.toString
}

class TodoPersistentActor extends PersistentActor{
  override def persistenceId = "todo-id"

  var state = TodoState()

  def updateState(event: Evt): Unit =
    state = state.updated(event)

  def numEvents =
    state.size

  val receiveRecover: Receive = {
    case evt: Evt => updateState(evt)
    case SnapshotOffer(_, snapshot: TodoState) => state = snapshot
  }

  val receiveCommand: Receive = {
    case Cmd(data) =>
      persist(Evt(s"${data}-${numEvents}"))(updateState)
      persist(Evt(s"${data}-${numEvents + 1}")){ event =>
        updateState(event)
        context.system.eventStream.publish(event)
      }
    case "snap" => saveSnapshot(state)
    case "print" => println(state)
  }

}

















































































