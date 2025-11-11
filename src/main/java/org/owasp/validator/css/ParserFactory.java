package org.owasp.validator.css;

import org.w3c.css.sac.Parser;

abstract class ParserFactory {

    private static final String SAC_PARSER_SYSTEM_PROPERTY_NAME = "org.w3c.css.sac.parser";

    private ParserFactory() {
        // prevent instantiation
    }

    static Parser makeParser() {
        final String sacParserClassName = System.getProperty(SAC_PARSER_SYSTEM_PROPERTY_NAME);
        Parser result = null;
        try {
           if (sacParserClassName == null || sacParserClassName.trim().isEmpty()) {
               result = new org.owasp.validator.css.batik.CssParser();
            } else {
                result =  (Parser)Class.forName(sacParserClassName).newInstance();
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NullPointerException | ClassCastException e) {
            result =  new org.owasp.validator.css.batik.CssParser();
        }
        return result;
    }
}
