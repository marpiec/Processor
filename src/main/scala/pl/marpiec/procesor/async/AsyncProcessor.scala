package pl.marpiec.procesor.async

import scala.collection.Iterable
import scala.concurrent.Future
import java.lang
import java.util.concurrent.ForkJoinPool
import scala.concurrent._

class AsyncProcessor {

  implicit val exec = ExecutionContext.fromExecutorService(new ForkJoinPool(100))

  var cache = Map[String, Future[Any]]()
  var derivatives = Map[String, Set[String]]()

  def /[T](block: List[String] => T)(identifier: String)(stack: List[String]): Future[T] = {

    markStackElementsAsDerivativesFromCurrent(identifier, stack)

    cache.get(identifier) match {
      case Some(cached) =>
        markStackElementsAsDerivativesOfDependenciesOfCurrent(identifier, stack)
        cached.asInstanceOf[Future[T]]
      case None =>
        evaluateExpression(identifier, block, stack)
    }
  }

  private def markStackElementsAsDerivativesOfDependenciesOfCurrent(identifier: String, stack: List[String]) {
    derivatives = derivatives.mapValues(value => if (dependencyOfCurrent(identifier, value)) value ++ stack else value)
  }

  private def dependencyOfCurrent(identifier: String, value: Set[String]): Boolean = {
    value.contains(identifier)
  }

  private def evaluateExpression[T](identifier: String, block: List[String] => T, stack: List[String]): Future[T] = {
    println(lang.System.currentTimeMillis() % 10000 + " Evaluating " + identifier)
    removeCurrentFromDerivatives(identifier)
    println(lang.System.currentTimeMillis() % 10000 + " Evaluating " + identifier)
    val futureValue = future {
      println(lang.System.currentTimeMillis() % 10000 + " Start " + identifier + " Thread " + Thread.currentThread().getName)
      val result = block(identifier :: stack)
      println(lang.System.currentTimeMillis() % 10000 + " End " + identifier + " Thread " + Thread.currentThread().getName)
      result
    }


    cache += identifier -> futureValue
    futureValue
  }

  private def markStackElementsAsDerivativesFromCurrent(key: String, values: Iterable[String]) {
    val newValues = derivatives.get(key).map(_ ++ values).getOrElse(values.toSet)
    derivatives = derivatives + (key -> newValues)
  }

  private def removeCurrentFromDerivatives(identifier: String) {
    derivatives.map { case (key, value) => value - identifier}
  }

  def invalidate(identifier: String) {
    cache -= identifier
    derivatives.getOrElse(identifier, Set()).foreach(cache -= _)
  }


  def setValue[T](identifier: String, value: T) {
    invalidate(identifier)
    cache += identifier -> Future.successful(value)
  }

}
