package org.owasp.validator.html.scan;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import org.apache.xml.serialize.ElementState;
import org.apache.xml.serialize.HTMLdtd;
import org.apache.xml.serialize.OutputFormat;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.TagMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@SuppressWarnings("deprecation")
public class ASHTMLSerializer extends org.apache.xml.serialize.HTMLSerializer {

  private static final Logger logger = LoggerFactory.getLogger(ASHTMLSerializer.class);
  private boolean encodeAllPossibleEntities;
  private final TagMatcher allowedEmptyTags;
  private final TagMatcher requireClosingTags;

  public ASHTMLSerializer(Writer w, OutputFormat format, InternalPolicy policy) {
    super(w, format);
    this.allowedEmptyTags = policy.getAllowedEmptyTags();
    this.requireClosingTags = policy.getRequiresClosingTags();
    this.encodeAllPossibleEntities = policy.isEntityEncodeIntlCharacters();
  }

  @Override
  protected String getEntityRef(int charToPrint) {
    if (encodeAllPossibleEntities || Constants.big5CharsToEncode.indexOf(charToPrint) != -1)
      return super.getEntityRef(charToPrint);
    return null;
  }

  /**
   * Called to serialize a DOM element. Equivalent to calling {@link #startElement}, {@link
   * #endElement} and serializing everything inbetween, but better optimized.
   */
  @Override
  protected void serializeElement(Element elem) throws IOException {
    Attr attr;
    NamedNodeMap attrMap;
    int i;
    Node child;
    ElementState state;
    boolean preserveSpace;
    String name;
    String value;
    String tagName;

    tagName = elem.getTagName();
    state = getElementState();
    if (isDocumentState()) {
      // If this is the root element handle it differently.
      // If the first root element in the document, serialize
      // the document's DOCTYPE. Space preserving defaults
      // to that of the output format.
      if (!_started) startDocument(tagName);
    } else {
      // For any other element, if first in parent, then
      // close parent's opening tag and use the parnet's
      // space preserving.
      if (state.empty) _printer.printText('>');
      // Indent this element on a new line if the first
      // content of the parent element or immediately
      // following an element.
      if (_indenting && !state.preserveSpace && (state.empty || state.afterElement))
        _printer.breakLine();
    }
    preserveSpace = state.preserveSpace;

    // Do not change the current element state yet.
    // This only happens in endElement().

    // XHTML: element names are lower case, DOM will be different
    _printer.printText('<');
    _printer.printText(tagName);
    _printer.indent();

    // Lookup the element's attribute, but only print specified
    // attributes. (Unspecified attributes are derived from the DTD.
    // For each attribute print it's name and value as one part,
    // separated with a space so the element can be broken on
    // multiple lines.
    attrMap = elem.getAttributes();
    if (attrMap != null) {
      for (i = 0; i < attrMap.getLength(); ++i) {
        attr = (Attr) attrMap.item(i);
        name = attr.getName().toLowerCase(Locale.ENGLISH);
        value = attr.getValue();
        if (attr.getSpecified()) {
          _printer.printSpace();
          // HTML: Empty values print as attribute name, no value.
          // HTML: URI attributes will print unescaped
          if (value == null) {
            value = "";
          }
          if (!_format.getPreserveEmptyAttributes() && value.length() == 0)
            _printer.printText(name);
          else if (HTMLdtd.isURI(tagName, name)) {
            _printer.printText(name);
            _printer.printText("=\"");
            _printer.printText(escapeURI(value));
            _printer.printText('"');
          } else if (HTMLdtd.isBoolean(tagName, name)) _printer.printText(name);
          else {
            _printer.printText(name);
            _printer.printText("=\"");
            printEscaped(value);
            _printer.printText('"');
          }
        }
      }
    }
    if (HTMLdtd.isPreserveSpace(tagName)) preserveSpace = true;

    // If element has children, or if element is not an empty tag,
    // serialize an opening tag.
    if (elem.hasChildNodes() || !HTMLdtd.isEmptyTag(tagName)) {
      // Enter an element state, and serialize the children
      // one by one. Finally, end the element.
      state = enterElementState(null, null, tagName, preserveSpace);

      // Prevents line breaks inside A/TD
      if (tagName.equalsIgnoreCase("A") || tagName.equalsIgnoreCase("TD")) {
        state.empty = false;
        _printer.printText('>');
      }

      // Handle SCRIPT and STYLE specifically by changing the
      // state of the current element to CDATA (XHTML) or
      // unescaped (HTML).
      if (tagName.equalsIgnoreCase("SCRIPT") || tagName.equalsIgnoreCase("STYLE")) {
        // HTML: Print contents unescaped
        state.unescaped = true;
      }
      child = elem.getFirstChild();
      while (child != null) {
        serializeNode(child);
        child = child.getNextSibling();
      }
      endElementIO(null, null, tagName);
    } else {
      _printer.unindent();
      // XHTML: Close empty tag with ' />' so it's XML and HTML compatible.
      // HTML: Empty tags are defined as such in DTD no in document.
      if (!elem.hasChildNodes() && isAllowedEmptyTag(tagName) && !requiresClosingTag(tagName))
        _printer.printText("/>");
      else _printer.printText('>');
      // After element but parent element is no longer empty.
      state.afterElement = true;
      state.empty = false;
      if (isDocumentState()) _printer.flush();
    }
  }

  @Override
  public void endElementIO(String namespaceURI, String localName, String rawName)
      throws IOException {

    ElementState state;

    // Works much like content() with additions for closing an element. Note the different checks
    // for the closed element's state and the parent element's state.
    _printer.unindent();
    state = getElementState();

    if (state.empty && isAllowedEmptyTag(rawName) && !requiresClosingTag(rawName)) { //
      _printer.printText("/>");
    } else {
      if (state.empty) _printer.printText('>');
      // This element is not empty and that last content was another element, so print a line break
      // before that last element and this element's closing tag. [keith] Provided this is not an
      // anchor. HTML: some elements do not print closing tag (e.g. LI)
      if (rawName == null
          || !HTMLdtd.isOnlyOpening(rawName)
          || HTMLdtd.isOptionalClosing(rawName)) {
        if (_indenting && !state.preserveSpace && state.afterElement) _printer.breakLine();
        // Must leave CData section first (Illegal in HTML, but still)
        if (state.inCData) _printer.printText("]]>");
        _printer.printText("</");
        _printer.printText(state.rawName);
        _printer.printText('>');
      }
    }

    // Leave the element state and update that of the parent (if we're not root) to not empty and
    // after element.
    state = leaveElementState();
    // Temporary hack to prevent line breaks inside A/TD
    if (rawName == null || (!rawName.equalsIgnoreCase("A") && !rawName.equalsIgnoreCase("TD")))
      state.afterElement = true;
    state.empty = false;
    if (isDocumentState()) _printer.flush();
  }

  /*
   * The override is to use printEscaped() which already escapes entity references
   * and writes them in the final serialized string. As escapeURI() is called like
   * "printer.printText(escapeURI(value))", if the URI is returned here it would
   * be double-printed and that is why the return value is an empty string.
   */
  @Override
  protected String escapeURI(String uri) {
    try {
      printEscaped(uri);
    } catch (IOException e) {
      logger.error("URI escaping failed for value: " + uri);
    }
    return "";
  }

  private boolean requiresClosingTag(String tagName) {
    return requireClosingTags.matches(tagName);
  }

  private boolean isAllowedEmptyTag(String tagName) {
    return "head".equals(tagName) || allowedEmptyTags.matches(tagName);
  }
}
