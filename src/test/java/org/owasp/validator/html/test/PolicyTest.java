/*
 * Copyright (c) 2007-2022, Jacob Coulter, Mark Oberhaus
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

package org.owasp.validator.html.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import org.junit.Test;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.TagMatcher;
import org.owasp.validator.html.scan.Constants;

/**
 * This class tests the Policy functionality to show that we can successfully parse the policy file.
 */
public class PolicyTest {

  private Policy policy;

  private static final String HEADER =
      "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
          + "<anti-samy-rules xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
          + "xsi:noNamespaceSchemaLocation=\"antisamy.xsd\">\n";
  private static final String DIRECTIVES = "<directives>\n</directives>\n";
  private static final String COMMON_ATTRIBUTES = "<common-attributes>\n</common-attributes>\n";
  private static final String GLOBAL_TAG_ATTRIBUTES =
      "<global-tag-attributes>\n</global-tag-attributes>\n";
  private static final String DYNAMIC_TAG_ATTRIBUTES =
      "<dynamic-tag-attributes>\n</dynamic-tag-attributes>\n";
  private static final String TAG_RULES = "<tag-rules>\n</tag-rules>";
  private static final String CSS_RULES = "<css-rules>\n</css-rules>\n";
  private static final String COMMON_REGEXPS = "<common-regexps>\n</common-regexps>";
  private static final String FOOTER = "</anti-samy-rules>";

  // Returns a valid policy file with the specified allowedEmptyTags
  private String assembleFile(String finalTagsSection) {
    return HEADER
        + DIRECTIVES
        + COMMON_REGEXPS
        + COMMON_ATTRIBUTES
        + GLOBAL_TAG_ATTRIBUTES
        + DYNAMIC_TAG_ATTRIBUTES
        + TAG_RULES
        + CSS_RULES
        + finalTagsSection
        + FOOTER;
  }

