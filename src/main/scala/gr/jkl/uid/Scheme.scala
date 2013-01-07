package gr.jkl.uid

/** Specification of an Id implementation.
  *
  * An [[gr.jkl.uid.Id! Id]] encodes its generation time and the node which 
  * created it. Ids also include an incremental number in order to differentiate  
  * Ids produced on the same time unit. The timestamp, node and the sequence 
  * data are packed into 64-bits and the Scheme specifies the number of bits of 
  * each parameter. Additionally, a Scheme specifies the beginning of time of
  * the Id implementation, a.k.a. its epoch.
  *
  * A Scheme is required by various parts of this library so it's suggested
  * to define it implicitly.
  *
  * @param timestampBits The number of bits devoted to the Id timestamp.
  * @param nodeBits The number of bits devoted to the Id node.
  * @param sequenceBits The number of bits devoted to the Id sequence.
  * @param epoch The beginning of time, in the Unix timeline, for this Scheme
  * in milliseconds (milliseconds since Unix epoch).
  * @throws IllegalArgumentException If timestamp, node and sequence bits 
  * aren't greater than 0, their sum isn't 64 or epoch is negative.
  */
@throws(classOf[IllegalArgumentException])
@SerialVersionUID(0L)
final case class Scheme(
  timestampBits: Long, 
  nodeBits: Long, 
  sequenceBits: Long,
  epoch: Long) {

  require(timestampBits > 0 && nodeBits > 0 && sequenceBits > 0 && 
    timestampBits + nodeBits + sequenceBits == 64L, 
    "Illegal timestamp, node and sequence bits")

  require(epoch >= 0L, "Epoch is negative")

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

  /** The max timestamp, in the Unix timeline, of this Id Scheme. */
  val maxTimestamp: Long = maxValue(timestampBits) + epoch

  /** The max node of this Id Scheme. */
  val maxNode: Long = maxValue(nodeBits)
  
  /** The max sequence of this Id Scheme. */
  val maxSequence: Long = maxValue(sequenceBits)

  /** Unpacks the timestamp from an Id in Long type. */
  private[uid] def unpackTimestamp(id: Long) = 
    ((id ^ Long.MinValue) >>> timestampShift) + epoch

  /** Unpacks the node from an Id in Long type. */
  private[uid] def unpackNode(id: Long) =
    (id >> sequenceBits) & maxNode

  /** Unpacks the sequence from an Id in Long type. */
  private[uid] def unpackSequence(id: Long) = id & maxSequence

  /** Creates a partial Id in Long type with the given node. */
  private[uid] def packNode(node: Long) = node << sequenceBits

  /** Creates a partial Id in Long type with the given timestamp. */
  private[uid] def packTimestamp(timestamp: Long) = 
    ((timestamp - epoch) << timestampShift) ^ Long.MinValue

  /** The number of bites the timestamp is shifted. */
  private[uid] val timestampShift = nodeBits + sequenceBits
}
