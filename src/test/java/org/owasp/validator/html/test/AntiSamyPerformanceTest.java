/*
 * Copyright (c) 2007-2011, Arshan Dabirsiaghi, Jason Li
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *                                                  1
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

package org.owasp.validator.html.test;

import org.junit.Before;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class tests AntiSamy functionality and the basic policy file which
 * should be immune to XSS and CSS phishing attacks.
 *
 * @author Arshan Dabirsiaghi
 * @author Kristian Rosenvold
 */

public class AntiSamyPerformanceTest {


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


    @org.junit.Test
    public void compareSpeedsLargeFiles() throws IOException, ScanException, PolicyException {

        URL[] urls = {
                this.getClass().getResource("/s/slashdot.org.htm"),
                this.getClass().getResource("/s/fark.com"),
                this.getClass().getResource("/s/cnn.com"),
                this.getClass().getResource("/s/google.com.html"),
                this.getClass().getResource("/s/microsoft.com"),
        };

        double totalDomTime = 0;
        double totalSaxTime = 0;

        int testReps = 15;

        for (URL url : urls) {
            URLConnection conn = url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            StringBuilder out = new StringBuilder();
            char[] buffer = new char[5000];
            int read;
            do {
                read = in.read(buffer, 0, buffer.length);
                if (read > 0) {
                    out.append(buffer, 0, read);
                }
            } while (read >= 0);

            in.close();

            String html = out.toString();

            System.out.println("About to scan: " + url + " size: " + html.length());
            if (html.length() > policy.determineMaxInputSize()) {
                System.out.println("   -Maximum input size exceeded. SKIPPING.");
                continue;
            }

            double domTime = 0;
            double saxTime = 0;

            for (int j = 0; j < testReps; j++) {
                domTime += as.scan(html, policy, AntiSamy.DOM).getScanTime();
                saxTime += as.scan(html, policy, AntiSamy.SAX).getScanTime();
            }

            domTime = domTime / testReps;
            saxTime = saxTime / testReps;

            totalDomTime += domTime;
            totalSaxTime += saxTime;
        }

        System.out.println("Total DOM time: " + totalDomTime);
        System.out.println("Total SAX time: " + totalSaxTime);
    }
}