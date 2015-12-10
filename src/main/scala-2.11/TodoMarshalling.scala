import spray.json._

import akka.http.scaladsl.marshallers.sprayjson._
/**
  * Created by xsobolx on 10.12.2015.
  */
trait TodoMarshalling extends SprayJsonSupport
  with DefaultJsonProtocol{
  val standardTodoFormat = jsonFormat3(Todo.apply)

  def todoFormatFor(baseUrl: String) = new RootJsonFormat[Todo] {
    def read(json: JsValue) = standardTodoFormat.read(json)

    def write(todo:Todo) = {
      val fields = standardTodoFormat.write(todo).asJsObject.fields
      JsObject(fields.updated("url", JsString(baseUrl + '/' + todo.id)))
    }
  }
}
