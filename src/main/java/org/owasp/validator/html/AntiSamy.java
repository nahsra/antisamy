/*
 * Copyright (c) 2007-2011, Arshan Dabirsiaghi, Jason Li
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of OWASP nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
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

package org.owasp.validator.html;

import org.owasp.validator.html.scan.AntiSamyDOMScanner;
import org.owasp.validator.html.scan.AntiSamySAXScanner;

import java.io.File;

/**
 * 
 * This is the only class from which the outside world should be calling. The
 * <code>scan()</code> method holds the meat and potatoes of AntiSamy. The file
 * contains a number of ways for <code>scan()</code>'ing depending on the
 * accessibility of the policy file.
 * 
 * @author Arshan Dabirsiaghi
 * 
 */

public class AntiSamy {

	public static int DOM = 0;
	public static int SAX = 1;

	private Policy policy = null;

	public AntiSamy() {
	}

	public AntiSamy(Policy policy) {
		this.policy = policy;
	}

	/**
	 * The meat and potatoes. The <code>scan()</code> family of methods are the
	 * only methods the outside world should be calling to invoke AntiSamy.
	 * 
	 * @param taintedHTML
	 *            Untrusted HTML which may contain malicious code.
	 * @return A <code>CleanResults</code> object which contains information
	 *         about the scan (including the results).
	 * @throws ScanException When there is a problem encountered
	 *         while scanning the HTML.
	 * @throws PolicyException When there is a problem reading the
	 *         policy file.
     *
     */

	public CleanResults scan(String taintedHTML) throws ScanException, PolicyException {

		if (policy == null) {
			throw new PolicyException("No policy loaded");
		}

		return this.scan(taintedHTML, this.policy, SAX);
	}

	public CleanResults scan(String taintedHTML, int scanType) throws ScanException, PolicyException {

		if (policy == null) {
			throw new PolicyException("No policy loaded");
		}
		return this.scan(taintedHTML, this.policy, scanType);
	}

	/**
	 * This method wraps <code>scan()</code> using the Policy object passed in.
	 */
	public CleanResults scan(String taintedHTML, Policy policy) throws ScanException, PolicyException {
		return new AntiSamyDOMScanner(policy).scan(taintedHTML);
	}

	public CleanResults scan(String taintedHTML, Policy policy, int scanType) throws ScanException, PolicyException {

		if (scanType == DOM) {
			return new AntiSamyDOMScanner(policy).scan(taintedHTML);
		} else {
			return new AntiSamySAXScanner(policy).scan(taintedHTML);
		}
	}

	/**
	 * This method wraps <code>scan()</code> using the Policy object passed in.
	 */
	public CleanResults scan(String taintedHTML, String filename) throws ScanException, PolicyException {

        Policy policy = Policy.getInstance(filename);

        return this.scan(taintedHTML, policy);
	}

	/**
	 * This method wraps <code>scan()</code> using the policy File object passed
	 * in.
	 */
	public CleanResults scan(String taintedHTML, File policyFile) throws ScanException, PolicyException {

        Policy policy = Policy.getInstance(policyFile);

        return this.scan(taintedHTML, policy);
	}

}
