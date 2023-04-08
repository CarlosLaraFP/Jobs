ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "Website"
  )

lazy val catsVersion = "2.9.0"
lazy val zioVersion = "2.0.10"
lazy val calibanVersion = "2.0.2"
lazy val doobieVersion = "1.0.0-RC1"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-prelude" % "1.0.0-RC18",
  "dev.zio" %% "zio-http" % "0.0.5",
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-test" % zioVersion,
  "dev.zio" %% "zio-test-sbt" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-test-junit" % zioVersion,
  "dev.zio" %% "zio-json" % "0.4.2",
  "dev.zio" %% "zio-config" % "3.0.7",
  "dev.zio" %% "zio-interop-cats" % "23.0.0.0",
  "org.typelevel" %% "cats-core" % catsVersion,
  "com.github.ghostdogpr" %% "caliban" % calibanVersion,
  "com.github.ghostdogpr" %% "caliban-zio-http" % calibanVersion,
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

scalacOptions ++= Seq(
  "-explaintypes",
  "-language:higherKinds",
  "-Xlint:infer-any"
)
