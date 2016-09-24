/*
 * Copyright (c) 2007-2011, Arshan Dabirsiaghi, Jason Li
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;

/**
 * A implementation of a SAC DocumentHandler for CSS validation. The appropriate
 * validation method is called whenever the handler is invoked by the parser.
 * The handler also builds a clean CSS document as the original CSS is scanned.
 * 
 * NOTE: keeping state in this class is not ideal as handler style parsing a la
 * SAX should generally be event driven. However, there is not a fully
 * implemented "DOM" equivalent to CSS at this time. Java has a StyleSheet class
 * that could accomplish this "DOM" like behavior but it has yet to be fully
 * implemented.
 * 
 * @see javax.swing.text.html.StyleSheet
 * @author Jason Li
 * 
 */
public class CssHandler implements DocumentHandler {

	/**
	 * The style sheet as it is being built by the handler
	 */
	private StringBuffer styleSheet = new StringBuffer();

	/**
	 * The validator to use when CSS constituents are encountered
	 */
	private final CssValidator validator;

	/**
	 * The policy file to use in validation
	 */
	private final InternalPolicy policy;

	/**
	 * The encaspulated results including the error messages
	 */
//	private final CleanResults results;
	private final Collection<String> errorMessages;
	
	/**
	 * The error message bundled to pull from.
	 */
	private ResourceBundle messages;
	
	/**
	 * A queue of imported stylesheets; used to track imported stylesheets
	 */
	private final LinkedList importedStyleSheets;

	/**
	 * The tag currently being examined (if any); used for inline stylesheet
	 * error messages
	 */
	private final String tagName;

	/**
	 * Indicates whether we are scanning a stylesheet or an inline declaration.
	 * true if this is an inline declaration; false otherwise
	 */
	private final boolean isInline;

	/**
	 * Indicates whether the handler is currently parsing the contents between
	 * an open selector tag and an close selector tag
	 */
	private boolean selectorOpen = false;

	/**
	 * Constructs a handler for stylesheets using the given policy and queue for
	 * imported stylesheets.
	 * 
	 * @param policy
	 *            the policy to use
	 * @param embeddedStyleSheets
	 *            the queue of stylesheets imported
	 */
	public CssHandler(Policy policy, LinkedList embeddedStyleSheets,
		List<String> errorMessages, ResourceBundle messages) {
		this(policy, embeddedStyleSheets, errorMessages, null, messages);
	}

	/**
	 * Constructs a handler for inline style declarations using the given policy
	 * and queue for imported stylesheets.
	 * 
	 * @param policy
	 *            the policy to use
	 * @param embeddedStyleSheets
	 *            the queue of stylesheets imported
	 * @param tagName
	 *            the associated tag name with this inline style
	 */
	public CssHandler(Policy policy, LinkedList embeddedStyleSheets,
			List<String> errorMessages, String tagName, ResourceBundle messages) {
		this.policy = (InternalPolicy) policy;
		this.errorMessages = errorMessages;
		this.messages = messages;
		this.validator = new CssValidator(policy);
		this.importedStyleSheets = embeddedStyleSheets;
		this.tagName = tagName;
		this.isInline = (tagName != null);
	}

	/**
	 * Returns the cleaned stylesheet.
	 * 
	 * @return the cleaned styesheet
	 */
	public String getCleanStylesheet() {
		// Always ensure results contain most recent generation of stylesheet
		return styleSheet.toString();
	}

