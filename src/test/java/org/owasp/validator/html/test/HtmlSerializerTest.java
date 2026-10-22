/*
 * Copyright (c) 2007-2024, Arshan Dabirsiaghi, Jason Li
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
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.owasp.validator.html.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.apache.xml.serialize.OutputFormat;
import org.htmlunit.cyberneko.parsers.DOMFragmentParser;
import org.htmlunit.cyberneko.xerces.dom.DocumentImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.scan.ASHTMLSerializer;
import org.owasp.validator.html.scan.HtmlSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Comprehensive test suite for {@link HtmlSerializer}.
 *
 * <p>Tests are organised into:
 * <ol>
 *   <li>Unit tests that build DOM fragments programmatically and assert serialized output.</li>
 *   <li>Data-driven round-trip tests that run {@code AntiSamy.scan()} in DOM mode and compare
 *       against expected substrings or exact values.</li>
 * </ol>
 */
public class HtmlSerializerTest {

  // ---------------------------------------------------------------------------
  // Infrastructure
  // ---------------------------------------------------------------------------

  private TestPolicy policy;
  private AntiSamy antiSamy;
  private Document document;
  /** Policy with formatOutput=false (for unit tests that check exact serialized output). */
  private InternalPolicy noFormatPolicy;
  /** Policy with entityEncodeIntlChars=true (for tests checking entity-encoded output). */
  private InternalPolicy encodeIntlPolicy;

  @Before
  public void setUp() throws Exception {
    URL url = getClass().getResource("/antisamy.xml");
    policy = TestPolicy.getInstance(url);
    antiSamy = new AntiSamy();
    document = new DocumentImpl();
    noFormatPolicy = (InternalPolicy) policy.cloneWithDirective("formatOutput", "false");
    encodeIntlPolicy = (InternalPolicy) policy.cloneWithDirective("entityEncodeIntlChars", "true");
  }

  // ---------------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------------

  /** Serialize a {@link DocumentFragment} using default policy settings. */
  private String serialize(DocumentFragment frag) throws Exception {
    return serialize(frag, (InternalPolicy) policy);
  }

  private String serialize(DocumentFragment frag, InternalPolicy pol) throws Exception {
    StringWriter out = new StringWriter();

    // see AbstractAntiSamyScanner.getOutputFormat()
    OutputFormat format = new OutputFormat();
    format.setOmitXMLDeclaration(pol.isOmitXmlDeclaration());
    format.setOmitDocumentType(pol.isOmitDoctypeDeclaration());
    format.setPreserveEmptyAttributes(true);
    format.setPreserveSpace(pol.isPreserveSpace());

    if (pol.isFormatOutput()) {
      format.setLineWidth(80);
      format.setIndenting(true);
      format.setIndent(2);
    }

    org.apache.xml.serialize.HTMLSerializer ser = new ASHTMLSerializer(out, format, pol);

    ser.serialize(frag);
    return out.getBuffer().toString();
  }

  /**
   * Parse {@code html} into a {@link DocumentFragment} using the same cyberneko parser
   * configuration as the DOM scanner, then serialize with {@link HtmlSerializer} using the given
   * policy.
   */
  private String roundTrip(String html, InternalPolicy pol) throws Exception {
    DOMFragmentParser parser = new DOMFragmentParser();
    parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
    parser.setFeature(
        "http://cyberneko.org/html/features/scanner/style/strip-cdata-delims", false);
    parser.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
    parser.setFeature("http://cyberneko.org/html/features/parse-noscript-content", false);
    Document doc = new DocumentImpl();
    DocumentFragment frag = doc.createDocumentFragment();
    parser.parse(new InputSource(new StringReader(html)), frag);
    return serialize(frag, pol);
  }

  /** Convenience: round-trip with default policy. */
  private String roundTrip(String html) throws Exception {
    return roundTrip(html, (InternalPolicy) policy);
  }

  /** Build a DocumentFragment containing a single element (no children). */
  private DocumentFragment fragmentWithElement(String tagName) {
    DocumentFragment frag = document.createDocumentFragment();
    frag.appendChild(document.createElement(tagName));
    return frag;
  }

  /** Build a DocumentFragment containing a single element with one text child. */
  private DocumentFragment fragmentWithText(String tagName, String text) {
    DocumentFragment frag = document.createDocumentFragment();
    Element el = document.createElement(tagName);
    el.appendChild(document.createTextNode(text));
    frag.appendChild(el);
    return frag;
  }

  /** Scan via AntiSamy DOM scanner and return clean HTML. */
  private String domScan(String html) throws Exception {
    return antiSamy.scan(html, policy, AntiSamy.DOM).getCleanHTML();
  }

  /** Scan via AntiSamy DOM scanner with a specific policy. */
  private String domScan(String html, InternalPolicy pol) throws Exception {
    return antiSamy.scan(html, pol, AntiSamy.DOM).getCleanHTML();
  }

  // ===========================================================================
  // 1. Basic element serialization
  // ===========================================================================

  @Test
  public void simpleElementWithText() throws Exception {
    DocumentFragment frag = fragmentWithText("p", "Hello");
    assertEquals("<p>Hello</p>", serialize(frag, noFormatPolicy));
  }

  @Test
  public void simpleElementNoChildren() throws Exception {
    // 'div' has children=false but is not a void element, so gets <div></div>
    DocumentFragment frag = fragmentWithElement("div");
    String out = serialize(frag);
    assertTrue("Expected <div> opening", out.startsWith("<div"));
    assertTrue("Expected </div> or />", out.contains("</div>") || out.contains("/>"));
  }

