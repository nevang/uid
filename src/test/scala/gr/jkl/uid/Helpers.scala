package gr.jkl.uid
import scala.util.Random

trait RandomIdParameters {
  
  val timestampBits: Long = Random.nextInt(8) + 38L 
  
  val sequenceBits: Long = Random.nextInt(8) + 8L        

  val nodeBits = 64L - timestampBits - sequenceBits

  val epoch = 1351728000000L
  
  def maxValue(bits: Long) = (-1L ^ (-1L << bits))
  
  def random(bits: Long) = Random.nextLong & maxValue(bits)

  def randomNode = random(nodeBits)

  def randomSequence = random(sequenceBits)

  def randomTimestamp = random(timestampBits) + epoch
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