package org.owasp.validator.html.test;


import junit.framework.TestCase;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.TagMatcher;
import org.owasp.validator.html.scan.Constants;

import java.io.ByteArrayInputStream;


/**
 * This class tests the Policy functionality to show that we can successfully parse the policy file.
 *
 * @author Jacob Coulter & Mark Oberhaus
 */

@SuppressWarnings("deprecation")
public class PolicyTest extends TestCase {

    private Policy policy;

    private static final String HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
                                         "<anti-samy-rules xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                                         "xsi:noNamespaceSchemaLocation=\"antisamy.xsd\">\n";
    private static final String DIRECTIVES = "<directives>\n</directives>\n";
    private static final String COMMON_ATTRIBUTES = "<common-attributes>\n</common-attributes>\n";
    private static final String GLOBAL_TAG_ATTRIBUTES = "<global-tag-attributes>\n</global-tag-attributes>\n";
    private static final String TAG_RULES = "<tag-rules>\n</tag-rules>";
    private static final String CSS_RULES = "<css-rules>\n</css-rules>\n";
    private static final String COMMON_REGEXPS = "<common-regexps>\n</common-regexps>";
    private static final String FOOTER = "</anti-samy-rules>";

    private String assembleFile(String allowedEmptyTagsSection) {
        return HEADER + DIRECTIVES + COMMON_REGEXPS + COMMON_ATTRIBUTES + GLOBAL_TAG_ATTRIBUTES + TAG_RULES + CSS_RULES +
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
}
