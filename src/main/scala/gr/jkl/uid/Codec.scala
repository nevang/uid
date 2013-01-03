package gr.jkl.uid

import java.lang.{ StringBuilder => JStringBuilder }
import scala.collection.immutable.{TreeMap, BitSet}

/** Base64 like encoding algorithm. */
private[uid] object Codec {
  
  /** Sorted set of the url safe base 64 chars. */
  private[this] val lexicon = 
    "-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz"

  /** Lexicon as StringBuilder. */
  private[this] val lexiconSB = new JStringBuilder(lexicon)

  /** Map of chars and their corresponding values. */
  private[this] val lexiconMap = 
    TreeMap(lexicon.map(c => (c, lexicon.indexOf(c).toLong)) :_*) 

  /** Set of the int representations of the chars. */
  private[this] val lexiconCharSet = BitSet(lexicon.map(_.toInt) :_*)

  /** Returns a ZeroBuilder representing the 0. */
  private[this] def zeroSB = new JStringBuilder("-----------")

  /** Encodes the given Long. */
  private[uid] def encode(long: Long): String = {
    val sb = new JStringBuilder(11)
    sb.setLength(11)
    sb.setCharAt(10, lexiconSB.charAt((long        & 63L).toInt))
    sb.setCharAt( 9, lexiconSB.charAt((long >>>  6 & 63L).toInt))
    sb.setCharAt( 8, lexiconSB.charAt((long >>> 12 & 63L).toInt))
    sb.setCharAt( 7, lexiconSB.charAt((long >>> 18 & 63L).toInt))
    sb.setCharAt( 6, lexiconSB.charAt((long >>> 24 & 63L).toInt))
    sb.setCharAt( 5, lexiconSB.charAt((long >>> 30 & 63L).toInt))
    sb.setCharAt( 4, lexiconSB.charAt((long >>> 36 & 63L).toInt))
    sb.setCharAt( 3, lexiconSB.charAt((long >>> 42 & 63L).toInt))
    sb.setCharAt( 2, lexiconSB.charAt((long >>> 48 & 63L).toInt))
    sb.setCharAt( 1, lexiconSB.charAt((long >>> 54 & 63L).toInt))
    sb.setCharAt( 0, lexiconSB.charAt((long >>> 60      ).toInt))
    sb.toString
  }

  /** Encodes the given Long omitting 0s from the beginning. */
  private[uid] def shortEncode(long: Long): String = 
    if(long == 0L) "-"
    else encode(long).dropWhile(_ == '-')

  /** Checks if a String can be decoded to Long. */
  private[uid] def isValid(str: String): Boolean = 
    str.length < 12 && str.forall(c => lexiconCharSet.contains(c.toInt))

  /** Decodes a String, shortened or not, to Long. */
  private[uid] def decode(str: String): Option[Long] = 
    if (isValid(str)) Some(decodeFull(
      if (str.length == 11) str
      else zeroSB.replace(11 - str.length, 11, str).toString))
    else None

  /** Decodes a String with 11 characters to Long. */
  private[this] def decodeFull(str: String): Long =
    (lexiconMap(str( 0)) << 60) |
    (lexiconMap(str( 1)) << 54) |
    (lexiconMap(str( 2)) << 48) |
    (lexiconMap(str( 3)) << 42) |
    (lexiconMap(str( 4)) << 36) |
    (lexiconMap(str( 5)) << 30) |
    (lexiconMap(str( 6)) << 24) |
    (lexiconMap(str( 7)) << 18) |
    (lexiconMap(str( 8)) << 12) |
    (lexiconMap(str( 9)) <<  6) |
     lexiconMap(str(10)) 
}
