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

val parfreeSettings = Seq( libraryDependencies ++= betterMonadicFor ++ kindProjector ++ splain )

val `parfree-core` = project
  .settings( parfreeSettings )
  .settings( libraryDependencies ++= cats ++ catsFree )
  .enablePlugins( SbtBuildInfo, ScalacPlugin )

val `parfree-tests` = project
  .settings( parfreeSettings )
  .settings( libraryDependencies ++= (scalatest ++ scalacheck).map( _ % "test" ) )
  .dependsOn( `parfree-core` )
  .enablePlugins( ScalacPlugin )

val `parallel-free-all` = project
  .in( file( "." ) )
  .aggregate( `parfree-core`, `parfree-tests` )
