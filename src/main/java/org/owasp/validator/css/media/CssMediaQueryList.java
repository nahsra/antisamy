package org.owasp.validator.css.media;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.batik.css.parser.CSSSACMediaList;

public class CssMediaQueryList extends CSSSACMediaList {

    private final List<CssMediaQuery> mediaQueries = new ArrayList<>();

    @Override
    public int getLength() {
        return mediaQueries.size();
    }

    /**
     * Use {@link CssMediaQueryList#getMediaQueryAt(int)} instead.
     * {@inheritDoc}
     */
    @Override
    public String item(int index) {
        CssMediaQuery query = getMediaQueryAt(index);
        if (query.getMediaFeatures().isEmpty() && query.getLogicalOperator() == null) {
            return query.getMediaType().toString();
        } else {
            throw new UnsupportedOperationException("CSS3 MediaQuery unsupported");
        }
    }

    public CssMediaQuery getMediaQueryAt(int index) {
        return mediaQueries.get(index);
    }

    public List<CssMediaQuery> getMediaQueries() {
        return Collections.unmodifiableList(mediaQueries);
    }


    public void append(CssMediaQuery mediaQuery) {
        mediaQueries.add(mediaQuery);
    }
}
