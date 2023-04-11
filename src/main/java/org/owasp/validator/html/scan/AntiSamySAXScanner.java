/*
 * Copyright (c) 2007-2023, Arshan Dabirsiaghi, Jason Li
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. Neither the name of OWASP nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.owasp.validator.html.scan;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import org.htmlunit.cyberneko.parsers.SAXParser;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * This class allows you to use a SAX scanner to scan HTML rather than a DOM scanner. Its primary
 * purpose is the support incremental scanning of large blocks of HTML using the scan(Reader reader,
 * Writer writer) API so the HTML input and output doesn't all have to be held in memory at the same
 * time. It should not be called directly. All scanning should be done through an <code>
 * AntiSamy.scan()</code> method invocation. The HTML sanitization logic built into
 * AntiSamyDOMScanner is leveraged by this class as well.
 *
 * @author Arshan Dabirsiaghi
 */
public class AntiSamySAXScanner extends AbstractAntiSamyScanner {

  private static final Queue<CachedItem> cachedItems = new ConcurrentLinkedQueue<CachedItem>();

  private static final TransformerFactory sTransformerFactory;

  static {
    // Per issue #103, an IllegalArgumentException could be thrown below if the SAX parser does
    // not support these JAXP 1.5 features. This did actually occur in certain environments where we
    // let the TransformerFactory create whatever instance it decided to create. For example, if
    // xalan:2.7.2 was on the classpath, which doesn't support these JAXP features. However, this
    // should never happen anymore because, by default, we now force the use of the
    // JDK provided Xalan SAX parser, which DOES support these features. However, if someone REALLY
    // wants to use a different implementation, they can set the new property
    // "antisamy.transformerfactory.impl" to whatever they prefer to use, but that class must
    // implement the two attributes we set.

    String TRANSFORMER_FACTORY_IMPL =
        System.getProperty(
            "antisamy.transformerfactory.impl",
            "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");

    sTransformerFactory = TransformerFactory.newInstance(TRANSFORMER_FACTORY_IMPL, null);

    // Disable external entities, etc.
    sTransformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    sTransformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
  }

  static class CachedItem {
    private final Transformer transformer;
    private final SAXParser saxParser;
    private final MagicSAXFilter magicSAXFilter;

