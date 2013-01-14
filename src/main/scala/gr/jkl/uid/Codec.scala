package gr.jkl.uid

import java.lang.{ StringBuilder => JStringBuilder }
import scala.collection.immutable.{ TreeMap, BitSet }

/** Base64 like encoding algorithm. */
private[uid] object Codec {

  /** Sorted url-safe Chars.
    *
    * Encoding Chars, safe for URIs. Characters are sorted. Consequently
    * unsigned Longs will be sorted on the same way.
    *
    * == RFC 2396 ==
    * unreserved = alphanum | mark
    * mark       = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
    *
    * == RFC 1808 & RFC 1738 ==
    * unreserved  = alpha | digit | safe | extra
    * safe        = "$" | "-" | "_" | "." | "+"
    * extra       = "!" | "*" | "'" | "(" | ")" | ","
    *
    * @see [[ftp://ftp.funet.fi/pub/doc/rfc/rfc1738.txt RFC 1738]]
    * @see [[ftp://ftp.funet.fi/pub/doc/rfc/rfc1808.txt RFC 1808]]
    * @see [[ftp://ftp.funet.fi/pub/doc/rfc/rfc2396.txt RFC 2396]]
    */
  private[this] val chars =
    "-0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz"

  /** Index as StringBuilder. */
  private[this] val index = new JStringBuilder(chars)

  /** Map of chars and their corresponding values. */
  private[this] val reverseIndex =
    TreeMap(chars zip (0 until chars.length).map(_.toLong): _*)

  /** Set of the Chars encoded bytes as Ints. */
  private[this] val byteSet = BitSet(chars.map(_.toInt): _*)

  /** 6-bit zero as Char. */
  private[this] val zeroChar = chars.head

  /** 64-bit unsigned zero as String. */
  private[this] val zeroString = zeroChar.toString * 11

  /** Returns a StringBuilder representing 0. */
  private[this] def zeroSB = new JStringBuilder(zeroString)

  /** Encodes the given Long. */
  private[uid] def encode(value: Long): String = {
    val sb = new JStringBuilder(11)
    sb.setLength(11)
    sb.setCharAt(10, index.charAt((value & 63L).toInt))
    sb.setCharAt(9, index.charAt((value >>> 6 & 63L).toInt))
    sb.setCharAt(8, index.charAt((value >>> 12 & 63L).toInt))
    sb.setCharAt(7, index.charAt((value >>> 18 & 63L).toInt))
    sb.setCharAt(6, index.charAt((value >>> 24 & 63L).toInt))
    sb.setCharAt(5, index.charAt((value >>> 30 & 63L).toInt))
    sb.setCharAt(4, index.charAt((value >>> 36 & 63L).toInt))
    sb.setCharAt(3, index.charAt((value >>> 42 & 63L).toInt))
    sb.setCharAt(2, index.charAt((value >>> 48 & 63L).toInt))
    sb.setCharAt(1, index.charAt((value >>> 54 & 63L).toInt))
    sb.setCharAt(0, index.charAt((value >>> 60).toInt))
    sb.toString
  }

  /** Encodes the given Long omitting 0s from the beginning. */
  private[uid] def shortEncode(value: Long): String =
    if (value == 0L) zeroChar.toString
    else encode(value).dropWhile(_ == zeroChar)

  /** Checks if a String can be decoded to Long. */
  private[uid] def isValid(value: String): Boolean =
    value.length < 12 && value.forall(c => byteSet.contains(c.toInt))

  /** Decodes a String, shortened or not, to Long. */
  private[uid] def decode(value: String): Option[Long] =
    if (isValid(value)) Some(decodeFull(
      if (value.length == 11) value
      else zeroSB.replace(11 - value.length, 11, value).toString))
    else None

  /** Decodes a String with 11 characters to Long. */
  private[this] def decodeFull(value: String): Long =
    (reverseIndex(value(0)) << 60) |
      (reverseIndex(value(1)) << 54) |
      (reverseIndex(value(2)) << 48) |
      (reverseIndex(value(3)) << 42) |
      (reverseIndex(value(4)) << 36) |
      (reverseIndex(value(5)) << 30) |
      (reverseIndex(value(6)) << 24) |
      (reverseIndex(value(7)) << 18) |
      (reverseIndex(value(8)) << 12) |
      (reverseIndex(value(9)) << 6) |
      reverseIndex(value(10))
}
