Documentation
=============

uid use is very simple. You define the structure of the ids via a [Scheme][1] 
and then you generate ids via a [Generator][2]. You can choose to generate ids 
as Longs or use the [Id][3] type of the library.

```scala
import gr.jkl.uid.{ Scheme, Generator }

// Define the Id specification with the following parameters:
// timestamp: 42 bits
// node     : 12 bits
// sequence : 10 bits
// epoch    : 1351728000000L (01 Nov 2012 00:00:00 GMT)
implicit val scheme = Scheme(42, 12, 10, 1351728000000L)

// Construct an Id Generator for a machine with id 0
val generator = Generator(0L)

// Create a new Id
val id = generator.newId

// Create a new Id as a Long
val longId = generator.newLong
```

[1]: api/latest/gr/jkl/uid/Scheme.html "gr.jkl.uid.Scheme"
[2]: api/latest/gr/jkl/uid/Generator.html "gr.jkl.uid.Generator"
[3]: api/latest/gr/jkl/uid/Id.html "gr.jkl.uid.Id"
