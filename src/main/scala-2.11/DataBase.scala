import scalikejdbc._

/**
  * Created by xsobolx on 10.12.2015.
  */
object DataBase {
  val user = "todo"
  val password = "todo"
  val dbUrl = "jdbc:postgresql://localhost:5432/todo"
  val tableName = "todos"
  val idsSeq = "todo_ids_seq"
  val columnId = "todo_id"
  val columnTitle = "title"
  val columnIsAchieved = "is_achieved"

  Class.forName("org.postgresql.Driver")
  ConnectionPool.singleton(dbUrl, user, password)

  implicit val session = AutoSession

  def run(): Unit = {
    DB readOnly{ implicit s =>
      try {
        sql"select 1 from todos limit 1".map(_.long(1)).single.apply()
      } catch {
        case e: java.sql.SQLException =>
          DB autoCommit{ implicit s =>
            sql"""
              create sequence todo_ids_seq;
              create table todos (
              id bigint not null default nextval('todo_ids_seq') primary key,
              todo_id bigint,
              title varchar(255),
              is_achieved smallint
            );

            insert into todos (todo_id, title, is_achieved) values ('1', 'Buy bread', '1');
            insert into todos (todo_id, title, is_achieved) values ('2', 'Go to cinema', '0');
            insert into todos (todo_id, title, is_achieved) values ('3', 'Do the dog', '0');
            """.execute.apply()
          }
      }
    }
  }

  def findAllTodos():List[Todo] = {
    sql"select * from todos".map(rs => Todo(rs)).list.apply()
  }

  def findTodoById(id: Int): Option[Todo] = {
    sql"select * from todos where todo_id = ${id} limit 1".map(rs => Todo(rs)).single.apply()
  }

  def addTodo(id: Int, title: String) = {
    sql"insert into todos (todo_id, title, is_achieved) values (${id}, ${title}, '0')".update.apply()
  }

  def updateTodoTitle(id: Int, title: String) = {
    sql"update todos set title = ${title} where todo_id = ${id}".update.apply()
  }

  def updateTodoAchieved(id: Int, isAchieved: Int) = {
    sql"update todos set is_achieved = ${isAchieved} where todo_id = ${id}".update.apply()
  }

  def deleteTodo(id: Int) = {
    sql"delete from todos where todo_id = ${id}".update.apply()
  }
}
