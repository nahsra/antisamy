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

import org.junit.Before;
import org.junit.Test;
import org.owasp.validator.html.*;
import org.owasp.validator.html.scan.Constants;
import org.owasp.validator.html.test.TestPolicy;

import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class CssScannerTest {
    private TestPolicy policy = null;
    private ResourceBundle messages = null;

    @Before
    public void setUp() throws Exception {
        // Load the policy. You may have to change the path to find the Policy file for your environment.
        URL url = getClass().getResource("/antisamy.xml");
        policy = TestPolicy.getInstance(url);
        // Load resource bundle
        try {
            messages = ResourceBundle.getBundle("AntiSamy", Locale.getDefault());
        } catch (MissingResourceException mre) {
            messages =  ResourceBundle.getBundle("AntiSamy", new Locale(Constants.DEFAULT_LOCALE_LANG,
                    Constants.DEFAULT_LOCALE_LOC));
        }
    }

    @Test
    public void testAvoidImportingStyles() throws ScanException {
        final String input = "@import url(https://raw.githubusercontent.com/nahsra/antisamy/main/src/test/resources/s/slashdot.org_files/classic.css);\n" +
                ".very-specific-antisamy {font: 15pt \"Arial\"; color: blue;}";
        // If not passing "shouldParseImportedStyles" then it's false by default.
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

    @Test
    public void testImportLimiting() throws ScanException {
        final String input = "@import url(https://raw.githubusercontent.com/nahsra/antisamy/main/src/test/resources/s/slashdot.org_files/classic.css);\n" +
                "@import url(https://raw.githubusercontent.com/nahsra/antisamy/main/src/test/resources/s/slashdot.org_files/providers.css);\n" +
                ".very-specific-antisamy {font: 15pt \"Arial\"; color: blue;}";
        TestPolicy revised = policy.cloneWithDirective(Policy.EMBED_STYLESHEETS,"true")
                .cloneWithDirective(Policy.MAX_INPUT_SIZE,"500")
                .cloneWithDirective(Policy.MAX_STYLESHEET_IMPORTS,"2");
        CssScanner scanner = new CssScanner(revised, messages, true);
        CleanResults result = scanner.scanStyleSheet(input, 500);
        // Both sheets are larger than 500 bytes
        assertThat(result.getErrorMessages().size(), is(2));
        assertThat(result.getErrorMessages().get(0), containsString("500"));

        // Limit to only one import
        revised = policy.cloneWithDirective(Policy.EMBED_STYLESHEETS,"true")
                .cloneWithDirective(Policy.MAX_STYLESHEET_IMPORTS,"1");
        scanner = new CssScanner(revised, messages, true);
        result = scanner.scanStyleSheet(input, 500000);
        // If only first style sheet was imported, .grid_1 class should be there and .janrain-provider150-sprit classes should not.
        assertThat(result.getCleanHTML(), containsString(".grid_1"));
        assertThat(result.getCleanHTML(), not(containsString(".janrain-provider150-sprit")));

        // Force timeout errors
        revised = policy.cloneWithDirective(Policy.EMBED_STYLESHEETS,"true")
                .cloneWithDirective(Policy.CONNECTION_TIMEOUT,"1");
        scanner = new CssScanner(revised, messages, true);
        result = scanner.scanStyleSheet(input, 500000);
        assertThat(result.getErrorMessages().size(), is(2));
        // If style sheets were imported, .grid_1 and .janrain-provider150-sprit classes should be there.
        assertThat(result.getCleanHTML(), not(containsString(".grid_1")));
        assertThat(result.getCleanHTML(), not(containsString(".janrain-provider150-sprit")));
    }
}
