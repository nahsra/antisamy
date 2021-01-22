/*
 * Copyright (c) 2007-2021, Jacob Coulter, Mark Oberhaus
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

import junit.framework.TestCase;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.TagMatcher;
import org.owasp.validator.html.scan.Constants;

import java.io.ByteArrayInputStream;
import java.net.URL;

/**
 * This class tests the Policy functionality to show that we can successfully parse the policy file.
 */
public class PolicyTest extends TestCase {

    private Policy policy;

    private static final String HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
                                         "<anti-samy-rules xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                                         "xsi:noNamespaceSchemaLocation=\"antisamy.xsd\">\n";
    private static final String DIRECTIVES = "<directives>\n</directives>\n";
    private static final String COMMON_ATTRIBUTES = "<common-attributes>\n</common-attributes>\n";
    private static final String GLOBAL_TAG_ATTRIBUTES = "<global-tag-attributes>\n</global-tag-attributes>\n";
    private static final String DYNAMIC_TAG_ATTRIBUTES = "<dynamic-tag-attributes>\n</dynamic-tag-attributes>\n";
    private static final String TAG_RULES = "<tag-rules>\n</tag-rules>";
    private static final String CSS_RULES = "<css-rules>\n</css-rules>\n";
    private static final String COMMON_REGEXPS = "<common-regexps>\n</common-regexps>";
    private static final String FOOTER = "</anti-samy-rules>";

    private String assembleFile(String allowedEmptyTagsSection) {
        return HEADER + DIRECTIVES + COMMON_REGEXPS + COMMON_ATTRIBUTES + GLOBAL_TAG_ATTRIBUTES + DYNAMIC_TAG_ATTRIBUTES + TAG_RULES + CSS_RULES +
               allowedEmptyTagsSection + FOOTER;
    }

    public void testGetAllowedEmptyTags() throws PolicyException {
        String allowedEmptyTagsSection = "<allowed-empty-tags>\n" +
                                         "    <literal-list>\n" +
                                         "                <literal value=\"td\"/>\n" +
                                         "                <literal value=\"span\"/>\n" +
                                         "    </literal-list>\n" +
                                         "</allowed-empty-tags>\n";
        String policyFile = assembleFile(allowedEmptyTagsSection);

        policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));

        TagMatcher actualTags = policy.getAllowedEmptyTags();

        assertTrue(actualTags.matches("td"));
        assertTrue(actualTags.matches("span"));
    }

    public void testGetAllowedEmptyTags_emptyList() throws PolicyException {
        String allowedEmptyTagsSection = "<allowed-empty-tags>\n" +
                                         "    <literal-list>\n" +
                                         "    </literal-list>\n" +
                                         "</allowed-empty-tags>\n";
        String policyFile = assembleFile(allowedEmptyTagsSection);

        policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));

        assertEquals(0, policy.getAllowedEmptyTags().size());
    }
    
    public void testGetAllowedEmptyTags_emptySection() throws PolicyException {
        String allowedEmptyTagsSection = "<allowed-empty-tags>\n" +
                                         "</allowed-empty-tags>\n";
        String policyFile = assembleFile(allowedEmptyTagsSection);

        policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));

        assertEquals(0, policy.getAllowedEmptyTags().size());
    }

    public void testGetAllowedEmptyTags_NoSection() throws PolicyException {
        String allowedEmptyTagsSection = "";

        String policyFile = assembleFile(allowedEmptyTagsSection);

        policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));

        assertTrue(policy.getAllowedEmptyTags().size() == Constants.defaultAllowedEmptyTags.size());
    }
    
    public void testInvalidPolicies() {
        String notSupportedTagsSection = "<notSupportedTag>\n" +
                                         "</notSupportedTag>\n";
        String policyFile = assembleFile(notSupportedTagsSection);
        try {
            policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));
            fail("Not supported tag on policy, but no PolicyException occurred.");
        } catch (PolicyException e) {
            assertNotNull(e);
        }

        String duplicatedTagsSection = "<tag-rules>\n" +
                                       "</tag-rules>\n";
        policyFile = assembleFile(duplicatedTagsSection);
        try {
            policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));
            fail("<tag-rules> is duplicated but it should not be, PolicyException was expected.");
        } catch (PolicyException e) {
            assertNotNull(e);
        }

        policyFile = assembleFile("")
                .replace("<tag-rules>", "")
                .replace("</tag-rules>", "");
        try {
            policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));
            fail("<tag-rules> is missing but it should not be, PolicyException was expected.");
        } catch (PolicyException e) {
            assertNotNull(e);
        }
    }

    public void testSchemaValidationToggleWithSource() {
        String notSupportedTagsSection = "<notSupportedTag>\n" +
                "</notSupportedTag>\n";
        String policyFile = assembleFile(notSupportedTagsSection);

        // Disable validation
        Policy.toggleSchemaValidation(false);

        try {
            policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));
            assertNotNull(policy);
        } catch (PolicyException e) {
            fail("Schema validation is disabled, policy creation should not fail.");
        }

        // This one should only print a warning on the console because validation is disabled
        try {
            policy = Policy.getInstance(new ByteArrayInputStream(assembleFile("").getBytes()));
            assertNotNull(policy);
        } catch (PolicyException e) {
            fail("Schema validation is disabled, policy creation should not fail.");
        }

        // Enable validation again
        Policy.toggleSchemaValidation(true);

        try {
            policy = Policy.getInstance(new ByteArrayInputStream(policyFile.getBytes()));
            fail("Not supported tag on policy, but no PolicyException occurred.");
        } catch (PolicyException e) {
            assertNotNull(e);
        }
    }

    public void testSchemaValidationToggleWithUrl() {
        URL urlOfValidPolicy = getClass().getResource("/antisamy.xml");
        URL urlOfInvalidPolicy = getClass().getResource("/invalidPolicy.xml");

        // Disable validation
        Policy.toggleSchemaValidation(false);

        try {
            policy = TestPolicy.getInstance(urlOfInvalidPolicy);
            assertNotNull(policy);
        } catch (PolicyException e) {
            fail("Schema validation is disabled, policy creation should not fail.");
        }

        // This one should only print a warning on the console because validation is disabled
        try {
            policy = TestPolicy.getInstance(urlOfValidPolicy);
            assertNotNull(policy);
        } catch (PolicyException e) {
            fail("Schema validation is disabled, policy creation should not fail.");
        }

        // Enable validation again
        Policy.toggleSchemaValidation(true);

        try {
            policy = TestPolicy.getInstance(urlOfInvalidPolicy);
            fail("Not supported tag on policy, but no PolicyException occurred.");
        } catch (PolicyException e) {
            assertNotNull(e);
        }
    }
}

