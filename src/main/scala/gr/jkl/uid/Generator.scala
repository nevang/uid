package gr.jkl.uid

/** Generates Ids based on a Scheme for a specific node.
  *
  * `newId` generates unique [[gr.jkl.uid.Id! Ids]] while `newLong` generates
  * unique Longs. As Id is a
  * [[http://docs.scala-lang.org/overviews/core/value-classes.html Value Class]]
  * both methods and data types perform equally.
  *
  * There is a thread-safe non-blocking Generator's implementation and you can
  * construct an instance of it using the [[gr.jkl.uid.Generator$ companion object]]
  * factory methods.
  */
trait Generator {

  /** Generates a new uinque Long.
    *
    * @throws GeneratorException If a Long can't be produced inside Scheme's limits.
    */
  @throws(classOf[GeneratorException])
  def newLong: Long

  /** Generates a new unique Id.
    *
    * @throws GeneratorException If an Id can't be produced inside Scheme's limits.
    */
  @throws(classOf[GeneratorException])
  final def newId: Id = new Id(newLong)

  /** The node of this Generator. */
  def node: Long

  /** The Scheme which structures this Generator's Ids. */
  def scheme: Scheme

  /** Returns this Generator as a String. */
  override def toString =
    s"Generator($node, $scheme)"
}

/** Factory for Generator instances. */
object Generator {

  /** Constructs a Generator instance for the given node and Scheme.
    *
    * @param node The node of the Generator.
    * @param scheme The scheme which will structure the Generator's Ids.
    * @throws IllegalArgumentException If node is out of Scheme's limits.
    */
  @throws(classOf[IllegalArgumentException])
  def apply(node: Long)(implicit scheme: Scheme): Generator = {
    require(scheme.isValidNode(node), "Node out of scheme's limits.")
    new GeneratorImpl(node, scheme, -1L, 0L)
  }

  /** Constructs a Generator instance for the given node and Scheme.
    *
    * The returned Generator will generate Ids that come after the Id
    * specified by the given timestamp and sequence.
    *
    * @note Providing the timestamp and the sequence of the last Id generated
    * for the specific node is a safety mechanism which will protect you in the
    * case of a system clock going back in time when the application is
    * restarted.
    *
    * @param node The node of the Generator.
    * @param lastTimestamp The timestamp of the last Id of this node.
    * @param lastSequence The sequence of the last Id of this node.
    * @param scheme The scheme which will structure the Generator's Ids.
    * @throws IllegalArgumentException If node, timestamp or sequence are out of
    * Scheme's limits.
    */
  @throws(classOf[IllegalArgumentException])
  def apply(node: Long, lastTimestamp: Long, lastSequence: Long)(implicit scheme: Scheme): Generator = {
    require(scheme.isValidNode(node), "Node out of scheme's limits.")
    require(scheme.isValidTimestamp(lastTimestamp), "Timestamp out of scheme's limits.")
    require(scheme.isValidSequence(lastSequence), "Sequence out of scheme's limits.")
    new GeneratorImpl(node, scheme, lastTimestamp, lastSequence)
  }

  /** Constructs a Generator instance for the given node and Scheme.
    *
    * The returned Generator will generate Ids that come after the provided Id.
    *
    * @note Providing the last Id generated for the specific node is a safety
    * mechanism which will protect you in the case of a system clock going back
    * in time when the application is restarted.
    *
    * @param node The node of the Generator.
    * @param lastId The last Id produced for the provided node.
    * @param scheme The scheme which will structure the Generator's Ids.
    * @throws IllegalArgumentException If node, timestamp or sequence are out of
    * Scheme's limits. Exception is also thrown if provided node doesn't match
    * the node of the provided Id.
    */
  @throws(classOf[IllegalArgumentException])
  def apply(node: Long, lastId: Id)(implicit scheme: Scheme): Generator = {
    require(node == lastId.node, "Node and last Id's node dont't match")
    apply(node, lastId.timestamp, lastId.sequence)(scheme)
  }
}

/** Generator exception. */
case class GeneratorException(message: String) extends RuntimeException(message)

import java.util.concurrent.atomic.AtomicLong
import scala.annotation.tailrec

/** A thread-safe, non-blocking Id Generator.
  *
  * @note Generator uses an AtomicLong to store the last generated Id. This Id
  * is stored in an internal format, which contains only the timestamp and the
  * sequence.
  */
private[uid] abstract class AbstractGenerator(
  final val node: Long,
  final val scheme: Scheme,
  lastTimestamp: Long,
  lastSequence: Long)
    extends Generator {

  /** Generates a new unique Long. */
  final def newLong: Long = generateLong

  /** Gets the current time. */
  private[uid] def currentTimeMillis: Long

  /** Node part of the this Generator's Ids. */
  private[this] val packedNode = scheme.packNode(node)

  /** Shifted mask of Scheme's timestamp. */
  private[this] val timestampMaskShifted =
    maxValue(scheme.timestampBits) << scheme.timestampShift

  /** Timestamp mask for the internal encoding. */
  private[this] val internalTimestampMask = maxValue(64L - scheme.sequenceBits)

  /** Packs the timstamp using the interal encoding. */
  private[this] def internalTimestampPack(timestamp: Long) =
    // if timestamp is much larger than scheme's max time it will get negative
    ((timestamp - scheme.epoch) & internalTimestampMask) << scheme.sequenceBits

  /** The max scheme's Id, in Long, translated into the internal encoding. */
  private[this] val internalMaxLong =
    internalTimestampPack(scheme.maxTimestamp) | scheme.maxSequence

  /** The last long generated by this Generator encoded. */
  private[this] val internalLastLong =
    new AtomicLong((internalTimestampPack(lastTimestamp) | lastSequence))

  //** Generates a Long for new Id. */
  private[this] def generateLong = {
    val currentTimestamp = currentTimeMillis
    if (currentTimestamp < scheme.epoch | currentTimestamp > scheme.maxTimestamp) {
      throw GeneratorException("Time out of scheme's limits.")
    }
    val nextLongInternal = getNextInternalLong(internalTimestampPack(currentTimestamp))
    if (nextLongInternal > internalMaxLong) { // it may be incremented after limit
      throw GeneratorException("Time out of scheme's limits.")
    }
    (((nextLongInternal << scheme.nodeBits) & timestampMaskShifted) |
      (nextLongInternal & scheme.maxSequence) | packedNode) ^ Long.MinValue
  }

  /** Calculates the next encoded Long and updates the atomic variable. */
  @tailrec
  private[this] def getNextInternalLong(internalPackedTimestamp: Long): Long = {
    val tempLong = internalLastLong.incrementAndGet
    if (tempLong >= internalPackedTimestamp) tempLong // time hasn't proceeded
    else if (internalLastLong.compareAndSet(tempLong, internalPackedTimestamp)) // time proceeded
      internalPackedTimestamp
    else getNextInternalLong(internalPackedTimestamp) // atomic update failed
  }
}

/** Implementation */
private[uid] final class GeneratorImpl(n: Long, sc: Scheme, t: Long, s: Long)
    extends AbstractGenerator(n, sc, t, s) {
  def currentTimeMillis = System.currentTimeMillis
}
