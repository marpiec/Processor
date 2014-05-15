package pl.marpiec.processor

import scala.collection.{Iterable, GenTraversableOnce}

class Processor {

  var cache = Map[String, Any]()
  var stack = List[String]()
  var derivatives = Map[String, Set[String]]()

  def /[T](block: => T)(identifier: String):T = {

    markStackElementsAsDerivativesFromCurrent(identifier, stack)

    cache.get(identifier) match {
      case Some(cached) =>
        markStackElementsAsDerivativesOfDependenciesOfCurrent(identifier)
        cached.asInstanceOf[T]
      case None =>
        evaluateExpression(identifier, block)
    }
  }

  private def markStackElementsAsDerivativesOfDependenciesOfCurrent(identifier: String) {
    derivatives = derivatives.mapValues(value => if(dependencyOfCurrent(identifier, value)) value ++ stack else value)
  }

  private def dependencyOfCurrent(identifier: String, value: Set[String]): Boolean = {
    value.contains(identifier)
  }

  private def evaluateExpression[T](identifier: String, block: => T): T = {
    println("Evaluating " + identifier)
    removeCurrentFromDerivatives(identifier)

    stack ::= identifier
    val value = try {
      block
    } finally {
      stack = stack.tail
    }

    cache += identifier -> value
    value
  }

  private def markStackElementsAsDerivativesFromCurrent(key: String, values: Iterable[String]) {
    val newValues = derivatives.get(key).map(_ ++ values).getOrElse(values.toSet)
    derivatives = derivatives + (key -> newValues)
  }

  private def removeCurrentFromDerivatives(identifier: String) {
    derivatives.map {case (key, value) => value - identifier}
  }

  def invalidate(identifier: String) {
    cache -= identifier
    derivatives.getOrElse(identifier, Set()).foreach(cache -= _)
  }


  def setValue[T](identifier: String, value: T) {
    invalidate(identifier)
    cache += identifier -> value
  }

}
