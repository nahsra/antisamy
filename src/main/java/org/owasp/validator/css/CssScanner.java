/*
 * Copyright (c) 2007-2022, Arshan Dabirsiaghi, Jason Li
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice,
 * 	 this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of OWASP nor the names of its contributors may be used to
 *   endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.owasp.validator.css;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Parser;

/**
 * Encapsulates the parsing and validation of a CSS stylesheet or inline declaration. To make use of
 * this class, instantiate the scanner with the desired policy and call either <code>
 * scanInlineSheet()</code> or <code>scanStyleSheet</code> as appropriate.
 *
 * @see #scanInlineStyle(String, String, int)
 * @see #scanStyleSheet(String, int)
 * @author Jason Li
 */
public class CssScanner {

  protected static final Logger logger = LoggerFactory.getLogger(CssScanner.class);

  protected static final Timeout DEFAULT_TIMEOUT = Timeout.ofMilliseconds(1000);

  private static final String CDATA = "^\\s*<!\\[CDATA\\[(.*)\\]\\]>\\s*$";

  /** The parser to be used in any scanning */
  private final Parser parser = ParserFactory.makeParser();

  /** The policy file to be used in any scanning */
  private final InternalPolicy policy;

  /** The message bundled to pull error messages from. */
  private final ResourceBundle messages;

  /** The message bundled to pull error messages from. */
  private final boolean shouldParseImportedStyles;

  private static final Pattern cdataMatchPattern = Pattern.compile(CDATA, Pattern.DOTALL);

  /**
   * Constructs a scanner based on the given AntiSamy policy. This version of the constructor
   * defaults shouldParseImportedStyles to false. Look at the other constructor for a description of
   * that parameter.
   *
   * @param policy the policy to follow when scanning
   * @param messages the error message bundle to pull from
   */
  public CssScanner(InternalPolicy policy, ResourceBundle messages) {
    this(policy, messages, false);
  }

  /**
   * Constructs a scanner based on the given AntiSamy policy.
   *
   * @param policy the policy to follow when scanning
   * @param messages the error message bundle to pull from
   * @param shouldParseImportedStyles Flag to indicate if styles within @import directives should be
   *     imported and parsed in the resulting style sheet. This boolean determines if URLs should be
   *     recognized when parsing styles (i.e., to fetch them or ignore them).
   * @deprecated Support for remote import of styles will be removed as that is a dangerous
   *     practice. The simpler constructor should be used which defaults to disallow such imports.
   */
  @Deprecated
  public CssScanner(
      InternalPolicy policy, ResourceBundle messages, boolean shouldParseImportedStyles) {
    this.policy = policy;
    this.messages = messages;
    this.shouldParseImportedStyles = shouldParseImportedStyles;
    if (shouldParseImportedStyles) {
      logger.warn(
          "Allowing CSS imports from external URLs is a dangerous practice. It is recommended you "
              + "disable this feature. Support for this feature in AntiSamy is deprecated and will "
              + "be removed in a future release.");
    }
  }

  /**
   * Scans the contents of a full stylesheet (ex. a file based stylesheet or the complete stylesheet
   * contents as declared within &lt;style&gt; tags)
   *
   * @param taintedCss a <code>String</code> containing the contents of the CSS stylesheet to
   *     validate
   * @param sizeLimit the limit on the total size in bytes of any imported stylesheets
   * @return a <code>CleanResuts</code> object containing the results of the scan
   * @throws ScanException if an error occurs during scanning
   */
  public CleanResults scanStyleSheet(String taintedCss, int sizeLimit) throws ScanException {

    long startOfScan = System.currentTimeMillis();
    List<String> errorMessages = new ArrayList<String>();

    /* Check to see if the text starts with (\s)*<![CDATA[
     * and end with ]]>(\s)*.
     */

    Matcher m = cdataMatchPattern.matcher(taintedCss);
    boolean isCdata = m.matches();

    if (isCdata) {
      taintedCss = m.group(1);
    }

    CssHandler handler = new CssHandler(policy, errorMessages, messages);

    // parse the stylesheet
    parser.setDocumentHandler(handler);

    try {
      // parse the style declaration
      // note this does not count against the size limit because it
      // should already have been counted by the caller since it was
      // embedded in the HTML
      parser.parseStyleSheet(new InputSource(new StringReader(taintedCss)));
    } catch (IOException e) {
      // @TODO: should a CSSException als be a ScanException or what's the reason
      // that the batik ParseExeption was handle here but not the CSSException
      throw new ScanException(e);
    }

    String cleaned = getCleanStylesheetWithImports(sizeLimit, errorMessages, handler);

    if (isCdata) {
      cleaned = "<![CDATA[" + cleaned + "]]>";
    }

    return new CleanResults(startOfScan, cleaned, null, errorMessages);
  }

