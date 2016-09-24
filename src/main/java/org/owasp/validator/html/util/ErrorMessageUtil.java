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
package org.owasp.validator.html.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public final class ErrorMessageUtil {

	public static final String ERROR_TAG_NOT_IN_POLICY = "error.tag.notfound";
	public static final String ERROR_TAG_DISALLOWED = "error.tag.removed";
	public static final String ERROR_TAG_FILTERED = "error.tag.filtered";
	public static final String ERROR_TAG_ENCODED = "error.tag.encoded";
	public static final String ERROR_TAG_EMPTY = "error.tag.empty";
	
	public static final String ERROR_CDATA_FOUND = "error.cdata.found";
	public static final String ERROR_PI_FOUND = "error.pi.found";
	
	public static final String ERROR_ATTRIBUTE_CAUSE_FILTER = "error.attribute.invalid.filtered";
	public static final String ERROR_ATTRIBUTE_CAUSE_ENCODE = "error.attribute.invalid.encoded";
	public static final String ERROR_ATTRIBUTE_INVALID_FILTERED = "error.attribute.invalid.filtered";
	public static final String ERROR_ATTRIBUTE_INVALID_REMOVED = "error.attribute.invalid.removed";
	public static final String ERROR_ATTRIBUTE_NOT_IN_POLICY = "error.attribute.notfound";
	public static final String ERROR_ATTRIBUTE_INVALID = "error.attribute.invalid";

	public static final String ERROR_COMMENT_REMOVED = "error.comment.removed";
	
	public static final String ERROR_INPUT_SIZE = "error.size.toolarge";

	public static final String ERROR_CSS_ATTRIBUTE_MALFORMED = "error.css.attribute.malformed";
	public static final String ERROR_CSS_TAG_MALFORMED = "error.css.tag.malformed";
	
	public static final String ERROR_STYLESHEET_NOT_ALLOWED = "error.css.disallowed";
	
	public static final String ERROR_CSS_IMPORT_DISABLED = "error.css.import.disabled";
	public static final String ERROR_CSS_IMPORT_EXCEEDED = "error.css.import.exceeded";
	public static final String ERROR_CSS_IMPORT_FAILURE = "error.css.import.failure";
	public static final String ERROR_CSS_IMPORT_INPUT_SIZE = "error.css.import.toolarge";
	public static final String ERROR_CSS_IMPORT_URL_INVALID = "error.css.import.url.invalid";

	public static final String ERROR_STYLESHEET_RELATIVE = "error.css.stylesheet.relative";
	public static final String ERROR_CSS_TAG_RELATIVE = "error.css.tag.relative";
	
	public static final String ERROR_STYLESHEET_RULE_NOTFOUND = "error.css.stylesheet.rule.notfound";
	public static final String ERROR_CSS_TAG_RULE_NOTFOUND = "error.css.tag.rule.notfound";
	
	public static final String ERROR_STYLESHEET_SELECTOR_NOTFOUND = "error.css.stylesheet.selector.notfound";
	public static final String ERROR_CSS_TAG_SELECTOR_NOTFOUND = "error.css.tag.selector.notfound";
	
	public static final String ERROR_STYLESHEET_SELECTOR_DISALLOWED = "error.css.stylesheet.selector.disallowed";
	public static final String ERROR_CSS_TAG_SELECTOR_DISALLOWED = "error.css.tag.selector.disallowed";
	
	public static final String ERROR_STYLESHEET_PROPERTY_INVALID = "error.css.stylesheet.property.invalid";
	public static final String ERROR_CSS_TAG_PROPERTY_INVALID = "error.css.tag.property.invalid";	

	private ErrorMessageUtil() {}

	public static String getMessage(ResourceBundle messages, String msgKey, Object[] arguments) {	
		return MessageFormat.format( messages.getString(msgKey), arguments );
	}
	
}
