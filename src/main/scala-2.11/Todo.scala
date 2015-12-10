import scalikejdbc._

import scala.util.Random

/**
  * Created by xsobolx on 09.12.2015.
  */
case class Todo (id: Int, var title: String, var achieved: Int = 0){
  def updateTitle(newTitle: String): Unit ={
    this.title = newTitle
  }

  def isAchieved: Int = {
    achieved
  }

  def setAchieved(isAchieved: Int) = {
    this.achieved = isAchieved
  }
}

object Todo extends SQLSyntaxSupport[Todo]{
  override val tableName = "todos"

  private def nextId() = Random.nextInt(Integer.MAX_VALUE)

  def apply(title: String): Todo ={
    Todo(nextId(), title)
  }

  def apply(rs: WrappedResultSet): Todo = new Todo(
    rs.int("todo_id"), rs.string("title"), rs.int("is_achieved")
  )

}

