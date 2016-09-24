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

import java.util.Iterator;
import java.util.regex.Pattern;

import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.model.AntiSamyPattern;
import org.owasp.validator.html.model.Property;
import org.owasp.validator.html.util.HTMLEntityEncoder;

import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

/**
 * Encapsulates all the neceesary operations for validating individual eleements
 * of a stylesheet (namely: selectors, conditions and properties).
 * 
 * @author Jason Li
 * 
 */
public class CssValidator {

	private final Policy policy;

	/**
	 * Constructs a validator for CSS selectors, conditions and properties based
	 * on the given policy.
	 * 
	 * @param policy
	 *            the policy file to use in this validator
	 */
	public CssValidator(Policy policy) {
		this.policy = policy;
	}

	/**
	 * Determines whether the given property (both name and value) are valid
	 * according to this validator's policy.
	 * 
	 * @param name
	 *            the name of the property
	 * @param lu
	 *            the value of the property
	 * @return true if this property name/value is valid; false otherwise
	 */
	public boolean isValidProperty(String name, LexicalUnit lu) {
		boolean isValid = false;
		Property property = null;

		if (name != null) {
			property = policy.getPropertyByName(name.toLowerCase());
		}

		// if we were able to find the property by name, validate the value
		if (property != null) {

			// validate all values attached to this property
			isValid = true;
			while (lu != null) {
				String value = lexicalValueToString(lu);

				if (value == null || !validateValue(property, value)) {
					isValid = false;
					break;
				}

				lu = lu.getNextLexicalUnit();
			}
		}

		return isValid;
	}

	/**
	 * Determines whether the given selector name is valid according to this
	 * validator's policy.
	 * 
	 * @param selectorName
	 *            the name of the selector
	 * @param selector
	 *            the object representation of the selector
	 * @param results
	 *            the <code>CleanResults</code> object to add any error
	 *            messages to
	 * @return true if this selector name is valid; false otherwise
	 */
	public boolean isValidSelector(String selectorName, Selector selector)
			throws ScanException {

		// determine correct behavior
		switch (selector.getSelectorType()) {
		case Selector.SAC_ANY_NODE_SELECTOR:
		case Selector.SAC_ELEMENT_NODE_SELECTOR:
		case Selector.SAC_PSEUDO_ELEMENT_SELECTOR:
		case Selector.SAC_ROOT_NODE_SELECTOR:
			// these selectors are the most base selectors
			return validateSimpleSelector((SimpleSelector) selector);
		case Selector.SAC_CHILD_SELECTOR:
		case Selector.SAC_DESCENDANT_SELECTOR:
			// these are compound selectors - decompose into simple selectors
			DescendantSelector descSelector = (DescendantSelector) selector;
			return isValidSelector(selectorName, descSelector
					.getSimpleSelector())
					& isValidSelector(selectorName, descSelector
							.getAncestorSelector());
		case Selector.SAC_CONDITIONAL_SELECTOR:
			// this is a compound selector - decompose into simple selectors
			ConditionalSelector condSelector = (ConditionalSelector) selector;
			return isValidSelector(selectorName, condSelector
					.getSimpleSelector())
					& isValidCondition(selectorName, condSelector
							.getCondition());
		case Selector.SAC_DIRECT_ADJACENT_SELECTOR:
			// this is a compound selector - decompose into simple selectors
			SiblingSelector sibSelector = (SiblingSelector) selector;
			return isValidSelector(selectorName, sibSelector
					.getSiblingSelector())
					& isValidSelector(selectorName, sibSelector.getSelector());
		case Selector.SAC_NEGATIVE_SELECTOR:
			// this is a compound selector with one simple selector
			return validateSimpleSelector((NegativeSelector) selector);
		case Selector.SAC_CDATA_SECTION_NODE_SELECTOR:
		case Selector.SAC_COMMENT_NODE_SELECTOR:
		case Selector.SAC_PROCESSING_INSTRUCTION_NODE_SELECTOR:
		case Selector.SAC_TEXT_NODE_SELECTOR:
		default:
		    
			throw new UnknownSelectorException(HTMLEntityEncoder.htmlEntityEncode(selector.toString()));
		}
	}

	/**
	 * Validates a basic selector against the policy
	 * 
	 * @param selector
	 *            the object representation of the selector
	 * @param results
	 *            the <code>CleanResults</code> object to add any error
	 *            messages to
	 * @return true if this selector name is valid; false otherwise
	 */
	private boolean validateSimpleSelector(SimpleSelector selector) {
		// ensure the name follows the valid pattern and is not blacklisted
		// by the exclusion pattern.
		// NOTE: intentionally using non-short-circuited AND operator to
		// generate all relevant error messages

        String selectorLowerCase = selector.toString().toLowerCase();
        return policy.getCommonRegularExpressions("cssElementSelector").matches(selectorLowerCase)
				& !policy.getCommonRegularExpressions("cssElementExclusion").matches(selectorLowerCase);
	}

