package gr.jkl.uid

import scala.concurrent.stm.Ref 

/** A thread safe Id Generator. */
class Generator(val node: Long)(implicit val scheme: Scheme) {
  require(node >= 0L && node <= scheme.maxNode)

  /** Generates a new Id. */
  def newId = pack(nextTimeAndSequence)

  /** Gets the current time (can be overriden for testing). */
  protected def currentTimeMillis = System.currentTimeMillis

  private[this] val pack = (scheme.pack(node)(_, _)).tupled

  private[this] val timestampAndSequence = Ref(-1L, -1L)

  private[this] def nextTimeAndSequence =
    // A single operation atomic block, first updates the Ref, then gets the pair
    timestampAndSequence.single transformAndGet { 
    val now = currentTimeMillis 
    _ match {
      // Fresh millisecond: update time and reset sequence
      case (t, _) if (t < now) => (now, 0L)    
      // Sequence hasn't exceeded limit, same millisecond or clock back in 
      // time: keep previous time and advance sequence
      case (t, s) if (s < scheme.maxSequence) => (t, s + 1L) 
      // Sequence exceeded limit: move one millisecond forward and reset sequence
      case (t, _) => (t + 1L, 0L)
    }
  }
}