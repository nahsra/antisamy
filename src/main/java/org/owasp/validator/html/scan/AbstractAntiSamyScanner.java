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

package org.owasp.validator.html.scan;

import java.io.Writer;
import java.util.*;

import org.apache.xml.serialize.OutputFormat;
import org.owasp.validator.html.*;
import org.owasp.validator.html.util.ErrorMessageUtil;

public abstract class AbstractAntiSamyScanner {

	protected final InternalPolicy policy;
	protected final List<String> errorMessages = new ArrayList<String>();

	protected static final ResourceBundle messages = getResourceBundle();
	protected final Locale locale = Locale.getDefault();

	protected boolean isNofollowAnchors = false;
	protected boolean isValidateParamAsEmbed = false;

	public abstract CleanResults scan(String html) throws ScanException;

	/** @noinspection UnusedDeclaration TODO: Investigate */
    public abstract CleanResults getResults();

	public AbstractAntiSamyScanner(Policy policy) {
		this.policy = (InternalPolicy) policy;
	}

	public AbstractAntiSamyScanner() throws PolicyException {
		policy = (InternalPolicy) Policy.getInstance();
	}

    private static ResourceBundle getResourceBundle() {
        try {
            return ResourceBundle.getBundle("AntiSamy", Locale.getDefault());
        } catch (MissingResourceException mre) {
            return  ResourceBundle.getBundle("AntiSamy", new Locale(Constants.DEFAULT_LOCALE_LANG, Constants.DEFAULT_LOCALE_LOC));
        }
    }

    protected void addError(String errorKey, Object[] objs) {
		errorMessages.add(ErrorMessageUtil.getMessage(messages, errorKey, objs));
	}
	
	
	protected OutputFormat getOutputFormat() {

        OutputFormat format = new OutputFormat();
        format.setOmitXMLDeclaration(policy.isOmitXmlDeclaration());
        format.setOmitDocumentType(policy.isOmitDoctypeDeclaration());
        format.setPreserveEmptyAttributes(true);
        format.setPreserveSpace(policy.isPreserveSpace());
        
        if (policy.isFormatOutput()) {
            format.setLineWidth(80);
            format.setIndenting(true);
            format.setIndent(2);
        }
        
        return format;
	}
	
	/** @noinspection deprecation*/
    protected org.apache.xml.serialize.HTMLSerializer getHTMLSerializer(Writer w, OutputFormat format) {

        if(policy.isUseXhtml()) {
        	return new ASXHTMLSerializer(w, format, policy);
        }
        
        return new ASHTMLSerializer(w, format, policy);
	}

	protected String trim(String original, String cleaned) {
        if (cleaned.endsWith("\n")) {
            if (!original.endsWith("\n")) {
                if (cleaned.endsWith("\r\n")) {
                	cleaned = cleaned.substring(0, cleaned.length() - 2);
                } else if (cleaned.endsWith("\n")) {
                	cleaned = cleaned.substring(0, cleaned.length() - 1);
                }
            }
        }
        
        return cleaned;
	}
}
