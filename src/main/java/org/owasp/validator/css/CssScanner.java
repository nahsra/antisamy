/*
 * Copyright (c) 2007-2021, Arshan Dabirsiaghi, Jason Li
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

import org.apache.batik.css.parser.ParseException;
import org.apache.batik.css.parser.Parser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import org.w3c.css.sac.InputSource;

/**
 * Encapsulates the parsing and validation of a CSS stylesheet or inline
 * declaration. To make use of this class, instantiate the scanner with the
 * desired policy and call either <code>scanInlineSheet()</code> or
 * <code>scanStyleSheet</code> as appropriate.
 * 
 * @see #scanInlineStyle(String, String, int)
 * @see #scanStyleSheet(String, int)
 * 
 * @author Jason Li
 */
public class CssScanner {

    protected static final int DEFAULT_TIMEOUT = 1000;

    private static final String CDATA = "^\\s*<!\\[CDATA\\[(.*)\\]\\]>\\s*$";
    
    /**
     * The parser to be used in any scanning
     */
    private final Parser parser = new Parser();

    /**
     * The policy file to be used in any scanning
     */
    private final InternalPolicy policy;

    /**
     * The message bundled to pull error messages from.
     */
    private final ResourceBundle messages;

    /**
     * The message bundled to pull error messages from.
     */
    private final boolean shouldParseImportedStyles;

    private static final Pattern cdataMatchPattern = Pattern.compile(CDATA, Pattern.DOTALL);

    /**
     * Constructs a scanner based on the given policy. This version of the constructor defaults
     * shouldParseImportedStyles to false, look at the other constructor for a description of that parameter.
     *
     * @param policy
     *                the policy to follow when scanning
     * @param messages
     *                the error message bundle to pull from
     */
    public CssScanner(InternalPolicy policy, ResourceBundle messages) {
        this(policy, messages, false);
    }

    /**
     * Constructs a scanner based on the given policy.
     * 
     * @param policy
     *                the policy to follow when scanning
     * @param messages
     *                the error message bundle to pull from
     * @param shouldParseImportedStyles
     *                Flag to indicate if styles within @import directives should be imported and
     *                parsed in the resulting style sheet. This boolean determines if URLs should be
     *                recognized when parsing styles (i.e., to fetch them or ignore them).
     */
    public CssScanner(InternalPolicy policy, ResourceBundle messages, boolean shouldParseImportedStyles) {
    	this.policy = policy;
    	this.messages = messages;
    	this.shouldParseImportedStyles = shouldParseImportedStyles;
    }

    /**
     * Scans the contents of a full stylesheet (ex. a file based stylesheet
     * or the complete stylesheet contents as declared within &lt;style&gt;
     * tags)
     * 
     * @param taintedCss
     *                a <code>String</code> containing the contents of the
     *                CSS stylesheet to validate
     * @param sizeLimit
     *                the limit on the total size in bytes of any imported
     *                stylesheets
     * @return a <code>CleanResuts</code> object containing the results of
     *         the scan
     * @throws ScanException
     *                 if an error occurs during scanning
     */
    public CleanResults scanStyleSheet(String taintedCss, int sizeLimit) throws ScanException {

        long startOfScan = System.currentTimeMillis();
        List<String> errorMessages = new ArrayList<String>();

        /* Check to see if the text starts with (\s)*<![CDATA[
         * and end with ]]>(\s)*.
         */

        Matcher m = cdataMatchPattern.matcher(taintedCss);
        boolean isCdata = m.matches();

        if ( isCdata ) {
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
	    } catch (IOException | ParseException e) {
            /*
             * ParseException, from batik, is unfortunately a RuntimeException.
             */
	        throw new ScanException(e);
        }

        String cleaned = getCleanStylesheetWithImports(sizeLimit, errorMessages, handler);

        if ( isCdata && !policy.isUseXhtml()) {
            cleaned = "<![CDATA[[" + cleaned + "]]>";
        }

        return new CleanResults(startOfScan, cleaned, null, errorMessages);
    }

