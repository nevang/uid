package gr.jkl.uid

/** Specification of an Id implementation.
  *
  * An Id encodes its generation time and the node which  created it. An 
  * incremental number is also included in order to differntiate Ids produced on 
  * the same millisecond. The timestamp, node and the sequence data are packed 
  * into 64-bits and the Scheme specifies the number of bits of each parameter. 
  * Additionaly, a Scheme specifies the Ids epoch.
  *
  * A Scheme is required by various parts of this library so it's suggested
  * to define it implicitly.
  *
  * @param timestampBits The number of bits devoted for the Ids timestamp.
  * @param nodeBits The number of bits devoted for the Ids node.
  * @param sequenceBits The number of bits devoted for the Ids sequence.
  * @param epoch The beginning timestamp of this Id Scheme.
  * @throws IllegalArgumentException If timestamp, node and sequence bits 
  * aren't greater than 0, their sum isn't 64 or epoch is negative.
  */
@throws(classOf[IllegalArgumentException])
final class Scheme(
  val timestampBits: Long, 
  val nodeBits: Long, 
  val sequenceBits: Long,
  val epoch: Long) {

  require(timestampBits > 0 && nodeBits > 0 && sequenceBits > 0 && 
    timestampBits + nodeBits + sequenceBits == 64L, 
    "Timestamp, Node and Sequence bits must be greater than 0 with sum equal to 64")

  require(epoch >= 0L, "Epoch must not be negative")

  require((Long.MaxValue - epoch) >= maxValue(timestampBits), 
    "Scheme max timestamp comes after end of time")

  /** Checks if a timestamp is valid for this Scheme. */
  def isValidTimestamp(timestamp: Long) = 
    timestamp >= epoch && timestamp <= maxTimestamp

  /** Checks if a node is valid for this Scheme. */
  def isValidNode(node: Long) = node >= 0 && node <= maxNode

  /** Checks if a sequence is valid for this Scheme. */
  def isValidSequence(sequence: Long) = 
    sequence >= 0 && sequence <= maxSequence

  /** The max timestamp of this Id Scheme. */
  val maxTimestamp: Long = maxValue(timestampBits) + epoch

  /** The max node of this Id Scheme. */
  val maxNode: Long = maxValue(nodeBits)
  
  /** The max sequence of this Id Scheme. */
  val maxSequence: Long = maxValue(sequenceBits)

  /** Unpacks the timestamp from an Id in Long type. */
  private[uid] def unpackTimestamp(long: Long) = 
    ((long ^ Long.MinValue) >>> timestampShift) + epoch

  /** Unpacks the node from an Id in Long type. */
  private[uid] def unpackNode(long: Long) =
    (long >> sequenceBits) & maxNode

  /** Unpacks the sequence from an Id in Long type. */
  private[uid] def unpackSequence(long: Long) = long & maxSequence

  /** Creates a partial Id in Long type with the given node. */
  private[uid] def packNode(node: Long) = node << sequenceBits

  /** Creates a partial Id in Long type with the given timestamp. */
  private[uid] def packTimestamp(timestamp: Long) = 
    ((timestamp - epoch) << timestampShift) ^ Long.MinValue

  /** The number of bites the timestamp is shifted. */
  private[uid] val timestampShift = nodeBits + sequenceBits

  /** Returns this Scheme as a String */
  override def toString = 
    "Scheme(" + timestampBits + ", " + nodeBits + ", " + sequenceBits + ", " + epoch + ")"
}
