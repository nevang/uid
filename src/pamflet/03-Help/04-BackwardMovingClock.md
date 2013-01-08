Backward Moving Clock
---------------------

Although the [`Generator`](latest/api/gr/jkl/uid/Generator.html) will continue 
to produce unique ids when time goes back, it's better to configure NTP in a 
mode which doesn't move the clock backwards. If your clock moves backwards it is
suggested while instantiating an id generator to provide the last id generated
for the related node.

```scala
// Constructs an Id generator for node 0 which will generate 
// Ids that come after the lastId
val generator = Generator(0L, lastId)
```
