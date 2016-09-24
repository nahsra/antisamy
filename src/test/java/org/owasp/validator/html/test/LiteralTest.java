package org.owasp.validator.html.test;

import java.net.URL;
import java.util.Iterator;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;

import junit.framework.TestCase;

/**
 * Test that literal values for HTML attributes are honored correctly.
 *
 * @author August Detlefsen
 */
public class LiteralTest extends TestCase {

	private Policy policy = null;

	protected void setUp() throws Exception {

		/*
		 * Load the policy. You may have to change the path to find the Policy
		 * file for your environment.
		 */
		//get Policy instance from a URL.
		URL url = getClass().getResource("/antisamy.xml");
		System.out.println("Loading policy from URL: " + url);
		policy = Policy.getInstance(url);
	}


	private URL getResource(String res) {
		URL url = this.getClass().getResource(res);
		System.out.println("Policy URL: " + url);
		return url;
	}

	public void testSAXGoodResult() throws Exception {
		System.out.println("Policy: " + policy);

		// good
		String html = "<div align=\"right\">html</div>";

		CleanResults cleanResults = new AntiSamy(policy).scan(html, AntiSamy.SAX);
		System.out.println("SAX cleanResults: " + cleanResults.getCleanHTML());
		System.out.println("SAX cleanResults error messages: " + cleanResults.getErrorMessages().size());

        for (String msg : cleanResults.getErrorMessages()) {
            System.out.println("error msg: " + msg);
        }

		assertTrue(cleanResults.getErrorMessages().isEmpty());
	}

    public void testSAXBadResult() throws Exception {
        System.out.println("Policy: " + policy);

        // AntiSamy should complain about the attribute value "foo" ... but it is not
        String badHtml = "<div align=\"foo\">badhtml</div>";

        CleanResults cleanResults2 = new AntiSamy(policy).scan(badHtml, AntiSamy.SAX);

        System.out.println("SAX cleanResults2: " + cleanResults2.getCleanHTML());
        System.out.println("SAX cleanResults2 error messages: " + cleanResults2.getErrorMessages().size());
        for (String msg : cleanResults2.getErrorMessages()) {
            System.out.println("error msg: " + msg);
        }
        assertTrue(cleanResults2.getErrorMessages().size() > 0);
    }

    public void testDOMGoodResult() throws Exception {
		System.out.println("Policy: " + policy);

		// good
		String html = "<div align=\"right\">html</div>";

		CleanResults cleanResults = new AntiSamy(policy).scan(html, AntiSamy.DOM);
		System.out.println("DOM cleanResults error messages: " + cleanResults.getErrorMessages().size());
        for (String msg : cleanResults.getErrorMessages()) {
            System.out.println("error msg: " + msg);
        }

		assertTrue(cleanResults.getErrorMessages().isEmpty());
	}

    public void testDOMBadResult() throws Exception {
        System.out.println("Policy: " + policy);

        // AntiSamy should complain about the attribute value "foo" ... but it is not
        String badHtml = "<div align=\"foo\">badhtml</div>";

        CleanResults cleanResults2 = new AntiSamy(policy).scan(badHtml, AntiSamy.DOM);

        System.out.println("DOM cleanResults2 error messages: " + cleanResults2.getErrorMessages().size());
        for (String msg : cleanResults2.getErrorMessages()) {
            System.out.println("error msg: " + msg);
        }
        assertTrue(cleanResults2.getErrorMessages().size() > 0);
    }
}
