package org.owasp.validator.css.media;

import org.w3c.css.sac.LexicalUnit;

public class CssMediaFeature {

    private final String name;
    private final LexicalUnit expression;

    /**
     * Constructor.
     *
     * @param name Feature-name
     * @param expression expression, may be null
     */
    public CssMediaFeature(String name, LexicalUnit expression) {
        this.name = name;
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public LexicalUnit getExpression() {
        return expression;
    }
}
