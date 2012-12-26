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
  final def unpackTimestamp(id: Id): Long = 
    ((id.underlying ^ Long.MinValue) >>> (nodeBits + sequenceBits)) + epoch

  /** Extracts the node of an Id. */
  final def unpackNode(id: Id): Long = 
    (id.underlying >> sequenceBits) & maxNode

  /** Extracts the sequence of an Id. */
  final def unpackSequence(id: Id): Long = id.underlying & maxSequence

  /** Creates an Id with the provided node, timestamp and sequence.
    * @todo Add exception.
    */
  final def pack(node: Long)(timestamp: Long, sequence: Long): Id =
    if (timestamp <= maxTimestamp && timestamp >= epoch)
      new Id(compose(packNode(node))(timestamp, sequence))
    else ???

  private[this] val timestampLeftShift = nodeBits + sequenceBits

  private[this] def packNode(node: Long) = (node & maxNode) << sequenceBits

  private[this] def compose(nodePartition: Long)(timestamp: Long, sequence: Long) =
    (((timestamp - epoch) << timestampLeftShift) | nodePartition | 
      (sequence & maxSequence)) ^ Long.MinValue

  private[this] def maxValue(bits: Long): Long = (-1L ^ (-1L << bits))  

}