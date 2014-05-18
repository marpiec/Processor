package pl.marpiec.processor.async

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import pl.marpiec.procesor.async.AsyncScript


class TestScript extends AsyncScript {

  def a(implicit s:List[String]) = /("a")()   {implicit s =>  findVal(2, 1000)                    }
  def b(implicit s:List[String]) = /("b")()   {implicit s =>  findVal(3, 1000)                    }
  def c(implicit s:List[String]) = /("c")(a,b){implicit s =>  $(a) + $(b)                          }
  def d(implicit s:List[String]) = /("d")()   {implicit s =>  findVal(5, 1000)                    }
  def e(implicit s:List[String]) = /("e")(c,d){implicit s =>  $(c) + $(d)                         }
  def f(implicit s:List[String]) = /("f")()   {implicit s =>  findVal(5, 1000)                    }
  def g(implicit s:List[String]) = /("g")(c,e){implicit s =>  $(c) + $(e)                         }
  def h(implicit s:List[String]) = /("h")(g,f){implicit s =>  $(g) + $(f)                         }


  def findVal(value: Int, delay: Int) = {
    Thread.sleep(delay)
    value
  }

}

/**
 *
 */
object AsyncScriptRunner {


  def main(args: Array[String]) {

    println(System.currentTimeMillis()%10000 + " Start")

    val script = new TestScript

    implicit val emptyStack = List()

    printWithTime("h = " + $(script.h))

    script.setValue("a", 3)

    printWithTime("h = " + $(script.h))

    script.invalidate("c")

    printWithTime("h = " + $(script.h))

    script.invalidate("a")
    script.invalidate("f")

    printWithTime("h = " + $(script.h))

    println(script.debug)
  }


  def $[T](futureValue: Future[T]): T = {
    Await.result(futureValue, Duration.fromNanos(50000000000L))
  }

  def printWithTime(text: => String) {
    val start = System.currentTimeMillis()
    println(text + ".......("+(System.currentTimeMillis() - start)+" mills)")
  }

}
