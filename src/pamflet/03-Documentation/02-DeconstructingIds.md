Deconstructing Ids
------------------

[Id][1] is a [Value Class][2] with an underlying Long and it contains methods 
which extract its parameters. Id's [companion object][3] contains factory
methods, extractors and [Orderings][4].

```scala
import gr.jkl.uid.{ Id, Scheme }
val id = Id(-9217076510208286673L)
implicit val scheme = Scheme(44, 12, 8, 1351728000000L)

id.timestamp
// res0: Long = 1357731882071

id.node
// res1: Long = 32

id.sequence
// res2: Long = 47

id.underlying
// res3: Long = -9217076510208286673

Id.create(1357731882071L, 32, 47)
// res4: Option[gr.jkl.uid.Id] = Some(--LMQy4R1-j)
```

Timestamp, node and sequence, among other methods, depend on the id's underlying 
Long and on the scheme. A different scheme would produce different results.

```scala
import gr.jkl.uid.{ Id, Scheme }

val id = Id(-9217076510208286673L)

implicit val scheme = Scheme(43, 16, 5, 1357700000000L)

id.timestamp
// res0: Long = 1360701941035

id.node
// res1: Long = 33025

id.sequence
// res2: Long = 15
```

[1]: api/latest/gr/jkl/uid/Id.html "gr.jkl.uid.Id"
[2]: http://docs.scala-lang.org/overviews/core/value-classes.html "Scala Value Classes"
[3]: api/latest/gr/jkl/uid/Id\$.html "gr.jkl.uid.Id\$"
[4]: http://www.scala-lang.org/api/current/scala/math/Ordering.html "scala.math.Ordering"
