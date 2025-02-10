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

import static org.owasp.validator.css.media.CssMediaQueryLogicalOperator.AND;
import static org.owasp.validator.css.media.CssMediaQueryLogicalOperator.OR;

import org.apache.batik.css.parser.CSSSACMediaList;
import org.apache.batik.css.parser.LexicalUnits;
import org.owasp.validator.css.media.CssMediaFeature;
import org.owasp.validator.css.media.CssMediaQuery;
import org.owasp.validator.css.media.CssMediaQueryList;
import org.owasp.validator.css.media.CssMediaQueryLogicalOperator;
import org.owasp.validator.css.media.CssMediaType;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.LexicalUnit;

public class CssParser extends org.apache.batik.css.parser.Parser {

  /**
   * This implementation is a workaround to solve leading dash errors on property names.
   *
   * @see <code>https://issues.apache.org/jira/browse/BATIK-1112</code>
   * @param inSheet Specifies if the style to parse is inside a sheet or the sheet itself.
   * @throws CSSException Thrown if there are parsing errors in CSS
   */
  @Override
  protected void parseStyleDeclaration(final boolean inSheet) throws CSSException {
    boolean leadingDash = false;
    for (; ; ) {
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

  @Override
  protected CSSSACMediaList parseMediaList() {
    CssMediaQueryList mediaList = new CssMediaQueryList();

    mediaList.append(parseMediaQuery());
    while (hasAnotherMediaQuery()) {
      nextIgnoreSpaces();
      mediaList.append(parseMediaQuery());
    }

    return mediaList;
  }

  private boolean hasAnotherMediaQuery() {
    return current == LexicalUnits.COMMA || (current == LexicalUnits.IDENTIFIER && scanner.getStringValue().equals(OR.toString()));
  }

  protected CssMediaQuery parseMediaQuery() {
    CssMediaQuery query = new CssMediaQuery();
    CssMediaType mediaType = null;
    CssMediaQueryLogicalOperator logicalOperator = null;
    switch (current) {
      case LexicalUnits.LEFT_BRACE:
        mediaType = CssMediaType.IMPLIED_ALL;
        break;
      case LexicalUnits.IDENTIFIER:
        logicalOperator = CssMediaQueryLogicalOperator.parse(scanner.getStringValue());
        if (logicalOperator != null) {
          switch (logicalOperator) {
            case ONLY:
              parseLogicalOperatorOnly();
              mediaType = parseMediaType();
              break;
            case NOT:
              mediaType = parseLogicalOperatorNot();
              break;
            case AND:
            case OR:
            case COMMA:
              throw createCSSParseException("identifier");
          }
        } else {
          mediaType = parseMediaType();
        }
        break;
      default:
        throw createCSSParseException("identifier");
    }
    query.setMediaType(mediaType);
    query.setLogicalOperator(logicalOperator);

    if (mediaType == CssMediaType.IMPLIED_ALL) {
      query.addMediaFeature(parseMediaFeature());
    }

    while (current == LexicalUnits.IDENTIFIER && CssMediaQueryLogicalOperator.parse(scanner.getStringValue()) == AND) {
      nextIgnoreSpaces();
      query.addMediaFeature(parseMediaFeature());
    }
    return query;
  }

  private CssMediaType parseMediaType() {
    CssMediaType mediaType;
    mediaType = CssMediaType.parse(scanner.getStringValue());
    if (mediaType == null) {
      throw createCSSParseException("identifier");
    }
    nextIgnoreSpaces();
    return mediaType;
  }

  private CssMediaType parseLogicalOperatorNot() {
    CssMediaType mediaType;
    if (nextIgnoreSpaces() == LexicalUnits.IDENTIFIER) {
      mediaType = parseMediaType();
    } else {
      mediaType = CssMediaType.IMPLIED_ALL;
    }
    return mediaType;
  }

  private void parseLogicalOperatorOnly() {
    if (nextIgnoreSpaces() != LexicalUnits.IDENTIFIER) {
      throw createCSSParseException("identifier");
    }
  }

  protected CssMediaFeature parseMediaFeature() {
    if (current != LexicalUnits.LEFT_BRACE) {
      throw createCSSParseException("'(' expected.");
    }
    nextIgnoreSpaces();
    String namePrefix = "";
    if (current == LexicalUnits.MINUS) {
      nextIgnoreSpaces();
      namePrefix = "-";
    }
    if (current != LexicalUnits.IDENTIFIER) {
      throw createCSSParseException("identifier");
    }
    String name = namePrefix + scanner.getStringValue();
    nextIgnoreSpaces();
    LexicalUnit exp = null;
    if (current == LexicalUnits.COLON) {
      nextIgnoreSpaces();
      exp = parseTerm(null);
    }
    if (current != LexicalUnits.RIGHT_BRACE) {
      throw createCSSParseException("')' expected.");
    }
    nextIgnoreSpaces();

    return new CssMediaFeature(name, exp);
  }

  @Override
  protected void parseMediaRule() {
    CSSSACMediaList ml = parseMediaList();
    try {
      documentHandler.startMedia(ml);

      if (current != LexicalUnits.LEFT_CURLY_BRACE) {
        reportError("left.curly.brace");
      } else {
        nextIgnoreSpaces();

        loop:
        for (; ; ) {
          switch (current) {
            case LexicalUnits.EOF:
            case LexicalUnits.RIGHT_CURLY_BRACE:
              break loop;
            default:
              parseRuleSet();
          }
        }

        nextIgnoreSpaces();
      }
    } finally {
      documentHandler.endMedia(ml);
    }
  }
}
