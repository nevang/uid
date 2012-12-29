package gr.jkl.uid

import org.scalatest.{ FeatureSpec, GivenWhenThen }
import org.scalatest.matchers.ShouldMatchers

class SchemeSpecSchemeSpecScheme 
  extends FeatureSpec 
  with GivenWhenThen 
  with ShouldMatchers 
  with RandomIdParameters {

    val iterations = 10000

    feature("Id Scheme packs and unpacks a timestamp, a node and a sequence on an Id.") {
      
      scenario("Timestamp is unpacked from an Id") {
        Given("a Scheme")
        val scheme = new Scheme(timestampBits, nodeBits, sequenceBits, epoch)

        When("it creates Ids for various timestamps")
        val ids = List.fill(iterations){
          val momment = randomTimestamp
          val id = scheme.create(momment, randomNode, randomSequence)
          (momment, id)
        }

        Then("the Scheme should be able to unpack the timestamp of each Id")
        ids foreach { t =>
          val (momment, id) = t
          scheme.getTimestamp(id) should be (momment)
        }
      }

      scenario("Node is unpacked from an Id") {
        Given("a Scheme")
        val scheme = new Scheme(timestampBits, nodeBits, sequenceBits, epoch)

        When("it creates ids for various nodes")
        val ids = List.fill(iterations){
          val node = randomNode
          val id = scheme.create(randomTimestamp, node, randomSequence)
          (node, id)
        }

        Then("the Scheme should be able to unpack the node of each Id")
        ids foreach { t =>
          val (node, id) = t
          scheme.getNode(id) should be (node)
        }
      }

      scenario("Sequence is unpacked from an Id") {
        Given("a Scheme")
        val scheme = new Scheme(timestampBits, nodeBits, sequenceBits, epoch)

        When("it creates ids for various sequences")
        val ids = List.fill(iterations){
          val sequence = randomSequence
          val id = scheme.create(randomTimestamp, randomNode, sequence)
          (sequence, id)
        }

        Then("the Shceme should be able to unpack the sequence of each")
        ids foreach { t =>
          val (sequence, id) = t
          scheme.getSequence(id) should be (sequence)
        }
      }

    }
  }
  