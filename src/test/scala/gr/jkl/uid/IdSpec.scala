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

    implicit val scheme = Scheme(timestampBits, nodeBits, sequenceBits, epoch)

    feature("IDs are also represented as Strigs") {
      
      scenario("IDs is created from Strings") {
        given(iterations + " IDs")
        val ids = List.fill(iterations)(Id.create(randomTimestamp, randomNode, randomSequence)).flatten

        when("they converted to Strings")
        val stringIds = ids.map { id => (id, id.toString)}

        then("each String should be recognized as a valid Id")
        stringIds foreach { t =>
          val (id, str) = t
          str should be an ID
        }

        and("each String should be converted to the corresponding ID")
        stringIds foreach { t =>
          val (id, str) = t
          Id.unapply(str).value should equal (id)
        }
      }
    }

    feature("IDs can be sorted by the timestamp, the node and the sequence") {

      scenario("IDs are sorted as Ids") {
        given(iterations + " IDs")
        val idComponents = 
          List.fill(iterations)((randomTimestamp, randomNode, randomSequence))
        val ids = idComponents.map(Id.unapply).flatten

        when("they are sorted")
        val sortedIds = ids.sorted

        then("their order should be by the timestamp, node and sequence")
        val sortedComponents = idComponents.sorted
        sortedIds.zip(sortedComponents) foreach { p =>
          val (id, (timestamp, node, sequence)) = p 
          Id.create(timestamp, node, sequence).value should equal (id)
        }
      }

      scenario("IDs are sorted as Longs") {
        given(iterations + " IDs")
        val ids = 
          List.fill(iterations)(Id.create(randomTimestamp, randomNode, randomSequence)).flatten

        when("they are sorted as Longs")
        val longs = ids.map(_.underlying)
        val sortedLongs = longs.sorted

        then("their order should be equal to the order of Ids")
        val sortedIds = ids.sorted
        sortedIds.zip(sortedLongs) foreach { p =>
          val (id, long) = p
          Id(long) should equal (id)
        }
      }

      scenario("IDs are sorted as Strings") {
        given(iterations + " IDs")
        val ids = 
          List.fill(iterations)(Id.create(randomTimestamp, randomNode, randomSequence)).flatten

        when("they are sorted as String")
        val strings = ids.map(_.toString)
        val sortedStrings = strings.sorted

        then("their order should be equal to the order of Ids")
        val sortedIds = ids.sorted
        sortedIds.zip(sortedStrings) foreach { p =>
          val (id, str) = p
          Id.unapply(str).value should equal (id)
        }
      }
    }
  }
  