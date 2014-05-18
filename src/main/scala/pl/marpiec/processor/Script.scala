package pl.marpiec.processor

abstract class Script {

  private val processor = new Processor

  def /[T](identifier: String)(block: => T):T = {
    processor./(block)(identifier)
  }

  def invalidate(identifier: String) {
    processor.invalidate(identifier)
  }


  def setValue[T](identifier: String, value: T) {
    processor.setValue(identifier, value)
  }

  def debug = "Cache:\n" + processor.cache +"\n\nDerivatives:\n"+processor.derivatives
}
