/*
 * Copyright (c) 2007-2021, Arshan Dabirsiaghi, Jason Li
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of OWASP nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.owasp.validator.html.scan;

import org.apache.batik.css.parser.ParseException;
import org.apache.xerces.dom.DocumentImpl;
import net.sourceforge.htmlunit.cyberneko.parsers.DOMFragmentParser;
import org.owasp.validator.css.CssScanner;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.model.Attribute;
import org.owasp.validator.html.model.Tag;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is where the magic lives. All the scanning/filtration logic resides
 * here, but it should not be called directly. All scanning should be done
 * through an <code>AntiSamy.scan()</code> method.
 * 
 * @author Arshan Dabirsiaghi
 */
public class AntiSamyDOMScanner extends AbstractAntiSamyScanner {
    private Document document = new DocumentImpl();
    private DocumentFragment dom = document.createDocumentFragment();
    private CleanResults results = null;
    private static final int maxDepth = 250;
    private static final Pattern invalidXmlCharacters =
            Pattern.compile("[\\u0000-\\u001F\\uD800-\\uDFFF\\uFFFE-\\uFFFF&&[^\\u0009\\u000A\\u000D]]");
    private static final Pattern conditionalDirectives = Pattern.compile("<?!?\\[\\s*(?:end)?if[^]]*\\]>?");

    private static final Queue<CachedItem> cachedItems = new ConcurrentLinkedQueue<CachedItem>();

    static class CachedItem {
        private final DOMFragmentParser parser;
        private final Matcher invalidXmlCharMatcher = invalidXmlCharacters.matcher("");


        CachedItem() throws SAXNotSupportedException, SAXNotRecognizedException {
            this.parser = getDomParser();
        }

        DOMFragmentParser getDomFragmentParser()  {
            return parser;
        }
    }

    public AntiSamyDOMScanner(Policy policy) {
        super(policy);
    }

    /* UnusedDeclaration TODO Investigate */
    public AntiSamyDOMScanner() throws PolicyException {
        super();
    }

    /**
     * This is where the magic lives.
     *
     * @param html A String whose contents we want to scan.
     * @return A <code>CleanResults</code> object with an
     *         <code>XMLDocumentFragment</code> object and its String
     *         representation, as well as some scan statistics.
     * @throws ScanException When there is a problem encountered
	 *         while scanning the HTML.
	 */
    @Override
    public CleanResults scan(String html) throws ScanException {

        if (html == null) {
            throw new ScanException(new NullPointerException("Null html input"));
        }

        errorMessages.clear();
        int maxInputSize = policy.getMaxInputSize();

        if (maxInputSize < html.length()) {
            addError(ErrorMessageUtil.ERROR_INPUT_SIZE, new Object[]{html.length(), maxInputSize});
            throw new ScanException(errorMessages.get(0));
        }

        isNofollowAnchors = policy.isNofollowAnchors();
        isNoopenerAndNoreferrerAnchors = policy.isNoopenerAndNoreferrerAnchors();
        isValidateParamAsEmbed = policy.isValidateParamAsEmbed();

        long startOfScan = System.currentTimeMillis();

        try {

            CachedItem cachedItem;
            cachedItem = cachedItems.poll();
            if (cachedItem == null){
                cachedItem = new CachedItem();
            }

            /*
             * We have to replace any invalid XML characters to prevent NekoHTML
             * from breaking when it gets passed encodings like %21.
             */

            html = stripNonValidXMLCharacters(html, cachedItem.invalidXmlCharMatcher);

            /*
             * First thing we do is call the HTML cleaner ("NekoHTML") on it
             * with the appropriate options. We choose not to omit tags due to
             * the fallibility of our own listing in the ever changing world of
             * W3C.
             */

            DOMFragmentParser parser = cachedItem.getDomFragmentParser();

            try {
                parser.parse(new InputSource(new StringReader(html)), dom);
            } catch (Exception e) {
                throw new ScanException(e);
            }

            processChildren(dom, 0);

            /*
             * Serialize the output and then return the resulting DOM object and
             * its string representation.
             */

            final String trimmedHtml = html;

            StringWriter out = new StringWriter();

            @SuppressWarnings("deprecation")
            org.apache.xml.serialize.OutputFormat format = getOutputFormat();

            //noinspection deprecation
            org.apache.xml.serialize.HTMLSerializer serializer = getHTMLSerializer(out, format);
            serializer.serialize(dom);

            /*
             * Get the String out of the StringWriter and rip out the XML
             * declaration if the Policy says we should.
             */
            final String trimmed = trim( trimmedHtml, out.getBuffer().toString() );

            Callable<String> cleanHtml = new Callable<String>() {
                public String call() throws Exception {
                    return trimmed;
                }
            };

            /*
             * Return the DOM object as well as string HTML.
             */
            results = new CleanResults(startOfScan, cleanHtml, dom, errorMessages);

            cachedItems.add( cachedItem);
            return results;

        } catch (SAXException | IOException e) {
            throw new ScanException(e);
        }

    }

