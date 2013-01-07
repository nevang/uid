package gr.jkl.uid

/** Value class of 64-bit Ids with underlying type of Long.
  *
  * An Id instance doesn't know about the way it is structured. This depends on  
  * the [[gr.jkl.uid.Scheme Scheme]] of its [[gr.jkl.uid.Generator! Generator]].
  * So, the methods whiches extract parameters (e.g. `timestamp`, `node` and 
  * `sequence`) from an Id require an implicit Scheme. Consequently, those 
  * methods will return different results depending on the provided Scheme.
  * 
  * Generally, when an Id is constructed by a timestamp, a node and a sequence
  * a Scheme is required. On the other hand, an Id can be constructed by
  * a Long or a String without a Scheme.
  *
  * Ids are sorted the same way as Ids, as Longs and as Strings. The default 
  * Ordering, implicit in the [[gr.jkl.uid.Id$ companion object]], sorts Ids 
  * first by their timestamp, then by their node and then by their sequence.
  *
  * @param underlying The Long behind this Id.
  */
class Id(val underlying: Long) extends AnyVal  {
  
  /** Extracts the timestamp of this Id. */
  def timestamp(implicit scheme: Scheme) = scheme.unpackTimestamp(underlying)
  
  /** Extracts the id of the node generated this Id. */
  def node(implicit scheme: Scheme) = scheme.unpackNode(underlying)
  
  /** Extracts the sequence of this Id. */
  def sequence(implicit scheme: Scheme) = scheme.unpackSequence(underlying)

  /** Returns true if this Id is less than the provided Id. */
  def < (that: Id) = underlying < that.underlying

   /** Returns true if this Id is greater than the provided Id. */
  def > (that: Id) = underlying > that.underlying

  /** Returns true if this Id is less than or equal to the provided Id. */
  def <= (that: Id) = underlying <= that.underlying

  /** Returns true if this Id is greater than or equal to the provided Id. */
  def >= (that: Id) = underlying >= that.underlying

  /** Returns a base64 like String representation of this Id. */
  override def toString = Codec.encode(underlying ^ Long.MinValue)

  /** Returns a shortened base64 like String representation of this Id. */
  def toShortString = Codec.shortEncode(underlying ^ Long.MinValue)

  /** Returns a tuple with the timestamp, the node and the sequence of this Id */
  def toTriple(implicit scheme: Scheme) =  
    (timestamp(scheme), node(scheme), sequence(scheme))
}

import scala.math.Ordering

/** Factory methods, extractors and Orderings. */
object Id {

  /** Creates an Id from a Long. */
  def apply(underlying: Long) = new Id(underlying)

  /** Extracts an Id from a String. */
  def unapply(str: String) = 
    Codec.decode(str).map(long => new Id(long ^ Long.MinValue))

  /** Extracts an Id from a Triple. */
  def unapply(triple: (Long, Long, Long))(implicit scheme: Scheme) = 
    create(triple._1, triple._2, triple._3)(scheme)

   /** Creates an Id with the provided node, timestamp and sequence. */
  def create(timestamp: Long, node: Long, sequence: Long)(implicit scheme: Scheme): Option[Id] =
    if (scheme.isValidTimestamp(timestamp) && scheme.isValidNode(node) && scheme.isValidSequence(sequence)) 
      Some(Id(scheme.packTimestamp(timestamp) | scheme.packNode(node) | sequence))
    else None

   /** Checks if a String represents an Id. */
  def isValid(str: String) = Codec.isValid(str)

  /** Default sorting strategy: timestamp first, node second and sequence last. */
  implicit val DefaultOrdering: Ordering[Id] = Ordering by (_.underlying)

  /** Alternative sorting strategy: timetamp first, sequence second and node last. */
  def TimeSequenceNodeOrdering(implicit scheme: Scheme): Ordering[Id] =
    Ordering by { id: Id => 
      (id.timestamp(scheme), id.sequence(scheme), id.node(scheme))
    }
}
