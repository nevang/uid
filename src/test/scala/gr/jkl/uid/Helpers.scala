package gr.jkl.uid

import scala.util.Random
import org.scalatest.matchers.{ BePropertyMatcher, BePropertyMatchResult }

trait RandomIdParameters {

  def randomTimestampBits: Long = Random.nextInt(8) + 38L
  
  def randomSequenceBits: Long = Random.nextInt(8) + 8L

  def calculateNodeBits(tb: Long, sb: Long)=  64L - tb - sb

  def randomEpoch(tb: Long): Long = 
    random(math.max((System.currentTimeMillis + 1000000) - maxValue(tb), 
      10000000), System.currentTimeMillis - 1000000)

  val timestampBits: Long = randomTimestampBits
  
  val sequenceBits: Long = randomSequenceBits

  val nodeBits = calculateNodeBits(timestampBits, sequenceBits)

  val epoch = randomEpoch(timestampBits)
  
  def maxValue(bits: Long) = (-1L ^ (-1L << bits))
  
  def random(bits: Long) = Random.nextLong & maxValue(bits)

  def randomNode = random(nodeBits)

  def randomSequence = random(sequenceBits)

  def randomTimestamp = random(timestampBits) + epoch

  def random(from: Long, to: Long) =
    math.round(Random.nextDouble * (to - from)).toLong + from
}

trait CustomMatchers {

  class IdValidPropertyMatcher extends BePropertyMatcher[String] {
    def apply(left: String) = BePropertyMatchResult(Id.isValid(left), "ID")
  }

  val ID = new IdValidPropertyMatcher
}

class FakeClock {
  
  def currentTimeMillis = 
    if (isFixed) fixed else System.currentTimeMillis + offset

  def setFixedTime(t: Long) { 
    isFixed = true
    fixed = t 
  }

  def setOffset(o: Long) { 
    isFixed = false
    offset = o 
  }

  def reset {
    isFixed = false
    fixed = 0L
    offset = 0L
  }

  private[this] var isFixed = false

  private[this] var fixed = 0L

  private[this] var offset = 0L  
}
