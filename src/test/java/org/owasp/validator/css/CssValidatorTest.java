package org.owasp.validator.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.batik.css.parser.CSSLexicalUnit;
import org.junit.Test;
import org.w3c.css.sac.LexicalUnit;

public class CssValidatorTest {
  @Test
  public void testLexicalValueToStringSacFunction() {
    CssValidator cssValidator = new CssValidator(null);

    final CSSLexicalUnit param =
        CSSLexicalUnit.createString(LexicalUnit.SAC_STRING_VALUE, "--ds-text-purple", null);
    final CSSLexicalUnit varFunc = CSSLexicalUnit.createFunction("var", param, null);

    assertEquals("var(--ds-text-purple)", cssValidator.lexicalValueToString(varFunc));

    final CSSLexicalUnit hslaParam = CSSLexicalUnit.createInteger(100, null);
    final CSSLexicalUnit hslaParam1 = CSSLexicalUnit.createDimension(98, "%", hslaParam);
    final CSSLexicalUnit hslaParam2 = CSSLexicalUnit.createDimension(50, "%", hslaParam1);
    CSSLexicalUnit.createFloat(LexicalUnit.SAC_REAL, 0.3f, hslaParam2);

    final CSSLexicalUnit hslaFunc = CSSLexicalUnit.createFunction("hsla", hslaParam, null);
    assertEquals("hsla(100, 98.0%, 50.0%, 0.3)", cssValidator.lexicalValueToString(hslaFunc));
  }

  @Test
  public void testLexicalValueToStringSacFunctionTwoParams() {
    CssValidator cssValidator = new CssValidator(null);

    final CSSLexicalUnit param =
        CSSLexicalUnit.createString(LexicalUnit.SAC_STRING_VALUE, "--ds-text-purple", null);
    CSSLexicalUnit.createString(LexicalUnit.SAC_STRING_VALUE, "#FFFFFF", param);
    final CSSLexicalUnit func = CSSLexicalUnit.createFunction("var", param, null);

    assertEquals("var(--ds-text-purple, #FFFFFF)", cssValidator.lexicalValueToString(func));
  }

  @Test
  public void testLexicalValueToStringUnsupported() {
    CssValidator cssValidator = new CssValidator(null);
    final CSSLexicalUnit param =
        CSSLexicalUnit.createString(LexicalUnit.SAC_STRING_VALUE, "section", null);
    final CSSLexicalUnit func =
        CSSLexicalUnit.createPredefinedFunction(LexicalUnit.SAC_COUNTER_FUNCTION, param, null);
    assertNull(cssValidator.lexicalValueToString(func));
  }

  @Test
  public void testLexicalValueToStringNestedVarsWithFallback() {
    CssValidator cssValidator = new CssValidator(null);

    // Create fallback first: --ds-text-purple, #FFFFFF
    final CSSLexicalUnit param =
        CSSLexicalUnit.createString(LexicalUnit.SAC_STRING_VALUE, "--ds-text-purple", null);
    final CSSLexicalUnit fallback =
        CSSLexicalUnit.createString(LexicalUnit.SAC_STRING_VALUE, "#FFFFFF", null);

    // Create first var() function with fallback
    final CSSLexicalUnit function = CSSLexicalUnit.createFunction("var", param, fallback);

    // Check if the output is as expected for first var()
    assertEquals("var(--ds-text-purple, #FFFFFF)", cssValidator.lexicalValueToString(function));

    // Create outer variable: --custom-prop
    final CSSLexicalUnit outerParam =
        CSSLexicalUnit.createString(LexicalUnit.SAC_STRING_VALUE, "--custom-prop", null);

    // Create outer var() with the first function as fallback
    final CSSLexicalUnit outerFunction = CSSLexicalUnit.createFunction("var", outerParam, function);

    // Ensure the output is as expected for the nested var() function
    assertEquals(
        "var(--custom-prop, var(--ds-text-purple, #FFFFFF))",
        cssValidator.lexicalValueToString(outerFunction));
  }

  @Test
  public void testDefaultPolicyUrlFunction() {
    CssValidator cssValidator = new CssValidator(null);

    // Test a simple url function
    final CSSLexicalUnit urlParam =
        CSSLexicalUnit.createString(LexicalUnit.SAC_STRING_VALUE, "http://example.com", null);
    final CSSLexicalUnit urlFunc = CSSLexicalUnit.createFunction("url", urlParam, null);

    assertEquals("url(http://example.com)", cssValidator.lexicalValueToString(urlFunc));
  }

  @Test
  public void testSacUriWithValidUrl() {
    CssValidator cssValidator = new CssValidator(null);

    // Test with a valid URL, which should be allowed
    final CSSLexicalUnit validUrl =
        CSSLexicalUnit.createString(LexicalUnit.SAC_URI, "https://example.com", null);
    assertEquals("url(https://example.com)", cssValidator.lexicalValueToString(validUrl));
  }
}
