package gr.jkl

/** uid is a library for the generation and handling of unique 64-bit Ids.
  * 
  * Each [[gr.jkl.uid.Id Id]] encodes a timestamp, a node and a sequence. 
  * Specific implementations are achieved by defining a 
  * [[gr.jkl.uid.Scheme Scheme]]. Id creation is handled by a 
  * [[gr.jkl.uid.Generator Generator]].
  *
  * @example {{{
  * // Define an implicit Scheme
  * implicit val idScheme = new Scheme(42, 12, 10 , 1351728000000L)
  *
  * // Construct a Generator for the node 0
  * val idGenerator = Generator(0L) 
  *
  * // Get a new unique Id
  * val id = idGenerator.newId
  * }}}
  */
package object uid {
  
  /** Calculates the max number for the provided bits. */
  private[uid] def maxValue(bits: Long) = (-1L ^ (-1L << bits))
}
