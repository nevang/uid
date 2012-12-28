package gr.jkl.uid

/** A thread safe Id Generator. */
@throws(classOf[IllegalArgumentException])
class Generator(val node: Long, val scheme: Scheme) {
  require(node >= 0L && node <= scheme.maxNode)

  /** Generates a new Id. */
  final def newId = new Id(generateLong)

  /** Gets the current time. */
  protected def currentTimeMillis = System.currentTimeMillis

  private[this] var lastLong: Long = partialFirstMillisLong ^ Long.MinValue

  /** The first Id of a ms for the provided node without the timestamp part. */
  private[this] val partialFirstMillisLong = scheme.packNode(node) // | 0L

  /** Generates a Long for the creation of an Id. */
  private[this] def generateLong = synchronized {
    lastLong = nextLong(currentTimeMillis, lastLong)
    lastLong
  }
    
  /** Calculates the next Long based on a timestamp and the previous Long. */
  private[this] def nextLong(timestamp: Long, previous: Long) = {
    val currentPackedTimestamp = scheme.packTimestamp(timestamp)
    val lastUnpackedSequence = scheme.unpackSequence(previous)
    val nextPackedTimestamp = scheme.packTimestamp(
      scheme.unpackTimestamp(previous) + 1L)
    
      if (currentPackedTimestamp > previous)
        partialFirstMillisLong | currentPackedTimestamp
      else if (lastUnpackedSequence < scheme.maxSequence)
       previous + 1L
      else 
        partialFirstMillisLong | nextPackedTimestamp
  }

}

/** Factory for [[gr.jkl.uid.Generator Generator]] instances. */
object Generator {

  /** Creates a [[gr.jkl.uid.Generator Generator]] instance. */
  @throws(classOf[IllegalArgumentException])
  def apply(node: Long)(implicit scheme: Scheme): Generator = 
    new GeneratorImpl(node, scheme)
}

/** Uid Generator exception. */
case class GeneratorException(message: String) extends RuntimeException(message)

private[uid] final class GeneratorImpl(n: Long, s: Scheme) extends Generator(n, s)