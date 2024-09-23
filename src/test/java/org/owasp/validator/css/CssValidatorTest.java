package org.owasp.validator.css;

import org.apache.batik.css.parser.CSSLexicalUnit;
import org.junit.Test;
import org.w3c.css.sac.LexicalUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
}
