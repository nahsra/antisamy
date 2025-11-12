package org.owasp.validator.css.media;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CssMediaQuery {

  private CssMediaQueryLogicalOperator logicalOperator;
  private String mediaType;
  private final List<CssMediaFeature> mediaFeatures = new ArrayList<>();

  public CssMediaQueryLogicalOperator getLogicalOperator() {
    return logicalOperator;
  }

  public void setLogicalOperator(CssMediaQueryLogicalOperator logicalOperator) {
    this.logicalOperator = logicalOperator;
  }

  public String getMediaType() {
    return mediaType;
  }

  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  public void addMediaFeature(CssMediaFeature mediaFeature) {
    mediaFeatures.add(mediaFeature);
  }

  public List<CssMediaFeature> getMediaFeatures() {
    return Collections.unmodifiableList(mediaFeatures);
  }
}
