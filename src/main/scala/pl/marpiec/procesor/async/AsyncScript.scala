package pl.marpiec.procesor.async

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

abstract class AsyncScript {

  type fun[T] = List[String] => Future[T]

  private val processor = new AsyncProcessor

  protected val emptyStack = List[String]()

  def /[T](identifier: String)(dependencies: Any*)(block: List[String] => T)(implicit stack: List[String]):Future[T] = {
    processor./(block)(identifier)(stack)
  }

  def invalidate(identifier: String) {
    processor.invalidate(identifier)
  }


  def setValue[T](identifier: String, value: T) {
    processor.setValue(identifier, value)
  }

  def $[T](futureValue: Future[T]):T = {
    Await.result(futureValue, 30 seconds)
  }

  def debug = "Cache:\n" + processor.cache +"\n\nDerivatives:\n"+processor.derivatives
}