    CachedItem(Transformer transformer, SAXParser saxParser, MagicSAXFilter magicSAXFilter) {
      this.transformer = transformer;
      this.saxParser = saxParser;
      this.magicSAXFilter = magicSAXFilter;
      MagicSAXFilter[] filters = {magicSAXFilter};
      try {
        saxParser.setProperty("http://cyberneko.org/html/properties/filters", filters);
      } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Create an instance of this class configured to use the specified policy.
   *
   * @param policy The policy to use.
   */
  public AntiSamySAXScanner(Policy policy) {
    super(policy);
  }

  /**
   * The SAXScanner can't remember and return the complete scan results in all cases because the
   * scan(Reader reader, Writer writer) version of the scan() API incrementally scans the input to
   * generate incremental output. So to be safe, this class always returns null for getResults(). As
   * such, when using this class, you can only rely on the CleanResults returned by the scan()
   * method you invoked, or the output from the writer passed into scan(Reader reader, Writer
   * writer).
   *
   * @return always returns null
   */
  @Override
  public CleanResults getResults() {
    return null;
  }

  /**
   * Scan the provided HTML using the AntiSamy policy specified when this Scanner instance was
   * constructed, and return a CleanResult object that contains the sanitized output, along with
   * possibly some error messages and scan time statistics. This version of the scan() API is the
   * one that is typically used.
   *
   * @param html A String whose contents is to be sanitized per the configured AntiSamy policy.
   * @return A <code>CleanResults</code> object with (possibly) an <code>XMLDocumentFragment</code>
   *     object and a String representation of the cleaned HTML, as well as some scan statistics.
   *     Note that ONLY the cleaned HTML can be considered trustworthy. The absence of errorMessages
   *     in the CleanResults does NOT necessarily indicate the input was safe (i.e., contained no
   *     attacks).
   * @throws ScanException When there is a problem encountered while scanning the HTML input.
   */
  @Override
  public CleanResults scan(String html) throws ScanException {
    return scan(html, this.policy);
  }

  /**
   * This is where the magic lives. Scan the provided HTML and return a CleanResult object that
   * contains the sanitized output, along with possibly some error messages and scan time
   * statistics.
   *
   * @param html A String whose contents is to be sanitized per the configured AntiSamy policy.
   * @param policy The policy to use, overriding the policy specified when this class was
   *     instantiated.
   * @return A <code>CleanResults</code> object with (possibly) an <code>XMLDocumentFragment</code>
   *     object and a String representation of the cleaned HTML, as well as some scan statistics.
   *     Note that ONLY the cleaned HTML can be considered trustworthy. The absence of errorMessages
   *     in the CleanResults does NOT necessarily indicate the input was safe (i.e., contained no
   *     attacks).
   * @throws ScanException When there is a problem encountered while scanning the HTML input.
   */
  public CleanResults scan(String html, Policy policy) throws ScanException {
    if (html == null) {
      throw new ScanException(new NullPointerException("Null html input"));
    }

    int maxInputSize = this.policy.getMaxInputSize();

    if (html.length() > maxInputSize) {
      addError(ErrorMessageUtil.ERROR_INPUT_SIZE, new Object[] {html.length(), maxInputSize});
      throw new ScanException(errorMessages.get(0));
    }

    final StringWriter out = new StringWriter();
    StringReader reader = new StringReader(html);

    CleanResults results = scan(reader, out);
    final String tainted = html;
    Callable<String> cleanCallable =
        new Callable<String>() {
          public String call() throws Exception {
            return trim(tainted, out.toString());
          }
        };
    return new CleanResults(
        results.getStartOfScan(), cleanCallable, null, results.getErrorMessages());
  }

  /**
   * Using a SAX parser, this supports Streams for input and output. The use case is where the input
   * is large and the caller does not need or want the entire string in memory all at one time.
   *
   * @param reader A Reader which can feed the SAXParser a little input at a time
   * @param writer A Writer that can take a little output at a time
   * @return CleanResults where the cleanHtml is null. If a caller wants the HTML as a string, it
   *     must capture the contents of the writer (i.e., use a StringWriter).
   * @throws ScanException When there is a problem encountered while scanning the HTML input.
   */
  public CleanResults scan(Reader reader, Writer writer) throws ScanException {
    try {

      CachedItem candidateCachedItem = cachedItems.poll();
      if (candidateCachedItem == null) {
        candidateCachedItem =
            new CachedItem(getNewTransformer(), getParser(), new MagicSAXFilter(messages));
      }

      final CachedItem cachedItem = candidateCachedItem;

      SAXParser parser = cachedItem.saxParser;
      cachedItem.magicSAXFilter.reset(policy);

      long startOfScan = System.currentTimeMillis();

      final SAXSource source = new SAXSource(parser, new InputSource(reader));

      final Transformer transformer = cachedItem.transformer;
      boolean formatOutput = policy.isFormatOutput();
      boolean omitXml = policy.isOmitXmlDeclaration();

      transformer.setOutputProperty(OutputKeys.INDENT, formatOutput ? "yes" : "no");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXml ? "yes" : "no");
      transformer.setOutputProperty(OutputKeys.METHOD, "html");

      //noinspection deprecation
      final org.apache.xml.serialize.OutputFormat format = getOutputFormat();
      //noinspection deprecation
      final org.apache.xml.serialize.HTMLSerializer serializer = getHTMLSerializer(writer, format);

      transformer.transform(source, new SAXResult(serializer));
      errorMessages.clear();
      errorMessages.addAll(cachedItem.magicSAXFilter.getErrorMessages());
      cachedItems.add(cachedItem);
      return new CleanResults(startOfScan, (String) null, null, errorMessages);

    } catch (Exception e) {
      throw new ScanException(e);
    }
  }

  /**
   * Return a new Transformer instance. This is wrapped in a synchronized method because there is no
   * guarantee that the TransformerFactory is thread-safe.
   *
   * @return a new Transformer instance.
   */
  private static synchronized Transformer getNewTransformer() {
    try {
      return sTransformerFactory.newTransformer();
    } catch (TransformerConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get a properly configured SAXParser instance.
   *
   * @return A newly created and configured SAXParser instance.
   */
  private static SAXParser getParser() {
    try {
      SAXParser parser = new SAXParser();
      parser.setFeature("http://xml.org/sax/features/namespaces", false);
      parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
      parser.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);

      parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
      return parser;
    } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
}