	/**
	 * Returns the error messages generated during parsing.
	 * @return the error messages generated during parsing
	 */
	public Collection getErrorMessages() {
	    return new ArrayList(errorMessages);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#comment(java.lang.String)
	 */
	public void comment(String text) throws CSSException {
		errorMessages.add(ErrorMessageUtil.getMessage(
				messages,
				ErrorMessageUtil.ERROR_COMMENT_REMOVED,
				new Object[] { HTMLEntityEncoder.htmlEntityEncode(text) }));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#ignorableAtRule(java.lang.String)
	 */
	public void ignorableAtRule(String atRule) throws CSSException {
		// this method is called when the parser hits an unrecognized
		// @-rule. Like the page/media/font declarations, this is
		// CSS2+ stuff
		if (tagName != null) {
			errorMessages.add(ErrorMessageUtil.getMessage(
					messages,
				ErrorMessageUtil.ERROR_CSS_TAG_RULE_NOTFOUND,
				new Object[] { 
					HTMLEntityEncoder.htmlEntityEncode(tagName), 
					HTMLEntityEncoder.htmlEntityEncode(atRule)
				}));
		} else {
			errorMessages.add(ErrorMessageUtil.getMessage(
					messages,
				ErrorMessageUtil.ERROR_STYLESHEET_RULE_NOTFOUND,
				new Object[] {  
					HTMLEntityEncoder.htmlEntityEncode(atRule)
				}));		    
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#importStyle(java.lang.String,
	 *      org.w3c.css.sac.SACMediaList, java.lang.String)
	 */
	public void importStyle(String uri, SACMediaList media,
			String defaultNamespaceURI) throws CSSException {

		if (!policy.isEmbedStyleSheets()) {
			errorMessages.add(ErrorMessageUtil.getMessage(
					messages,
					ErrorMessageUtil.ERROR_CSS_IMPORT_DISABLED,
					new Object[] {}));
			return;
		}
				
		try {
			// check for non-nullness (validate after canonicalization)
			if (uri == null) {
			    errorMessages.add(ErrorMessageUtil.getMessage(
						messages,
					ErrorMessageUtil.ERROR_CSS_IMPORT_URL_INVALID,
					new Object[] { HTMLEntityEncoder.htmlEntityEncode(uri) }));
			    return;			
			} 
			
			URI importedStyleSheet = new URI(uri);

			// canonicalize the URI
			importedStyleSheet.normalize();

			// validate the URL

            if (!policy.getCommonRegularExpressions("offsiteURL").matches(importedStyleSheet.toString())
				&& !policy.getCommonRegularExpressions("onsiteURL").matches(importedStyleSheet.toString())) {
			    errorMessages.add(ErrorMessageUtil.getMessage(
						messages,
					ErrorMessageUtil.ERROR_CSS_IMPORT_URL_INVALID,
					new Object[] { HTMLEntityEncoder.htmlEntityEncode(uri) }));
			    return;			
			} 

			
			if (!importedStyleSheet.isAbsolute()) {
				// we have no concept of relative reference for free form
				// text as an end user can't know where the corresponding
				// free form will end up
			    	if (tagName != null) {
			    	    errorMessages.add(ErrorMessageUtil.getMessage(
			    				messages,
					ErrorMessageUtil.ERROR_CSS_TAG_RELATIVE,
					new Object[] { 
						HTMLEntityEncoder.htmlEntityEncode(tagName),
						HTMLEntityEncoder.htmlEntityEncode(uri) }));
			    	} else {
			    	    errorMessages.add(ErrorMessageUtil.getMessage(
			    				messages,
					ErrorMessageUtil.ERROR_STYLESHEET_RELATIVE,
					new Object[] { HTMLEntityEncoder.htmlEntityEncode(uri) }));
			    	}
				return;
			}


			importedStyleSheets.add(importedStyleSheet);
		} catch (URISyntaxException use) {
			errorMessages.add(ErrorMessageUtil.getMessage(
					messages,
				ErrorMessageUtil.ERROR_CSS_IMPORT_URL_INVALID,
				new Object[] { HTMLEntityEncoder.htmlEntityEncode(uri) }));
			return;
		}	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#namespaceDeclaration(java.lang.String,
	 *      java.lang.String)
	 */
	public void namespaceDeclaration(String prefix, String uri)
			throws CSSException {
		// CSS3 - Namespace declaration - ignore for now
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#startDocument(org.w3c.css.sac.InputSource)
	 */
	public void startDocument(InputSource arg0) throws CSSException {
		// no-op
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#endDocument(org.w3c.css.sac.InputSource)
	 */
	public void endDocument(InputSource source) throws CSSException {
		// no-op
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#startFontFace()
	 */
	public void startFontFace() throws CSSException {
		// CSS2 Font Face declaration - ignore this for now
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#endFontFace()
	 */
	public void endFontFace() throws CSSException {
		// CSS2 Font Face declaration - ignore this for now
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#startMedia(org.w3c.css.sac.SACMediaList)
	 */
	public void startMedia(SACMediaList media) throws CSSException {
		// CSS2 Media declaration - ignore this for now
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#endMedia(org.w3c.css.sac.SACMediaList)
	 */
	public void endMedia(SACMediaList media) throws CSSException {
		// CSS2 Media declaration - ignore this for now
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#startPage(java.lang.String,
	 *      java.lang.String)
	 */
	public void startPage(String name, String pseudoPage) throws CSSException {
		// CSS2 Page declaration - ignore this for now
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#endPage(java.lang.String,
	 *      java.lang.String)
	 */
	public void endPage(String name, String pseudoPage) throws CSSException {
		// CSS2 Page declaration - ignore this for now
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#startSelector(org.w3c.css.sac.SelectorList)
	 */
	public void startSelector(SelectorList selectors) throws CSSException {

		// keep track of number of valid selectors from this rule
		int selectorCount = 0;

		// check each selector from this rule
		for (int i = 0; i < selectors.getLength(); i++) {
			Selector selector = selectors.item(i);

			if (selector != null) {
				String selectorName = selector.toString();

				boolean isValidSelector = false;

				try {
					isValidSelector = validator.isValidSelector(selectorName,
							selector);
				} catch (ScanException se) {
				    if (tagName != null) {					
					errorMessages.add(ErrorMessageUtil.getMessage(
							messages,
						ErrorMessageUtil.ERROR_CSS_TAG_SELECTOR_NOTFOUND,
						new Object[] {
							HTMLEntityEncoder.htmlEntityEncode(selector.toString())
						}));
				    } else {
					errorMessages.add(ErrorMessageUtil.getMessage(
							messages,
						ErrorMessageUtil.ERROR_STYLESHEET_SELECTOR_NOTFOUND,
						new Object[] {
							HTMLEntityEncoder.htmlEntityEncode(tagName),
							HTMLEntityEncoder.htmlEntityEncode(selector.toString())
						}));
				    }
				}

				// if the selector is valid, add to list
				if (isValidSelector) {
					if (selectorCount > 0) {
						styleSheet.append(',');
						styleSheet.append(' ');
					}
					styleSheet.append(selectorName);

					selectorCount++;

				} else {
					if (tagName != null) {
						errorMessages.add(ErrorMessageUtil.getMessage(
								messages,
								ErrorMessageUtil.ERROR_CSS_TAG_SELECTOR_DISALLOWED,
								new Object[] {
									HTMLEntityEncoder.htmlEntityEncode(tagName),
									HTMLEntityEncoder.htmlEntityEncode(selector.toString())
								}));

					} else {
						errorMessages.add(ErrorMessageUtil.getMessage(
								messages,
								ErrorMessageUtil.ERROR_STYLESHEET_SELECTOR_DISALLOWED,
								new Object[] {
									HTMLEntityEncoder.htmlEntityEncode(selector.toString())
								}));								
					}

				}
			}
		}

		// if and only if there were selectors that were valid, append
		// appropriate open brace and set state to within selector
		if (selectorCount > 0) {
			styleSheet.append(' ');
			styleSheet.append('{');
			styleSheet.append('\n');
			selectorOpen = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#endSelector(org.w3c.css.sac.SelectorList)
	 */
	public void endSelector(SelectorList selectors) throws CSSException {
		// if we are in a state within a selector, close brace
		if (selectorOpen) {
			styleSheet.append('}');
			styleSheet.append('\n');
		}

		// reset state
		selectorOpen = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.css.sac.DocumentHandler#property(java.lang.String,
	 *      org.w3c.css.sac.LexicalUnit, boolean)
	 */
	public void property(String name, LexicalUnit value, boolean important)
			throws CSSException {
		// only bother validating and building if we are either inline or within
		// a selector tag

		if (!selectorOpen && !isInline) {
			return;
		}

		// validate the property
		if (validator.isValidProperty(name, value)) {

			if (!isInline) { styleSheet.append('\t'); }
			styleSheet.append(name);
			styleSheet.append(':');

			// append all values
			while (value != null) {
				styleSheet.append(' ');
				styleSheet.append(validator.lexicalValueToString(value));
				value = value.getNextLexicalUnit();
			}
			styleSheet.append(';');
			if (!isInline) { styleSheet.append('\n'); }

		} else {

			if (tagName != null) {
				errorMessages.add(ErrorMessageUtil.getMessage(
					messages,
					ErrorMessageUtil.ERROR_CSS_TAG_PROPERTY_INVALID,
					new Object[] {
						HTMLEntityEncoder.htmlEntityEncode(tagName),
						HTMLEntityEncoder.htmlEntityEncode(name),
						HTMLEntityEncoder.htmlEntityEncode(validator
							.lexicalValueToString(value)) }));			
			} else {
			    	errorMessages.add(ErrorMessageUtil.getMessage(
			    		messages,
			    		ErrorMessageUtil.ERROR_STYLESHEET_PROPERTY_INVALID,
					new Object[] {
						HTMLEntityEncoder.htmlEntityEncode(name),
						HTMLEntityEncoder.htmlEntityEncode(validator
							.lexicalValueToString(value)) }));
			}

		}
	}
}
