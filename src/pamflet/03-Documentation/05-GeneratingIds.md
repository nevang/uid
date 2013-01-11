Generating Ids
--------------

A thread-safe non-blocking [Generator][1] is included in the library which 
relies on the System clock to generates ids. You construct a generator having a 
defined scheme and providing a node id. A generator produces unique [Ids][2] 
or Longs.

```scala
import gr.jkl.uid.{ Scheme, Generator }

implicit val scheme = Scheme(44, 12, 8, 1351728000000L)

val generator = Generator(714)

generator.newId
// res0: gr.jkl.uid.Id = --LRP8nVgc-

generator.newLong
// res1: Long = -9217054643603715584
```

The generator produce ids according to the following policies:

* If the **number of ids generated on a random millisecond exceeds scheme's 
  limit, the next id will be produced with the timestamp of the following 
  millisecond**. 
* If the **system clock goes back in time the generator will continue to produce 
  ids maintaining the timestamp of the last generated id**. If sequence exceeds 
  scheme's limit generator will move to the next millisecond. 

The above policies are implemented by the following logic:

1. If current timestamp is greater than the timestamp of the last id, the new id
   will be constructed by the current timestamp and a sequence equal to 0.
2. If current timestamp is less than or equal to the timestamp of the last id
   and the sequence of the last id is less than the maximum scheme's sequence, 
   the new id will be constructed by the previous timestamp and the previous 
   sequence incremented by 1.
3. If current timestamp is less than or equal to the timestamp of the last id 
   and the sequence of the last id is equal to the maximum scheme's sequence, 
   the new id will be constructed by the previous timestamp incremented by 1 and 
   a sequence equal to 0.

[1]: api/latest/gr/jkl/uid/Generator.html "gr.jkl.uid.Generator"
[2]: api/latest/gr/jkl/uid/Id.html "gr.jkl.uid.Id"
