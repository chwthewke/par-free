package fr.thomasdufour.parfree

import cats.arrow.FunctionK
import cats.instances.parallel._
import cats.instances.string._
import cats.syntax.parallel._
import cats.~>
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.Inside
import org.scalatest.Matchers
import org.scalatest.WordSpec

class ParFreeSpec extends WordSpec with Matchers with Inside with TypeCheckedTripleEquals {

  import ParFree._
  import ParFreeSpec._

  val inc: Int => Int = Func( "inc", _ + 1 )

  val eitherInterp: Box ~> Either[String, *] = new FunctionK[Box, Either[String, *]] {
    override def apply[A]( fa: Box[A] ): Either[String, A] = Left( fa.a.toString )
  }

  "Interpreting a parallel Ap" when {

    val prog = catsSyntaxParallelApply( FreeBox.lift( inc ) ) <&> FreeBox.lift( 1 )

    "to Either[String, *]" should {

      "combine the errors" in {
        val res = prog.parFoldMap( eitherInterp )

        inside( res ) {
          case Left( s ) => s should ===( "inc1" )
        }
      }
    }
  }

}

object ParFreeSpec {
  case class Box[A]( a: A )
  case class Func[A, B]( name: String, f: A => B ) extends ( A => B ) {
    override def apply( a: A ): B = f( a )
    override def toString: String = name
  }

  type FreeBox[A] = ParFree[Box, A]
  object FreeBox {
    def lift[A]( a: A ): FreeBox[A] = ParFree.liftF( Box( a ) )
  }
}
