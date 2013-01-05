package gr.jkl.uid

import org.scalatest.{ FeatureSpec, GivenWhenThen }
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.concurrent.Conductors

class GeneratorSpec extends FeatureSpec 
  with GivenWhenThen 
  with ShouldMatchers 
  with Conductors
  with RandomIdParameters {

  implicit val scheme = new Scheme(timestampBits, nodeBits, sequenceBits, epoch)

  feature("Generator generates IDs depended on time, node and sequence") {
    
    scenario("IDs are produced by " + (scheme.maxNode + 1) + " nodes") {
      given((scheme.maxNode + 1) + " unique nodes")
      val idGenerators = (0 to scheme.maxNode.toInt).map(n => (n -> Generator(n)))
      
      when("they generate IDs")
      val ids = idGenerators map { t =>
        val (node, idGenerator) = t
        (node -> idGenerator.newId)
      }

      then("each ID should include the related node")
      ids foreach { t =>
        val (node, id) = t
        id.node should equal (node)
      }
    }

    scenario("the number of IDs produced on one millisecond exceeds limit") {
      given("an ID Generator operating on one millisecond")
      val aMillisecond: Long = randomTimestamp
      val idGenerator = new AbstractGenerator(randomNode, scheme, -1L, 0L) {
        val currentTimeMillis = aMillisecond
      }

      when("it generates IDs up to the limit (" + (scheme.maxSequence + 1) + ")")
      val ids = (0 to scheme.maxSequence.toInt).map { i =>
        (i -> idGenerator.newId)
      }

      then("its ID should include a sequential number")
      ids foreach { t =>
        val (idx, id) = t 
        id.sequence should equal (idx)
      }

      when("it generates the next ID")
      val id = idGenerator.newId

      then("it should have sequence equal to 0")
      id.sequence should equal (0L)

      and("its timestamp should be increased by 1 millisecond")
      id.timestamp should equal (aMillisecond + 1L)
    }

    scenario("IDs are produced on various momments in time") {
      val iterations = 100000

      given("various momments in time")
      val clock = new FakeClock  
      val timestamps = epoch :: List.fill(iterations)(randomTimestamp).sorted :::
        List(scheme.maxTimestamp)
      val idGenerator = new AbstractGenerator(randomNode, scheme, -1L, 0L) {
          def currentTimeMillis = clock.currentTimeMillis
        }

      when("a Generator is producing an ID")
      val ids = timestamps map {  t =>
        clock.setFixedTime(t)
        (t -> idGenerator.newId)
      }

      then("each ID should include the related timestamp")
      ids foreach { t =>
        val (timestamp, id) = t 
        id.timestamp should equal (timestamp)
      }
    }
  }

  feature("Generator produces unique IDs under any circumstances") {

    scenario("IDs are produced on multiple threads") {
      val conductor = new Conductor
      import conductor._
      val threads = 6
      val idsPerThread = 50000
      val idsCount = threads * idsPerThread
      class IdHolder {
        var list: List[Id] = Nil
      } 
      val idHolders = List.fill(threads)(new IdHolder)

      given("an ID Generator for a random node")
      val node = randomNode
      val idGenerator = Generator(node)
      
      when(idsCount + " IDs are produced by " + threads + " threads")
      def createIds(idHolder: IdHolder) {
        idHolder.list = List.fill(idsPerThread)(idGenerator.newId)
      } 
      val start = System.currentTimeMillis 
      idHolders.foreach {
        i => thread(createIds(i))
      }

      whenFinished {
        val stop = System.currentTimeMillis
        val time = stop - start
        val ids = idHolders.foldLeft(List.empty[Id])(_ ::: _.list).toSet
        then("all IDs should be unique")
        ids should have size (idsCount)
        and("all IDs should contain the correct data")
        ids foreach { id =>
          id.node should equal (node)
        }
        info("Info: Generated " + idsCount + " unique Ids in " + time + " milliseconds")
      }
    }

    scenario("The clock goes back in time") {
      given("an ID produced by a Generator")
      val clock = new FakeClock
      val idGenerator = new AbstractGenerator(randomNode, scheme, -1L, 0L) {
        def currentTimeMillis = clock.currentTimeMillis
      }
      val id = idGenerator.newId
      
      when("clock goes back in time")
      clock.setOffset(- util.Random.nextInt(10000) - 20000)

      then("the next ID should be produced with the timestamp of the previous ID")
      val nextId = idGenerator.newId
      nextId.timestamp should equal (id.timestamp)

      and("its sequence should be increased by 1")
      nextId.sequence should equal (id.sequence + 1)
    }

     scenario("Generator is restarted while clock goes back in time") {
      given("an Id generator instantiated with the timestamp, and the sequence of the last Id while clock goes back in time")
      val lastTimestamp = randomTimestamp
      val lastSequence = random(0, scheme.maxSequence - 1)
      val clock = new FakeClock
      val idGenerator = new AbstractGenerator(randomNode, scheme, lastTimestamp, lastSequence) {
        def currentTimeMillis = clock.currentTimeMillis
      }
      clock.setFixedTime(lastTimestamp - 100L)

      when("an Id is generated")
      val nextId = idGenerator.newId

      then("the Id should be produced with the timestamp of the last Id")
      nextId.timestamp should equal (lastTimestamp)

      and("its sequence should be increased by 1")
      nextId.sequence should equal (lastSequence + 1)
    }
  }

  feature("Generator generates IDs only under the provided Scheme's limits") {

    scenario("Generator is called before Scheme's epoch") {
      given("an ID Generator")
      val clock = new FakeClock
      val idGenerator = new AbstractGenerator(randomNode, scheme, -1L, 0L) {
        def currentTimeMillis = clock.currentTimeMillis
      }

      when("is is asked to generate IDs before Scheme's epoch")
      val results = 
        (0L :: List.fill(10000)(random(0, epoch - 1)).sorted ::: List(epoch - 1)) map { t =>
          clock.setFixedTime(t)
          evaluating { idGenerator.newId }
        }

      then("it should throw exceptions")
      results foreach { r =>
        r should produce [GeneratorException]
      }
    }

    scenario("Generator is called after the end of Scheme's time") {
      given("an Id Generator")
      val clock = new FakeClock
      val idGenerator = new AbstractGenerator(randomNode, scheme, -1L, 0L) {
        def currentTimeMillis = clock.currentTimeMillis
      }

      when("is is asked to generate IDs after the end of Scheme's time")
      val results = 
        ((scheme.maxTimestamp + 1) :: 
          List.fill(10000)(random(scheme.maxTimestamp + 1, Long.MaxValue)).sorted ::: 
          List(Long.MaxValue)) map { t =>
          clock.setFixedTime(t)
          evaluating { idGenerator.newId }
        }
      
      then("it should throw exceptions")
      results foreach { r =>
        r should produce [GeneratorException]
      }
    }

    scenario("Generator produces the last ID of a Scheme") {
      given("an ID Generator having produced the last Scheme's ID")
      val clock = new FakeClock
      clock.setFixedTime(scheme.maxTimestamp)
      val idGenerator = new AbstractGenerator(randomNode, scheme, -1L, 0L) {
        def currentTimeMillis = clock.currentTimeMillis
      }
      (0 to scheme.maxSequence.toInt) foreach { s => 
        idGenerator.newId.sequence should equal (s)
      }

      when("is is asked to generate a new ID")
      val result = evaluating { idGenerator.newId }
      
      then("it should throw an exception")
      result should produce [GeneratorException]
    }
  }
}