    /**
     * Scans the contents of an inline style declaration (ex. in the style
     * attribute of an HTML tag) and validates the style sheet according to
     * this <code>CssScanner</code>'s policy file.
     * 
     * @param taintedCss
     *                a <code>String</code> containing the contents of the
     *                CSS stylesheet to validate
     * @param tagName
     *                the name of the tag for which this inline style was
     *                declared
     * 
     * @param sizeLimit
     *                the limit on the total size in bites of any imported
     *                stylesheets
     * @return a <code>CleanResuts</code> object containing the results of
     *         the scan
     * @throws ScanException
     *                 if an error occurs during scanning
     */
    public CleanResults scanInlineStyle(String taintedCss, String tagName,
	    int sizeLimit) throws ScanException {

        long startOfScan = System.currentTimeMillis();

        List<String> errorMessages = new ArrayList<String>();

        CssHandler handler = new CssHandler(policy, errorMessages, tagName, messages);

        parser.setDocumentHandler(handler);

        try {
            // parse the inline style declaration
            // note this does not count against the size limit because it
            // should already have been counted by the caller since it was
            // embedded in the HTML
            parser.parseStyleDeclaration(taintedCss);
        } catch (IOException ioe) {
            throw new ScanException(ioe);
        }

        String cleaned = getCleanStylesheetWithImports(sizeLimit, errorMessages, handler);

        return new CleanResults(startOfScan, cleaned, null, errorMessages);
    }

    private String getCleanStylesheetWithImports(int sizeLimit, List<String> errorMessages, CssHandler handler)
            throws ScanException {
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
     * Parses through a <code>LinkedList</code> of imported stylesheet
     * URIs, this method parses through those stylesheets and validates them
     *
     * @param stylesheets the <code>LinkedList</code> of stylesheet URIs to parse
     * @param errorMessages the list of error messages to append to
     * @param sizeLimit the limit on the total size in bites of any imported stylesheets
     * @throws ScanException if an error occurs during scanning
     */
    private void parseImportedStylesheets(LinkedList<URI> stylesheets, List<String> errorMessages, int sizeLimit)
            throws ScanException {
        // if stylesheets were imported by the inline style declaration,
        // continue parsing the nested styles. Note this only happens
        // if CSS importing was enabled in the policy file
        if (!stylesheets.isEmpty()) {
            int importedStylesheets = 0;

            // Ensure that we have appropriate timeout values so we don't
            // get DoSed waiting for returns
            int timeout = DEFAULT_TIMEOUT;
            try {
                timeout = Integer.parseInt(policy.getDirective(Policy.CONNECTION_TIMEOUT));
            } catch (NumberFormatException nfe) {
            }

            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .build();

            HttpClient httpClient = HttpClientBuilder.create().
                    disableAutomaticRetries().
                    disableConnectionState().
                    disableCookieManagement().
                    setDefaultRequestConfig(requestConfig).
                    build();

            int allowedImports = Policy.DEFAULT_MAX_STYLESHEET_IMPORTS;
            try {
                allowedImports = Integer.parseInt(policy.getDirective("maxStyleSheetImports"));
            } catch (NumberFormatException nfe) {
            }

            while (!stylesheets.isEmpty()) {

                URI stylesheetUri = stylesheets.removeFirst();

                if (++importedStylesheets > allowedImports) {
                    errorMessages.add(ErrorMessageUtil.getMessage(
                            messages,
                            ErrorMessageUtil.ERROR_CSS_IMPORT_EXCEEDED,
                            new Object[] {
                                    HTMLEntityEncoder.htmlEntityEncode(stylesheetUri.toString()),
                                    String.valueOf(allowedImports) }));
                    continue;
                }

                HttpGet stylesheetRequest = new HttpGet(stylesheetUri);

                byte[] stylesheet = null;
                try {
                    // pull down stylesheet, observing size limit
                    HttpResponse response = httpClient.execute(stylesheetRequest);
                    stylesheet = EntityUtils.toByteArray(response.getEntity());
                    if (stylesheet != null && stylesheet.length > sizeLimit) {
                        errorMessages.add(ErrorMessageUtil.getMessage(
                                messages,
                                ErrorMessageUtil.ERROR_CSS_IMPORT_INPUT_SIZE,
                                new Object[] {
                                        HTMLEntityEncoder.htmlEntityEncode(stylesheetUri.toString()),
                                        String.valueOf(policy.getMaxInputSize()) }));
                        stylesheet = null;
                    }
                } catch (IOException ioe) {
                    errorMessages.add(ErrorMessageUtil.getMessage(
                            messages,
                            ErrorMessageUtil.ERROR_CSS_IMPORT_FAILURE,
                            new Object[] { HTMLEntityEncoder.htmlEntityEncode(stylesheetUri.toString()) }));
                } finally {
                    stylesheetRequest.releaseConnection();
                }

                if (stylesheet != null) {
                    // decrease the size limit based on the
                    sizeLimit -= stylesheet.length;

                    try {
                        InputSource nextStyleSheet = new InputSource(
                                new InputStreamReader(new ByteArrayInputStream(stylesheet), Charset.forName("UTF8")));
                        parser.parseStyleSheet(nextStyleSheet);

                    } catch (IOException ioe) {
                        throw new ScanException(ioe);
                    }
                }
            } // end while
        } // end if
    } // end parseImportedStylesheets()
	
}
