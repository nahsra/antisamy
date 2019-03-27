/*
 * Copyright (c) 2007-2019, Arshan Dabirsiaghi, Jason Li
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, 
 * 	 this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of OWASP nor the names of its contributors may be used to
 *   endorse or promote products derived from this software without specific
 *   prior written permission.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import org.w3c.css.sac.InputSource;

public class ExternalCssScanner extends CssScanner {

	public ExternalCssScanner(InternalPolicy policy, ResourceBundle messages) {
		super(policy, messages);
	}

	/**
	 * Parses through a <code>LinkedList</code> of imported stylesheet
	 * URIs, this method parses through those stylesheets and validates them
	 * 
	 * @param stylesheets the <code>LinkedList</code> of stylesheet URIs to parse
	 * @param handler the <code>CssHandler</code> to use for parsing
	 * @param errorMessages the list of error messages to append to
	 * @param sizeLimit the limit on the total size in bites of any imported stylesheets
	 * @throws ScanException if an error occurs during scanning
	 */
	protected void parseImportedStylesheets(LinkedList<?> stylesheets, CssHandler handler,
			ArrayList<String> errorMessages, int sizeLimit) throws ScanException {
			
			int importedStylesheets = 0;
			
			// if stylesheets were imported by the inline style declaration,
			// continue parsing the nested styles. Note this only happens
			// if CSS importing was enabled in the policy file
			if (!stylesheets.isEmpty()) {
				// Ensure that we have appropriate timeout values so we don't
			    // get DoSed waiting for returns
			    int timeout = DEFAULT_TIMEOUT;
			    try {
			    	timeout = Integer.parseInt(policy.getDirective(Policy.CONNECTION_TIMEOUT));
			    } catch (NumberFormatException nfe) {
			    }
			    
			    RequestConfig requestConfig = RequestConfig.custom()
						  .setSocketTimeout(timeout)
						  .setConnectTimeout(timeout)
						  .setConnectionRequestTimeout(timeout)
						  .build();
			    
			    HttpClient httpClient = HttpClientBuilder.create().
			    		disableAutomaticRetries().
			    		disableConnectionState().
			    		disableCookieManagement().
			    		setDefaultRequestConfig(requestConfig).
			    		build();
			
			    int allowedImports = Policy.DEFAULT_MAX_STYLESHEET_IMPORTS;
			    try {
					allowedImports = Integer.parseInt(policy.getDirective("maxStyleSheetImports"));
			    } catch (NumberFormatException nfe) {
			    }
			
			    while (!stylesheets.isEmpty()) {
			
				URI stylesheetUri = (URI) stylesheets.removeFirst();
			
				if (++importedStylesheets > allowedImports) {
				    errorMessages.add(ErrorMessageUtil.getMessage(
				    	messages,
					    ErrorMessageUtil.ERROR_CSS_IMPORT_EXCEEDED,
					    new Object[] {
						    HTMLEntityEncoder
							    .htmlEntityEncode(stylesheetUri
								    .toString()),
						    String.valueOf(allowedImports) }));
				    continue;
				}
			
				HttpGet stylesheetRequest = new HttpGet(stylesheetUri);
			
				byte[] stylesheet = null;
				try {
				    // pull down stylesheet, observing size limit
				    HttpResponse response = httpClient.execute(stylesheetRequest);
				    stylesheet = EntityUtils.toByteArray(response.getEntity());
				    if(stylesheet != null && stylesheet.length > sizeLimit) {
				    	errorMessages
					    .add(ErrorMessageUtil
						    .getMessage(
						    	messages,
							    ErrorMessageUtil.ERROR_CSS_IMPORT_INPUT_SIZE,
							    new Object[] {
								    HTMLEntityEncoder
									    .htmlEntityEncode(stylesheetUri
										    .toString()),
								    String.valueOf(policy
									    .getMaxInputSize()) }));
				    	stylesheet = null;
				    }
				} catch (IOException ioe) {
				    errorMessages.add(ErrorMessageUtil
					    .getMessage(
					    	messages,
						    ErrorMessageUtil.ERROR_CSS_IMPORT_FAILURE,
						    new Object[] { HTMLEntityEncoder
							    .htmlEntityEncode(stylesheetUri
								    .toString()) }));
				} finally {
				    stylesheetRequest.releaseConnection();
				}
			
				if (stylesheet != null) {
				    // decrease the size limit based on the
				    sizeLimit -= stylesheet.length;
			
				    try {
					InputSource nextStyleSheet = new InputSource(
						new InputStreamReader(new ByteArrayInputStream(
							stylesheet)));
					parser.parseStyleSheet(nextStyleSheet);
			
				    } catch (IOException ioe) {
					throw new ScanException(ioe);
				    }
			
				}
			}
		}
	}
}
