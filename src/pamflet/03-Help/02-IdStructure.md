Id Structure
------------

Ids may be generated with the type of [`Id`](latest/api/gr/jkl/uid/Id.html), which 
is a [Value Class](http://docs.scala-lang.org/overviews/core/value-classes.html), 
or as Longs. The value class contains some methods which extract the parameter of 
the id. Each id is composed of:

* A timestamp with millisecond precision and a custom epoch.
* A node id in order to produce unique ids on a multi-server environment.
* A sequence number to avoid collisions on the same millisecond.

The number of bits devodeted to each of the parameters and the epoch is defined
by a [`Scheme`](latest/api/gr/jkl/uid/Scheme.html).
