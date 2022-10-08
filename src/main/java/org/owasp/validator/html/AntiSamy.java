/*
 * Copyright (c) 2007-2022, Arshan Dabirsiaghi, Jason Li
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

package org.owasp.validator.html;

import java.io.*;

import org.owasp.validator.html.scan.AntiSamyDOMScanner;
import org.owasp.validator.html.scan.AntiSamySAXScanner;

/**
 * This and the {@code CleanResults} class are generally the only classes which the outside world
 * should be calling. The {@code scan()} method holds the meat and potatoes of AntiSamy. The file
 * contains a number of ways for {@code scan()}'ing, depending on the accessibility of the policy
 * file. However, it should be noted that the SAX scan type, which uses a SAX-based parser should be
 * the preferred way of using AntiSamy as it is much more efficient, and generally faster, than the
 * DOM-based parser.
 *
 * @author Arshan Dabirsiaghi
 */
public class AntiSamy {

    /**
     * Designates DOM scan type which calls the DOM parser.
     */
    public static final int DOM = 0;

    /**
     * Designates SAX scan type which calls the SAX parser.
     */
    public static final int SAX = 1;

    private Policy policy = null;

    public AntiSamy() throws PolicyException {
        InputStream inputStreamRoute = new ByteArrayInputStream(
                ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                        "<anti-samy-rules xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"antisamy.xsd\">\n" +
                        "    <directives>\n" +
                        "        <directive name=\"omitXmlDeclaration\" value=\"true\" />\n" +
                        "        <directive name=\"omitDoctypeDeclaration\" value=\"true\" />\n" +
                        "        <directive name=\"maxInputSize\" value=\"5000\" />\n" +
                        "        <directive name=\"formatOutput\" value=\"true\" />\n" +
                        "        <directive name=\"embedStyleSheets\" value=\"false\" />\n" +
                        "        <directive name=\"noopenerAndNoreferrerAnchors\" value=\"true\" />\n" +
                        "    </directives>\n" +
                        "    <common-regexps>\n" +
                        "        <regexp name=\"htmlTitle\" value=\"[\\p{L}\\p{N}\\s\\-_',:\\[\\]!\\./\\\\\\(\\)&amp;]*\" /> <!-- force non-empty with a '+' at the end instead of '*' -->\n" +
                        "        <regexp name=\"onsiteURL\"\n" +
                        "                value=\"^(?!//)(?![\\p{L}\\p{N}\\\\\\.\\#@\\$%\\+&amp;;\\-_~,\\?=/!]*(&amp;colon))[\\p{L}\\p{N}\\\\\\.\\#@\\$%\\+&amp;;\\-_~,\\?=/!]*\" />\n" +
                        "        <regexp name=\"offsiteURL\"\n" +
                        "                value=\"(\\s)*((ht|f)tp(s?)://|mailto:)[\\p{L}\\p{N}]+[~\\p{L}\\p{N}\\p{Zs}\\-_\\.@\\#\\$%&amp;;:,\\?=/\\+!\\(\\)]*(\\s)*\" />\n" +
                        "    </common-regexps>\n" +
                        "    <common-attributes>\n" +
                        "        <attribute name=\"lang\"\n" +
                        "                   description=\"The 'lang' attribute tells the browser what language the element's attribute values and content are written in\">\n" +
                        "            <regexp-list>\n" +
                        "                <regexp value=\"[a-zA-Z0-9-]{2,20}\" />\n" +
                        "            </regexp-list>\n" +
                        "        </attribute>\n" +
                        "        <attribute name=\"title\"\n" +
                        "                   description=\"The 'title' attribute provides text that shows up in a 'tooltip' when a user hovers their mouse over the element\">\n" +
                        "            <regexp-list>\n" +
                        "                <regexp name=\"htmlTitle\" />\n" +
                        "            </regexp-list>\n" +
                        "        </attribute>\n" +
                        "        <attribute name=\"href\" onInvalid=\"filterTag\">\n" +
                        "            <regexp-list>\n" +
                        "                <regexp name=\"onsiteURL\" />\n" +
                        "                <regexp name=\"offsiteURL\" />\n" +
                        "            </regexp-list>\n" +
                        "        </attribute>\n" +
                        "        <attribute name=\"align\"\n" +
                        "                   description=\"The 'align' attribute of an HTML element is a direction word, like 'left', 'right' or 'center'\">\n" +
                        "            <literal-list>\n" +
                        "                <literal value=\"center\" />\n" +
                        "                <literal value=\"left\" />\n" +
                        "                <literal value=\"right\" />\n" +
                        "                <literal value=\"justify\" />\n" +
                        "                <literal value=\"char\" />\n" +
                        "            </literal-list>\n" +
                        "        </attribute>\n" +
                        "    </common-attributes>\n" +
                        "    <global-tag-attributes>\n" +
                        "        <attribute name=\"title\" />\n" +
                        "        <attribute name=\"lang\" />\n" +
                        "    </global-tag-attributes>\n" +
                        "    <tags-to-encode>\n" +
                        "        <tag>g</tag>\n" +
                        "        <tag>grin</tag>\n" +
                        "    </tags-to-encode>\n" +
                        "    <tag-rules>\n" +
                        "        <tag name=\"script\" action=\"remove\" />\n" +
                        "        <tag name=\"noscript\" action=\"remove\" />\n" +
                        "        <tag name=\"iframe\" action=\"remove\" />\n" +
                        "        <tag name=\"frameset\" action=\"remove\" />\n" +
                        "        <tag name=\"frame\" action=\"remove\" />\n" +
                        "        <tag name=\"noframes\" action=\"remove\" />\n" +
                        "        <tag name=\"style\" action=\"remove\" />\n" +
                        "        <tag name=\"p\" action=\"validate\">\n" +
                        "            <attribute name=\"align\" />\n" +
                        "        </tag>\n" +
                        "        <tag name=\"div\" action=\"validate\" />\n" +
                        "        <tag name=\"i\" action=\"validate\" />\n" +
                        "        <tag name=\"b\" action=\"validate\" />\n" +
                        "        <tag name=\"em\" action=\"validate\" />\n" +
                        "        <tag name=\"blockquote\" action=\"validate\" />\n" +
                        "        <tag name=\"tt\" action=\"validate\" />\n" +
                        "        <tag name=\"strong\" action=\"validate\" />\n" +
                        "        <tag name=\"br\" action=\"truncate\" />\n" +
                        "        <tag name=\"quote\" action=\"validate\" />\n" +
                        "        <tag name=\"ecode\" action=\"validate\" />\n" +
                        "        <tag name=\"a\" action=\"validate\">\n" +
                        "            <attribute name=\"href\" onInvalid=\"filterTag\" />\n" +
                        "            <attribute name=\"nohref\">\n" +
                        "                <literal-list>\n" +
                        "                    <literal value=\"nohref\" />\n" +
                        "                    <literal value=\"\" />\n" +
                        "                </literal-list>\n" +
                        "            </attribute>\n" +
                        "            <attribute name=\"rel\">\n" +
                        "                <literal-list>\n" +
                        "                    <literal value=\"nofollow\" />\n" +
                        "                </literal-list>\n" +
                        "            </attribute>\n" +
                        "        </tag>\n" +
                        "        <tag name=\"ul\" action=\"validate\" />\n" +
                        "        <tag name=\"ol\" action=\"validate\" />\n" +
                        "        <tag name=\"li\" action=\"validate\" />\n" +
                        "    </tag-rules>\n" +
                        "    <css-rules>\n" +
                        "    </css-rules>\n" +
                        "    <allowed-empty-tags>\n" +
                        "        <literal-list>\n" +
                        "            <literal value=\"br\" />\n" +
                        "            <literal value=\"hr\" />\n" +
                        "            <literal value=\"a\" />\n" +
                        "            <literal value=\"img\" />\n" +
                        "            <literal value=\"link\" />\n" +
                        "            <literal value=\"iframe\" />\n" +
                        "            <literal value=\"script\" />\n" +
                        "            <literal value=\"object\" />\n" +
                        "            <literal value=\"applet\" />\n" +
                        "            <literal value=\"frame\" />\n" +
                        "            <literal value=\"base\" />\n" +
                        "            <literal value=\"param\" />\n" +
                        "            <literal value=\"meta\" />\n" +
                        "            <literal value=\"input\" />\n" +
                        "            <literal value=\"textarea\" />\n" +
                        "            <literal value=\"embed\" />\n" +
                        "            <literal value=\"basefont\" />\n" +
                        "            <literal value=\"col\" />\n" +
                        "            <literal value=\"div\" />\n" +
                        "        </literal-list>\n" +
                        "    </allowed-empty-tags>\n" +
                        "</anti-samy-rules>").getBytes());
        Policy policy = Policy.getInstance(inputStreamRoute);
        this.policy = policy;
    }

    public AntiSamy(Policy policy) {
        this.policy = policy;
    }

    /**
     * The <code>scan()</code> family of methods are the only methods the outside world should be
     * calling to invoke AntiSamy. This is the primary method that most AntiSamy users should be
     * using. This method scans the supplied HTML input and produces clean/sanitized results per the
     * previously configured AntiSamy policy using the SAX parser.
     *
     * @param taintedHTML Untrusted HTML which may contain malicious code.
     * @return A <code>CleanResults</code> object which contains information about the scan (including
     * the results).
     * @throws ScanException   When there is a problem encountered while scanning the HTML input.
     * @throws PolicyException When there is a problem validating or parsing the policy file.
     */
    public CleanResults scan(String taintedHTML) throws ScanException, PolicyException {
        return this.scan(taintedHTML, this.policy, SAX);
    }

    /**
     * This method scans the supplied HTML input and produces clean/sanitized results per the
     * previously configured AntiSamy policy using the specified DOM or SAX parser.
     *
     * @param taintedHTML Untrusted HTML which may contain malicious code.
     * @param scanType    The type of scan (DOM or SAX).
     * @return A <code>CleanResults</code> object which contains information about the scan (including
     * the results).
     * @throws ScanException   When there is a problem encountered while scanning the HTML input.
     * @throws PolicyException When there is a problem validating or parsing the policy file.
     */
    public CleanResults scan(String taintedHTML, int scanType) throws ScanException, PolicyException {

        return this.scan(taintedHTML, this.policy, scanType);
    }

    /**
     * This method scans the supplied HTML input and produces clean/sanitized results per the supplied
     * AntiSamy policy using the DOM parser.
     *
     * @param taintedHTML Untrusted HTML which may contain malicious code.
     * @param policy      The custom policy to enforce.
     * @return A <code>CleanResults</code> object which contains information about the scan (including
     * the results).
     * @throws ScanException   When there is a problem encountered while scanning the HTML input.
     * @throws PolicyException When there is a problem validating or parsing the policy file.
     */
    public CleanResults scan(String taintedHTML, Policy policy)
            throws ScanException, PolicyException {
        return this.scan(taintedHTML, policy, DOM);
    }

    /**
     * This method scans the supplied HTML input and produces clean/sanitized results per the supplied
     * AntiSamy policy using the specified DOM or SAX parser.
     *
     * @param taintedHTML Untrusted HTML which may contain malicious code.
     * @param policy      The custom policy to enforce.
     * @param scanType    The type of scan (DOM or SAX).
     * @return A <code>CleanResults</code> object which contains information about the scan (including
     * the results).
     * @throws ScanException   When there is a problem encountered while scanning the HTML input.
     * @throws PolicyException When there is a problem validating or parsing the policy file.
     */
    public CleanResults scan(String taintedHTML, Policy policy, int scanType)
            throws ScanException, PolicyException {
        if (policy == null) {
            throw new PolicyException("No policy loaded");
        }

        if (scanType == DOM) {
            return new AntiSamyDOMScanner(policy).scan(taintedHTML);
        } else {
            return new AntiSamySAXScanner(policy).scan(taintedHTML);
        }
    }

    /**
     * Use this method if caller has Streams rather than Strings for I/O. This uses the SAX parser. It
     * is useful for when the input being processed is expected to be very large and we don't
     * validate, but rather simply encode as bytes are consumed from the stream.
     *
     * @param reader Reader that produces the input, possibly a little at a time
     * @param writer Writer that receives the cleaned output, possibly a little at a time
     * @param policy Policy that directs the scan
     * @return CleanResults where the cleanHtml is null. If caller wants the clean HTML, it must
     * capture the writer's contents. When using Streams, caller generally doesn't want to create
     * a single string containing clean HTML.
     * @throws ScanException When there is a problem encountered while scanning the HTML input.
     */
    public CleanResults scan(Reader reader, Writer writer, Policy policy) throws ScanException {
        return (new AntiSamySAXScanner(policy)).scan(reader, writer);
    }

    /**
     * This method scans the supplied HTML input and produces clean/sanitized results per the supplied
     * AntiSamy policy file using the DOM parser.
     *
     * @param taintedHTML    Untrusted HTML which may contain malicious code.
     * @param policyFilename The file name of the custom policy to enforce.
     * @return A <code>CleanResults</code> object which contains information about the scan (including
     * the results).
     * @throws ScanException   When there is a problem encountered while scanning the HTML input.
     * @throws PolicyException When there is a problem validating or parsing the policy file.
     */
    public CleanResults scan(String taintedHTML, String policyFilename)
            throws ScanException, PolicyException {

        Policy policy = Policy.getInstance(policyFilename);

        return this.scan(taintedHTML, policy);
    }

    /**
     * This method scans the supplied HTML input and produces clean/sanitized results per the supplied
     * AntiSamy policy file using the DOM parser.
     *
     * @param taintedHTML Untrusted HTML which may contain malicious code.
     * @param policyFile  The File object of the custom policy to enforce.
     * @return A <code>CleanResults</code> object which contains information about the scan (including
     * the results).
     * @throws ScanException   When there is a problem encountered while scanning the HTML input.
     * @throws PolicyException When there is a problem validating or parsing the policy file.
     */
    public CleanResults scan(String taintedHTML, File policyFile)
            throws ScanException, PolicyException {

        Policy policy = Policy.getInstance(policyFile);

        return this.scan(taintedHTML, policy);
    }
}
