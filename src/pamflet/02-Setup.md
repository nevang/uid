Setup
=====

uid is published on the Sonatype and the Maven Central repositories. 

Scala 2.10
----------
uid 1.1 is built for Scala 2.10. The difference from 1.0 is the use of 
[Value Classes][1]. In order to use uid 1.1, include the following dependency in 
your sbt build: 

```scala
libraryDependencies += "gr.jkl" %% "uid" % "1.1"
```

Scala 2.8.1+
-------------
uid 1.0 is cross built for the following Scala versions:

* 2.8.1
* 2.8.2
* 2.9.0
* 2.9.0-1
* 2.9.1
* 2.9.1-1
* 2.9.2
* 2.10.0

In order to use uid 1.0, include the following dependency in your sbt build:

```scala
libraryDependencies += "gr.jkl" %% "uid" % "1.0"
```

Snapshot Releases
-----------------

To install the current snapshot release add the Sonatype snapshot repository in
your sbt build.

```scala
resolvers += Opts.resolver.sonatypeSnapshots

libraryDependencies += "gr.jkl" %% "uid" % "$version$"
```

[1]: http://docs.scala-lang.org/overviews/core/value-classes.html "Scala Value Classes"
