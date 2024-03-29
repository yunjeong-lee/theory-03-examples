package concurrent

import java.util.concurrent.atomic.{AtomicInteger, AtomicReferenceArray}

import util.ThreadID

import scala.reflect.ClassTag

/**
  * @author Ilya Sergey
  */
class AMGStack[T: ClassTag] {
  private val CAPACITY: Int = 1024
  private val items = new AtomicReferenceArray[Option[T]](CAPACITY)
  private val tail = new AtomicInteger(0)

  def push(x: T): Unit = {
    val i = ThreadID.get
    val j = tail.getAndIncrement()
    items.set(j, Some(x))
  }

  def pop(): Option[T] = {
    val i = ThreadID.get
    val range = tail.get()
    for (i <- range - 1 until -1 by -1) {
      val value = items.getAndSet(i, None)
      if (value.isDefined) {
        return value
      }
    }
    None
  }

}
