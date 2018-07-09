package btr.akka.course.chapter1

/**
  * HelloWorldActor
  * 继承Directives就可以使用Akka-Http高级路由功能编写DSL
  */
class HelloWorldActor(implicit as: ActorSystem, ac: ActorMaterializer, ec: ExecutionContext) extends Actor with ActorLogging with Directives
{
  /**
    * 服务器地址、端口
    */
  val (host, port) = ("0.0.0.0", 9000)

  /**
    * 在本地9000端口上启动一个Akka-http服务
    */
  Http() bindAndHandle (route, host, port) onComplete
  {
    case Success(d) => log info s"HelloWorld Http Server Started: ${d.localAddress}"
    case Failure(e) => log error s"HelloWorld Http Server Failed: ${e.getMessage}"
  }

  /**
    * Akka-http High Level Api
    */
  def route = get
  {
    complete("Hello World")
  }

  //无任何行为
  override def receive = Actor.emptyBehavior
}

object HelloWorldActor
{
  final val NAME = "hello-world-actor"
  @inline
  def props(implicit as: ActorSystem, ac: ActorMaterializer, ec: ExecutionContext) = Props(new HelloWorldActor)
}
