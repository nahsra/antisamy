/*
 * Copyright (c) 2007-2022, Arshan Dabirsiaghi, Sebastián Passaro
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

import org.apache.batik.css.parser.LexicalUnits;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.LexicalUnit;

public class CssParser extends org.apache.batik.css.parser.Parser {

    /**
     * This implementation is a workaround to solve leading dash errors on property names.
     * @see <code>https://issues.apache.org/jira/browse/BATIK-1112</code>
     * @param inSheet Specifies if the style to parse is inside a sheet or the sheet itself.
     * @throws CSSException Thrown if there are parsing errors in CSS
     */
    @Override
    protected void parseStyleDeclaration(final boolean inSheet) throws CSSException {
        boolean leadingDash = false;
        for (;;) {
            switch (current) {
                case LexicalUnits.EOF:
                    if (inSheet) {
                        throw createCSSParseException("eof");
                    }
                    return;
                case LexicalUnits.RIGHT_CURLY_BRACE:
                    if (!inSheet) {
                        throw createCSSParseException("eof.expected");
                    }
                    nextIgnoreSpaces();
                    return;
                case LexicalUnits.SEMI_COLON:
                    nextIgnoreSpaces();
                    continue;
                case LexicalUnits.MINUS:
                    leadingDash = true;
                    next();
                    break;
                default:
                    throw createCSSParseException("identifier");
                case LexicalUnits.IDENTIFIER:
            }

            final String name = (leadingDash ? "-" : "") + scanner.getStringValue();
            leadingDash = false;

            if (nextIgnoreSpaces() != LexicalUnits.COLON) {
                throw createCSSParseException("colon");
            }
            nextIgnoreSpaces();

            LexicalUnit exp = null;

            try {
                exp = parseExpression(false);
            } catch (final CSSParseException e) {
                reportError(e);
            }

            if (exp != null) {
                boolean important = false;
                if (current == LexicalUnits.IMPORTANT_SYMBOL) {
                    important = true;
                    nextIgnoreSpaces();
                }
                documentHandler.property(name, exp, important);
            }
        }
    }
}
