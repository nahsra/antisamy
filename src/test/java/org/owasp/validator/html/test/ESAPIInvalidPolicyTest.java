package org.owasp.validator.html.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.After;
import org.junit.Test;

import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;

public class ESAPIInvalidPolicyTest {

	@After
	public void resetSystemProp() throws Exception {
		System.clearProperty(Policy.VALIDATIONPROPERTY);
		PolicyTest.reloadSchemaValidation();
		if (!Policy.getSchemaValidation()) System.out.println(
			"ERROR: resetSystemProp() test method NOT properly enabling AntiSamy policy schema validation!");
	}

	@Test
	public void testDirectConfigAsBaisValidationOn() throws Exception {
		Policy.setSchemaValidation(true);
		assertTrue("AntiSamy XSD Validation should be enabled", Policy.getSchemaValidation());
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		try {
			Policy.getInstance(toByteArrayStream(stream));
			fail("Invalid policy with schema validation ON should throw exception.");
		} catch (PolicyException e) {
			// This is expected, so do nothing. Any other kind of exception is a failed test case.
		}
	}

	@Test
	public void testDirectConfigAsBaisValidationOff() throws Exception {
		Policy.setSchemaValidation(false);
		assertFalse("AntiSamy XSD Validation should be disabled", Policy.getSchemaValidation());
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		Policy.getInstance(toByteArrayStream(stream));
	}

	@Test
	public void testDirectConfigValidationOn() throws Exception {
		Policy.setSchemaValidation(true);
		assertTrue("AntiSamy XSD Validation should be enabled", Policy.getSchemaValidation());
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		try {
			Policy.getInstance(stream);
			fail("Invalid policy with schema validation ON should throw exception.");
		} catch (PolicyException e) {
			// This is expected, so do nothing. Any other kind of exception is a failed test case.
		}
	}

	@Test
	public void testDirectConfigValidationOff() throws Exception {
		Policy.setSchemaValidation(false);
		assertFalse("AntiSamy XSD Validation should be disabled", Policy.getSchemaValidation());
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		Policy.getInstance(stream);
	}

	@Test
	public void testSystemPropValidationOn() throws Exception {
		System.setProperty(Policy.VALIDATIONPROPERTY, "true");
		PolicyTest.reloadSchemaValidation();
		assertTrue("AntiSamy XSD Validation should be enabled", Policy.getSchemaValidation());
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		try {
			Policy.getInstance(stream);
			fail("Invalid policy with schema validation ON should throw exception.");
		} catch (PolicyException e) {
			// This is expected, so do nothing. Any other kind of exception is a failed test case.
		}
	}

	@Test
	public void testSystemPropValidationOff() throws Exception {
		System.setProperty(Policy.VALIDATIONPROPERTY, "false");
		PolicyTest.reloadSchemaValidation();
		assertFalse("AntiSamy XSD Validation should be disabled", Policy.getSchemaValidation());
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		Policy.getInstance(stream);
	}

	@Test
	public void testSystemPropAsBaisValidationOn() throws Exception {
		System.setProperty(Policy.VALIDATIONPROPERTY, "true");
		PolicyTest.reloadSchemaValidation();
		assertTrue("AntiSamy XSD Validation should be enabled", Policy.getSchemaValidation());
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		try {
			Policy.getInstance(toByteArrayStream(stream));
			fail("Invalid policy with schema validation ON should throw exception.");
		} catch (PolicyException e) {
			// This is expected, so do nothing. Any other kind of exception is a failed test case.
		}
	}

	@Test
	public void testSystemPropAsBaisValidationOff() throws Exception {
		System.setProperty(Policy.VALIDATIONPROPERTY, "false");
		PolicyTest.reloadSchemaValidation();
		assertFalse("AntiSamy XSD Validation should be disabled", Policy.getSchemaValidation());
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		Policy.getInstance(toByteArrayStream(stream));
	}

	static InputStream toByteArrayStream(InputStream in) throws Exception {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[1024];
		while ((nRead = in.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		byte[] byteArray = buffer.toByteArray();
		return new ByteArrayInputStream(byteArray);
	}
}
