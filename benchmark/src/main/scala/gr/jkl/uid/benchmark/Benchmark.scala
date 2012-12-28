package gr.jkl.uid.benchmark

import com.google.caliper.Param
import gr.jkl.uid.{ Generator, Scheme }
import java.util.concurrent.atomic.AtomicLong
import java.util.UUID
import com.eaio.uuid.{UUID => EAIOUUID}

class Benchmark extends SimpleScalaBenchmark {
  
  implicit val scheme = new Scheme(44, 5, 15, 1351728000000L)
  var generator: Generator = _
  var atomicLong: AtomicLong = _
  
  override def setUp() { 
    generator = Generator(0L)
    atomicLong = new AtomicLong
  }
  
  def timeGenerator(reps: Int) = repeat(reps) {
    generator.newId.underlying // else it gets packed
  }

  def timeAtomicLong(reps: Int) = repeat(reps) {
    atomicLong.getAndIncrement
  }

  def timeJavaUUID(reps: Int) = repeat(reps) {
    UUID.randomUUID
  }

  def timeeaioUUID(reps: Int) = repeat(reps) {
    new EAIOUUID
  }
  
  override def tearDown() {
    // clean up after yourself if required
  }
  
}
