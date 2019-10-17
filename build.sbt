import sbt._
import sbt.Keys._

// format: off
organization      in ThisBuild := "fr.thomasdufour"
scalaOrganization in ThisBuild := "org.scala-lang"
scalaVersion      in ThisBuild := "2.13.1"
// TODO when I can make sense of lm-coursier
conflictManager   in ThisBuild                         := ConflictManager.strict
conflictManager   in updateSbtClassifiers in ThisBuild := ConflictManager.default
// format: on

enablePlugins( FormatPlugin, DependenciesPlugin )

val `parfree-core` = project
  .settings( libraryDependencies ++= cats ++ catsEffect )
  .enablePlugins( SbtBuildInfo, ScalacPlugin )

val `parallel-free-tests` = project
  .settings( libraryDependencies ++= (scalatest ++ scalacheck).map( _ % "test" ) )
  .dependsOn( `parfree-core` )
  .enablePlugins( ScalacPlugin )

val `parallel-free-all` = project
  .in( file( "." ) )
  .aggregate(
    `parfree-core`,
    `parallel-free-tests`
  )
