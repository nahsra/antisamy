package org.owasp.validator.html.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import static org.junit.Assert.fail;
import org.junit.After;
import org.junit.Test;

import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;

public class ESAPIInvalidPolicyTest {
	/**
	 * Property specified by the AntiSamy project which may be used to disable
	 * schema validation on policy files.
	 */
	private static final String ANTISAMY_PROJECT_PROP_SCHEMA_VALIDATION = "owasp.validator.validateschema";

	@After
	public void resetSystemProp() throws Exception {
		System.clearProperty(ANTISAMY_PROJECT_PROP_SCHEMA_VALIDATION);
		Policy.setSchemaValidation(true);
		reloadSchemaValidation();
	}

	@Test
	public void testDirectConfigAsBaisValidationOn() throws Exception {
		Policy.setSchemaValidation(true);
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
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		Policy.getInstance(toByteArrayStream(stream));
	}

	@Test
	public void testDirectConfigValidationOn() throws Exception {
		Policy.setSchemaValidation(true);
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
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		Policy.getInstance(stream);
	}

	@Test
	public void testSystemPropValidationOn() throws Exception {
		System.setProperty(ANTISAMY_PROJECT_PROP_SCHEMA_VALIDATION, "true");
		reloadSchemaValidation();
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
		System.setProperty(ANTISAMY_PROJECT_PROP_SCHEMA_VALIDATION, "false");
		reloadSchemaValidation();
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		Policy.getInstance(stream);
	}

	@Test
	public void testSystemPropAsBaisValidationOn() throws Exception {
		System.setProperty(ANTISAMY_PROJECT_PROP_SCHEMA_VALIDATION, "true");
		reloadSchemaValidation();
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
		System.setProperty(ANTISAMY_PROJECT_PROP_SCHEMA_VALIDATION, "false");
		reloadSchemaValidation();
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		Policy.getInstance(toByteArrayStream(stream));
	}

	private static void reloadSchemaValidation() throws Exception {
		// Emulates the static code block first called
		Method method = Policy.class.getDeclaredMethod("loadValidateSchemaProperty");
		method.setAccessible(true);
		method.invoke(null);
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