    static DOMFragmentParser getDomParser()
            throws SAXNotRecognizedException, SAXNotSupportedException {
        DOMFragmentParser parser = new DOMFragmentParser();
        parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");

        parser.setFeature("http://cyberneko.org/html/features/scanner/style/strip-cdata-delims", false);
        parser.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);

        try {
            parser.setFeature("http://cyberneko.org/html/features/enforce-strict-attribute-names", true);
        } catch (SAXNotRecognizedException se) {
            // this indicates that the patched nekohtml is not on the
            // classpath
        }
        return parser;
    }

    /**
     * The workhorse of the scanner. Recursively scans document elements
     * according to the policy. This should be called implicitly through the
     * AntiSamy.scan() method.
     *
     * @param node The node to validate.
     */
    private void recursiveValidateTag(final Node node, int currentStackDepth) throws ScanException {

        currentStackDepth++;

        if(currentStackDepth > maxDepth) {
            throw new ScanException("Too many nested tags");
        }

        if (node instanceof Comment) {
            processCommentNode(node);
            return;
        }

        boolean isElement = node instanceof Element;
        NodeList eleChildNodes = node.getChildNodes();
        if (isElement && eleChildNodes.getLength() == 0) {
            if (removeDisallowedEmpty(node)){
                return;
            }
        }

        if (node instanceof Text && Node.CDATA_SECTION_NODE == node.getNodeType()) {
            stripCData(node);
            return;
        }

        if (node instanceof ProcessingInstruction) {
            removePI(node);
        }

        if (!isElement) {
            return;
        }

        final Element ele = (Element) node;
        final Node parentNode = ele.getParentNode();

        final String tagName = ele.getNodeName();
        final String tagNameLowerCase = tagName.toLowerCase();
        Tag tagRule = policy.getTagByLowercaseName(tagNameLowerCase);

        /*
         * If <param> and no policy and isValidateParamAsEmbed and policy in
         * place for <embed> and <embed> policy is to validate, use custom
         * policy to get the tag through to the validator.
         */
        Tag embedTag = policy.getEmbedTag();
        boolean masqueradingParam = isMasqueradingParam(tagRule, embedTag, tagNameLowerCase);
        if (masqueradingParam){
            tagRule = Constants.BASIC_PARAM_TAG_RULE;
        }

        if ((tagRule == null && policy.isEncodeUnknownTag()) || (tagRule != null && tagRule.isAction( "encode"))) {
            encodeTag(currentStackDepth, ele, tagName, eleChildNodes);
        } else if (tagRule == null || tagRule.isAction( Policy.ACTION_FILTER)) {
            actionFilter(currentStackDepth, ele, tagName, tagRule, eleChildNodes);
        } else if (tagRule.isAction( Policy.ACTION_VALIDATE)) {
            actionValidate(currentStackDepth, ele, parentNode, tagName, tagNameLowerCase, tagRule, masqueradingParam, embedTag, eleChildNodes);
        } else if (tagRule.isAction( Policy.ACTION_TRUNCATE)) {
            actionTruncate(ele, tagName, eleChildNodes);
        } else {
            /*
             * If we reached this that means that the tag's action is "remove",
             * which means to remove the tag (including its contents).
             */
            addError(ErrorMessageUtil.ERROR_TAG_DISALLOWED, new Object[]{HTMLEntityEncoder.htmlEntityEncode(tagName)});
            removeNode(ele);
        }
    }

    private boolean isMasqueradingParam(Tag tagRule, Tag embedTag, String tagNameLowerCase){
        if (tagRule == null && isValidateParamAsEmbed && "param".equals(tagNameLowerCase)) {
            return embedTag != null && embedTag.isAction(Policy.ACTION_VALIDATE);
        }
        return false;
    }

    private void encodeTag(int currentStackDepth, Element ele, String tagName, NodeList eleChildNodes) throws ScanException {
        addError(ErrorMessageUtil.ERROR_TAG_ENCODED, new Object[]{HTMLEntityEncoder.htmlEntityEncode(tagName)});
        processChildren(eleChildNodes, currentStackDepth);

   /*
    * Transform the tag to text, HTML-encode it and promote the
    * children. The tag will be kept in the fragment as one or two text
    * Nodes located before and after the children; representing how the
    * tag used to wrap them.
    */

        encodeAndPromoteChildren(ele);
    }

    private void actionFilter(int currentStackDepth, Element ele, String tagName, Tag tag, NodeList eleChildNodes) throws ScanException {
        if (tag == null) {
            addError(ErrorMessageUtil.ERROR_TAG_NOT_IN_POLICY, new Object[]{HTMLEntityEncoder.htmlEntityEncode(tagName)});
        } else {
            addError(ErrorMessageUtil.ERROR_TAG_FILTERED, new Object[]{HTMLEntityEncoder.htmlEntityEncode(tagName)});
        }

        processChildren(eleChildNodes, currentStackDepth);
        promoteChildren(ele);
    }

    private void actionValidate(int currentStackDepth, Element ele, Node parentNode, String tagName, String tagNameLowerCase, Tag tag, boolean masqueradingParam, Tag embedTag, NodeList eleChildNodes) throws ScanException {
   /*
    * If doing <param> as <embed>, now is the time to convert it.
    */
        String nameValue = null;
        if (masqueradingParam) {
            nameValue = ele.getAttribute("name");
            if (nameValue != null && !"".equals(nameValue)) {
                String valueValue = ele.getAttribute("value");
                ele.setAttribute(nameValue, valueValue);
                ele.removeAttribute("name");
                ele.removeAttribute("value");
                tag = embedTag;
            }
        }

   /*
    * Check to see if it's a <style> tag. We have to special case this
    * tag so we can hand it off to the custom style sheet validating
    * parser.
    */

        if ("style".equals(tagNameLowerCase) && policy.getStyleTag() != null) {
            if (processStyleTag(ele, parentNode)) return;
        }

   /*
    * Go through the attributes in the tainted tag and validate them
    * against the values we have for them.
    *
    * If we don't have a rule for the attribute we remove the
    * attribute.
    */

        if (processAttributes(ele, tagName, tag, currentStackDepth)) return; // can't process any more if we

        if ("a".equals(tagNameLowerCase)) {
            boolean addNofollow = isNofollowAnchors;
            boolean addNoopenerAndNoreferrer = false;

            if (isNoopenerAndNoreferrerAnchors) {
                Node targetAttribute = ele.getAttributes().getNamedItem("target");
                if (targetAttribute != null && targetAttribute.getNodeValue().equalsIgnoreCase("_blank")) {
                    addNoopenerAndNoreferrer = true;
                }
            }

            Node relAttribute = ele.getAttributes().getNamedItem("rel");
            String relValue = Attribute.mergeRelValuesInAnchor(addNofollow, addNoopenerAndNoreferrer, relAttribute == null ? "" : relAttribute.getNodeValue());
            if (!relValue.isEmpty()){
                ele.setAttribute("rel", relValue.trim());
            }
        }

        processChildren(eleChildNodes, currentStackDepth);

   /*
    * If we have been dealing with a <param> that has been converted to
    * an <embed>, convert it back
    */
        if (masqueradingParam && nameValue != null && !"".equals(nameValue)) {
            String valueValue = ele.getAttribute(nameValue);
            ele.setAttribute("name", nameValue);
            ele.setAttribute("value", valueValue);
            ele.removeAttribute(nameValue);
        }
    }

    private boolean processStyleTag(Element ele, Node parentNode) {
        /*
         * Invoke the css parser on this element.
         */
        CssScanner styleScanner = new CssScanner(policy, messages, policy.isEmbedStyleSheets());

        try {
            if (ele.getChildNodes().getLength() > 0) {
                String toScan = "";

                for (int i = 0; i < ele.getChildNodes().getLength(); i++) {
                    Node childNode = ele.getChildNodes().item(i);
                    if (!toScan.isEmpty()){
                        toScan += "\n";
                    }
                    toScan += childNode.getTextContent();
                }

                CleanResults cr = styleScanner.scanStyleSheet(toScan, policy.getMaxInputSize());
                errorMessages.addAll(cr.getErrorMessages());

                /*
                 * If IE gets an empty style tag, i.e. <style/> it will
                 * break all CSS on the page. I wish I was kidding. So,
                 * if after validation no CSS properties are left, we
                 * would normally be left with an empty style tag and
                 * break all CSS. To prevent that, we have this check.
                 */

                String cleanHTML = cr.getCleanHTML();
                cleanHTML = cleanHTML == null || cleanHTML.equals("") ? "/* */" : cleanHTML;

                ele.getFirstChild().setNodeValue(cleanHTML);
                /*
                 * Remove every other node after cleaning CSS, there will
                 * be only one node in the end, as it always should have.
                 */
                for (int i = 1; i < ele.getChildNodes().getLength(); i++) {
                    Node childNode = ele.getChildNodes().item(i);
                    ele.removeChild(childNode);
                }
            }

        } catch (DOMException | ScanException | ParseException | NumberFormatException e) {

            /*
             * ParseException shouldn't be possible anymore, but we'll leave it
             * here because I (Arshan) am hilariously dumb sometimes.
             * Batik can throw NumberFormatExceptions (see bug #48).
             */

            addError(ErrorMessageUtil.ERROR_CSS_TAG_MALFORMED, new Object[]{HTMLEntityEncoder.htmlEntityEncode(ele.getFirstChild().getNodeValue())});
            parentNode.removeChild(ele);
            return true;
        }
        return false;
    }

    private void actionTruncate(Element ele, String tagName, NodeList eleChildNodes) {
   /*
    * Remove all attributes. This is for tags like i, b, u, etc. Purely
    * formatting without any need for attributes. It also removes any
    * children.
    */

        NamedNodeMap nnmap = ele.getAttributes();
        while (nnmap.getLength() > 0) {
            addError(ErrorMessageUtil.ERROR_ATTRIBUTE_NOT_IN_POLICY,
                new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(nnmap.item(0).getNodeName())});
            ele.removeAttribute(nnmap.item(0).getNodeName());
        }

        int i = 0;
        int j = 0;
        int length = eleChildNodes.getLength();

        while (i < length) {
            Node nodeToRemove = eleChildNodes.item(j);
            if (nodeToRemove.getNodeType() != Node.TEXT_NODE) {
                ele.removeChild(nodeToRemove);
            } else {
                j++;
            }
            i++;
        }
    }

    private boolean processAttributes(Element ele, String tagName, Tag tag, int currentStackDepth) throws ScanException {
        Node attribute;

        NamedNodeMap attributes = ele.getAttributes();
        for (int currentAttributeIndex = 0; currentAttributeIndex < attributes.getLength(); currentAttributeIndex++) {

            attribute = attributes.item(currentAttributeIndex);

            String name = attribute.getNodeName();
            String value = attribute.getNodeValue();

            Attribute attr = tag.getAttributeByName(name.toLowerCase());

            /*
             * If we there isn't an attribute by that name in our policy
             * check to see if it's a globally defined attribute. Validate
             * against that if so.
             */
            if (attr == null) {
                attr = policy.getGlobalAttributeByName(name);
                if (attr == null && policy.isAllowDynamicAttributes()) {
                    // not a global attribute, perhaps it is a dynamic attribute, if allowed
                    attr = policy.getDynamicAttributeByName(name);
                }
            }

            /*
             * We have to special case the "style" attribute since it's
             * validated quite differently.
             */
            if ("style".equals(name.toLowerCase()) && attr != null) {

                /*
                 * Invoke the CSS parser on this element.
                 */
                CssScanner styleScanner = new CssScanner(policy, messages, false);

                try {
                    CleanResults cr = styleScanner.scanInlineStyle(value, tagName, policy.getMaxInputSize());
                    attribute.setNodeValue(cr.getCleanHTML());
                    List<String> cssScanErrorMessages = cr.getErrorMessages();
                    errorMessages.addAll(cssScanErrorMessages);

                } catch (DOMException | ScanException e) {

                    addError(ErrorMessageUtil.ERROR_CSS_ATTRIBUTE_MALFORMED,
                        new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(ele.getNodeValue())});
                    ele.removeAttribute(attribute.getNodeName());
                    currentAttributeIndex--;
                }

            } else {

                if (attr != null) {

                    // See if attribute is invalid
                    if (!(attr.containsAllowedValue( value.toLowerCase()) ||
                         (attr.matchesAllowedExpression( value ))) ) {

                        /*
                         * Attribute is NOT valid, so: Document transgression and perform the
                         * "onInvalid" action. The default action is to
                         * strip the attribute and leave the rest intact.
                         */

                        String onInvalidAction = attr.getOnInvalid();

                        if ("removeTag".equals(onInvalidAction)) {

                            /*
                             * Remove the tag and its contents.
                             */

                            removeNode(ele);

                            addError(ErrorMessageUtil.ERROR_ATTRIBUTE_INVALID_REMOVED,
                              new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
                            return true;

                        } else if ("filterTag".equals(onInvalidAction)) {

                            /*
                             * Remove the attribute and keep the rest of the tag.
                             */

                            processChildren(ele, currentStackDepth);
                            promoteChildren(ele);
                            addError(ErrorMessageUtil.ERROR_ATTRIBUTE_CAUSE_FILTER,
                              new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
                            return true;
                        } else if ("encodeTag".equals(onInvalidAction)) {

                            /*
                             * Remove the attribute and keep the rest of the tag.
                             */

                            processChildren(ele, currentStackDepth);
                            encodeAndPromoteChildren(ele);
                            addError(ErrorMessageUtil.ERROR_ATTRIBUTE_CAUSE_ENCODE,
                              new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
                            return true;
                        } else {

                            /*
                             * onInvalidAction = "removeAttribute"
                             */

                            ele.removeAttribute(attribute.getNodeName());
                            currentAttributeIndex--;
                            addError(ErrorMessageUtil.ERROR_ATTRIBUTE_INVALID,
                              new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
                            
                        }
                    }

                } else {
                    /*
                     * the attribute they specified isn't in our policy
                     * - remove it (whitelisting!)
                     */

                    addError(ErrorMessageUtil.ERROR_ATTRIBUTE_NOT_IN_POLICY,
                      new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
                    ele.removeAttribute(attribute.getNodeName());
                    currentAttributeIndex--;

                } // end if attribute is found in policy file

            } // end while loop through attributes

        } // loop through each attribute
        return false;
    }

    private void processChildren(Node ele, int currentStackDepth) throws ScanException {
        processChildren(ele.getChildNodes(), currentStackDepth);
    }

    private void processChildren(NodeList childNodes, int currentStackDepth ) throws ScanException {
        Node tmp;
        for (int i = 0; i < childNodes.getLength(); i++) {

            tmp = childNodes.item(i);
            recursiveValidateTag(tmp, currentStackDepth);

            /*
             * This indicates the node was removed/failed validation.
             */
            if (tmp.getParentNode() == null) {
                i--;
            }
        }
    }

    private void removePI(Node node) {
        addError(ErrorMessageUtil.ERROR_PI_FOUND, new Object[]{HTMLEntityEncoder.htmlEntityEncode(node.getTextContent())});
        removeNode(node);
    }

    private void stripCData(Node node) {
        addError(ErrorMessageUtil.ERROR_CDATA_FOUND, new Object[]{HTMLEntityEncoder.htmlEntityEncode(node.getTextContent())});
        Node text = document.createTextNode(node.getTextContent());
        node.getParentNode().insertBefore(text, node);
        node.getParentNode().removeChild(node);
    }

    private void processCommentNode(Node node) {
        if (!policy.isPreserveComments()) {
            node.getParentNode().removeChild(node);
        } else {
            String value = ((Comment) node).getData();
            // Strip conditional directives regardless of the
            // PRESERVE_COMMENTS setting.
            if (value != null) {
                ((Comment) node).setData(conditionalDirectives.matcher(value).replaceAll(""));
            }
        }
    }

    private boolean removeDisallowedEmpty(Node node){
        String tagName = node.getNodeName();

        if (!isAllowedEmptyTag(tagName)) {
           /*
            * Wasn't in the list of allowed elements, so we'll nuke it.
            */
            addError(ErrorMessageUtil.ERROR_TAG_EMPTY, new Object[]{HTMLEntityEncoder.htmlEntityEncode(node.getNodeName())});
            removeNode(node);
            return true;
        }
        return false;
    }

    private void removeNode(Node node) {
		Node parent = node.getParentNode();
		parent.removeChild(node);
		String tagName = parent.getNodeName();
		if(	parent instanceof Element && 
			parent.getChildNodes().getLength() == 0 && 
			!isAllowedEmptyTag(tagName)) {
			removeNode(parent);
		}
	}

	private boolean isAllowedEmptyTag(String tagName) {
        return "head".equals(tagName ) || policy.getAllowedEmptyTags().matches(tagName);
	}
	
    /**
     * Used to promote the children of a parent to accomplish the "filterTag" action.
     *
     * @param ele The Element we want to filter.
     */
    private void promoteChildren(Element ele) {
        promoteChildren(ele, ele.getChildNodes());
    }

    private void promoteChildren(Element ele, NodeList eleChildNodes) {

        Node parent = ele.getParentNode();

        while (eleChildNodes.getLength() > 0) {
            Node node = ele.removeChild(eleChildNodes.item(0));
            parent.insertBefore(node, ele);
        }

        if (parent != null) {
            removeNode(ele);
        }
    }

    /**
     * This method was borrowed from Mark McLaren, to whom I owe much beer.
     *
     * This method ensures that the output has only valid XML unicode characters
     * as specified by the XML 1.0 standard. For reference, please see <a
     * href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
     * standard</a>. This method will return an empty String if the input is
     * null or empty.
     *
     * @param in The String whose non-valid characters we want to remove.
     * @param invalidXmlCharsMatcher  The reusable regex matcher
     * @return The in String, stripped of non-valid characters.
     */
    private String stripNonValidXMLCharacters(String in, Matcher invalidXmlCharsMatcher) {

        if (in == null || ("".equals(in))) {
            return ""; // vacancy test.
        }
        invalidXmlCharsMatcher.reset(in);
        return invalidXmlCharsMatcher.matches() ? invalidXmlCharsMatcher.replaceAll("") : in;
    }

    /**
     * Transform the element to text, HTML-encode it and promote the children.
     * The element will be kept in the fragment as one or two text Nodes located
     * before and after the children; representing how the tag used to wrap
     * them. If the element didn't have any children then only one text Node is
     * created representing an empty element.
     *
     * @param ele Element to be encoded
     */
    private void encodeAndPromoteChildren(Element ele) {
        Node parent = ele.getParentNode();
        String tagName = ele.getTagName();
        Node openingTag = parent.getOwnerDocument().createTextNode(toString(ele));
        parent.insertBefore(openingTag, ele);
        if (ele.hasChildNodes()) {
            Node closingTag = parent.getOwnerDocument().createTextNode("</" + tagName + ">");
            parent.insertBefore(closingTag, ele.getNextSibling());
        }
        promoteChildren(ele);
    }

    /**
     * Returns a text version of the passed element
     *
     * @param ele Element to be converted
     * @return String representation of the element
     */
    private String toString(Element ele) {
        StringBuilder eleAsString = new StringBuilder("<" + ele.getNodeName());
        NamedNodeMap attributes = ele.getAttributes();
        Node attribute;
        for (int i = 0; i < attributes.getLength(); i++) {
            attribute = attributes.item(i);

            String name = attribute.getNodeName();
            String value = attribute.getNodeValue();

            eleAsString.append(" ");
            eleAsString.append(HTMLEntityEncoder.htmlEntityEncode(name));
            eleAsString.append("=\"");
            eleAsString.append(HTMLEntityEncoder.htmlEntityEncode(value));
            eleAsString.append("\"");
        }
        if (ele.hasChildNodes()) {
            eleAsString.append(">");
        } else {
            eleAsString.append("/>");
        }
        return eleAsString.toString();
    }

    @Override
    public CleanResults getResults() {
        return results;
    }
}
