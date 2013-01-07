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
        given("a Scheme")
        val scheme = Scheme(timestampBits, nodeBits, sequenceBits, epoch)

        when("it is used to create IDs for various timestamps")
        val ids = List.fill(iterations) {
          val momment = randomTimestamp
          val maybeId = Id.create(momment, randomNode, randomSequence)(scheme)
          (momment, maybeId)
        }

        then("it should be able to extract the timestamp of each ID")
        ids foreach { t =>
          val (momment, maybeId) = t
         maybeId.value.timestamp(scheme) should equal (momment)
        }
      }

      scenario("Node is extracted from an ID") {
        given("a Scheme")
        val scheme = Scheme(timestampBits, nodeBits, sequenceBits, epoch)

        when("it is used to create IDs for various nodes")
        val ids = List.fill(iterations){
          val node = randomNode
          val maybeId = Id.create(randomTimestamp, node, randomSequence)(scheme)
          (node, maybeId)
        }

        then("the Scheme should be able to extract the node of each ID")
        ids foreach { t =>
          val (node, maybeId) = t
          maybeId.value.node(scheme) should equal (node)
        }
      }

      scenario("Sequence is extracted from an ID") {
        given("a Scheme")
        val scheme = Scheme(timestampBits, nodeBits, sequenceBits, epoch)

        when("it is used to create IDs for various sequences")
        val ids = List.fill(iterations){
          val sequence = randomSequence
          val maybeId = Id.create(randomTimestamp, randomNode, sequence)(scheme)
          (sequence, maybeId)
        }

        then("the Shceme should be able to extract the sequence of each ID")
        ids foreach { t =>
          val (sequence, maybeId) = t
          maybeId.value.sequence(scheme) should equal (sequence)
        }
      }
    }

    feature("ID Scheme is serializable") {
      scenario("Scheme is serialized and desialized") {
        import java.io.{ ByteArrayOutputStream, ObjectOutputStream, 
          ByteArrayInputStream, InputStream, ObjectInputStream }

        given(iterations + " Schemes")
        val schemes = List.fill(iterations) {
          val timestampBits = randomTimestampBits
          val sequenceBits = randomSequenceBits
          val nodeBits = calculateNodeBits(timestampBits, sequenceBits)
          val epoch = randomEpoch(timestampBits)
          Scheme(timestampBits, nodeBits, sequenceBits, epoch)
        }

        when("Schemes are serialized and desiralized")
        val desiralizedSchemes = schemes map { scheme =>
          val out = new ByteArrayOutputStream
          val oos = new ObjectOutputStream(out)
          oos.writeObject(scheme)
          oos.close
          out
        } map { serializedScheme =>
          val pickled = serializedScheme.toByteArray();
          val in = new ByteArrayInputStream(pickled);
          val ois = new ObjectInputStream(in);
          val o = ois.readObject();
          o.asInstanceOf[Scheme]
        }


      }
    }
  }
  