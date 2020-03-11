# AntiSamy

A library for performing fast, configurable cleansing of HTML coming from untrusted sources.

Another way of saying that could be: It's an API that helps you make sure that clients don't supply malicious cargo code in the HTML they supply for their profile, comments, etc., 
that get persisted on the server. The term "malicious code" in regards to web applications usually mean "JavaScript." Mostly, Cascading Stylesheets are only considered malicious 
when they invoke JavaScript. However, there are many situations where "normal" HTML and CSS can be used in a malicious manner.

More details on AntiSamy are available at: https://www.owasp.org/index.php/Category:OWASP_AntiSamy_Project. Particularly at: https://www.owasp.org/index.php/Category:OWASP_AntiSamy_Project#tab=How_do_I_get_started_3F.

There is also a legacy developers guide at: https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/owaspantisamy/Developer%20Guide.pdf (not sure how long that will remain accessible).

## Contributing to AntiSamy

### Find an Issue?
If you have found a bug, then create an issue in the AntiSamy repo: https://github.com/nahsra/antisamy/issues

### Find a Vulnerability?
If you have found a vulnerability in AntiSamy, first search the issues list (see above) to see if it has already been reported. If it has not, then please contact Dave Wichers (dave.wichers at owasp.org) directly. Please do not report vulnerabilities via GitHub issues as we wish to keep our users secure while a patch is implemented and deployed. If you wish to be acknowledged for finding the vulnerability, then please follow this process.

More detail is available in the file: [SECURITY.md](https://github.com/nahsra/antisamy/blob/master/SECURITY.md).


## How to Import
First, add the dependency from Maven:
```xml
<dependency>
   <groupId>org.owasp.antisamy</groupId>
   <artifactId>antisamy</artifactId>
   <version>LATEST_VERSION</version>
</dependency>
```

## How to Build
You can build and test from source pretty easily:
```
$ git clone https://github.com/nahsra/antisamy
$ cd antisamy
$ mvn package
```

