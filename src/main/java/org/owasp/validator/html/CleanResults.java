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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.w3c.dom.DocumentFragment;

/**
 * This class contains the results of a scan.
 * 
 * The list of error messages (<code>errorMessages</code>) will let the user
 * know what, if any HTML errors existed, and what, if any, security or
 * validation-related errors existed, and what was done about them.
 * 
 * @author Arshan Dabirsiaghi
 * 
 */

public class CleanResults {

	private List<String> errorMessages = new ArrayList<String>();
	private Callable<String> cleanHTML;
	private long elapsedScan;

	private DocumentFragment cleanXMLDocumentFragment;

	/*
	 * For extension.
	 */
	public CleanResults() {

	}

	public CleanResults(long startOfScan, final String cleanHTML,
			DocumentFragment XMLDocumentFragment, List<String> errorMessages) {
		this.elapsedScan = System.currentTimeMillis() - startOfScan;
		this.cleanXMLDocumentFragment = XMLDocumentFragment;
		this.cleanHTML = new Callable<String>() {
            public String call() throws Exception {
                return cleanHTML;
            }
        };
		this.errorMessages = errorMessages;
	}

    public CleanResults(long startOfScan, Callable<String> cleanHTML,
                        DocumentFragment XMLDocumentFragment, List<String> errorMessages) {
        this.elapsedScan = System.currentTimeMillis() - startOfScan;
        this.cleanXMLDocumentFragment = XMLDocumentFragment;
        this.cleanHTML = cleanHTML;
        this.errorMessages = errorMessages;
    }

	public DocumentFragment getCleanXMLDocumentFragment() {
		return cleanXMLDocumentFragment;
	}

    /**
	 * Return the filtered HTML as a String.
	 * 
	 * @return A String object which contains the serialized, safe HTML.
	 */
	public String getCleanHTML() {
        try {
            return cleanHTML.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	/**
	 * Return a list of error messages.
	 * 
	 * @return An ArrayList object which contain the error messages after a
	 *         scan.
	 */
	public List<String> getErrorMessages() {
		return errorMessages;
	}

    /**
	 * Return the time elapsed during the scan.
	 * 
	 * @return A double primitive indicating the amount of time elapsed between
	 *         the beginning and end of the scan in seconds.
	 */
	public double getScanTime() {
		return (elapsedScan) / 1000D;
	}

	/**
	 * Return the number of errors encountered during filtering.
	 */
	public int getNumberOfErrors() {
		return errorMessages.size();
	}

}
