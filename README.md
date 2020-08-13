# AntiSamy

A library for performing fast, configurable cleansing of HTML coming from untrusted sources.

Another way of saying that could be: It's an API that helps you make sure that clients don't supply malicious cargo code in the HTML they supply for their profile, comments, etc., that get persisted on the server. The term "malicious code" in regards to web applications usually mean "JavaScript." Mostly, Cascading Stylesheets are only considered malicious when they invoke JavaScript. However, there are many situations where "normal" HTML and CSS can be used in a malicious manner.

## How to Use

### Import the dependency

First, add the dependency from Maven:
```xml
<dependency>
   <groupId>org.owasp.antisamy</groupId>
   <artifactId>antisamy</artifactId>
   <version>LATEST_VERSION</version>
</dependency>
```

### Choosing a base policy file
Chances are that your site’s use case for AntiSamy is at least roughly comparable to one of the predefined policy files. They each represent a “typical” scenario for allowing users to provide HTML (and possibly CSS) formatting information. Let’s look into the different policy files:

1) antisamy-slashdot.xml

Slashdot is a techie news site that allows users to respond anonymously to news posts with very limited HTML markup. Now, Slashdot is not only one of the coolest sites around, it’s also one that’s been subject to many different successful attacks. The rules for Slashdot are fairly strict: users can only submit the following HTML tags and no CSS: `<b>`, `<u>`, `<i>`, `<a>`, `<blockquote>`.

Accordingly, we’ve built a policy file that allows fairly similar functionality. All text-formatting tags that operate directly on the font, color or emphasis have been allowed.

2) antisamy-ebay.xml

eBay is the most popular online auction site in the universe, as far as I can tell. It is a public site so anyone is allowed to post listings with rich HTML content. It’s not surprising that given the attractiveness of eBay as a target that it has been subject to a few complex XSS attacks. Listings are allowed to contain much more rich content than, say, Slashdot -- so it’s attack surface is considerably larger. 

3) antisamy-myspace.xml

MySpace was, at the time this project was born, arguably the most popular social networking site. Users were allowed to submit pretty much all HTML and CSS they want -- as long as it doesn’t contain JavaScript. MySpace was using a word blacklist to validate users’ HTML, which is why they were subject to the infamous Samy worm. The Samy worm, which used fragmentation attacks combined with a word that should have been blacklisted (eval) - was the inspiration for the project.

4) antisamy-anythinggoes.xml

I don’t know of a possible use case for this policy file. If you wanted to allow every single valid HTML and CSS element (but without JavaScript or blatant CSS-related phishing attacks), you can use this policy file. Not even MySpace was this crazy. However, it does serve as a good reference because it contains base rules for every element, so you can use it as a knowledge base when using tailoring the other policy files.

### Tailoring the policy file
You may want to deploy AntiSamy in a default configuration, but it’s equally likely that a site may want to have strict, business-driven rules for what users can allow. The discussion that decides the tailoring should also consider attack surface - which grows in relative proportion to the policy file.

### Calling the AntiSamy API
Using AntiSamy is easy. Here is an example of invoking AntiSamy with a policy file:

```
import org.owasp.validator.html.*;

Policy policy = Policy.getInstance(POLICY_FILE_LOCATION);

AntiSamy as = new AntiSamy();
CleanResults cr = as.scan(dirtyInput, policy);

MyUserDAO.storeUserProfile(cr.getCleanHTML()); // some custom function
```

There are a few ways to create a Policy object. The `getInstance()` method can take any of the following:

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

### - Analyzing CleanResults
The `CleanResults` object provides a lot of useful stuff.

 * `getErrorMessages()` - a list of String error messages -- *if this returns 0 that does not mean there were no attacks!*
 * `getCleanHTML()` - the clean, safe HTML output
 * `getCleanXMLDocumentFragment()` - the clean, safe `XMLDocumentFragment` which is reflected in `getCleanHTML()`
 * `getScanTime()` - returns the scan time in seconds
 
__Important Note__: There has been much confusion about `getErrorMessages()` method. The `getErrorMessages()` method does not subtly answer the question "is this safe input?" in the affirmative if it returns an empty list. You must always use the sanitized input and there is no way to be sure the input passed in had no attacks. 

The serialization and deserialization process that is critical to the effectiveness of the sanitizer is purposefully lossy and will filter attacks a number of attack classes. Unfortunately, one of the tradeoffs in using this strategy is that we don't always know in retrospect that an attack was seen. Thus, the `getErrorMessages()` API is there to help users understand their well-intentioned input meet the requirements of the system, not help a developer detect if an attack was present. 

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
