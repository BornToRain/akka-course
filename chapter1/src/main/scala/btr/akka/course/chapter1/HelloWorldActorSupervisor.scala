package btr.akka.course.chapter1

/**
  * HelloWorldActor监管者
  * 负责创建以及监管HelloWorldActor的生命周期
  */
class HelloWorldActorSupervisor(implicit as: ActorSystem, ac: ActorMaterializer, ec: ExecutionContext) extends Actor with ActorLogging
{
  //创建HelloWorldActor
  private[this] val api = context actorOf (HelloWorldActor props, HelloWorldActor.NAME)

  //监管api
  context watch api

  override def receive =
  {
    case Terminated(ref) => log info s"Actor已经停止: ${ref.path}"
      context.system terminate
  }
}

object HelloWorldActorSupervisor
{
  final val NAME = "hello-world-actor-supervisor"
  @inline
  def props(implicit as: ActorSystem, ac: ActorMaterializer, ec: ExecutionContext) = Props(new HelloWorldActorSupervisor)
}