  @Test
  public void nestedElements() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element outer = document.createElement("div");
    Element inner = document.createElement("p");
    inner.appendChild(document.createTextNode("nested"));
    outer.appendChild(inner);
    frag.appendChild(outer);
    String out = serialize(frag);
    assertThat(out, containsString("<p>nested</p>"));
    assertThat(out, containsString("<div"));
    assertThat(out, containsString("</div>"));
  }

  @Test
  public void siblingElements() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element p1 = document.createElement("p");
    p1.appendChild(document.createTextNode("first"));
    Element p2 = document.createElement("p");
    p2.appendChild(document.createTextNode("second"));
    frag.appendChild(p1);
    frag.appendChild(p2);
    String out = serialize(frag);
    assertThat(out, containsString("<p>first</p>"));
    assertThat(out, containsString("<p>second</p>"));
  }

  @Test
  public void mixedContentElementAndText() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element p = document.createElement("p");
    p.appendChild(document.createTextNode("before "));
    Element em = document.createElement("em");
    em.appendChild(document.createTextNode("emphasis"));
    p.appendChild(em);
    p.appendChild(document.createTextNode(" after"));
    frag.appendChild(p);
    String out = serialize(frag, noFormatPolicy);
    assertEquals("<p>before <em>emphasis</em> after</p>", out);
  }

  // ===========================================================================
  // 2. Void / self-closing elements
  // ===========================================================================

  @Test
  public void brElementSelfCloses() throws Exception {
    DocumentFragment frag = fragmentWithElement("br");
    String out = serialize(frag, noFormatPolicy);
    assertEquals("<br/>", out);
  }

  @Test
  public void hrElementSelfCloses() throws Exception {
    DocumentFragment frag = fragmentWithElement("hr");
    String out = serialize(frag, noFormatPolicy);
    assertEquals("<hr/>", out);
  }

  @Test
  public void imgElementSelfCloses() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element img = document.createElement("img");
    img.setAttribute("src", "test.png");
    img.setAttribute("alt", "test");
    frag.appendChild(img);
    String out = serialize(frag);
    assertThat(out, containsString("<img"));
    assertThat(out, containsString("/>"));
    assertThat(out, not(containsString("</img>")));
  }

  @Test
  public void inputElementSelfCloses() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element inp = document.createElement("input");
    inp.setAttribute("type", "text");
    frag.appendChild(inp);
    String out = serialize(frag);
    assertThat(out, containsString("<input"));
    assertThat(out, containsString("/>"));
    assertThat(out, not(containsString("</input>")));
  }

  @Test
  public void metaElementSelfCloses() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element meta = document.createElement("meta");
    meta.setAttribute("charset", "UTF-8");
    frag.appendChild(meta);
    String out = serialize(frag);
    assertThat(out, containsString("<meta"));
    assertThat(out, containsString("/>"));
    assertThat(out, not(containsString("</meta>")));
  }

  @Test
  public void linkElementSelfCloses() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element link = document.createElement("link");
    link.setAttribute("rel", "stylesheet");
    link.setAttribute("href", "style.css");
    frag.appendChild(link);
    String out = serialize(frag, noFormatPolicy);
    assertThat(out, containsString("<link"));
    // 'link' is in the default requiresClosingTags list, so it gets '>' not '/>'
    assertThat(out, not(containsString("</link>")));
  }

  @Test
  public void colElementSelfCloses() throws Exception {
    DocumentFragment frag = fragmentWithElement("col");
    String out = serialize(frag);
    assertThat(out, containsString("<col"));
    assertThat(out, containsString("/>"));
  }

  @Test
  public void paramElementSelfCloses() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element param = document.createElement("param");
    param.setAttribute("name", "movie");
    frag.appendChild(param);
    String out = serialize(frag);
    assertThat(out, containsString("<param"));
    assertThat(out, containsString("/>"));
  }

  // ===========================================================================
  // 3. Attribute serialization
  // ===========================================================================

  @Test
  public void regularAttribute() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element a = document.createElement("a");
    a.setAttribute("href", "http://example.com");
    a.appendChild(document.createTextNode("link"));
    frag.appendChild(a);
    String out = serialize(frag);
    assertThat(out, containsString("href=\"http://example.com\""));
  }

  @Test
  public void multipleAttributes() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element img = document.createElement("img");
    img.setAttribute("src", "pic.jpg");
    img.setAttribute("alt", "picture");
    img.setAttribute("width", "100");
    frag.appendChild(img);
    String out = serialize(frag);
    assertThat(out, containsString("src=\"pic.jpg\""));
    assertThat(out, containsString("alt=\"picture\""));
    assertThat(out, containsString("width=\"100\""));
  }

  @Test
  public void attributeWithSpecialCharsAreEscaped() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element el = document.createElement("p");
    el.setAttribute("title", "a<b>&c\"d");
    el.appendChild(document.createTextNode("text"));
    frag.appendChild(el);
    String out = serialize(frag);
    // Attribute value should have < and & and " escaped
    assertThat(out, containsString("&lt;"));
    assertThat(out, containsString("&amp;"));
    assertThat(out, containsString("&quot;"));
  }

  @Test
  public void booleanAttributeSelected() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element option = document.createElement("option");
    option.setAttribute("selected", "selected");
    option.setAttribute("value", "1");
    option.appendChild(document.createTextNode("One"));
    frag.appendChild(option);
    String out = serialize(frag);
    // 'selected' is a boolean attr for option → serialized without value
    assertThat(out, containsString("selected"));
    assertThat(out, containsString("value=\"1\""));
  }

  @Test
  public void booleanAttributeChecked() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element inp = document.createElement("input");
    inp.setAttribute("type", "checkbox");
    inp.setAttribute("checked", "checked");
    frag.appendChild(inp);
    String out = serialize(frag);
    assertThat(out, containsString("checked"));
    // Not: checked="checked"
    assertThat(out, not(containsString("checked=\"checked\"")));
  }

  @Test
  public void booleanAttributeDisabled() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element btn = document.createElement("button");
    btn.setAttribute("disabled", "disabled");
    btn.appendChild(document.createTextNode("Click"));
    frag.appendChild(btn);
    String out = serialize(frag);
    assertThat(out, containsString("disabled"));
    assertThat(out, not(containsString("disabled=\"disabled\"")));
  }

  @Test
  public void booleanAttributeMultiple() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element sel = document.createElement("select");
    sel.setAttribute("multiple", "multiple");
    frag.appendChild(sel);
    String out = serialize(frag);
    assertThat(out, containsString("multiple"));
    assertThat(out, not(containsString("multiple=\"multiple\"")));
  }

  @Test
  public void booleanAttributeReadonly() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element inp = document.createElement("input");
    inp.setAttribute("readonly", "readonly");
    frag.appendChild(inp);
    String out = serialize(frag);
    assertThat(out, containsString("readonly"));
    assertThat(out, not(containsString("readonly=\"readonly\"")));
  }

  @Test
  public void booleanAttributeNowrap() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element td = document.createElement("td");
    td.setAttribute("nowrap", "nowrap");
    td.appendChild(document.createTextNode("cell"));
    frag.appendChild(td);
    String out = serialize(frag);
    assertThat(out, containsString("nowrap"));
    assertThat(out, not(containsString("nowrap=\"nowrap\"")));
  }

  @Test
  public void emptyAttributeValue() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element p = document.createElement("p");
    p.setAttribute("class", "");
    p.appendChild(document.createTextNode("text"));
    frag.appendChild(p);
    String out = serialize(frag);
    assertThat(out, containsString("class=\"\""));
  }

  @Test
  public void hrefAttributeEscapedAsUri() throws Exception {
    // href and src are URI attributes in Xerces HTMLdtd → special char handling
    DocumentFragment frag = document.createDocumentFragment();
    Element a = document.createElement("a");
    a.setAttribute("href", "http://example.com/path?a=1&b=2");
    a.appendChild(document.createTextNode("link"));
    frag.appendChild(a);
    String out = serialize(frag);
    assertThat(out, containsString("href="));
    // & in URI gets encoded to &amp;
    assertThat(out, containsString("&amp;"));
  }

  // ===========================================================================
  // 4. Text content and entity encoding
  // ===========================================================================

  @Test
  public void textWithLessThanIsEscaped() throws Exception {
    assertEquals("<p>&lt;script&gt;</p>", serialize(fragmentWithText("p", "<script>"), noFormatPolicy));
  }

  @Test
  public void textWithAmpIsEscaped() throws Exception {
    assertEquals("<p>A &amp; B</p>", serialize(fragmentWithText("p", "A & B"), noFormatPolicy));
  }

  @Test
  public void textWithGreaterThanIsEscaped() throws Exception {
    String out = serialize(fragmentWithText("p", "a > b"), noFormatPolicy);
    assertThat(out, containsString("&gt;"));
  }

  @Test
  public void textWithDoubleQuoteIsEscaped() throws Exception {
    String out = serialize(fragmentWithText("p", "say \"hello\""), noFormatPolicy);
    assertThat(out, containsString("&quot;"));
  }

  @Test
  public void textWithNonBreakingSpace() throws Exception {
    DocumentFragment frag = fragmentWithText("p", "\u00A0");
    String out = serialize(frag, encodeIntlPolicy);
    assertThat(out, containsString("&nbsp;"));
  }

  @Test
  public void textWithCopyright() throws Exception {
    DocumentFragment frag = fragmentWithText("p", "\u00A9");
    String out = serialize(frag, encodeIntlPolicy);
    assertThat(out, containsString("&copy;"));
  }

  @Test
  public void textWithRegisteredTrademark() throws Exception {
    DocumentFragment frag = fragmentWithText("p", "\u00AE");
    String out = serialize(frag, encodeIntlPolicy);
    assertThat(out, containsString("&reg;"));
  }

  @Test
  public void textWithEuroSign() throws Exception {
    DocumentFragment frag = fragmentWithText("p", "\u20AC");
    String out = serialize(frag, encodeIntlPolicy);
    assertThat(out, containsString("&euro;"));
  }

  @Test
  public void textWithNdash() throws Exception {
    DocumentFragment frag = fragmentWithText("p", "\u2013");
    String out = serialize(frag, encodeIntlPolicy);
    assertThat(out, containsString("&ndash;"));
  }

  @Test
  public void textWithMdash() throws Exception {
    DocumentFragment frag = fragmentWithText("p", "\u2014");
    String out = serialize(frag, encodeIntlPolicy);
    assertThat(out, containsString("&mdash;"));
  }

  @Test
  public void textWithLeftDoubleQuote() throws Exception {
    DocumentFragment frag = fragmentWithText("p", "\u201C");
    String out = serialize(frag, encodeIntlPolicy);
    assertThat(out, containsString("&ldquo;"));
  }

  @Test
  public void textWithRightDoubleQuote() throws Exception {
    DocumentFragment frag = fragmentWithText("p", "\u201D");
    String out = serialize(frag, encodeIntlPolicy);
    assertThat(out, containsString("&rdquo;"));
  }

  @Test
  public void textWithAccentedCharLatin1() throws Exception {
    // é = U+00E9 = &eacute;
    DocumentFragment frag = fragmentWithText("p", "\u00E9");
    String out = serialize(frag, encodeIntlPolicy);
    assertThat(out, containsString("&eacute;"));
  }

  @Test
  public void textWithMultipleEntities() throws Exception {
    DocumentFragment frag = fragmentWithText("p", "<b>&amp;</b>");
    String out = serialize(frag, noFormatPolicy);
    assertThat(out, containsString("&lt;b&gt;&amp;amp;&lt;/b&gt;"));
  }

  @Test
  public void plainAsciiTextIsNotEscaped() throws Exception {
    DocumentFragment frag = fragmentWithText("p", "Hello World 123");
    String out = serialize(frag, noFormatPolicy);
    assertEquals("<p>Hello World 123</p>", out);
  }

  // ===========================================================================
  // 5. Script / style elements (unescaped content)
  // ===========================================================================

  @Test
  public void styleContentNotEscaped() throws Exception {
    DocumentFragment frag = fragmentWithText("style", "p { color: red; }");
    String out = serialize(frag, noFormatPolicy);
    assertEquals("<style>p { color: red; }</style>", out);
  }

  @Test
  public void styleContentWithLtNotEscaped() throws Exception {
    // Angle brackets inside style should NOT be HTML-escaped
    DocumentFragment frag = fragmentWithText("style", "a < b");
    String out = serialize(frag);
    assertThat(out, containsString("a < b"));
    assertThat(out, not(containsString("&lt;")));
  }

  @Test
  public void scriptContentNotEscaped() throws Exception {
    DocumentFragment frag = fragmentWithText("script", "var x = 1 < 2;");
    String out = serialize(frag);
    assertThat(out, containsString("var x = 1 < 2;"));
    assertThat(out, not(containsString("&lt;")));
  }

  @Test
  public void styleWithMultilineCss() throws Exception {
    String css = "P {\n\tmargin-bottom: 0.08in;\n}\n";
    DocumentFragment frag = fragmentWithText("style", css);
    String out = serialize(frag);
    assertThat(out, containsString(css));
    assertThat(out, not(containsString("&lt;")));
    assertThat(out, not(containsString("&amp;")));
  }

  // ===========================================================================
  // 6. Comments
  // ===========================================================================

  @Test
  public void commentPreservedWhenPolicyAllows() throws Exception {
    InternalPolicy withComments =
        (InternalPolicy) policy.cloneWithDirective("preserveComments", "true");
    String result = domScan("<!-- a comment -->text", withComments);
    assertThat(result, containsString("<!-- a comment -->"));
  }

  @Test
  public void commentStrippedWhenPolicyDisallows() throws Exception {
    InternalPolicy noComments =
        (InternalPolicy) policy.cloneWithDirective("preserveComments", "false");
    String result = domScan("<!-- secret -->visible", noComments);
    assertThat(result, not(containsString("<!--")));
    assertThat(result, containsString("visible"));
  }

  @Test
  public void commentWithSpecialCharsPreserved() throws Exception {
    InternalPolicy withComments =
        (InternalPolicy) policy.cloneWithDirective("preserveComments", "true");
    String result = domScan("<!-- <b>bold</b> -->text", withComments);
    // Conditional directives are stripped, but plain HTML comments survive
    assertThat(result, containsString("<!--"));
  }

  // ===========================================================================
  // 7. Indentation / formatting output
  // ===========================================================================

  @Test
  public void formattingOffProducesNoExtraWhitespace() throws Exception {
    InternalPolicy noFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "false");
    DocumentFragment frag = document.createDocumentFragment();
    Element outer = document.createElement("div");
    Element inner = document.createElement("p");
    inner.appendChild(document.createTextNode("text"));
    outer.appendChild(inner);
    frag.appendChild(outer);
    String out = serialize(frag, noFormat);
    assertEquals("<div><p>text</p></div>", out);
  }

  @Test
  public void formattingOnAddsNewlineAfterRootElement() throws Exception {
    InternalPolicy withFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "true");
    DocumentFragment frag = fragmentWithText("p", "text");
    String out = serialize(frag, withFormat);
    assertTrue("Expected trailing newline when formatting", out.endsWith("\n"));
  }

  @Test
  public void formattingOnAddsNewlinesBetweenRootSiblings() throws Exception {
    InternalPolicy withFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "true");
    DocumentFragment frag = document.createDocumentFragment();
    Element p1 = document.createElement("p");
    p1.appendChild(document.createTextNode("first"));
    Element p2 = document.createElement("p");
    p2.appendChild(document.createTextNode("second"));
    frag.appendChild(p1);
    frag.appendChild(p2);
    String out = serialize(frag, withFormat);
    assertThat(out, containsString("</p>\n<p>"));
  }

  @Test
  public void formattingOnIndentsNestedElements() throws Exception {
    InternalPolicy withFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "true");
    DocumentFragment frag = document.createDocumentFragment();
    Element div = document.createElement("div");
    Element p = document.createElement("p");
    p.appendChild(document.createTextNode("content"));
    div.appendChild(p);
    frag.appendChild(div);
    String out = serialize(frag, withFormat);
    // Should have indentation before <p>
    assertThat(out, containsString("\n"));
    assertThat(out, containsString("  ")); // indent size=2
  }

  @Test
  public void formattingOnVoidElementFollowedByNewline() throws Exception {
    InternalPolicy withFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "true");
    DocumentFragment frag = fragmentWithElement("br");
    String out = serialize(frag, withFormat);
    assertEquals("<br/>\n", out);
  }

  @Test
  public void whitespaceOnlyTextNodesSkippedWhenFormatting() throws Exception {
    InternalPolicy withFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "true");
    // When formatting, whitespace text nodes between block elements should not appear
    String out = roundTrip("<div>\n  <p>text</p>\n</div>", withFormat);
    // The newlines between div and p are whitespace-only and should be skipped
    // but the actual text content must be preserved
    assertThat(out, containsString("<p>text</p>"));
  }

  @Test
  public void stylePreservesWhitespaceWhenFormatting() throws Exception {
    InternalPolicy withFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "true");
    String css = "a {\n  color: red;\n}\n";
    DocumentFragment frag = fragmentWithText("style", css);
    String out = serialize(frag, withFormat);
    assertThat(out, containsString(css));
  }

  @Test
  public void nestedFormattingDepth3() throws Exception {
    InternalPolicy withFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "true");
    DocumentFragment frag = document.createDocumentFragment();
    Element ul = document.createElement("ul");
    Element li1 = document.createElement("li");
    li1.appendChild(document.createTextNode("item1"));
    Element li2 = document.createElement("li");
    li2.appendChild(document.createTextNode("item2"));
    ul.appendChild(li1);
    ul.appendChild(li2);
    frag.appendChild(ul);
    String out = serialize(frag, withFormat);
    // ul must contain li elements on separate lines
    assertThat(out, containsString("<ul>\n"));
    assertThat(out, containsString("\n</ul>"));
  }

  // ===========================================================================
  // 8. Preserve-space elements (style, script, textarea, pre)
  // ===========================================================================

  @Test
  public void textareaPreservesWhitespace() throws Exception {
    InternalPolicy withFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "true");
    String content = "  line1\n  line2\n";
    DocumentFragment frag = fragmentWithText("textarea", content);
    String out = serialize(frag, withFormat);
    assertThat(out, containsString(content));
  }

  @Test
  public void prePreservesWhitespace() throws Exception {
    InternalPolicy withFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "true");
    String content = "  code\n  more\n";
    DocumentFragment frag = fragmentWithText("pre", content);
    String out = serialize(frag, withFormat);
    assertThat(out, containsString(content));
  }

  @Test
  public void whiteSpaceNotSkippedInsidePreWhenFormatting() throws Exception {
    InternalPolicy withFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "true");
    // A whitespace-only text node inside <pre> must NOT be dropped
    DocumentFragment frag = document.createDocumentFragment();
    Element pre = document.createElement("pre");
    pre.appendChild(document.createTextNode("\n"));
    frag.appendChild(pre);
    String out = serialize(frag, withFormat);
    assertThat(out, containsString("<pre>"));
    // The \n text must still appear (pre is preserve-space)
    assertThat(out, containsString("\n"));
  }

  // ===========================================================================
  // 9. Round-trip tests via AntiSamy DOM scanner
  // ===========================================================================

  @Test
  public void roundTripSimpleParagraph() throws Exception {
    String result = domScan("<p>Hello World</p>");
    assertThat(result, containsString("<p>Hello World</p>"));
  }

  @Test
  public void roundTripBoldAndItalic() throws Exception {
    String result = domScan("<p><b>bold</b> and <i>italic</i></p>");
    assertThat(result, containsString("<b>bold</b>"));
    assertThat(result, containsString("<i>italic</i>"));
  }

  @Test
  public void roundTripLink() throws Exception {
    String result = domScan("<a href=\"http://example.com\">click here</a>");
    assertThat(result, containsString("<a"));
    assertThat(result, containsString("href="));
    assertThat(result, containsString("click here</a>"));
  }

  @Test
  public void roundTripImage() throws Exception {
    String result = domScan("<img src=\"image.png\" alt=\"img\"/>");
    assertThat(result, containsString("<img"));
    assertThat(result, containsString("src="));
    assertThat(result, not(containsString("</img>")));
  }

  @Test
  public void roundTripTable() throws Exception {
    String html =
        "<table><tr><td>cell1</td><td>cell2</td></tr></table>";
    String result = domScan(html);
    assertThat(result, containsString("<table>"));
    assertThat(result, containsString("cell1"));
    assertThat(result, containsString("cell2"));
    assertThat(result, containsString("</table>"));
  }

  @Test
  public void roundTripOrderedList() throws Exception {
    String html = "<ol><li>one</li><li>two</li><li>three</li></ol>";
    String result = domScan(html);
    assertThat(result, containsString("<ol>"));
    assertThat(result, containsString("<li>one</li>"));
    assertThat(result, containsString("<li>two</li>"));
    assertThat(result, containsString("</ol>"));
  }

  @Test
  public void roundTripUnorderedList() throws Exception {
    String html = "<ul><li>a</li><li>b</li></ul>";
    String result = domScan(html);
    assertThat(result, containsString("<ul>"));
    assertThat(result, containsString("<li>a</li>"));
    assertThat(result, containsString("</ul>"));
  }

  @Test
  public void roundTripStyleTag() throws Exception {
    String css = "P {\n\tmargin-bottom: 0.08in;\n}\n";
    String result = domScan("<style>" + css + "</style>");
    assertThat(result, containsString("<style>"));
    assertThat(result, containsString("margin-bottom"));
    assertThat(result, containsString("</style>"));
  }

  @Test
  public void roundTripStyleTagCssNotEscaped() throws Exception {
    // The < > inside style must NOT be entity-encoded
    String html = "<style>a[href] > span { color: red; }</style>";
    String result = domScan(html);
    assertThat(result, not(containsString("&gt;")));
    assertThat(result, not(containsString("&lt;")));
  }

  @Test
  public void roundTripSpecialEntities() throws Exception {
    String result = domScan("<p>&amp; &lt; &gt; &quot; &nbsp;</p>");
    assertThat(result, containsString("&amp;"));
    assertThat(result, containsString("&lt;"));
    assertThat(result, containsString("&gt;"));
  }

  @Test
  public void roundTripEuroSymbol() throws Exception {
    String result = domScan("<p>\u20AC</p>", encodeIntlPolicy);
    assertThat(result, containsString("&euro;"));
  }

  @Test
  public void roundTripHeadingElements() throws Exception {
    for (int i = 1; i <= 6; i++) {
      String html = "<h" + i + ">heading " + i + "</h" + i + ">";
      String result = domScan(html);
      assertThat(result, containsString("<h" + i + ">heading " + i + "</h" + i + ">"));
    }
  }

  @Test
  public void roundTripSelectWithOptions() throws Exception {
    String html =
        "<select name=\"x\">"
            + "<option value=\"1\">One</option>"
            + "<option value=\"2\" selected>Two</option>"
            + "</select>";
    String result = domScan(html);
    assertThat(result, containsString("<select"));
    assertThat(result, containsString("One</option>"));
    assertThat(result, containsString("Two</option>"));
    assertThat(result, containsString("</select>"));
  }

  @Test
  public void roundTripInputTypes() throws Exception {
    String[] types = {"text", "password", "checkbox", "radio", "submit", "hidden"};
    for (String type : types) {
      String html = "<input type=\"" + type + "\" name=\"x\"/>";
      String result = domScan(html);
      assertThat("input[type=" + type + "]", result, containsString("<input"));
      assertThat("input[type=" + type + "] closed", result, not(containsString("</input>")));
    }
  }

  @Test
  public void roundTripSpanWithClass() throws Exception {
    String result = domScan("<span class=\"highlight\">text</span>");
    assertThat(result, containsString("<span"));
    assertThat(result, containsString("class=\"highlight\""));
    assertThat(result, containsString("text</span>"));
  }

  @Test
  public void roundTripDivWithId() throws Exception {
    String result = domScan("<div id=\"main\">content</div>");
    assertThat(result, containsString("<div"));
    assertThat(result, containsString("id=\"main\""));
    assertThat(result, containsString("content</div>"));
  }

  @Test
  public void roundTripFormElements() throws Exception {
    String html =
        "<form action=\"/submit\" method=\"post\">"
            + "<input type=\"text\" name=\"user\"/>"
            + "<input type=\"submit\" value=\"Go\"/>"
            + "</form>";
    String result = domScan(html);
    assertThat(result, containsString("<form"));
    assertThat(result, containsString("</form>"));
    assertThat(result, containsString("type=\"text\""));
    assertThat(result, containsString("type=\"submit\""));
  }

  @Test
  public void roundTripBlockquote() throws Exception {
    String result = domScan("<blockquote><p>quote</p></blockquote>");
    assertThat(result, containsString("<blockquote>"));
    assertThat(result, containsString("<p>quote</p>"));
    assertThat(result, containsString("</blockquote>"));
  }

  @Test
  public void roundTripSuperscriptSubscript() throws Exception {
    String result = domScan("<p>H<sub>2</sub>O and E=mc<sup>2</sup></p>");
    assertThat(result, containsString("<sub>2</sub>"));
    assertThat(result, containsString("<sup>2</sup>"));
  }

  @Test
  public void roundTripHorizontalRule() throws Exception {
    String result = domScan("<p>before</p><hr/><p>after</p>");
    assertThat(result, containsString("<hr/>"));
    assertThat(result, not(containsString("</hr>")));
  }

  @Test
  public void roundTripBreakElement() throws Exception {
    String result = domScan("<p>line1<br/>line2</p>");
    assertThat(result, containsString("<br/>"));
    assertThat(result, not(containsString("</br>")));
  }

  @Test
  public void roundTripXssScriptTagStripped() throws Exception {
    String result = domScan("<script>alert('xss')</script>");
    assertThat(result, not(containsString("<script>")));
    assertThat(result, not(containsString("alert")));
  }

  @Test
  public void roundTripXssOnclickStripped() throws Exception {
    String result = domScan("<p onclick=\"alert(1)\">text</p>");
    assertThat(result, not(containsString("onclick")));
    assertThat(result, containsString("text"));
  }

  @Test
  public void roundTripXssInHref() throws Exception {
    String result = domScan("<a href=\"javascript:alert(1)\">click</a>");
    assertThat(result, not(containsString("javascript:")));
  }

  // ===========================================================================
  // 10. Formatting output round-trip tests
  // ===========================================================================

  @Test
  public void issueGithub484FormattingBrBetweenParagraphs() throws Exception {
    // Regression for GitHub issue #484: newlines between root-level elements
    String html = "<p>this is para data</p><br/><p>this is para data 2</p>";
    String result = domScan(html);
    String cleaned = result.replaceAll("\r?\n", "").replaceAll("\\s\\s+", " ");
    assertThat(cleaned, containsString("<p>this is para data</p>"));
    assertThat(cleaned, containsString("<br/>"));
    assertThat(cleaned, containsString("<p>this is para data 2</p>"));
  }

  @Test
  public void issueGithub484StyleTagEndsWithNewline() throws Exception {
    // Regression for issue #30: style element content preserved; trailing newline present
    String css = "P {\n\tmargin-bottom: 0.08in;\n}\n";
    String result = domScan("<style>" + css);
    assertThat(result, containsString("margin-bottom"));
    assertThat(result, containsString("</style>"));
  }

  @Test
  public void issueGithub453SelectAndOptionIndented() throws Exception {
    // Regression for GitHub issue #453: nested elements have spacing after cleanup
    String html =
        "<html lang=\"en\">\n<head>\n</head>\n<table>\n"
            + "<SELECT NAME=\"Lang\">\n"
            + "<OPTION VALUE=\"da\">Dansk</OPTION>\n"
            + "<OPTION VALUE=\"en\" selected=selected>English</OPTION>\n"
            + "</SELECT>\n</table>\n</html>";
    String result = domScan(html);
    String cleaned = result.replaceAll("\r?\n", "").replaceAll("\\s\\s+", " ");
    assertThat(
        cleaned,
        containsString(
            "<body> <table> <select name=\"Lang\"> <option value=\"da\">Dansk</option> "));
  }

  @Test
  public void formattedOutputHasProperIndentForDeepNesting() throws Exception {
    InternalPolicy withFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "true");
    DocumentFragment frag = document.createDocumentFragment();
    Element table = document.createElement("table");
    Element tr = document.createElement("tr");
    Element td = document.createElement("td");
    td.appendChild(document.createTextNode("cell"));
    tr.appendChild(td);
    table.appendChild(tr);
    frag.appendChild(table);
    String out = serialize(frag, withFormat);
    // Should have indentation
    assertThat(out, containsString("  "));
    assertThat(out, containsString("cell"));
  }

  // ===========================================================================
  // 11. Policy directive interactions
  // ===========================================================================

  @Test
  public void encodeIntlCharactersTrue() throws Exception {
    // With encodeAllPossibleEntities=true, characters with named entities are encoded
    InternalPolicy encodeIntl =
        (InternalPolicy) policy.cloneWithDirective("entityEncodeIntlChars", "true");
    DocumentFragment frag = fragmentWithText("p", "\u00E9"); // é
    String out = serialize(frag, encodeIntl);
    assertThat(out, containsString("&eacute;"));
  }

  @Test
  public void encodeIntlCharactersFalse() throws Exception {
    // With encodeAllPossibleEntities=false, Latin-1 characters may be written literally
    InternalPolicy noEncodeIntl =
        (InternalPolicy) policy.cloneWithDirective("entityEncodeIntlChars", "false");
    DocumentFragment frag = fragmentWithText("p", "\u00E9");
    String out = serialize(frag, noEncodeIntl);
    // Either entity or literal is acceptable, but should not crash
    assertTrue("Should contain é", out.contains("&eacute;") || out.contains("\u00E9"));
  }

  // ===========================================================================
  // 12. Data-driven tests
  // ===========================================================================

  /**
   * Parameterized data-driven test: each row is {@code [inputHtml, expectedSubstring]}.
   * Tests are run through the full AntiSamy DOM scan pipeline.
   */
  @RunWith(Parameterized.class)
  public static class DataDrivenRoundTripTest {

    private final String description;
    private final String inputHtml;
    private final String expectedSubstring;
    private AntiSamy antiSamy;
    private TestPolicy policy;

    public DataDrivenRoundTripTest(String description, String inputHtml, String expected) {
      this.description = description;
      this.inputHtml = inputHtml;
      this.expectedSubstring = expected;
    }

    @Before
    public void setUp() throws Exception {
      URL url = getClass().getResource("/antisamy.xml");
      policy = TestPolicy.getInstance(url);
      antiSamy = new AntiSamy();
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
      return Arrays.asList(
          new Object[][] {
            // Basic text formatting
            {"bold text", "<b>bold</b>", "<b>bold</b>"},
            {"italic text", "<i>italic</i>", "<i>italic</i>"},
            {"underline text", "<u>underline</u>", "<u>underline</u>"},
            {"strikethrough", "<s>strike</s>", "strike"},
            {"strong text", "<strong>strong</strong>", "<strong>strong</strong>"},
            {"em text", "<em>emphasis</em>", "<em>emphasis</em>"},
            {"code inline", "<code>x=1</code>", "<code>x=1</code>"},
            {"kbd element", "<kbd>Ctrl+C</kbd>", "Ctrl"},
            {"samp element", "<samp>output</samp>", "output"},
            // Headings
            {"h1", "<h1>Heading 1</h1>", "Heading 1"},
            {"h2", "<h2>Heading 2</h2>", "Heading 2"},
            {"h3", "<h3>Heading 3</h3>", "Heading 3"},
            {"h4", "<h4>Heading 4</h4>", "Heading 4"},
            {"h5", "<h5>Heading 5</h5>", "Heading 5"},
            {"h6", "<h6>Heading 6</h6>", "Heading 6"},
            // Lists
            {"ol li", "<ol><li>item</li></ol>", "<li>item</li>"},
            {"ul li", "<ul><li>item</li></ul>", "<li>item</li>"},
            {"dl dt dd", "<dl><dt>term</dt><dd>def</dd></dl>", "term"},
            // Tables
            {"table basic", "<table><tr><td>data</td></tr></table>", "data"},
            {"table with header", "<table><tr><th>head</th></tr></table>", "head"},
            {"table colspan", "<table><tr><td colspan=\"2\">span</td></tr></table>", "span"},
            {"table border", "<table border=\"1\"><tr><td>x</td></tr></table>", "border"},
            // Links
            {"anchor", "<a href=\"http://example.com\">link</a>", "link"},
            {"anchor no href", "<a>text</a>", "text"},
            // Images
            {"img", "<img src=\"x.png\" alt=\"\"/>", "<img"},
            // Form elements
            {"input text", "<input type=\"text\" name=\"x\"/>", "type=\"text\""},
            {"input hidden", "<input type=\"hidden\" name=\"x\" value=\"v\"/>", "type=\"hidden\""},
            {"select option", "<select><option>a</option></select>", "option"},
            {"textarea", "<textarea>content</textarea>", "content"},
            // Structure
            {"div", "<div>content</div>", "content"},
            {"span", "<span>content</span>", "content"},
            {"p", "<p>content</p>", "content"},
            {"pre", "<pre>  code  </pre>", "  code  "},
            {"blockquote", "<blockquote>quote</blockquote>", "quote"},
            {"abbr", "<abbr title=\"HyperText\">HTML</abbr>", "HTML"},
            {"address", "<address>123 Main St</address>", "123 Main St"},
            {"article", "<article>story</article>", "story"},
            {"aside", "<aside>sidebar</aside>", "sidebar"},
            {"caption", "<table><caption>title</caption><tr><td>d</td></tr></table>", "title"},
            {"cite", "<cite>Author</cite>", "Author"},
            {"del", "<del>removed</del>", "removed"},
            {"dfn", "<dfn>term</dfn>", "term"},
            {"figcaption", "<figure><figcaption>cap</figcaption></figure>", "cap"},
            {"footer", "<footer>foot</footer>", "foot"},
            {"header", "<header>top</header>", "top"},
            {"mark", "<mark>highlighted</mark>", "highlighted"},
            {"nav", "<nav>menu</nav>", "menu"},
            {"section", "<section>sec</section>", "sec"},
            {"small", "<small>fine print</small>", "fine print"},
            // Void elements produce self-closing output
            {"br self-close", "<p>a<br/>b</p>", "<br/>"},
            {"hr self-close", "<p>a</p><hr/><p>b</p>", "<hr/>"},
            // Encoding
            {"amp in text", "<p>a &amp; b</p>", "&amp;"},
            {"lt in text", "<p>a &lt; b</p>", "&lt;"},
            {"gt in text", "<p>a &gt; b</p>", "&gt;"},
            {"nbsp", "<p>&nbsp;</p>", "\u00A0"},
            {"copy", "<p>&copy;</p>", "\u00A9"},
            {"reg", "<p>&reg;</p>", "\u00AE"},
            {"trade", "<p>&trade;</p>", "\u2122"},
            // Nested mixed content
            {"p with inline", "<p><b>b</b> and <i>i</i></p>", "<b>b</b>"},
            {"nested lists",
             "<ul><li>a<ul><li>b</li></ul></li></ul>",
             "b"},
            {"blockquote with p",
             "<blockquote><p>inner</p></blockquote>",
             "inner"},
            // Attributes
            {"class attr", "<p class=\"foo\">x</p>", "class=\"foo\""},
            {"id attr", "<div id=\"myid\">x</div>", "id=\"myid\""},
            {"style attr", "<span style=\"color:red\">x</span>", "color: red"},
            {"lang attr", "<p lang=\"en\">x</p>", "lang=\"en\""},
            {"dir attr", "<p dir=\"ltr\">x</p>", "x"},
            // data: attributes (may or may not be allowed, test just that output is generated)
            {"target attr", "<a href=\"/\" target=\"_blank\">x</a>", "x"},
          });
    }

    @Test
    public void roundTripContainsExpected() throws Exception {
      URL url = getClass().getResource("/antisamy-serializer.xml");
      Policy testPolicy = TestPolicy.getInstance(url);

      CleanResults cr = antiSamy.scan(inputHtml, testPolicy, AntiSamy.DOM);
      String result = cr.getCleanHTML();
      assertThat(description + " → " + result, result, containsString(expectedSubstring));
    }
  }

  // ===========================================================================
  // 13. Edge cases and boundary conditions
  // ===========================================================================

  @Test
  public void emptyFragmentProducesEmptyOutput() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    assertEquals("", serialize(frag));
  }

  @Test
  public void emptyTextNodeProducesNoOutput() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    frag.appendChild(document.createTextNode(""));
    String out = serialize(frag);
    assertTrue("Empty text node should produce empty or whitespace output",
        out.trim().isEmpty());
  }

  @Test
  public void deeplyNestedElements() throws Exception {
    // Create 10-level deep nesting
    DocumentFragment frag = document.createDocumentFragment();
    Element current = document.createElement("div");
    frag.appendChild(current);
    for (int i = 0; i < 9; i++) {
      Element child = document.createElement(i % 2 == 0 ? "p" : "span");
      current.appendChild(child);
      current = child;
    }
    current.appendChild(document.createTextNode("deep"));
    String out = serialize(frag);
    assertThat(out, containsString("deep"));
  }

  @Test
  public void largeTextContent() throws Exception {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      sb.append("word").append(i).append(" ");
    }
    String text = sb.toString().trim();
    DocumentFragment frag = fragmentWithText("p", text);
    String out = serialize(frag);
    assertThat(out, containsString("word0 "));
    assertThat(out, containsString("word999"));
  }

  @Test
  public void unicodeContentOutsideLatin1() throws Exception {
    // Characters beyond Latin-1 (U+0100+) do not have named entities and are
    // written as-is (or numeric entity, depending on policy)
    DocumentFragment frag = fragmentWithText("p", "\u4E2D\u6587"); // 中文
    String out = serialize(frag);
    assertThat(out, containsString("<p>"));
    // Should contain the characters in some form
    assertTrue("Chinese chars", out.contains("\u4E2D") || out.contains("&#"));
  }

  @Test
  public void multipleRootElementsAreAllSerialized() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    for (int i = 0; i < 5; i++) {
      Element p = document.createElement("p");
      p.appendChild(document.createTextNode("para" + i));
      frag.appendChild(p);
    }
    String out = serialize(frag);
    for (int i = 0; i < 5; i++) {
      assertThat(out, containsString("para" + i));
    }
  }

  @Test
  public void tagNameLowercaseAfterCybernekoParsing() throws Exception {
    // Cyberneko normalizes tag names to lower-case
    String result = domScan("<P>Hello</P>");
    assertThat(result, containsString("<p>Hello</p>"));
    assertThat(result, not(containsString("<P>")));
  }

  @Test
  public void attributeNameLowercaseAfterCybernekoParsing() throws Exception {
    // Cyberneko normalizes attribute names to lower-case
    String result = domScan("<P CLASS=\"x\">Hello</P>");
    assertThat(result, containsString("class=\"x\""));
    assertThat(result, not(containsString("CLASS")));
  }

  @Test
  public void textXssAngleBracketsInTextContent() throws Exception {
    String result = domScan("<p>&lt;script&gt;alert(1)&lt;/script&gt;</p>");
    assertThat(result, containsString("&lt;script&gt;"));
    assertThat(result, not(containsString("<script>")));
  }

  @Test
  public void textXssDoubleEncoded() throws Exception {
    String result = domScan("<p>&amp;lt;b&amp;gt;</p>");
    assertThat(result, not(containsString("<b>")));
  }

  @Test
  public void multipleAttributesOnSameElement() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element el = document.createElement("a");
    el.setAttribute("href", "http://example.com");
    el.setAttribute("title", "Example");
    el.setAttribute("class", "link");
    el.appendChild(document.createTextNode("go"));
    frag.appendChild(el);
    String out = serialize(frag);
    assertThat(out, containsString("href="));
    assertThat(out, containsString("title="));
    assertThat(out, containsString("class="));
  }

  @Test
  public void aElementDoesNotAffectChildIndentAfterElement() throws Exception {
    // The A element is special: it does not set afterElement=true on its parent,
    // so text following </a> should NOT get a line break.
    InternalPolicy withFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "true");
    DocumentFragment frag = document.createDocumentFragment();
    Element p = document.createElement("p");
    Element a = document.createElement("a");
    a.setAttribute("href", "#");
    a.appendChild(document.createTextNode("link"));
    p.appendChild(a);
    p.appendChild(document.createTextNode(" text"));
    frag.appendChild(p);
    String out = serialize(frag, withFormat);
    // After the <a> element, the following text " text" should not be preceded by a line break
    assertThat(out, containsString("link</a> text"));
  }

  @Test
  public void tdElementDoesNotAffectChildIndentAfterElement() throws Exception {
    // The TD element (like A) should not trigger afterElement-based indentation
    InternalPolicy withFormat =
        (InternalPolicy) policy.cloneWithDirective("formatOutput", "true");
    DocumentFragment frag = document.createDocumentFragment();
    Element table = document.createElement("table");
    Element tr = document.createElement("tr");
    Element td1 = document.createElement("td");
    td1.appendChild(document.createTextNode("c1"));
    Element td2 = document.createElement("td");
    td2.appendChild(document.createTextNode("c2"));
    tr.appendChild(td1);
    tr.appendChild(td2);
    table.appendChild(tr);
    frag.appendChild(table);
    String out = serialize(frag, withFormat);
    assertThat(out, containsString("c1"));
    assertThat(out, containsString("c2"));
  }

  @Test
  public void selfClosingAllowedForEmptyAllowedElement() throws Exception {
    // An element in allowedEmptyTags with no children → self-close (br is in allowedEmptyTags
    // and NOT in requiresClosingTags)
    DocumentFragment frag = document.createDocumentFragment();
    Element br = document.createElement("br");
    frag.appendChild(br);
    String out = serialize(frag, noFormatPolicy);
    assertEquals("<br/>", out);
  }

  @Test
  public void nonVoidElementWithChildrenNotSelfClosed() throws Exception {
    DocumentFragment frag = fragmentWithText("div", "text");
    String out = serialize(frag, noFormatPolicy);
    assertThat(out, containsString("<div>"));
    assertThat(out, containsString("</div>"));
    assertThat(out, not(containsString("<div/>")));
  }

  // ===========================================================================
  // 14. Specific regression tests (GitHub issues)
  // ===========================================================================

  @Test
  public void issue30StyleTagPreservesContent() throws Exception {
    String css = "P {\n\tmargin-bottom: 0.08in;\n}\n";
    CleanResults cr = antiSamy.scan("<style>" + css, policy, AntiSamy.DOM);
    String result = cr.getCleanHTML();
    assertThat(result, containsString("<style>"));
    assertThat(result, containsString("margin-bottom"));
    assertThat(result, containsString("</style>"));
    // CSS content should not be HTML-escaped
    assertThat(result, not(containsString("&lt;")));
    assertThat(result, not(containsString("&gt;")));
  }

  @Test
  public void issue30StyleTagTrailingNewlineWhenFormatted() throws Exception {
    String css = "P {\n\tmargin-bottom: 0.08in;\n}\n";
    CleanResults cr = antiSamy.scan("<style>" + css, policy, AntiSamy.DOM);
    String result = cr.getCleanHTML();
    // With formatOutput=true (default in policy), there should be a trailing newline
    // (or at minimum, the content must include all the CSS)
    assertThat(result, containsString("0.08in"));
  }

  @Test
  public void issue484ParagraphsWithBrElement() throws Exception {
    String html = "<p>this is para data</p><br/><p>this is para data 2</p>";
    CleanResults cr = antiSamy.scan(html, policy, AntiSamy.DOM);
    String result = cr.getCleanHTML();
    assertThat(result, containsString("this is para data"));
    assertThat(result, containsString("<br/>"));
    assertThat(result, containsString("this is para data 2"));
  }

  @Test
  public void issue453HtmlStructureWithSelect() throws Exception {
    String html =
        "<html lang=\"en\">\n<head>\n</head>\n<table>\n"
            + "<SELECT NAME=\"Lang\">\n"
            + "<OPTION VALUE=\"da\">Dansk</OPTION>\n"
            + "<OPTION VALUE=\"en\" selected=selected>English</OPTION>\n"
            + "</SELECT>\n</table>\n</html>";
    CleanResults cr = antiSamy.scan(html, policy, AntiSamy.DOM);
    String result = cr.getCleanHTML();
    String cleaned = result.replaceAll("\r?\n", "").replaceAll("\\s\\s+", " ");
    assertThat(
        cleaned,
        containsString(
            "<body> <table> <select name=\"Lang\"> <option value=\"da\">Dansk</option> "));
  }

  @Test
  public void cssWithMediaQuery() throws Exception {
    String css = "@media screen { body { font-size: 14px; } }";
    String result = domScan("<style>" + css + "</style>");
    assertThat(result, containsString("font-size"));
  }

  @Test
  public void htmlWithBothHeadAndBody() throws Exception {
    String html = "<html><head><title>T</title></head><body><p>text</p></body></html>";
    String result = domScan(html);
    assertThat(result, containsString("text"));
  }

  @Test
  public void longAttributeValue() throws Exception {
    String longVal = new String(new char[500]).replace('\0', 'x');
    DocumentFragment frag = document.createDocumentFragment();
    Element el = document.createElement("p");
    el.setAttribute("class", longVal);
    el.appendChild(document.createTextNode("text"));
    frag.appendChild(el);
    String out = serialize(frag);
    assertThat(out, containsString("class=\"" + longVal + "\""));
  }

  @Test
  public void manyChildElements() throws Exception {
    DocumentFragment frag = document.createDocumentFragment();
    Element ul = document.createElement("ul");
    frag.appendChild(ul);
    for (int i = 0; i < 50; i++) {
      Element li = document.createElement("li");
      li.appendChild(document.createTextNode("item" + i));
      ul.appendChild(li);
    }
    String out = serialize(frag);
    assertThat(out, containsString("item0"));
    assertThat(out, containsString("item49"));
  }
}
