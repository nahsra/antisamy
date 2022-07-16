package org.owasp.validator.html.test;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.junit.Test;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;

public class ESAPIInvalidPolicyTest {

  @Test
  public void testBaisValidation() throws Exception {
    InputStream stream =
        ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
    try {
      Policy.getInstance(toByteArrayStream(stream));
      fail("Invalid policy with schema should throw exception.");
    } catch (PolicyException e) {
      // This is expected, so do nothing. Any other kind of exception is a failed test case.
    }
  }

  @Test
  public void testDirectValidation() throws Exception {
    InputStream stream =
        ESAPIInvalidPolicyTest.class.getResourceAsStream("/esapi-antisamy-InvalidPolicy.xml");
    try {
      Policy.getInstance(stream);
      fail("Invalid policy should throw exception.");
    } catch (PolicyException e) {
      // This is expected, so do nothing. Any other kind of exception is a failed test case.
    }
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
