package org.owasp.validator.css.media;

public enum CssMediaType {
    ALL, PRINT, SCREEN;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static CssMediaType parse(String mediaType) {
        try {
            return CssMediaType.valueOf(mediaType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
