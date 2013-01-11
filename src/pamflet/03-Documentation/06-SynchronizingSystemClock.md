Synchronizing System Clock
--------------------------

Id [Generator][1] depends strongly on the system's clock. Although the generator 
will continue to produce unique ids when clock goes back, it's better to 
synchronize your server's time with NTP in a  [mode which doesn't move the clock 
backwards][2].

**If your can't control time synchronization and your clock may move 
backwards it is suggested to instantiate your id generators providing the 
last id generated for each node.**

```scala
// Constructs an Id generator for node 0 which will 
// generate ids that come after the lastId
val generator = Generator(0L, lastId)
```

[1]: api/latest/gr/jkl/uid/Generator.html "gr.jkl.uid.Generator"
[2]: http://wiki.dovecot.org/TimeMovedBackwards#Time_synchronization "Time Synchronization"
