name         := "chapter1"
version      := "0.1"
scalaVersion := "2.12.6"
organization := "btr.akka.course"

val akkaVersion     = "2.5.13"
val akkaHttpVersion = "10.1.3"

//标记函数内联
@inline
def applyAkka(name: String, version: String = akkaVersion) = "com.typesafe.akka" %% s"akka-$name" % version

libraryDependencies ++= Seq(
  applyAkka("actor"),
  applyAkka("slf4j"),
  applyAkka("stream"),
  applyAkka("http", akkaHttpVersion)
)