  /**
   * Scans the contents of an inline style declaration (ex. in the style attribute of an HTML tag)
   * and validates the style sheet according to this <code>CssScanner</code>'s policy file.
   *
   * @param taintedCss a <code>String</code> containing the contents of the CSS stylesheet to
   *     validate
   * @param tagName the name of the tag for which this inline style was declared
   * @param sizeLimit the limit on the total size in bites of any imported stylesheets
   * @return a <code>CleanResuts</code> object containing the results of the scan
   * @throws ScanException if an error occurs during scanning
   */
  public CleanResults scanInlineStyle(String taintedCss, String tagName, int sizeLimit)
      throws ScanException {

    long startOfScan = System.currentTimeMillis();

    List<String> errorMessages = new ArrayList<String>();

    CssHandler handler = new CssHandler(policy, errorMessages, messages, tagName);

    parser.setDocumentHandler(handler);

    try {
      // parse the inline style declaration
      // note this does not count against the size limit because it
      // should already have been counted by the caller since it was
      // embedded in the HTML
      parseStyleDeclaration(parser, taintedCss);
    } catch (IOException ioe) {
      throw new ScanException(ioe);
    }

    String cleaned = getCleanStylesheetWithImports(sizeLimit, errorMessages, handler);

    return new CleanResults(startOfScan, cleaned, null, errorMessages);
  }

  private static void parseStyleDeclaration(final Parser parser, final String cssDeclaration) throws CSSException, IOException {
    InputSource source = new InputSource(new StringReader(cssDeclaration));
    parser.parseStyleDeclaration(source);
  }

  private String getCleanStylesheetWithImports(
      int sizeLimit, List<String> errorMessages, CssHandler handler) throws ScanException {
    String cleaned = handler.getCleanStylesheet();
    if (shouldParseImportedStyles) {
      handler.emptyStyleSheet();
      parseImportedStylesheets(handler.getImportedStylesheetsURIList(), errorMessages, sizeLimit);
      // If there are styles to import they must be added to the beginning
      cleaned = handler.getCleanStylesheet() + cleaned;
    }
    return cleaned;
  }

