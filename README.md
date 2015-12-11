# akka-http-persistence
Для запуска:

1. Настроить url, user, password в DataBase.scala
2. type $sbt
>compile
>run
3. запросы:
4. 127.0.0.1:8080/todos 
   GET - получить все тодо
   POST - создать новое тодо
127.0.0.1:8080/todos/id_todo
    GET - получиь тодо по id
    POST - изменить параметр isAchieved
    PUT - изменить параметр title
    DELETE - удалить тодо
