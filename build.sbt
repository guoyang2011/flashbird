name := "flashbird"

version := "0.1.12"

organization := "com.changhong.flashbird"

scalaVersion := "2.10.5"

libraryDependencies += "com.twitter" % "finagle-httpx_2.10" % "6.29.0" withSources() withJavadoc()

libraryDependencies += "com.twitter" % "finagle-core_2.10" % "6.29.0" withSources() withJavadoc()

libraryDependencies += "com.twitter.finatra" % "finatra-http_2.10" % "2.1.0" withJavadoc() withSources()

libraryDependencies += "net.liftweb" % "lift-json_2.10" % "3.0-M1" withSources() withJavadoc()

libraryDependencies += "com.typesafe.slick" % "slick-codegen_2.10" % "2.1.0" withSources() withJavadoc()

libraryDependencies += "com.typesafe.slick" % "slick_2.10" % "2.1.0" withSources() withJavadoc()

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.33"

libraryDependencies += "commons-dbcp" % "commons-dbcp" % "1.4" withSources() withJavadoc()

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.4" withSources() withJavadoc()

libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.6.4" withSources() withJavadoc()

libraryDependencies += "com.twitter" % "finagle-redis_2.10" % "6.28.0" withSources() withJavadoc()

libraryDependencies += "redis.clients" % "jedis" % "2.6.1" withSources() withJavadoc()

libraryDependencies += "org.apache.commons" % "commons-pool2" % "2.0" withSources() withJavadoc()

libraryDependencies += "org.specs2" % "specs2_2.10" % "3.0-M2" withSources() withJavadoc()

libraryDependencies += "org.mongodb" % "casbah_2.10" % "2.8.0-RC2"

libraryDependencies += "nl.razko" %% "scraper" % "0.4.1" withSources() withJavadoc()

libraryDependencies += "org.yaml" % "snakeyaml" % "1.5" withJavadoc() withSources()