	/**
	 * Determines whether the given condition is valid according to this
	 * validator's policy.
	 * 
	 * @param selectorName
	 *            the name of the selector that contains this condition
	 * @param condition
	 *            the object representation of this condition
	 * @param results
	 *            the <code>CleanResults</code> object to add any error
	 *            messages to
	 * @return true if this condition is valid; false otherwise
	 */
	public boolean isValidCondition(String selectorName, Condition condition)
			throws ScanException {
		switch (condition.getConditionType()) {
		case Condition.SAC_AND_CONDITION:
		case Condition.SAC_OR_CONDITION:
			// these are compound condition - decompose into simple conditions
			CombinatorCondition comboCondition = (CombinatorCondition) condition;
			return isValidCondition(selectorName, comboCondition
					.getFirstCondition())
					& isValidCondition(selectorName, comboCondition
							.getSecondCondition());
		case Condition.SAC_CLASS_CONDITION:
			// this is a basic class condition; compare condition against
			// valid pattern and is not blacklisted by exclusion pattern

            return validateCondition((AttributeCondition) condition, policy.getCommonRegularExpressions("cssClassSelector"), policy.getCommonRegularExpressions("cssClassExclusion"));
		case Condition.SAC_ID_CONDITION:
			// this is a basic ID condition; compare condition against
			// valid pattern and is not blacklisted by exclusion pattern

            return validateCondition((AttributeCondition) condition, policy.getCommonRegularExpressions("cssIDSelector"), policy.getCommonRegularExpressions("cssIDExclusion"));
		case Condition.SAC_PSEUDO_CLASS_CONDITION:
			// this is a basic psuedo element condition; compare condition
			// against valid pattern and is not blacklisted by exclusion pattern

            return validateCondition((AttributeCondition) condition, policy.getCommonRegularExpressions("cssPseudoElementSelector"), policy.getCommonRegularExpressions("cssPsuedoElementExclusion"));
		case Condition.SAC_BEGIN_HYPHEN_ATTRIBUTE_CONDITION:
		case Condition.SAC_ONE_OF_ATTRIBUTE_CONDITION:
		case Condition.SAC_ATTRIBUTE_CONDITION:
			// this is a basic class condition; compare condition against
			// valid pattern and is not blacklisted by exclusion pattern

            return validateCondition((AttributeCondition) condition, policy.getCommonRegularExpressions("cssAttributeSelector"), policy.getCommonRegularExpressions("cssAttributeExclusion"));
		case Condition.SAC_NEGATIVE_CONDITION:
			// this is a compound condition; decompose to simple condition
			return isValidCondition(selectorName,
					((NegativeCondition) condition).getCondition());
		case Condition.SAC_ONLY_CHILD_CONDITION:
		case Condition.SAC_ONLY_TYPE_CONDITION:
			// :only-child and :only-of-type are constants
			return true;
		case Condition.SAC_POSITIONAL_CONDITION:
		case Condition.SAC_CONTENT_CONDITION:
		case Condition.SAC_LANG_CONDITION:
		default:
		    	throw new UnknownSelectorException(HTMLEntityEncoder.htmlEntityEncode(selectorName));
		}
	}

	/**
	 * Validates a basic condition against the white list pattern and the
	 * blacklist pattern
	 * 
	 * @param condition
	 *            the object representation of the condition
	 * @param pattern
	 *            the positive pattern of valid conditions
	 * @param exclusionPattern
	 *            the negative pattern of excluded conditions
	 * @param results
	 *            the <code>CleanResults</code> object to add any error
	 *            messages to
	 * @return true if this selector name is valid; false otherwise
	 */
	private boolean validateCondition(AttributeCondition condition,
			AntiSamyPattern pattern, AntiSamyPattern exclusionPattern) {
		// check that the name of the condition matches valid pattern and does
		// not match exclusion pattern
		// NOTE: intentionally using non-short-circuited AND operator to
		// generate all relevant error messages
        String otherLower = condition.toString().toLowerCase();
        return pattern.matches(otherLower) & !exclusionPattern.matches(otherLower);
	}

