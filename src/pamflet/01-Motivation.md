Motivation
==========

Modern web applications are polyglot and they are deployed on multiserver 
environments. Ids are used on DBMS, including NoSQL systems, server-side
applications and web browsers. 

[UUIDs][1] are safe and quick but they are not handy on databases and on 
browsers. Also, UUIDs are usually not ordered. [Twitter Snowflake][2] is an 
excellent solution but it is designed to fulfill specific requirements and to 
fit to Twitter's architecture. For instance, it includes a Thrift Server, it is
configured by Zookeper and its ids are consstructed according to a defined 
format.

In consideration of the above premises, a 64-bit id solution seems convenient 
for databases and server-side applications. Web browsers, on the contrary, as 
JavaScript represents numbers according to the [IEEE 754 standard][3], don't 
handle well 64-bit integers. For browsers, a decent string representation of the 
ids could be a useful.

To conclude, the targets if this library are summarized below:

* Ids shall be 64-bit values.
* Id packaging shall be customized. 
* Ids shall be ordered. 
* Id generation shall be safe on a multiserver environment. 
* Id generation shall be efficient on multi-threaded applications. 
* Ids shall be usable in multiple formats including strings. 

[1]: http://en.wikipedia.org/wiki/Universally_unique_identifier "Universally unique identifier"
[2]: http://engineering.twitter.com/2010/06/announcing-snowflake.html "Announcing Snowflake"
[3]: http://grouper.ieee.org/groups/754/ "IEEE 754: Standard for Binary Floating-Point Arithmetic"
