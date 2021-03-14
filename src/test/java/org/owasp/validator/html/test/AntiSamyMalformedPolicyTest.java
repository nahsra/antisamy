package org.owasp.validator.html.test;

import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.Test;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;

/**
 * @author Gerardo Canedo
 */
public class AntiSamyMalformedPolicyTest {

    @Test
    public void testDirectConfigMalformedXMLNoSchemaValidation() throws Exception {
        Policy.setSchemaValidation(false);
        InputStream stream = AntiSamyMalformedPolicyTest.class.getResourceAsStream("/invalidPolicyMalformedXml.xml");
        try {
            Policy.getInstance(stream);
            fail("Malformed XML, PolicyException expected ");
        } catch (PolicyException e) {
            // Expected behavior
        }
    }

    @Test
    public void testDirectConfigMalformedXMLSchemaValidation() throws Exception {
        Policy.setSchemaValidation(true);
        InputStream stream = AntiSamyMalformedPolicyTest.class.getResourceAsStream("/invalidPolicyMalformedXml.xml");
        try {
            Policy.getInstance(stream);
            fail("Malformed XML, PolicyException expected ");
        } catch (PolicyException e) {
            // Expected behavior
        }
    }
}
