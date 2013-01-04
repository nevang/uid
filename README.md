uid
===

uid is a Scala 2.10 library for the generation and handling of 64-bit unique Ids. 
It is ispired by [Twitter Snowflake](https://github.com/twitter/snowflake "Twitter Snowflake") 
but it aims to be more flexible.

Target
------
* Id packaging parameters shall be customized. 
* Id generation shall be efficient on a multi-threaded environment.
* Ids shall be sorted on multiple formats including their String representation.

Installation
------------
uid is available on Sonatype OSS snapshots repository. To install it add the
following library dependency and resolver to your build.sbt file or to your 
scala build file.
```scala
libraryDependencies += "gr.jkl" %% "uid" % "1.0-SNAPSHOT"

resolvers += Opts.resolver.sonatypeSnapshots
```
 
Example
-------

```scala
import gr.jkl.uid._

// Define the structure of the implementation's Ids with the following parameters:
// timestamp: 42 bits
// node: 12 bits
// sequence: 10 bits
// epoch: 1351728000000L (01 Nov 2012 00:00:00 GMT)
implicit val scheme = new Scheme(42, 12, 10, 1351728000000L)

// Construct an Id Generator for a machine with id O
val generator = Generator(0L)

// Create a new Id
val id = generator.newId

// Create a new Id as a Long
val longId = generator.newLong
```

Id Structure
------------
Ids may be generated with the type of `Id`, which is a [Value Class]
(http://docs.scala-lang.org/overviews/core/value-classes.html), or as Longs. 
The value class contains some methods which extract the parameter of  the Id. 
Each Id is composed of:
* A timestamp with millisecond precision and a custom epoch.
* A node Id in order to produce unique Ids on a multi-server environment.
* A sequence number to avoid collisions on the same millisecond.

The number of bits devodeted to each of the parameters and the epoch is defined
by a `Scheme`.

Id Generation Details
---------------------
A thread-safe non-blocking `Generator` is included in the library which relies 
on the System clock to generates Ids. 

If the **number of Ids generated on a random millisecond exceeds the limit**, 
which depends on the sequence bits, **the next Id will be produced with the 
timestamp of the following millisecond**. 

If the **system clock goes back in time the Generator will continue to produce 
Ids maintaining the timestamp of the last generated Id**. Although the Generator
will continue to produce unique Ids, it's better to configure NTP in a mode which
doesn't move the clock backwards. If your clock moves backwards it is
suggested while instantiating an Id Generator to provide the last Id generated
for the related node.
 ```scala
 // Constructs an Id generator for node 0 which will generate Ids that come after the lastId
 val generator = Generator(0L, lastId)
 ```