  /**
   * Parses through a <code>LinkedList</code> of imported stylesheet URIs, this method parses
   * through those stylesheets and validates them
   *
   * @param stylesheets the <code>LinkedList</code> of stylesheet URIs to parse
   * @param errorMessages the list of error messages to append to
   * @param sizeLimit the limit on the total size in bites of any imported stylesheets
   * @throws ScanException if an error occurs during scanning
   * @deprecated Support for remote import of styles will be removed as that is dangerous.
   */
  @Deprecated
  private void parseImportedStylesheets(
      LinkedList<URI> stylesheets, List<String> errorMessages, int sizeLimit) throws ScanException {
    // if stylesheets were imported by the inline style declaration,
    // continue parsing the nested styles. Note this only happens
    // if CSS importing was enabled in the policy file
    if (!stylesheets.isEmpty()) {
      int importedStylesheets = 0;

      // Ensure that we have appropriate timeout values so we don't
      // get DoSed waiting for returns
      Timeout timeout = DEFAULT_TIMEOUT;
      try {
        timeout =
            Timeout.ofMilliseconds(Long.parseLong(policy.getDirective(Policy.CONNECTION_TIMEOUT)));
      } catch (NumberFormatException nfe) {
        // Use default if can't parse policy specified value
      }

      RequestConfig requestConfig =
          RequestConfig.custom()
              .setConnectTimeout(timeout)
              .setResponseTimeout(timeout)
              .setConnectionRequestTimeout(timeout)
              .build();

      HttpClient httpClient =
          HttpClientBuilder.create()
              .disableAutomaticRetries()
              .disableConnectionState()
              .disableCookieManagement()
              .setDefaultRequestConfig(requestConfig)
              .build();

      int allowedImports = Policy.DEFAULT_MAX_STYLESHEET_IMPORTS;
      try {
        allowedImports = Integer.parseInt(policy.getDirective("maxStyleSheetImports"));
      } catch (NumberFormatException nfe) {
        // Use default if can't parse policy specified value
      }

      while (!stylesheets.isEmpty()) {

        URI stylesheetUri = stylesheets.removeFirst();

        if (++importedStylesheets > allowedImports) {
          errorMessages.add(
              ErrorMessageUtil.getMessage(
                  messages,
                  ErrorMessageUtil.ERROR_CSS_IMPORT_EXCEEDED,
                  new Object[] {
                    HTMLEntityEncoder.htmlEntityEncode(stylesheetUri.toString()),
                    String.valueOf(allowedImports)
                  }));
          continue;
        }

        // Pulled directly from:
        // https://github.com/apache/httpcomponents-client/blob/5.1.x/httpclient5/src/test/java/org/apache/hc/client5/http/examples/ClientWithResponseHandler.java
        // Create a custom response handler to read in the stylesheet
        final HttpClientResponseHandler<String> responseHandler =
            new HttpClientResponseHandler<String>() {

              @Override
              public String handleResponse(final ClassicHttpResponse response) throws IOException {
                final int status = response.getCode();
                if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
                  final HttpEntity entity = response.getEntity();
                  try {
                    return entity != null ? EntityUtils.toString(entity) : null;
                    // @TODO: it is not clear how a Batik Parse Exception could be catched here
                    // the response handler does not know anything about the css parsing
                    // the css parsing happens after the response body (string) was returned.
                  } catch (final org.apache.hc.core5.http.ParseException ex) {
                    throw new ClientProtocolException(ex);
                  }
                } else {
                  throw new ClientProtocolException("Unexpected response status: " + status);
                }
              }
            };

        byte[] stylesheet = null;

        try {
          String responseBody = httpClient.execute(new HttpGet(stylesheetUri), responseHandler);
          // pull down stylesheet, observing size limit.
          // Note: There is a SpotBugs warning on the next line: "Found reliance on default encoding
          // in org.owasp.validator.css.CssScanner.parseImportedStylesheets(LinkedList, List, int):
          // String.getBytes()" but since this method is deprecated, not going to address it as it
          // will 'go away' eventually.
          stylesheet = responseBody.getBytes();
          if (stylesheet != null && stylesheet.length > sizeLimit) {
            errorMessages.add(
                ErrorMessageUtil.getMessage(
                    messages,
                    ErrorMessageUtil.ERROR_CSS_IMPORT_INPUT_SIZE,
                    new Object[] {
                      HTMLEntityEncoder.htmlEntityEncode(stylesheetUri.toString()),
                      String.valueOf(policy.getMaxInputSize())
                    }));
            stylesheet = null;
          }
        } catch (IOException ioe) {
          errorMessages.add(
              ErrorMessageUtil.getMessage(
                  messages,
                  ErrorMessageUtil.ERROR_CSS_IMPORT_FAILURE,
                  new Object[] {HTMLEntityEncoder.htmlEntityEncode(stylesheetUri.toString())}));
        }

        if (stylesheet != null) {
          // decrease the size limit based on the
          sizeLimit -= stylesheet.length;

          try {
            InputSource nextStyleSheet =
                new InputSource(
                    new InputStreamReader(
                        new ByteArrayInputStream(stylesheet), Charset.forName("UTF8")));
            parser.parseStyleSheet(nextStyleSheet);

          } catch (IOException ioe) {
            throw new ScanException(ioe);
          }
        }
      } // end while
    } // end if
  } // end parseImportedStylesheets()
}
