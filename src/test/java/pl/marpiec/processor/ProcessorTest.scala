package pl.marpiec.processor


class TestScript extends Script {

  def a = /{ 2        }("a")
  def b = /{ 3        }("b")
  def c = /{ a + b    }("c")
  def d = /{ 5        }("d")
  def e = /{ c + d    }("e")
  def f = /{ 5        }("f")
  def g = /{ c + e    }("g")
  def h = /{ g + f    }("h")

}

/**
 *
 */
object ScriptRunner {


  def main(args: Array[String]) {

    val script = new TestScript

    printWithTime("h = " + script.h)

    script.setValue("a", 3)

    printWithTime("h = " + script.h)

    script.invalidate("c")

    printWithTime("h = " + script.h)

    script.invalidate("a")
    script.invalidate("f")

    printWithTime("h = " + script.h)

    println(script.debug)
  }



  def printWithTime(text: => String) {
    val start = System.currentTimeMillis()
    println(text + ".......("+(System.currentTimeMillis() - start)+" mills)")
  }

}
