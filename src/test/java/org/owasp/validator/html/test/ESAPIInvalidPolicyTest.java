package org.owasp.validator.html.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Test;
import org.owasp.validator.html.Policy;

public class ESAPIInvalidPolicyTest {
	/**
	 * Property specified by the Antisamy project which may be used to disable
	 * schema validation on policy files.
	 */
	private static final String ANTISAMY_PROJECT_PROP_SCHEMA_VALIDATION = "owasp.validator.validateschema";

	@After
	public void resetSystemProp() throws Exception {
		System.clearProperty(ANTISAMY_PROJECT_PROP_SCHEMA_VALIDATION);
		reloadSchemaValidation();
	}

	@Test
	public void testDirectConfigAsBais() throws Exception {
		Policy.setSchemaValidation(false);
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		Policy.getInstance(toByteArrayStream(stream));
	}

	@Test
	public void testDirectConfig() throws Exception {
		Policy.setSchemaValidation(false);
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		Policy.getInstance(stream);
	}

	@Test
	public void testSystemProp() throws Exception {

		System.setProperty(ANTISAMY_PROJECT_PROP_SCHEMA_VALIDATION, "false");
		reloadSchemaValidation();
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		Policy.getInstance(stream);
	}

	@Test
	public void testSystemPropAsBais() throws Exception {
		System.setProperty(ANTISAMY_PROJECT_PROP_SCHEMA_VALIDATION, "false");
		reloadSchemaValidation();
		InputStream stream = ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
		Policy.getInstance(toByteArrayStream(stream));
	}

	private void reloadSchemaValidation() throws Exception {
		// Emulates the static code block fist called
		Method method = Policy.class.getDeclaredMethod("loadValidateSchemaProperty");
		method.setAccessible(true);
		method.invoke(null);
	}

	private InputStream toByteArrayStream(InputStream in) throws Exception {
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
