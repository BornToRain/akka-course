package btr.akka.course.chapter1

/**
  * 继承Directives就可以使用Akka-Http高级路由功能编写DSL
  */
object HelloWorld extends App with Directives
{
  implicit val as                   = ActorSystem("hello-world-system")
  implicit val ac                   = ActorMaterializer()
  implicit val ec: ExecutionContext = as.dispatcher

  /**
    * 服务器地址、端口
    */
  val (host, port) = ("0.0.0.0", 9000)

  /**
    * Akka-http High Level Api
    */
  def route = get
  {
    complete("Hello World")
  }

  /**
    * 在本地9000端口上启动一个Akka-http服务
    */
  Http() bindAndHandle (route, host, port) onComplete
  {
    case Success(d) => as.log info s"HelloWorld Http Server Started: ${d.localAddress}"
    case Failure(e) => as.log error s"HelloWorld Http Server Failed: ${e.getMessage}"
  }
}