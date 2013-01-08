Id Generation
-------------

A thread-safe non-blocking [`Generator`](latest/api/gr/jkl/uid/Generator.html) 
is included in the library which relies on the System clock to generates ids. 
The included generetor implements the following policies:

* If the **number of ids generated on a random millisecond exceeds the limit**, 
which depends on the sequence bits, **the next id will be produced with the 
timestamp of the following millisecond**. 
* If the **system clock goes back in time the generator will continue to produce 
ids maintaining the timestamp of the last generated id**. If sequence exceeds the
generator will move to the next millisecond. 
