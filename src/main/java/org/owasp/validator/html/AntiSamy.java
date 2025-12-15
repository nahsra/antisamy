/*
 * Copyright (c) 2007-2022, Arshan Dabirsiaghi, Jason Li
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. Neither the name of OWASP nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.owasp.validator.html;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import org.owasp.validator.html.scan.AntiSamyDOMScanner;
import org.owasp.validator.html.scan.AntiSamySAXScanner;

/**
 * This and the {@code CleanResults} class are generally the only classes which the outside world
 * should be calling. The {@code scan()} method holds the meat and potatoes of AntiSamy. The file
 * contains a number of ways for {@code scan()}'ing, depending on the accessibility of the policy
 * file. However, it should be noted that the SAX scan type, which uses a SAX-based parser should be
 * the preferred way of using AntiSamy as it is much more efficient, and generally faster, than the
 * DOM-based parser.
 *
 * @author Arshan Dabirsiaghi
 */
public class AntiSamy {

  /**
   * Designates DOM scan type which calls the DOM parser.
   *
   * @deprecated Support for DOM parser will be removed.
   */
  @Deprecated public static final int DOM = 0;

  /** Designates SAX scan type which calls the SAX parser. */
  public static final int SAX = 1;

  private Policy policy = null;

  public AntiSamy() {}

  public AntiSamy(Policy policy) {
    this.policy = policy;
  }

  /**
   * The <code>scan()</code> family of methods are the only methods the outside world should be
   * calling to invoke AntiSamy. This is the primary method that most AntiSamy users should be
   * using. This method scans the supplied HTML input and produces clean/sanitized results per the
   * previously configured AntiSamy policy using the SAX parser.
   *
   * @param taintedHTML Untrusted HTML which may contain malicious code.
   * @return A <code>CleanResults</code> object which contains information about the scan (including
   *     the results).
   * @throws ScanException When there is a problem encountered while scanning the HTML input.
   * @throws PolicyException When there is a problem validating or parsing the policy file.
   */
  public CleanResults scan(String taintedHTML) throws ScanException, PolicyException {
    return this.scan(taintedHTML, this.policy, SAX);
  }

  /**
   * This method scans the supplied HTML input and produces clean/sanitized results per the
   * previously configured AntiSamy policy using the specified DOM or SAX parser.
   *
   * @param taintedHTML Untrusted HTML which may contain malicious code.
   * @param scanType The type of scan (DOM or SAX).
   * @return A <code>CleanResults</code> object which contains information about the scan (including
   *     the results).
   * @throws ScanException When there is a problem encountered while scanning the HTML input.
   * @throws PolicyException When there is a problem validating or parsing the policy file.
   * @deprecated Support for DOM parser will be removed, usage of method without <code>scanType
   *     </code> is encouraged.
   */
  @Deprecated
  public CleanResults scan(String taintedHTML, int scanType) throws ScanException, PolicyException {

    return this.scan(taintedHTML, this.policy, scanType);
  }

  /**
   * This method scans the supplied HTML input and produces clean/sanitized results per the supplied
   * AntiSamy policy using the DOM parser.
   *
   * @param taintedHTML Untrusted HTML which may contain malicious code.
   * @param policy The custom policy to enforce.
   * @return A <code>CleanResults</code> object which contains information about the scan (including
   *     the results).
   * @throws ScanException When there is a problem encountered while scanning the HTML input.
   * @throws PolicyException When there is a problem validating or parsing the policy file.
   * @deprecated Support for DOM parser will be removed.
   */
  @Deprecated
  public CleanResults scan(String taintedHTML, Policy policy)
      throws ScanException, PolicyException {
    return this.scan(taintedHTML, policy, DOM);
  }

  /**
   * This method scans the supplied HTML input and produces clean/sanitized results per the supplied
   * AntiSamy policy using the specified DOM or SAX parser.
   *
   * @param taintedHTML Untrusted HTML which may contain malicious code.
   * @param policy The custom policy to enforce.
   * @param scanType The type of scan (DOM or SAX).
   * @return A <code>CleanResults</code> object which contains information about the scan (including
   *     the results).
   * @throws ScanException When there is a problem encountered while scanning the HTML input.
   * @throws PolicyException When there is a problem validating or parsing the policy file.
   * @deprecated Support for DOM parser will be removed, SAX parser will be used in the future.
   *     Usage of method without <code>scanType</code> is encouraged.
   */
  @Deprecated
  public CleanResults scan(String taintedHTML, Policy policy, int scanType)
      throws ScanException, PolicyException {
    if (policy == null) {
      throw new PolicyException("No policy loaded");
    }

    if (scanType == DOM) {
      return new AntiSamyDOMScanner(policy).scan(taintedHTML);
    } else {
      return new AntiSamySAXScanner(policy).scan(taintedHTML);
    }
  }

  /**
   * Use this method if caller has Streams rather than Strings for I/O. This uses the SAX parser. It
   * is useful for when the input being processed is expected to be very large and we don't
   * validate, but rather simply encode as bytes are consumed from the stream.
   *
   * @param reader Reader that produces the input, possibly a little at a time
   * @param writer Writer that receives the cleaned output, possibly a little at a time
   * @param policy Policy that directs the scan
   * @return CleanResults where the cleanHtml is null. If caller wants the clean HTML, it must
   *     capture the writer's contents. When using Streams, caller generally doesn't want to create
   *     a single string containing clean HTML.
   * @throws ScanException When there is a problem encountered while scanning the HTML input.
   */
  public CleanResults scan(Reader reader, Writer writer, Policy policy) throws ScanException {
    return (new AntiSamySAXScanner(policy)).scan(reader, writer);
  }

  /**
   * This method scans the supplied HTML input and produces clean/sanitized results per the supplied
   * AntiSamy policy file using the DOM parser.
   *
   * @param taintedHTML Untrusted HTML which may contain malicious code.
   * @param policyFilename The file name of the custom policy to enforce.
   * @return A <code>CleanResults</code> object which contains information about the scan (including
   *     the results).
   * @throws ScanException When there is a problem encountered while scanning the HTML input.
   * @throws PolicyException When there is a problem validating or parsing the policy file.
   * @deprecated Support for DOM parser will be removed, SAX parser will be used in the future.
   */
  @Deprecated
  public CleanResults scan(String taintedHTML, String policyFilename)
      throws ScanException, PolicyException {

    Policy policy = Policy.getInstance(policyFilename);

    return this.scan(taintedHTML, policy);
  }

  /**
   * This method scans the supplied HTML input and produces clean/sanitized results per the supplied
   * AntiSamy policy file using the DOM parser.
   *
   * @param taintedHTML Untrusted HTML which may contain malicious code.
   * @param policyFile The File object of the custom policy to enforce.
   * @return A <code>CleanResults</code> object which contains information about the scan (including
   *     the results).
   * @throws ScanException When there is a problem encountered while scanning the HTML input.
   * @throws PolicyException When there is a problem validating or parsing the policy file.
   * @deprecated Support for DOM parser will be removed, SAX parser will be used in the future.
   */
  @Deprecated
  public CleanResults scan(String taintedHTML, File policyFile)
      throws ScanException, PolicyException {

    Policy policy = Policy.getInstance(policyFile);

    return this.scan(taintedHTML, policy);
  }
}
