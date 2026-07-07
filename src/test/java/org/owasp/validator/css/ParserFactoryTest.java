package org.owasp.validator.css;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import java.io.IOException;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SelectorList;

public class ParserFactoryTest {

    private static final String PROP_NAME = "org.w3c.css.sac.parser";

    @Before
    @After
    public void clearSystemProperty() {
        System.clearProperty(PROP_NAME);
    }

    @Test
    public void whenNoSystemProperty_thenDefaultParserIsReturned() {
        Parser parser = ParserFactory.makeParser();
        assertThat(parser, instanceOf(org.owasp.validator.css.batik.CssParser.class));
    }

    @Test
    public void whenValidCustomParserClass_thenThatParserIsReturned() {
        System.setProperty(PROP_NAME, DummyParser.class.getName());

        Parser parser = ParserFactory.makeParser();
        assertThat(parser, instanceOf(DummyParser.class));
    }

    @Test
    public void whenInvalidClassName_thenDefaultParserIsReturned() {
        System.setProperty(PROP_NAME, "non.existent.ClassName");

        Parser parser = ParserFactory.makeParser();
        assertThat(parser, instanceOf(org.owasp.validator.css.batik.CssParser.class));
    }

    @Test
    public void whenClassDoesNotImplementParser_thenDefaultParserIsReturned() {
        System.setProperty(PROP_NAME, NotAParser.class.getName());

        Parser parser = ParserFactory.makeParser();
        assertThat(parser, instanceOf(org.owasp.validator.css.batik.CssParser.class));
    }

    public static class DummyParser implements Parser {
        @Override
        public void parseStyleSheet(InputSource source) {
        }

        @Override
        public void parseStyleDeclaration(InputSource source) {
        }

        @Override
        public void parseRule(InputSource source) {
        }

        @Override
        public SelectorList parseSelectors(InputSource source) {
            throw new UnsupportedOperationException("Unimplemented method 'parseSelectors'");
        }

        @Override
        public LexicalUnit parsePropertyValue(InputSource source) {
            throw new UnsupportedOperationException("Unimplemented method 'parsePropertyValue'");
        }

        @Override
        public boolean parsePriority(InputSource source) {
            throw new UnsupportedOperationException("Unimplemented method 'parsePriority'");
        }

        @Override
        public void setLocale(Locale locale) {
        }

        @Override
        public void setErrorHandler(ErrorHandler handler) {
        }

        @Override
        public void setSelectorFactory(SelectorFactory factory) {
        }

        @Override
        public void setConditionFactory(ConditionFactory factory) {
        }

        @Override
        public void setDocumentHandler(DocumentHandler handler) {
        }

        @Override
        public String getParserVersion() {
            return "dummy-parser 0.0.0";
        }

        @Override
        public void parseStyleSheet(String uri) throws CSSException, IOException {
            throw new UnsupportedOperationException("Unimplemented method 'parseStyleSheet'");
        }
    }

    public static class NotAParser {
    }
}
