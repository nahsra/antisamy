package org.owasp.validator.css.media;

public enum CssMediaType {
    ALL("all"), PRINT("print"), SCREEN("screen"), IMPLIED_ALL("");

    public final String label;

    CssMediaType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static CssMediaType parse(String mediaType) {
        for (CssMediaType element : values()) {
            if (element.toString().equalsIgnoreCase(mediaType)) {
                return element;
            }
        }
        return null;
    }
}
