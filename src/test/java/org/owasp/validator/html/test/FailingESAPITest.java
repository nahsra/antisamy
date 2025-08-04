/**
 * Copyright - The OWASP Foundation - 2021-2023 - All rights reserved.
 *
 * <p>This is examples of ESAPI JUnit tests that previously were working until we updated to
 * AntiSamy 1.7.4. It appears that AntiSamy is now sanitizing these differently.
 *
 * <p>Note: I am NOT asking how to "fix" these to make them work. That is pretty much obvious.
 * Rather, I am curious if this is SURPRISING to you? Nothing is mentioned in your 1.7.4 release
 * notes that would allude to this change in behavor. At the very least, people should be made aware
 * of this is clearly can break developer's regression tests against AntiSamy.
 *
 * @author: kevin.w.wall@gmail.com
 */
import static org.junit.Assert.assertEquals;

import java.net.URL;
import org.junit.Before;
import org.junit.Test;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;

public class FailingESAPITest {
  private AntiSamy as = new AntiSamy();
  private Policy policy = null;

  @Before
  public void setUp() throws Exception {
    // Load up ESAPI's AntiSamy policy file. (This was from ESAPI 2.5.2.0.)
    URL url = getClass().getResource("/antisamy-esapi.xml");
    policy = Policy.getInstance(url);
  }

  @Test
  public void testAntiSamyRegressionCDATAWithJavascriptURL() throws Exception {
    String tainted = "<style/>b<![cdata[</style><a href=javascript:alert(1)>test";
    String expected = "b&lt;/style&gt;&lt;a href=javascript:alert(1)&gt;test";

    CleanResults cr = as.scan(tainted, policy, AntiSamy.DOM); // ESAPI 2.5.2.0 uses DOM parser.
    String cleansed = cr.getCleanHTML();

    assertEquals(expected, cleansed);
  }

  @Test
  public void testOnfocusAfterStyleClosing() throws Exception {
    String tainted = "<select<style/>k<input<</>input/onfocus=alert(1)>";
    String expected =
        "k&lt;input/onfocus=alert(1)&gt;"; // Suspicious? Doesn't agree w/ AntiSamy test.

    CleanResults cr = as.scan(tainted, policy, AntiSamy.DOM); // ESAPI 2.5.2.0 uses DOM parser.
    String cleansed = cr.getCleanHTML();

    assertEquals(expected, cleansed);
  }

  @Test
  public void testScriptTagAfterStyleClosing() throws Exception {
    String tainted = "<select<style/>W<xmp<script>alert(1)</script>";
    String expected = "W&lt;script&gt;alert(1)&lt;/script&gt;";

    CleanResults cr = as.scan(tainted, policy, AntiSamy.DOM); // ESAPI 2.5.2.0 uses DOM parser.
    String cleansed = cr.getCleanHTML();

    assertEquals(expected, cleansed);
  }
}
