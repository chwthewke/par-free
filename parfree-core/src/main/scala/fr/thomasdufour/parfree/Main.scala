package fr.thomasdufour.parfree

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.syntax.functor._

object Main extends IOApp {

  override def run( args: List[String] ): IO[ExitCode] =
    IO( println( s"${buildinfo.ParallelFree.name} ${buildinfo.ParallelFree.version}" ) )
      .as( ExitCode.Success )

}
