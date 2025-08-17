/*
 * Copyright (c) 2007-2022, Arshan Dabirsiaghi, Jason Li, Sebastián Passaro
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
package org.owasp.validator.css;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.junit.Before;
import org.junit.Test;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.scan.Constants;
import org.owasp.validator.html.test.TestPolicy;

public class CssScannerTest {
  private TestPolicy policy = null;
  private ResourceBundle messages = null;

  @Before
  public void setUp() throws Exception {
    // Load the policy. You may have to change the path to find the Policy file for your
    // environment.
    URL url = getClass().getResource("/antisamy.xml");
    policy = TestPolicy.getInstance(url);
    // Load resource bundle
    try {
      messages = ResourceBundle.getBundle("AntiSamy", Locale.getDefault());
    } catch (MissingResourceException mre) {
      messages =
          ResourceBundle.getBundle(
              "AntiSamy", new Locale(Constants.DEFAULT_LOCALE_LANG, Constants.DEFAULT_LOCALE_LOC));
    }
  }

  @Test
  public void testAvoidImportingStyles() throws ScanException {
    final String input =
        "@import url(https://raw.githubusercontent.com/nahsra/antisamy/main/src/test/resources/s/slashdot.org_files/classic.css);\n"
            + ".very-specific-antisamy {font: 15pt \"Arial\"; color: blue;}";
    CssScanner scanner = new CssScanner(policy, messages);
    String result = scanner.scanStyleSheet(input, 1000).getCleanHTML();
    // If style sheet was imported, .grid_1 class should be there.
    assertThat(result, not(containsString(".grid_1")));
    assertThat(result, containsString(".very-specific-antisamy"));
  }

  @Test
  public void testReAddCdataIfPresentInStyle() throws ScanException {
    final String input = "<![CDATA[.very-specific-antisamy {font: 15pt \"Arial\"; color: blue;}]]>";
    CssScanner scanner = new CssScanner(policy, messages);
    assertThat(scanner.scanStyleSheet(input, 1000).getCleanHTML(), containsString("CDATA"));
  }
}
