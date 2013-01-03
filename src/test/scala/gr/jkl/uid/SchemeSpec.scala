package gr.jkl.uid

import org.scalatest.{ FeatureSpec, GivenWhenThen, OptionValues }
import org.scalatest.matchers.ShouldMatchers

class SchemeSpec
  extends FeatureSpec 
  with GivenWhenThen 
  with ShouldMatchers
  with OptionValues 
  with RandomIdParameters {

    val iterations = 100000

    feature("ID Scheme is used to pack and unpack a timestamp, a node and a sequence on an ID.") {
      
      scenario("Timestamp is extracted from an Id") {
        Given("a Scheme")
        val scheme = new Scheme(timestampBits, nodeBits, sequenceBits, epoch)

        When("it is used to create IDs for various timestamps")
        val ids = List.fill(iterations) {
          val momment = randomTimestamp
          val maybeId = Id.create(momment, randomNode, randomSequence)(scheme)
          (momment, maybeId)
        }

        Then("it should be able to extract the timestamp of each ID")
        ids foreach { t =>
          val (momment, maybeId) = t
         maybeId.value.timestamp(scheme) should equal (momment)
        }
      }

      scenario("Node is extracted from an ID") {
        Given("a Scheme")
        val scheme = new Scheme(timestampBits, nodeBits, sequenceBits, epoch)

        When("it is used to create IDs for various nodes")
        val ids = List.fill(iterations){
          val node = randomNode
          val maybeId = Id.create(randomTimestamp, node, randomSequence)(scheme)
          (node, maybeId)
        }

        Then("the Scheme should be able to extract the node of each ID")
        ids foreach { t =>
          val (node, maybeId) = t
          maybeId.value.node(scheme) should equal (node)
        }
      }

      scenario("Sequence is extracted from an ID") {
        Given("a Scheme")
        val scheme = new Scheme(timestampBits, nodeBits, sequenceBits, epoch)

        When("it is used to create IDs for various sequences")
        val ids = List.fill(iterations){
          val sequence = randomSequence
          val maybeId = Id.create(randomTimestamp, randomNode, sequence)(scheme)
          (sequence, maybeId)
        }

        Then("the Shceme should be able to extract the sequence of each ID")
        ids foreach { t =>
          val (sequence, maybeId) = t
          maybeId.value.sequence(scheme) should equal (sequence)
        }
      }
    }
  }
  