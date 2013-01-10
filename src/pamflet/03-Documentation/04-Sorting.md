Sorting
-------

Ids are roughly sortable. Like [Twitter Snowflake][1] they are [k-sorted][2]. 
The default [Ordering][3], which is defined implicitly, sorts ids first by the 
timestamp, then by the node and then by the sequence. Sorting ids as Strings or 
Longs will produce the same result. 

```scala
import gr.jkl.uid.Id

val ids = List.fill(1000)(Id(util.Random.nextLong))

val sortedIds = ids.sorted

val longIds = ids.map(_.underlying)

val sortedLongIds = longIds.sorted

val stringIds = ids.map(_.toString)

val sortedStringIds = stringIds.sorted

sortedLongIds == sortedIds.map(_.underlying)
// res0: Boolean = true

sortedStringIds == sortedIds.map(_.toString)
// res1: Boolean = true
```

It's easy to implement an ordering for a class which contains an id.

```scala
import gr.jkl.uid.Id
import scala.math.Ordering

case class Comment(id: Id, commenter: String, body: String)

object Comment {
  implicit val IdOrdering: Ordering[Comment] = Ordering by (_.id)  
}
```

Alternatively, you can sort ids first by the timestamp, then by the sequence 
and then by the node. This ordering requires a scheme.

```scala
import gr.jkl.uid.{ Id, Scheme }

val ids = List.fill(1000)(Id(util.Random.nextLong))

implicit val scheme = Scheme(44, 12, 8, 1351728000000L)

val sortedIds = ids.sorted(Id.TimeSequenceNodeOrdering)
```

[1]: http://engineering.twitter.com/2010/06/announcing-snowflake.html "Announcing Snowflake"
[2]: http://ci.nii.ac.jp/naid/110002673489/ "Roughly Sorting: Sequential and Parallel Approach"
[3]: http://www.scala-lang.org/api/current/scala/math/Ordering.html "scala.math.Ordering"
