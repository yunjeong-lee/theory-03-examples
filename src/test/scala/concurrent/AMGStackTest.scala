package concurrent

import org.scalatest.{FunSpec, Matchers}

/**
  * @author Ilya Sergey
  */
class AMGStackTest extends FunSpec with Matchers {

  describe("The concurrent stack") {
    it("should exhibit LIFO properties with multiple threads") {

      val n = 500
      val s = new AMGStack[Int]
      val input1 = (1 to n).toList
      val input2 = (n + 1 to 2 * n).toList
      
      val e1 = new Pusher(s, input1)
      val e2 = new Pusher(s, input2)
      e1.start()
      e2.start()
      e1.join()
      e2.join()


      val d1 = new Popper(s, n)
      val d2 = new Popper(s, n)
      d1.start()
      d2.start()
      d1.join()
      d2.join()
      
      val result1 = d1.accumulated
      val result2 = d2.accumulated
      
      assert(result1.forall(_.isDefined))
      assert(result2.forall(_.isDefined))
      
      val out1 = result1.map(_.get)
      val out2 = result2.map(_.get)
      
      val totalInput = input1
      val proj11 = out1.filter(input1.contains(_))
      val proj12 = out1.filter(input2.contains(_))
      val proj21 = out2.filter(input1.contains(_))
      val proj22 = out2.filter(input2.contains(_))
      
      assert(isLIFO(proj11))
      assert(isLIFO(proj12))
      assert(isLIFO(proj21))
      assert(isLIFO(proj22))
    }
  }
  
  def isLIFO(l: List[Int]) = l.reverse == l.sorted

  class Pusher(val stack: AMGStack[Int], items: List[Int]) extends Thread {
    override def run() = {
      for (e <- items) {
        stack.push(e)
      }
    }
  }

  class Popper(val stack: AMGStack[Int], val iterations: Int) extends Thread {
    private var myAccumulator: List[Option[Int]] = Nil

    def accumulated: List[Option[Int]] = myAccumulator

    override def run() = {
      for (e <- 1 to iterations) {
        myAccumulator = myAccumulator ++ List(stack.pop())
      }
    }
  }

}
