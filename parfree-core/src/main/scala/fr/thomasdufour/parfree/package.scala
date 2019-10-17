package fr.thomasdufour

import cats.free.Free
import cats.free.FreeApplicative

package object parfree {
  type ParFree[F[_], A] = Free[FreeApplicative[F, *], A]
}
