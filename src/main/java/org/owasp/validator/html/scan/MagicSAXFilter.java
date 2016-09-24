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

import java.util.*;
import java.util.regex.Pattern;

import org.apache.xerces.util.AugmentationsImpl;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.DefaultFilter;
import org.owasp.validator.css.CssScanner;
import org.owasp.validator.css.ExternalCssScanner;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.model.Attribute;
import org.owasp.validator.html.model.Tag;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.owasp.validator.html.util.HTMLEntityEncoder;

/**
 * Implementation of an HTML-filter that adheres to an AntiSamy policy. This
 * filter is SAX-based which means it is much more memory-efficient and also a
 * bit faster than the DOM-based implementation.
 */
public class MagicSAXFilter extends DefaultFilter implements XMLDocumentFilter {

    private static enum Ops {
        CSS, FILTER, REMOVE, TRUNCATE, KEEP
    }
	private final Stack<Ops> operations = new Stack<Ops>();
	private List<String> errorMessages = new ArrayList<String>();
	private StringBuffer cssContent = null;
	private XMLAttributes cssAttributes = null;
	private CssScanner cssScanner = null;
	private InternalPolicy policy;
	private ResourceBundle messages;

	private boolean isNofollowAnchors;
	private boolean isValidateParamAsEmbed;
	private boolean inCdata = false;
    // From policy
    private boolean preserveComments;
    private int maxInputSize;
    private boolean externalCssScanner;

    public MagicSAXFilter(ResourceBundle messages) {
		this.messages = messages;
    }

    public void reset(InternalPolicy instance){
        this.policy = instance;
        isNofollowAnchors = policy.isNofollowAnchors();
        isValidateParamAsEmbed = policy.isValidateParamAsEmbed();
        preserveComments = policy.isPreserveComments();
        maxInputSize = policy.getMaxInputSize();
        externalCssScanner = policy.isEmbedStyleSheets();
        operations.clear();
        errorMessages.clear();
        cssContent = null;
        cssAttributes = null;
        cssScanner = null;
        inCdata = false;

    }

	public void characters(XMLString text, Augmentations augs) throws XNIException {
        //noinspection StatementWithEmptyBody
        Ops topOp = peekTop();
        //noinspection StatementWithEmptyBody
        if (topOp ==  Ops.REMOVE) {
			// content is removed altogether
		} else if (topOp == Ops.CSS) {
			// we record the style element's text content
			// to filter it later
			cssContent.append(text.ch, text.offset, text.length);
		} else {
			// pass through all character content.
			if ( inCdata ) {
				String encoded = HTMLEntityEncoder.htmlEntityEncode(text.toString());
                addError(ErrorMessageUtil.ERROR_CDATA_FOUND, new Object[]{encoded});
			}
			super.characters(text, augs);
		}
	}

    private static final Pattern conditionalDirectives =
            Pattern.compile("<?!?\\[\\s*(?:end)?if[^]]*\\]>?");

    public void comment(XMLString text, Augmentations augs) throws XNIException {

		if (preserveComments) {
			String value = text.toString();
			// Strip conditional directives regardless of the
			// PRESERVE_COMMENTS setting.
			if (value != null) {
                value = conditionalDirectives.matcher(value).replaceAll("");
				super.comment(new XMLString(value.toCharArray(), 0, value.length()), augs);
			}
		}
	}

	public void doctypeDecl(String root, String publicId, String systemId, Augmentations augs) throws XNIException {
		// user supplied doctypes are ignored
	}

