package fr.thomasdufour.parfree

import cats.Applicative
import cats.arrow.FunctionK
import cats.Monad
import cats.Parallel
import cats.free.Free
import cats.free.FreeApplicative
import cats.~>

object ParFree {

  implicit def parFreeParallel[G[_]]: Parallel[ParFree[G, *]] = new Parallel[ParFree[G, *]] {
    override type F[X] = FreeApplicative[G, ParFree[G, X]]

    override def applicative: Applicative[F] = Applicative[FreeApplicative[G, *]].compose[ParFree[G, *]]

    override def monad: Monad[ParFree[G, *]] = Monad[Free[FreeApplicative[G, *], *]]

    override def sequential: F ~> ParFree[G, *] = new FunctionK[F, ParFree[G, *]] {
      override def apply[A]( fa: F[A] ): ParFree[G, A] = Free.roll( fa )
    }

    override def parallel: ParFree[G, *] ~> F = new FunctionK[ParFree[G, *], F] {
      override def apply[A]( fa: ParFree[G, A] ): F[A] = FreeApplicative.pure( fa )
    }
  }

  implicit class ParFreeOps[F[_], A]( private val self: ParFree[F, A] ) extends AnyVal {
    def parFoldMap[G[_]]( f: F ~> G )( implicit P: Parallel[G] ): G[A] =
      ParFree.parFoldMap( f ).apply( self )
  }

  def liftF[F[_], A]( fa: F[A] ): ParFree[F, A] = Free.liftF( FreeApplicative.lift( fa ) )

  def parFoldMap[F[_], G[_]]( f: F ~> G )( implicit P: Parallel[G] ): ParFree[F, *] ~> G =
    new FunctionK[ParFree[F, *], G] {
      implicit def PA: Applicative[P.F] = P.applicative
      implicit def PM: Monad[G]         = P.monad

      override def apply[A]( fa: ParFree[F, A] ): G[A] = {
        fa.foldMap( new FunctionK[FreeApplicative[F, *], G] {
          override def apply[X]( fa: FreeApplicative[F, X] ): G[X] =
            P.sequential( fa.foldMap( f.andThen( P.parallel ) ) )
        } )
      }
    }

}
