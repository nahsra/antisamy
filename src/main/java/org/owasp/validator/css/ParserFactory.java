package org.owasp.validator.css;

import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.css.sac.Parser;

abstract class ParserFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(ParserFactory.class);

  /**
   * Defines a system property which can be used to instantiate a different parser.
   * The value should be the fully qualified class name of a class that implements
   * {@link org.w3c.css.sac.Parser}. If not set or invalid, the default Batik
   * parser will be used.
   */
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
        LOGGER.debug("No system property \"{}\" found. AntiSamy will use default \"{}\" CSS Parser.", 
          SAC_PARSER_SYSTEM_PROPERTY_NAME,
          result.getClass().getCanonicalName());
      } else {
        LOGGER.debug("Found system property \"{}\", trying to use it as CSS Parser in AntiSamy");
        result = (Parser) Class.forName(sacParserClassName)
          .getDeclaredConstructor().newInstance();
        LOGGER.warn("AntiSamy will be run with the unsupported \"{}\" CSS Parser. Using an unsupported parser is at your own risk.", 
          result.getClass().getCanonicalName());  
      }
    } catch (ClassNotFoundException
        | NoSuchMethodException
        | InvocationTargetException
        | IllegalAccessException
        | InstantiationException
        | NullPointerException
        | ClassCastException e) {
      result = new org.owasp.validator.css.batik.CssParser();
      LOGGER.error("Failed to instantiate \"{}\" as CSS Parser. AntiSamy will fall back to use default \"{}\" CSS Parser.",
        sacParserClassName,
        result.getClass().getCanonicalName(),
        e
      );
    }
    return result;
  }
}
