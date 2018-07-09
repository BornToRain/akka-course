package btr.akka.course.chapter1

object HelloWorldApp extends App
{
  implicit val as                   = ActorSystem("hello-world-system")
  implicit val ac                   = ActorMaterializer()
  implicit val ec: ExecutionContext = as.dispatcher

  //创建一个HelloWorldActor监管者
  as actorOf (HelloWorldActorSupervisor props, HelloWorldActorSupervisor.NAME)
}
