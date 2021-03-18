# AntiSamy

A library for performing fast, configurable cleansing of HTML coming from untrusted sources. Supports Java 7+.

Another way of saying that could be: It's an API that helps you make sure that clients don't supply malicious cargo code in the HTML they supply for their profile, comments, etc., that get persisted on the server. The term "malicious code" in regards to web applications usually mean "JavaScript." Mostly, Cascading Stylesheets are only considered malicious when they invoke JavaScript. However, there are many situations where "normal" HTML and CSS can be used in a malicious manner.

## How to Use

### 1. Import the dependency

First, add the dependency from Maven:
```xml
<dependency>
   <groupId>org.owasp.antisamy</groupId>
   <artifactId>antisamy</artifactId>
   <version>LATEST_VERSION</version>
</dependency>
```

### 2. Choosing a base policy file
Chances are that your site’s use case for AntiSamy is at least roughly comparable to one of the predefined policy files. They each represent a “typical” scenario for allowing users to provide HTML (and possibly CSS) formatting information. Let’s look into the different policy files:

1) antisamy-slashdot.xml

Slashdot is a techie news site that allows users to respond anonymously to news posts with very limited HTML markup. Now, Slashdot is not only one of the coolest sites around, it’s also one that’s been subject to many different successful attacks. The rules for Slashdot are fairly strict: users can only submit the following HTML tags and no CSS: `<b>`, `<u>`, `<i>`, `<a>`, `<blockquote>`.

Accordingly, we’ve built a policy file that allows fairly similar functionality. All text-formatting tags that operate directly on the font, color, or emphasis have been allowed.

2) antisamy-ebay.xml

eBay is the most popular online auction site in the universe, as far as I can tell. It is a public site so anyone is allowed to post listings with rich HTML content. It’s not surprising that given the attractiveness of eBay as a target that it has been subject to a few complex XSS attacks. Listings are allowed to contain much more rich content than, say, Slashdot -- so it’s attack surface is considerably larger.

3) antisamy-myspace.xml

MySpace was, at the time this project was born, the most popular social networking site. Users were allowed to submit pretty much all the HTML and CSS they wanted -- as long as it didn’t contain JavaScript. MySpace was using a word blacklist to validate users’ HTML, which is why they were subject to the infamous Samy worm. The Samy worm, which used fragmentation attacks combined with a word that should have been blacklisted (eval) - was the inspiration for this project.

4) antisamy-anythinggoes.xml

I don’t know of a possible use case for this policy file. If you wanted to allow every single valid HTML and CSS element (but without JavaScript or blatant CSS-related phishing attacks), you can use this policy file. Not even MySpace was this crazy. However, it does serve as a good reference because it contains base rules for every element, so you can use it as a knowledge base when using tailoring the other policy files.

### NOTE: Schema validation behavior change starting with AntiSamy 1.6.0

While working on some improvements to AntiSamy's XML Schema Definition (XSD) for AntiSamy policy files, we noticed that AntiSamy was NOT actually enforcing the XSD. So, we've CHANGED the default behavior starting with AntiSamy 1.6.0 to enforce the schema, and not continue if the AntiSamy policy is invalid. However ...

we recognize that it might not be possible for developers to fix their AntiSamy policies right away if they are non-compliant, and yet still want to upgrade AntiSamy to pick up any security improvements, feature enhancements, and bug fixes. As such, we've provided two ways to (temporarily!) disable schema validation:

1) Set the Java System property: owasp.validator.validateschema to false. This can be done at the command line (e.g., -Dowasp.validator.validateschema=false) or via the Java System properties file. Neither requires a code change.

2) Change the code using AntiSamy to invoke: Policy.setSchemaValidation(false) before loading the AntiSamy policy. This is a static call so once disabled, it is disabled for all new Policy instances.

To encourage AntiSamy users to only use XSD compliant policies, AntiSamy will always log some type of warning when schema validation is disabled. It will either WARN that the policy is non-compliant so it can be fixed, or it will WARN that the policy is compliant, but schema validation is OFF, so validation should be turned back on (i.e., stop disabling it). We also added INFO level logging when AntiSamy schema's are loaded and validated.

### Disabling schema validation is deprecated immediately, and will go away in AntiSamy 1.7+

The ability to disable the new schema validation feature is intended to be temporary, to smooth the transition to properly valid AntiSamy policy files. We plan to drop this feature in the next major release. We estimate that this will be some time mid-late 2022, so not any time soon. The idea is to give dev teams using AntiSamy directly, or through other libraries like ESAPI, plenty of time to get their policy files schema compliant before schema validation becomes required. 

