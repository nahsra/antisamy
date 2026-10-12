package org.owasp.validator.css.media;

public enum CssMediaQueryLogicalOperator {
  AND("and"),
  NOT("not"),
  ONLY("only"),
  OR("or"),
  COMMA(",");

  public final String label;

  CssMediaQueryLogicalOperator(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return label;
  }

  public static CssMediaQueryLogicalOperator parse(String operator) {
    for (CssMediaQueryLogicalOperator element : values()) {
      if (element.label.equalsIgnoreCase(operator)) {
        return element;
      }
    }
    return null;
  }
}
