Setup
-----
uid is available on the Sonatype and on the Maven Central repositories. 

uid 1.1
-------
uid 1.1 is built for Scala 2.10. The main difference from 1.0 is the use of 
[Value Classes](http://docs.scala-lang.org/overviews/core/value-classes.html) for
the ids.

```scala
libraryDependencies += "gr.jkl" %% "uid" % "1.1"
```

uid 1.0
-------
uid 1.0 is cross built for Scala 2.10.0, 2.9.3-RC1, 2.9.2, 2.9.1-1, 2.9.1, 
2.9.0-1, 2.9.0, 2.8.2 and 2.8.1.

```scala
libraryDependencies += "gr.jkl" %% "uid" % "1.0"
```

Snaposhot Releases
------------------
To install the current snapshot release add the
following resolver to your build.sbt file or to your 
scala build file.

```scala
resolvers += Opts.resolver.sonatypeSnapshots
```
