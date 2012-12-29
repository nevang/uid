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
      Given((scheme.maxNode + 1) + " unique nodes")
      val idGenerators = (0 to scheme.maxNode.toInt).map(n => (n -> Generator(n)))
      
      When("they generate IDs")
      val ids = idGenerators map { t =>
        val (node, idGenerator) = t
        (node -> idGenerator.newId)
      }

      Then("each ID should include the related node")
      ids foreach { t =>
        val (node, id) = t
        id.node should be (node)
      }
    }

    scenario("the number of IDs produced on one millisecond exceeds limit") {
      Given("an ID Generator operating on one millisecond")
      val aMillisecond: Long = randomTimestamp
      val idGenerator = new Generator(randomNode, scheme) {
        override val currentTimeMillis = aMillisecond
      }

      When("it generates IDs up to the limit (" + (scheme.maxSequence + 1) + ")")
      val ids = (0 to scheme.maxSequence.toInt).map { i =>
        (i -> idGenerator.newId)
      }

      Then("its ID should include a sequential number")
      ids foreach { t =>
        val (idx, id) = t 
        id.sequence should be (idx)
      }

      When("it generates the next Id")
      val id = idGenerator.newId

      Then("it should have sequence equal to 0")
      id.sequence should be (0L)

      And("its timestamp should be increased by 1 millisecond")
      id.timestamp should be (aMillisecond + 1L)
    }

    scenario("IDs are produced on various momments in time") {
      val iterations = 50000

      Given("various momments in time")
      val clock = new FakeClock
      
      val timestamps = epoch :: List.fill(iterations)(randomTimestamp).sorted :::
        List(scheme.maxTimestamp)

      val idGenerator = new Generator(randomNode, scheme) {
          override def currentTimeMillis = clock.currentTimeMillis
        }

      When("a Generator is producing an ID")
      val ids = timestamps map {  t =>
        clock.setFixedTime(t)
        (t -> idGenerator.newId)
      }

      Then("the ID should include the related timestamp")
      ids foreach { t =>
        val (timestamp, id) = t 
        id.timestamp should be (timestamp)
      }
    }
  }

  feature("Generator should produce unique IDs under any circumstances") {

    scenario("IDs are produced on multiple threads") {
      val conductor = new Conductor
      import conductor._
      val threads = 6
      val idsPerThread = 20000
      class IdHolder {
        var list: List[Id] = Nil
      } 
      val idHolders = List.fill(threads)(new IdHolder)

      Given("an ID Generator")
      val idGenerator = Generator(randomNode)
      
      When("IDs are produced by multiple threads")
      def createIds(idHolder: IdHolder) {
        idHolder.list = List.fill(idsPerThread)(idGenerator.newId)
      } 
      idHolders.foreach {
        i => thread(createIds(i))
      }

      Then("all IDs should be unique")
      whenFinished {
        val idsCount = threads * idsPerThread
        val ids = idHolders.foldLeft(List.empty[Id])(_ ::: _.list).toSet
        ids should have size (idsCount)
      }
    }

    scenario("the clock may go back in time") {
      Given("an ID produced by a Generator")
      val clock = new FakeClock
      val idGenerator = new Generator(0, scheme) {
        override def currentTimeMillis = clock.currentTimeMillis
      }
      val id = idGenerator.newId
      
      When("lock goes back in time")
      clock.setOffset(- util.Random.nextInt(150) - 50)

      Then("the next ID should be produced with the timestamp of the previous ID")
      val nextId = idGenerator.newId
      nextId.timestamp should be (id.timestamp)
    }
  }

}