  @Test
  public void testGetAllowedEmptyTags() throws PolicyException {
    String allowedEmptyTagsSection =
        "<allowed-empty-tags>\n"
            + "    <literal-list>\n"
            + "                <literal value=\"td\"/>\n"
            + "                <literal value=\"span\"/>\n"
            + "    </literal-list>\n"
            + "</allowed-empty-tags>\n";
    String policyFile = assembleFile(allowedEmptyTagsSection);

    policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));

    TagMatcher actualTags = policy.getAllowedEmptyTags();

    assertTrue(actualTags.matches("td"));
    assertTrue(actualTags.matches("span"));
  }

  @Test
  public void testGetAllowedEmptyTags_emptyList() throws PolicyException {
    String allowedEmptyTagsSection =
        "<allowed-empty-tags>\n"
            + "    <literal-list>\n"
            + "    </literal-list>\n"
            + "</allowed-empty-tags>\n";
    String policyFile = assembleFile(allowedEmptyTagsSection);

    policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));

    assertEquals(0, policy.getAllowedEmptyTags().size());
  }

  @Test
  public void testGetAllowedEmptyTags_emptySection() throws PolicyException {
    String allowedEmptyTagsSection = "<allowed-empty-tags>\n" + "</allowed-empty-tags>\n";
    String policyFile = assembleFile(allowedEmptyTagsSection);

    policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));

    assertEquals(0, policy.getAllowedEmptyTags().size());
  }

  @Test
  public void testGetAllowedEmptyTags_NoSection() throws PolicyException {
    String allowedEmptyTagsSection = "";
    String policyFile = assembleFile(allowedEmptyTagsSection);

    policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));

    assertTrue(policy.getAllowedEmptyTags().size() == Constants.defaultAllowedEmptyTags.size());
  }

  @Test
  public void testGetRequireClosingTags() throws PolicyException {
    String requireClosingTagsSection =
        "<require-closing-tags>\n"
            + "    <literal-list>\n"
            + "                <literal value=\"td\"/>\n"
            + "                <literal value=\"span\"/>\n"
            + "    </literal-list>\n"
            + "</require-closing-tags>\n";
    String policyFile = assembleFile(requireClosingTagsSection);

    policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));

    TagMatcher actualTags = policy.getRequiresClosingTags();

    assertTrue(actualTags.matches("td"));
    assertTrue(actualTags.matches("span"));
  }

  @Test
  public void testGetRequireClosingTags_emptyList() throws PolicyException {
    String requireClosingTagsSection =
        "<require-closing-tags>\n"
            + "    <literal-list>\n"
            + "    </literal-list>\n"
            + "</require-closing-tags>\n";
    String policyFile = assembleFile(requireClosingTagsSection);

    policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));

    assertEquals(0, policy.getRequiresClosingTags().size());
  }

  @Test
  public void testGetRequireClosingTags_emptySection() throws PolicyException {
    String requireClosingTagsSection = "<require-closing-tags>\n" + "</require-closing-tags>\n";
    String policyFile = assembleFile(requireClosingTagsSection);

    policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));

    assertEquals(0, policy.getRequiresClosingTags().size());
  }

  @Test
  public void testGetRequireClosingTags_NoSection() throws PolicyException {
    String requireClosingTagsSection = "";
    String policyFile = assembleFile(requireClosingTagsSection);

    policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));

    assertTrue(
        policy.getRequiresClosingTags().size() == Constants.defaultRequireClosingTags.size());
  }

  @Test
  public void testInvalidPolicies() {
    // Starting with v1.7.0, schema validation is always enforced on policy files.
    // These tests verify various schema violations are detected and flagged.
    String notSupportedTagsSection = "<notSupportedTag>\n" + "</notSupportedTag>\n";
    String policyFile = assembleFile(notSupportedTagsSection);
    try {
      policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));
      fail("No PolicyException thrown for <notSupportedTag>.");
    } catch (PolicyException e) {
      assertNotNull(e);
    }

    String duplicatedTagsSection = "<tag-rules>\n" + "</tag-rules>\n";
    policyFile = assembleFile(duplicatedTagsSection);
    try {
      policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));
      fail("No PolicyException thrown when <tag-rules> duplicated.");
    } catch (PolicyException e) {
      assertNotNull(e);
    }

    policyFile = assembleFile("").replace("<tag-rules>", "").replace("</tag-rules>", "");
    try {
      policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));
      fail("No PolicyException thrown when <tag-rules> missing.");
    } catch (PolicyException e) {
      assertNotNull(e);
    }
  }

  @Test
  public void testSchemaValidationToggleWithSource() {
    String notSupportedTagsSection = "<notSupportedTag>\n" + "</notSupportedTag>\n";
    String policyFile = assembleFile(notSupportedTagsSection);

    try {
      policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));
      fail("Not supported tag on policy, but no PolicyException occurred.");
    } catch (PolicyException e) {
      assertNotNull(e);
    }
  }

  @Test
  public void testSchemaValidationWithUrl() {
    URL urlOfInvalidPolicy = getClass().getResource("/invalidPolicy.xml");

    try {
      policy = TestPolicy.getInstance(urlOfInvalidPolicy);
      fail("PolicyException not thrown for policy w/invalid schema.");
    } catch (PolicyException e) {
      assertNotNull(e);
    }
  }

  @Test
  public void testSchemaValidationWithInclude() {
    // This policy will also include invalidPolicy.xml
    URL url = getClass().getResource("/emptyPolicyWithInclude.xml");

    try {
      policy = TestPolicy.getInstance(url);
      fail("PolicyException not thrown for policy w/invalid schema.");
    } catch (PolicyException e) {
      assertNotNull(e);
    }
  }

  @Test
  public void testSchemaValidationWithOptionallyDefinedTags() throws PolicyException {
    String allowedEmptyTagsSection =
        "<allowed-empty-tags>\n"
            + "    <literal-list>\n"
            + "                <literal value=\"span\"/>\n"
            + "    </literal-list>\n"
            + "</allowed-empty-tags>\n";
    String requireClosingTagsSection =
        "<require-closing-tags>\n"
            + "    <literal-list>\n"
            + "                <literal value=\"span\"/>\n"
            + "    </literal-list>\n"
            + "</require-closing-tags>\n";
    String policyFile = assembleFile(allowedEmptyTagsSection + requireClosingTagsSection);

    policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));
    // If it reaches this point, it passed schema validation, which is what we want.
  }

  @Test
  public void testGithubIssue66() {
    // Concern is that LSEP characters are not being considered on .* pattern
    // Note: Change was done in Policy loading, so test is located here
    String tagRules =
        "<tag-rules>"
            + "<tag name=\"tag1\" action=\"validate\">"
            + "   <attribute name=\"attribute1\">"
            + "       <regexp-list>"
            + "           <regexp value=\".*\"/>"
            + "       </regexp-list>"
            + "   </attribute>"
            + "</tag>"
            + "</tag-rules>";
    String rawPolicy =
        HEADER
            + DIRECTIVES
            + COMMON_REGEXPS
            + COMMON_ATTRIBUTES
            + GLOBAL_TAG_ATTRIBUTES
            + tagRules
            + CSS_RULES
            + FOOTER;

    try {
      policy = Policy.getInstance(new ByteArrayInputStream(rawPolicy.getBytes()));
      assertThat(
          new AntiSamy()
              .scan("<tag1 attribute1='Line 1\u2028Line 2'>Content</tag1>", policy, AntiSamy.DOM)
              .getCleanHTML(),
          containsString("Line 1"));
      assertThat(
          new AntiSamy()
              .scan("<tag1 attribute1='Line 1\u2028Line 2'>Content</tag1>", policy, AntiSamy.SAX)
              .getCleanHTML(),
          containsString("Line 1"));
    } catch (Exception e) {
      fail("Policy nor scan should fail:" + e.getMessage());
    }
  }

  static void reloadSchemaValidation() throws Exception {
    // Emulates the static code block used in Policy to set schema validation on/off if
    // the Policy.VALIDATIONPROPERTY system property is set. If not set, it sets it to the
    // default.
    Method method = Policy.class.getDeclaredMethod("loadValidateSchemaProperty");
    method.setAccessible(true);
    method.invoke(null);
  }

  @Test
  public void testGithubIssue79() {
    URL policyUrl;

    // Case 1: Loading policy from a URL beginning with "jar:file:"
    try {
      java.net.URLClassLoader child =
          new java.net.URLClassLoader(
              new URL[] {
                Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("policy-in-external-library.jar")
              },
              this.getClass().getClassLoader());

      policyUrl =
          Class.forName("org.owasp.antisamy.test.Dummy", true, child)
              .getClassLoader()
              .getResource("policyInsideJar.xml");
      assertThat(policyUrl, notNullValue());

      policy = Policy.getInstance(policyUrl);
      assertThat(policy, notNullValue());
    } catch (Exception e) {
      fail("Policy nor scan should fail:" + e.getMessage());
    }

    // Case 2: Loading policy from a URL beginning with "jar:https:"
    policyUrl = null;
    try {
      policyUrl = new URL("jar:https://somebadsite.com/foo.xml");
      policy = Policy.getInstance(policyUrl);
      fail("URL creation or policy loading should have failed");
    } catch (Exception e) {
    }
    assertNull(policyUrl);
  }
}