	public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
		this.startElement(element, attributes, augs);
		this.endElement(element, augs);
	}

    private Ops peekTop(){
         return operations.empty() ? null : operations.peek();
    }

	public void endElement(QName element, Augmentations augs) throws XNIException {
        Ops topOp = peekTop();
        if (Ops.REMOVE == topOp) {
			// content is removed altogether
			operations.pop();
		} else if (Ops.FILTER == topOp) {
			// content is removed, but child nodes not
			operations.pop();
		} else if (Ops.CSS == topOp) {
			operations.pop();
			// now scan the CSS.
			CssScanner cssScanner = makeCssScanner();
			try {
				CleanResults results = cssScanner.scanStyleSheet(cssContent.toString(), maxInputSize);
				// report all errors found
				errorMessages.addAll(results.getErrorMessages());
				/*
				 * If IE gets an empty style tag, i.e. <style/> it will break
				 * all CSS on the page. I wish I was kidding. So, if after
				 * validation no CSS properties are left, we would normally be
				 * left with an empty style tag and break all CSS. To prevent
				 * that, we have this check.
				 */
                //noinspection StatementWithEmptyBody
                if (results.getCleanHTML() == null || results.getCleanHTML().equals("")) {
					// we do not generate empty style elements
				} else {
					// XMLAttributes attributes = new XMLAttributesImpl();
					// attributes.addAttribute(makeSimpleQname("type"), "CDATA",
					// "text/css");
					// start the CSS element

					super.startElement(element, cssAttributes, new AugmentationsImpl());
					// send the cleaned content
					super.characters(new XMLStringBuffer(results.getCleanHTML()), new AugmentationsImpl());
					// end the CSS element
					super.endElement(element, augs);
				}
			} catch (ScanException e) {
				// if the CSS is unscannable, we report the error, but skip the
				// style element
				addError(ErrorMessageUtil.ERROR_CSS_TAG_MALFORMED, new Object[] {
					HTMLEntityEncoder.htmlEntityEncode(cssContent.toString())
				});
			} finally {
				// reset the string buffer to allow fresh recording of next
				// style tag
				cssContent = null;
				cssAttributes = null;
			}
		} else {
			// keep or truncate means the end-tag stays intact
			operations.pop();
			super.endElement(element, augs);
		}
	}

	private CssScanner makeCssScanner() {
		if (cssScanner == null) {
            cssScanner = externalCssScanner ? new ExternalCssScanner(policy, messages) : new CssScanner(policy, messages);
		}
		return cssScanner;
	}

	public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
		// processing instructions are being removed
	}
	
	public void startCDATA(Augmentations augs) throws XNIException {
		inCdata = true;
		super.startCDATA(augs);
	}
	
	public void endCDATA(Augmentations augs) throws XNIException {
		inCdata = false;
		super.endCDATA(augs);
	}

	public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
		// see if we have a policy for this tag.
        String tagNameLowerCase = element.localpart.toLowerCase();
        Tag tag = policy.getTagByLowercaseName(tagNameLowerCase);

		/*
		 * Handle the automatic translation of <param> to nested <embed> for IE.
		 * This is only if the "validateParamAsEmbed" directive is enabled.
		 */
		boolean masqueradingParam = false;
		String embedName = null;
		String embedValue = null;
		if (tag == null && isValidateParamAsEmbed && "param".equals(tagNameLowerCase)) {
			Tag embedPolicy = policy.getEmbedTag();
			if (embedPolicy != null && embedPolicy.isAction( Policy.ACTION_VALIDATE)) {
				tag = embedPolicy;// Constants.BASIC_PARAM_TAG_RULE;
				masqueradingParam = true;
				// take <param name=x value=y> and turn into
				// <embed x=y></embed>
				embedName = attributes.getValue("name");
				embedValue = attributes.getValue("value");
				XMLAttributes masqueradingAttrs = new XMLAttributesImpl();
				masqueradingAttrs.addAttribute(makeSimpleQname(embedName), "CDATA", embedValue);
				attributes = masqueradingAttrs;
			}
		}

		XMLAttributes validattributes = new XMLAttributesImpl();
        Ops topOp = peekTop();
        if (Ops.REMOVE == topOp || Ops.CSS == topOp) {
			// we are in removal-mode, so remove this tag as well
			// we also remove all child elements of a style element
			this.operations.push( Ops.REMOVE);
		} else if ((tag == null && policy.isEncodeUnknownTag()) || (tag != null && tag.isAction( "encode" ))) {
			String name = "<" + element.localpart + ">";
			super.characters( new XMLString( name.toCharArray(), 0, name.length() ), augs );
			this.operations.push(Ops.FILTER);
		} else if (tag == null) {
			addError( ErrorMessageUtil.ERROR_TAG_NOT_IN_POLICY,
                      new Object[]{ HTMLEntityEncoder.htmlEntityEncode( element.localpart ) } );
			this.operations.push(Ops.FILTER);
		} else if (tag.isAction( "filter")) {
			addError(ErrorMessageUtil.ERROR_TAG_FILTERED, new Object[] {
				HTMLEntityEncoder.htmlEntityEncode(element.localpart)
			});
			this.operations.push(Ops.FILTER);
		} else if (tag.isAction( "validate")) {

			boolean isStyle = "style".endsWith(element.localpart);

			if (isStyle) {
				this.operations.push(Ops.CSS);
				cssContent = new StringBuffer();
				cssAttributes = attributes;
			} else {
				// validate all attributes, we need to do this now to find out
				// how to deal with the element
				boolean removeTag = false;
				boolean filterTag = false;
				for (int i = 0; i < attributes.getLength(); i++) {
					String name = attributes.getQName(i);
					String value = attributes.getValue(i);
					String nameLower = name.toLowerCase();
					Attribute attribute = tag.getAttributeByName(nameLower);
					if (attribute == null) {
						// no policy defined, perhaps it is a global attribute
						attribute = policy.getGlobalAttributeByName(nameLower);
					}
					// boolean isAttributeValid = false;
					if ("style".equalsIgnoreCase(name)) {
						CssScanner styleScanner = makeCssScanner();
						try {
							CleanResults cr = styleScanner.scanInlineStyle(value, element.localpart, maxInputSize);
							attributes.setValue(i, cr.getCleanHTML());
							validattributes.addAttribute(makeSimpleQname(name), "CDATA", cr.getCleanHTML());
							errorMessages.addAll(cr.getErrorMessages());
						} catch (ScanException e) {
							addError(ErrorMessageUtil.ERROR_CSS_ATTRIBUTE_MALFORMED, new Object[] {
									element.localpart, HTMLEntityEncoder.htmlEntityEncode(value)
							});
						}
					} else if (attribute != null) {
						// validate the values against the policy
						boolean isValid = false;
                        if (attribute.containsAllowedValue(value.toLowerCase())) {
                            validattributes.addAttribute(makeSimpleQname(name), "CDATA", value);
                            isValid = true;
                        }


                        if (!isValid) {
                            isValid = attribute.matchesAllowedExpression(value);
                            if (isValid) {
                                validattributes.addAttribute(makeSimpleQname(name), "CDATA", value);
                            }
                        }


                        // if value or regexp matched, attribute is already
						// copied, but what happens if not
						if (!isValid && "removeTag".equals(attribute.getOnInvalid())) {
							
							addError(ErrorMessageUtil.ERROR_ATTRIBUTE_INVALID_REMOVED,
								new Object[] { tag.getName(), HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value) });
							
							removeTag = true;
							
						} else if (!isValid && ("filterTag".equals(attribute.getOnInvalid()) || masqueradingParam)) {
							
							addError(ErrorMessageUtil.ERROR_ATTRIBUTE_CAUSE_FILTER, 
								new Object[] { tag.getName(), HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value) });
							
							filterTag = true;
							
						} else if (!isValid) {
							addError(ErrorMessageUtil.ERROR_ATTRIBUTE_INVALID, new Object[] { tag.getName(), HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value) });
						}
						
					} else { // attribute == null
						addError(ErrorMessageUtil.ERROR_ATTRIBUTE_NOT_IN_POLICY, new Object[] {
								element.localpart, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)
						});
						
						if (masqueradingParam) {
							filterTag = true;
						}
					}
				}

				if (removeTag) {
					this.operations.push(Ops.REMOVE);
				} else if (filterTag) {
					this.operations.push(Ops.FILTER);
				} else {

					if (isNofollowAnchors && "a".equals(element.localpart)) {
						validattributes.addAttribute(makeSimpleQname("rel"), "CDATA", "nofollow");
					}

					if (masqueradingParam) {
						validattributes = new XMLAttributesImpl();
						validattributes.addAttribute(makeSimpleQname("name"), "CDATA", embedName);
						validattributes.addAttribute(makeSimpleQname("value"), "CDATA", embedValue);
					}

					this.operations.push(Ops.KEEP);
				}
			}
		} else if (tag.isAction( "truncate")) {
			this.operations.push(Ops.TRUNCATE);
		} else {
			// no options left, so the tag will be removed
			addError(ErrorMessageUtil.ERROR_TAG_DISALLOWED, new Object[] {
				HTMLEntityEncoder.htmlEntityEncode(element.localpart)
			});
			this.operations.push(Ops.REMOVE);
		}
		// now we know exactly what to do, let's do it
		if ( Ops.TRUNCATE.equals( operations.peek() )) {
			// copy the element, but remove all attributes
			super.startElement(element, new XMLAttributesImpl(), augs);
		} else if ( Ops.KEEP.equals(operations.peek())) {
			// copy the element, but only copy accepted attributes
			super.startElement(element, validattributes, augs);
		}
	}

	private QName makeSimpleQname(String name) {
		return new QName("", name, name, "");
	}

	private void addError(String errorKey, Object[] objs) {
		errorMessages.add(ErrorMessageUtil.getMessage(messages, errorKey, objs));
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

}
