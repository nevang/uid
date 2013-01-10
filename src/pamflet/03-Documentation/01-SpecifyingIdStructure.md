Specifying Id Structure
-----------------------

An id occupies 64-bits and is composed of:

* A timestamp with millisecond precision and a custom epoch.
* A node id in order to produce unique ids on a different machines.
* A sequence number to avoid collisions on the same millisecond.

The number of bits devoted to each of the parameters and the epoch is defined
by a [Scheme][1]. Various parts of the library require a scheme and it is 
convenient to define one implicitly.

```scala
import gr.jkl.uid.Scheme

val scheme = Scheme(
  timestampBits = 44, 
  nodeBits      = 12, 
  sequenceBits  = 8,
  epoch         = 1351728000000L)

scheme.maxTimestamp 
// res0: Long = 18943914044415

scheme.maxNode 
// res1: Long = 4095

scheme.maxSequence 
// res2: Long = 255
```

[1]: latest/api/gr/jkl/uid/Scheme.html    "gr.jkl.uid.Scheme"
