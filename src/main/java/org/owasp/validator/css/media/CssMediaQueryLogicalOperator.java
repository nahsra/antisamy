package org.owasp.validator.css.media;

public enum CssMediaQueryLogicalOperator {
    AND, NOT, ONLY, OR;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static CssMediaQueryLogicalOperator parse(String operator) {
        try {
            return CssMediaQueryLogicalOperator.valueOf(operator.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
