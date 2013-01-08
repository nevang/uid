Example
-------

```scala
import gr.jkl.uid._

// Define the structure of the implementation's Ids with the following parameters:
// timestamp: 42 bits
// node: 12 bits
// sequence: 10 bits
// epoch: 1351728000000L (01 Nov 2012 00:00:00 GMT)
implicit val scheme = Scheme(42, 12, 10, 1351728000000L)

// Construct an Id Generator for a machine with id O
val generator = Generator(0L)

// Create a new Id
val id = generator.newId

// Create a new Id as a Long
val longId = generator.newLong
```
