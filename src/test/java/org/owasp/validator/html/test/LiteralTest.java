/*
 * Copyright (c) 2007-2022, Arshan Dabirsiaghi, Jason Li
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

import java.net.URL;

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
		//System.out.println("Loading policy from URL: " + url);
		policy = Policy.getInstance(url);
	}
	
	public void testSAXGoodResult() throws Exception {
		//System.out.println("Policy: " + policy);

		// good
		String html = "<div align=\"right\">html</div>";

		CleanResults cleanResults = new AntiSamy(policy).scan(html, AntiSamy.SAX);
		//System.out.println("SAX cleanResults: " + cleanResults.getCleanHTML());
		//System.out.println("SAX cleanResults error messages: " + cleanResults.getErrorMessages().size());

        for (String msg : cleanResults.getErrorMessages()) {
            System.out.println("error msg: " + msg);
        }

		assertTrue(cleanResults.getErrorMessages().isEmpty());
	}

    public void testSAXBadResult() throws Exception {
        //System.out.println("Policy: " + policy);

        // AntiSamy should complain about the attribute value "foo" ... but it is not
        String badHtml = "<div align=\"foo\">badhtml</div>";

        CleanResults cleanResults2 = new AntiSamy(policy).scan(badHtml, AntiSamy.SAX);

        //System.out.println("SAX cleanResults2: " + cleanResults2.getCleanHTML());
        //System.out.println("SAX cleanResults2 error messages: " + cleanResults2.getErrorMessages().size());
        /* for (String msg : cleanResults2.getErrorMessages()) {
            System.out.println("error msg: " + msg);
        } */
        assertTrue(cleanResults2.getErrorMessages().size() > 0);
    }

    public void testDOMGoodResult() throws Exception {
		//System.out.println("Policy: " + policy);

		// good
		String html = "<div align=\"right\">html</div>";

		CleanResults cleanResults = new AntiSamy(policy).scan(html, AntiSamy.DOM);
		//System.out.println("DOM cleanResults error messages: " + cleanResults.getErrorMessages().size());
        for (String msg : cleanResults.getErrorMessages()) {
            System.out.println("error msg: " + msg);
        }

		assertTrue(cleanResults.getErrorMessages().isEmpty());
	}

    public void testDOMBadResult() throws Exception {
        //System.out.println("Policy: " + policy);

        // AntiSamy should complain about the attribute value "foo" ... but it is not
        String badHtml = "<div align=\"foo\">badhtml</div>";

        CleanResults cleanResults2 = new AntiSamy(policy).scan(badHtml, AntiSamy.DOM);

        //System.out.println("DOM cleanResults2 error messages: " + cleanResults2.getErrorMessages().size());
        /* for (String msg : cleanResults2.getErrorMessages()) {
            System.out.println("error msg: " + msg);
        } */
        assertTrue(cleanResults2.getErrorMessages().size() > 0);
    }
}
