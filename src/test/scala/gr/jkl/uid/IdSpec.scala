package gr.jkl.uid

import org.scalatest.{ FeatureSpec, GivenWhenThen, OptionValues }
import org.scalatest.matchers.ShouldMatchers

class IdSpec
  extends FeatureSpec 
  with GivenWhenThen 
  with ShouldMatchers
  with OptionValues 
  with CustomMatchers
  with RandomIdParameters {

    val iterations = 100000

    implicit val scheme = new Scheme(timestampBits, nodeBits, sequenceBits, epoch)

    feature("IDs are also represented as Strigs") {
      
      scenario("IDs is created from Strings") {
        Given(s"$iterations IDs")
        val ids = List.fill(iterations)(Id.create(randomTimestamp, randomNode, randomSequence)).flatten

        When("they converted to Strings")
        val stringIds = ids.map { id => (id, id.toString)}

        Then("each String should be recognized as a valid Id")
        stringIds foreach { t =>
          val (id, str) = t
          str should be an ID
        }

        And("each String should be converted to the corresponding ID")
        stringIds foreach { t =>
          val (id, str) = t
          Id.unapply(str).value should equal (id)
        }
      }
    }

    feature("IDs can be sorted by the timestamp, the node and the sequence") {

      scenario("IDs are sorted as Ids") {
        Given(s"$iterations IDs")
        val idComponents = 
          List.fill(iterations)((randomTimestamp, randomNode, randomSequence))
        val ids = idComponents.map(Id.unapply).flatten

        When("they are sorted")
        val sortedIds = ids.sorted

        Then("their order should be by the timestamp, node and sequence")
        val sortedComponents = idComponents.sorted
        sortedIds.zip(sortedComponents) foreach { p =>
          val (id, (timestamp, node, sequence)) = p 
          Id.create(timestamp, node, sequence).value should equal (id)
        }
      }

      scenario("IDs are sorted as Longs") {
        Given(s"$iterations IDs")
        val ids = 
          List.fill(iterations)(Id.create(randomTimestamp, randomNode, randomSequence)).flatten

        When("they are sorted as Longs")
        val longs = ids.map(_.underlying)
        val sortedLongs = longs.sorted

        Then("their order should be equal to the order of Ids")
        val sortedIds = ids.sorted
        sortedIds.zip(sortedLongs) foreach { p =>
          val (id, long) = p
          Id(long) should equal (id)
        }
      }

      scenario("IDs are sorted as Strings") {
        Given(s"$iterations IDs")
        val ids = 
          List.fill(iterations)(Id.create(randomTimestamp, randomNode, randomSequence)).flatten

        When("they are sorted as String")
        val strings = ids.map(_.toString)
        val sortedStrings = strings.sorted

        Then("their order should be equal to the order of Ids")
        val sortedIds = ids.sorted
        sortedIds.zip(sortedStrings) foreach { p =>
          val (id, str) = p
          Id.unapply(str).value should equal (id)
        }
      }
    }
  }
  