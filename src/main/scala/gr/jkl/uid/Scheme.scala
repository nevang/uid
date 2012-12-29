package gr.jkl.uid

/** The scheme of the Ids.
  *
  * @param timestampBits The number of bits devoted for the ids timestamp.
  * @param nodeBits The number of bits devoted for the ids node.
  * @param sequenceBits The number of bits devoted for the ids sequence.
  * @param epoch The beginning timestamp of this id scheme.
  * @throws IllegalArgumentException If timestamp, node ande sequence bits 
  * aren't greater than 0, their sum isn't 64 or epoch is negative.
  */
@throws(classOf[IllegalArgumentException])
class Scheme(
  val timestampBits: Long, 
  val nodeBits: Long, 
  val sequenceBits: Long,
  val epoch: Long) {

  require(timestampBits > 0 && nodeBits > 0 && sequenceBits > 0 && 
    timestampBits + nodeBits + sequenceBits == 64L, 
    "Timestamp, Node and Sequence bits must be greater than 0 with sum equal to 64")

  require(epoch >= 0L, "Epoch must not be negative")

  /** The max timestamp of this Id Scheme. */
  final val maxTimestamp: Long = maxValue(timestampBits) + epoch

  /** The max node of this Id Scheme. */
  final val maxNode: Long = maxValue(nodeBits)
  
  /** The max sequence of this Id Scheme. */
  final val maxSequence: Long = maxValue(sequenceBits)

  /** Extracts the timestamp of an Id. */
  final def getTimestamp(id: Id): Long = unpackTimestamp(id.underlying)

  /** Extracts the node of an Id. */
  final def getNode(id: Id): Long = unpackNode(id.underlying)
    
  /** Extracts the sequence of an Id. */
  final def getSequence(id: Id): Long = unpackSequence(id.underlying)

  /** Creates an Id with the provided node, timestamp and sequence. */
  @throws(classOf[IllegalArgumentException])
  final def create(timestamp: Long, node: Long, sequence: Long): Id = {
    require(node >= 0 && node <= maxNode, "Node out of scheme's limits.")
    require(timestamp >= epoch && timestamp <= maxTimestamp, "Timestamp out of scheme's limits.")
    require(sequence >= 0 && sequence <= maxSequence, "Sequence out of scheme's limits.")
    new Id(packTimestamp(timestamp) | packNode(node) | sequence)
  }

  /** Unpacks the timestamp from an Id in Long type. */
  private[uid] final def unpackTimestamp(long: Long): Long = 
    ((long ^ Long.MinValue) >>> timestampShift) + epoch

  /** Unpacks the node from an Id in Long type. */
  private[uid] final def unpackNode(long: Long): Long =
    (long >> sequenceBits) & maxNode

  /** Unpacks the sequence from an Id in Long type. */
  private[uid] final def unpackSequence(long: Long): Long = long & maxSequence

  /** Creates a partial Id in Long type with the given node. */
  private[uid] final def packNode(node: Long) = node << sequenceBits

  /** Creates a partial Id in Long type with the given timestamp. */
  private[uid] final def packTimestamp(timestamp: Long) = 
    ((timestamp - epoch) << timestampShift) ^ Long.MinValue

  /** The number of bites the timestamp is shifted. */
  private[this] val timestampShift = nodeBits + sequenceBits
 
  /** Calculates the max number for the provided bits. */
  private[this] def maxValue(bits: Long): Long = (-1L ^ (-1L << bits))  

}