### Logging: The logging introduced in 1.6.0 accidentally used log4j, while declaring slf4 as the logging API.

This was quickly fixed in 1.6.1 to use slf4j APIs only. AntiSamy now includes the slf4j-simple library for its logging, but AntiSamy users can import and use an alternate slf4j compatible logging library if they prefer. They can also then exclude slf4j-simple if they want to.

WARNING: AntiSamy's use of slf4j-simple, without any configuration file, logs messages in a buffered manner to standard output. As such, some or all of these log messages may get lost if an Exception, such as a PolicyException is thrown. This can likely be rectified by configuring slf4j-simple to log to standard error instead, or use an alternate slf4j logger that does so.

### 3. Tailoring the policy file
You may want to deploy AntiSamy in a default configuration, but it’s equally likely that a site may want to have strict, business-driven rules for what users can allow. The discussion that decides the tailoring should also consider attack surface - which grows in relative proportion to the policy file.

### 4. Calling the AntiSamy API
Using AntiSamy is easy. Here is an example of invoking AntiSamy with a policy file:

```
import org.owasp.validator.html.*;

Policy policy = Policy.getInstance(POLICY_FILE_LOCATION);

AntiSamy as = new AntiSamy();
CleanResults cr = as.scan(dirtyInput, policy);

MyUserDAO.storeUserProfile(cr.getCleanHTML()); // some custom function
```

There are a few ways to create a `Policy` object. The `getInstance()` method can take any of the following:

 * a `String` filename
 * a `File` object
 * an `InputStream`
 * `Policy` files can also be referenced by filename by passing a second argument to the `AntiSamy#scan()` method as the following examples show:

```
AntiSamy as = new AntiSamy();
CleanResults cr = as.scan(dirtyInput, policyFilePath);
```

Finally, policy files can also be referenced by `File` objects directly in the second parameter:

```
AntiSamy as = new AntiSamy();
CleanResults cr = as.scan(dirtyInput, new File(policyFilePath));
```

### 5. Analyzing CleanResults
The `CleanResults` object provides a lot of useful stuff.

 * `getErrorMessages()` - a list of String error messages -- *if this returns 0 that does not mean there were no attacks!*
 * `getCleanHTML()` - the clean, safe HTML output
 * `getCleanXMLDocumentFragment()` - the clean, safe `XMLDocumentFragment` which is reflected in `getCleanHTML()`
 * `getScanTime()` - returns the scan time in seconds
 
__Important Note__: There has been much confusion about the `getErrorMessages()` method. The `getErrorMessages()` method does not subtly answer the question "is this safe input?" in the affirmative if it returns an empty list. You must always use the sanitized input and there is no way to be sure the input passed in had no attacks.

The serialization and deserialization process that is critical to the effectiveness of the sanitizer is purposefully lossy and will filter out attacks via a number of attack vectors. Unfortunately, one of the tradeoffs of this strategy is that we don't always know in retrospect that an attack was seen. Thus, the `getErrorMessages()` API is there to help users understand their well-intentioned input meet the requirements of the system, not help a developer detect if an attack was present. 

## Other Documentation

Additional documentation is available on this Github project's wiki page: https://github.com/nahsra/antisamy/wiki
and the OWASP AntiSamy Project Page: https://owasp.org/www-project-antisamy/

## Contributing to AntiSamy

### Find an Issue?
If you have found a bug, then create an issue in the AntiSamy repo: https://github.com/nahsra/antisamy/issues

### Find a Vulnerability?
If you have found a vulnerability in AntiSamy, first search the issues list (see above) to see if it has already been reported. If it has not, then please contact Dave Wichers (dave.wichers at owasp.org) directly. Please do not report vulnerabilities via GitHub issues as we wish to keep our users secure while a patch is implemented and deployed. If you wish to be acknowledged for finding the vulnerability, then please follow this process.

More detail is available in the file: [SECURITY.md](https://github.com/nahsra/antisamy/blob/master/SECURITY.md).

## How to Build
You can build and test from source pretty easily:
```
$ git clone https://github.com/nahsra/antisamy
$ cd antisamy
$ mvn package
```

## License
Released under the [BSD-3-Clause](https://opensource.org/licenses/BSD-3-Clause) license as specified here: [LICENSE](https://github.com/nahsra/antisamy/blob/master/LICENSE). 
