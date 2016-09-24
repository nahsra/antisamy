antisamy
========

A library for performing fast, configurable cleansing of HTML coming from untrusted sources.

Another way of saying that could be: It's an API that helps you make sure that clients don't supply malicious cargo code in the HTML they supply for their profile, comments, etc., 
that get persisted on the server. The term "malicious code" in regards to web applications usually mean "JavaScript." Mostly, Cascading Stylesheets are only considered malicious 
when they invoke the JavaScript. However, there are many situations where "normal" HTML and CSS can be used in a malicious manner.

How to Use
----------
First, add the dependency from Maven:
```xml
<dependency>
   <groupId>org.owasp.antisamy</groupId>
   <projectId>antisamy</projectId>
   <version>1.5.5</version>
</dependency>
```

How to Build
------------
You can build and test from source pretty easily:
```
$ git clone https://github.com/nahsra/antisamy
$ cd antisamy
$ mvn package
```
