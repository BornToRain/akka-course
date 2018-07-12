name         := "chapter3"
version      := "0.1"
scalaVersion := "2.12.6"
organization := "btr.akka.course"

val akkaVersion     = "2.5.13"
val akkaHttpVersion = "10.1.3"

@inline
def applyAkka(version: String = akkaVersion)(name: String*) = name map (d => "com.typesafe.akka" %% s"akka-$d") map (_ % version)
@inline
def applyCirce(version: String = "0.10.0-M1")(name: String*) = name map (d => "io.circe" %% s"circe-$d") map (_ % version)

libraryDependencies ++= applyAkka()("actor", "slf4j", "stream", "persistence") ++
  //akka-http支持
  applyAkka(akkaHttpVersion)("http") ++
  //circe解析支持、自动派生类支持、java8日期类型支持
  applyCirce()("parser", "generic-extras", "java8")