	/**
	 * Determines whether the given property value is valid according to this
	 * validator's policy.
	 * 
	 * @param property
	 *            the object representation of the property and its associated
	 *            policy
	 * @param value
	 *            the string representation of the value
	 * @return true if the property is valid; false otherwise
	 */
	private boolean validateValue(Property property, String value) {
		boolean isValid = false;

		// normalize the value to lowercase
		value = value.toLowerCase();

		// check if the value matches any of the allowed literal values
		Iterator allowedValues = property.getAllowedValues().iterator();
		while (allowedValues.hasNext() && !isValid) {
			String allowedValue = (String) allowedValues.next();

			if (allowedValue != null && allowedValue.equals(value)) {
				isValid = true;
			}
		}

		// check if the value matches any of the allowed regular expressions
		Iterator allowedRegexps = property.getAllowedRegExp().iterator();
		while (allowedRegexps.hasNext() && !isValid) {
			Pattern pattern = (Pattern) allowedRegexps.next();

			if (pattern != null && pattern.matcher(value).matches()) {
				isValid = true;
			}
		}

		// check if the value matches any of the allowed shorthands
		Iterator shorthandRefs = property.getShorthandRefs().iterator();
		while (shorthandRefs.hasNext() && !isValid) {
			String shorthandRef = (String) shorthandRefs.next();
			Property shorthand = policy.getPropertyByName(shorthandRef);

			if (shorthand != null) {
				isValid = validateValue(shorthand, value);
			}
		}

		return isValid;
	}

	/**
	 * Converts the given lexical unit to a <code>String</code>
	 * representation. This method does not perform any validation - it is meant
	 * to be used in conjunction with the validator/logging methods.
	 * 
	 * @param lu
	 *            the lexical unit to convert
	 * @return a <code>String</code> representation of the given lexical unit
	 */
	public String lexicalValueToString(LexicalUnit lu) {
		switch (lu.getLexicalUnitType()) {
		case LexicalUnit.SAC_PERCENTAGE:
		case LexicalUnit.SAC_DIMENSION:
		case LexicalUnit.SAC_EM:
		case LexicalUnit.SAC_EX:
		case LexicalUnit.SAC_PIXEL:
		case LexicalUnit.SAC_INCH:
		case LexicalUnit.SAC_CENTIMETER:
		case LexicalUnit.SAC_MILLIMETER:
		case LexicalUnit.SAC_POINT:
		case LexicalUnit.SAC_PICA:
		case LexicalUnit.SAC_DEGREE:
		case LexicalUnit.SAC_GRADIAN:
		case LexicalUnit.SAC_RADIAN:
		case LexicalUnit.SAC_MILLISECOND:
		case LexicalUnit.SAC_SECOND:
		case LexicalUnit.SAC_HERTZ:
		case LexicalUnit.SAC_KILOHERTZ:
			// these are all measurements
			return lu.getFloatValue() + lu.getDimensionUnitText();
		case LexicalUnit.SAC_INTEGER:
			// just a number
			return String.valueOf(lu.getIntegerValue());
		case LexicalUnit.SAC_REAL:
			// just a number
			return String.valueOf(lu.getFloatValue());
		case LexicalUnit.SAC_STRING_VALUE:
		case LexicalUnit.SAC_IDENT:
			// just a string/identifier
			String stringValue = lu.getStringValue();
			if(stringValue.indexOf(" ") != -1)
				stringValue = "\""+stringValue+"\"";
			return stringValue;
		case LexicalUnit.SAC_URI:
			// this is a URL
			return "url(" + lu.getStringValue() + ")";
		case LexicalUnit.SAC_RGBCOLOR:
			// this is a rgb encoded color
			StringBuffer sb = new StringBuffer("rgb(");
			LexicalUnit param = lu.getParameters();
			sb.append(param.getIntegerValue()); // R value
			sb.append(',');
			param = param.getNextLexicalUnit(); // comma
			param = param.getNextLexicalUnit(); // G value
			sb.append(param.getIntegerValue());
			sb.append(',');
			param = param.getNextLexicalUnit(); // comma
			param = param.getNextLexicalUnit(); // B value
			sb.append(param.getIntegerValue());
			sb.append(')');

			return sb.toString();
		case LexicalUnit.SAC_INHERIT:
			// constant
			return "inherit";
		case LexicalUnit.SAC_OPERATOR_COMMA:
		    	return ",";
		case LexicalUnit.SAC_ATTR:
		case LexicalUnit.SAC_COUNTER_FUNCTION:
		case LexicalUnit.SAC_COUNTERS_FUNCTION:
		case LexicalUnit.SAC_FUNCTION:
		case LexicalUnit.SAC_RECT_FUNCTION:
		case LexicalUnit.SAC_SUB_EXPRESSION:
		case LexicalUnit.SAC_UNICODERANGE:
		default:
			// these are properties that shouldn't be necessary for most run
			// of the mill HTML/CSS
			return null;
		}
	}
}
