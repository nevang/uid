package gr.jkl.uid

/** A value class representing a 64-bit Id. */
class Id(val underlying: Long) extends AnyVal {
  
  /** Extracts the timestamp of this Id. */
  def timestamp(implicit scheme: Scheme) = scheme.unpackTimestamp(this)
  
  /** Extracts the id of the node generated this Id. */
  def node(implicit scheme: Scheme) = scheme.unpackNode(this)
  
  /** Extracts the sequence of this Id. */
  def sequence(implicit scheme: Scheme) = scheme.unpackSequence(this)
}