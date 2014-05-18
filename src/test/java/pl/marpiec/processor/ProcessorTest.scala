package pl.marpiec.processor


class TestScript extends Script {

  def a = /("a"){  2        }
  def b = /("b"){  3        }
  def c = /("c"){  a + b    }
  def d = /("d"){  5        }
  def e = /("e"){  c + d    }
  def f = /("f"){  5        }
  def g = /("g"){  c + e    }
  def h = /("h"){  g + f    }

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
