/*
 * Copyright (c) 2007-2022, Arshan Dabirsiaghi, Jason Li, Sebastián Passaro
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.  Redistributions in binary form must
 * reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of OWASP nor the names of its contributors may be used to endorse
 * or promote products derived from this software without specific prior written permission.
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.batik.css.parser.CSSSelectorList;
import org.junit.Before;
import org.junit.Test;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.scan.Constants;
import org.owasp.validator.html.test.TestPolicy;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import org.w3c.css.sac.Selector;

public class CssHandlerTest {
  private TestPolicy policy = null;
  private ResourceBundle messages = null;

  @Before
  public void setUp() throws Exception {
    // Load the policy. You may have to change the path to find the Policy file for your
    // environment.
    URL url = getClass().getResource("/antisamy.xml");
    policy = TestPolicy.getInstance(url);
    // Load resource bundle
    try {
      messages = ResourceBundle.getBundle("AntiSamy", Locale.getDefault());
    } catch (MissingResourceException mre) {
      messages =
          ResourceBundle.getBundle(
              "AntiSamy", new Locale(Constants.DEFAULT_LOCALE_LANG, Constants.DEFAULT_LOCALE_LOC));
    }
  }

  @Test
  public void testImportStyleErrors() {
    // Tests different errors when building a list of URIs for importing styles
    List<String> errorMessages = new ArrayList<String>();
    TestPolicy revised = policy.cloneWithDirective(Policy.EMBED_STYLESHEETS, "true");
    CssHandler handler = new CssHandler(revised, errorMessages, messages, "style");

    handler.importStyle(null, null, null);
    assertThat(handler.getErrorMessages().size(), is(1));
    assertThat(
        handler.getErrorMessages().toArray()[0],
        is(
            ErrorMessageUtil.getMessage(
                messages, ErrorMessageUtil.ERROR_CSS_IMPORT_URL_INVALID, new Object[] {})));

    errorMessages.clear();
    handler.importStyle("javascript:invalidUrl()", null, null);
    assertThat(handler.getErrorMessages().size(), is(1));
    assertThat(
        handler.getErrorMessages().toArray()[0],
        is(
            ErrorMessageUtil.getMessage(
                messages,
                ErrorMessageUtil.ERROR_CSS_IMPORT_URL_INVALID,
                new Object[] {HTMLEntityEncoder.htmlEntityEncode("javascript:invalidUrl()")})));

    errorMessages.clear();
    handler.importStyle("Invalid:\\Url", null, null);
    assertThat(handler.getErrorMessages().size(), is(1));
    assertThat(
        handler.getErrorMessages().toArray()[0],
        is(
            ErrorMessageUtil.getMessage(
                messages,
                ErrorMessageUtil.ERROR_CSS_IMPORT_URL_INVALID,
                new Object[] {HTMLEntityEncoder.htmlEntityEncode("Invalid:\\Url")})));

    errorMessages.clear();
    handler.importStyle("relative.url", null, null);
    assertThat(handler.getErrorMessages().size(), is(1));
    assertThat(
        handler.getErrorMessages().toArray()[0],
        is(
            ErrorMessageUtil.getMessage(
                messages,
                ErrorMessageUtil.ERROR_CSS_TAG_RELATIVE,
                new Object[] {
                  HTMLEntityEncoder.htmlEntityEncode("style"),
                  HTMLEntityEncoder.htmlEntityEncode("relative.url")
                })));

    errorMessages.clear();
    CssHandler handlerWithoutTag = new CssHandler(revised, errorMessages, messages, null);
    handlerWithoutTag.importStyle("relative.url", null, null);
    assertThat(handler.getErrorMessages().size(), is(1));
    assertThat(
        handler.getErrorMessages().toArray()[0],
        is(
            ErrorMessageUtil.getMessage(
                messages,
                ErrorMessageUtil.ERROR_STYLESHEET_RELATIVE,
                new Object[] {HTMLEntityEncoder.htmlEntityEncode("relative.url")})));
  }

  @Test
  public void testStartSelectorErrors() {
    // Tests different errors when building a CSS selector with an overridden method
    List<String> errorMessages = new ArrayList<String>();
    CssHandler handler = new CssHandler(policy, errorMessages, messages, "style");

    // Set an invalid selector name, based on regular expressions form policy
    CSSSelectorList selectors = new CSSSelectorList();
    Selector selector =
        new Selector() {
          @Override
          public short getSelectorType() {
            return -1;
          }

          @Override
          public String toString() {
            return "]invalidSelectorName";
          }
        };
    selectors.append(selector);

    handler.startSelector(selectors);
    assertThat(handler.getErrorMessages().size(), is(2));
    assertThat(
        handler.getErrorMessages().toArray()[0],
        is(
            ErrorMessageUtil.getMessage(
                messages,
                ErrorMessageUtil.ERROR_CSS_TAG_SELECTOR_NOTFOUND,
                new Object[] {
                  HTMLEntityEncoder.htmlEntityEncode("style"),
                  HTMLEntityEncoder.htmlEntityEncode(selector.toString())
                })));
    assertThat(
        handler.getErrorMessages().toArray()[1],
        is(
            ErrorMessageUtil.getMessage(
                messages,
                ErrorMessageUtil.ERROR_CSS_TAG_SELECTOR_DISALLOWED,
                new Object[] {
                  HTMLEntityEncoder.htmlEntityEncode("style"),
                  HTMLEntityEncoder.htmlEntityEncode(selector.toString())
                })));

    // Repeat without setting a tag name on handler
    errorMessages.clear();
    CssHandler handlerWithoutTag = new CssHandler(policy, errorMessages, messages, null);
    handlerWithoutTag.startSelector(selectors);
    assertThat(handlerWithoutTag.getErrorMessages().size(), is(2));
    assertThat(
        handlerWithoutTag.getErrorMessages().toArray()[0],
        is(
            ErrorMessageUtil.getMessage(
                messages,
                ErrorMessageUtil.ERROR_STYLESHEET_SELECTOR_NOTFOUND,
                new Object[] {HTMLEntityEncoder.htmlEntityEncode(selector.toString())})));
    assertThat(
        handlerWithoutTag.getErrorMessages().toArray()[1],
        is(
            ErrorMessageUtil.getMessage(
                messages,
                ErrorMessageUtil.ERROR_STYLESHEET_SELECTOR_DISALLOWED,
                new Object[] {HTMLEntityEncoder.htmlEntityEncode(selector.toString())})));
  }

  @Test
  public void testIgnorableAtRuleErrors() {
    // Tests error when notifying about an ignorable @-rule
    List<String> errorMessages = new ArrayList<String>();
    CssHandler handler = new CssHandler(policy, errorMessages, messages, "style");

    handler.ignorableAtRule("@font");
    assertThat(handler.getErrorMessages().size(), is(1));
    assertThat(
        handler.getErrorMessages().toArray()[0],
        is(
            ErrorMessageUtil.getMessage(
                messages,
                ErrorMessageUtil.ERROR_CSS_TAG_RULE_NOTFOUND,
                new Object[] {
                  HTMLEntityEncoder.htmlEntityEncode("style"),
                  HTMLEntityEncoder.htmlEntityEncode("@font")
                })));
  }
}
