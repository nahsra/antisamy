/*
 * Copyright (c) 2007-2020, Arshan Dabirsiaghi, Jason Li
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

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.parsers.SAXParser;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class AntiSamySAXScanner extends AbstractAntiSamyScanner {
    
    private static final Queue<CachedItem> cachedItems = new ConcurrentLinkedQueue<CachedItem>();

    private static final TransformerFactory sTransformerFactory = TransformerFactory.newInstance();

    static {
        // Disable external entities, etc.
        sTransformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        sTransformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
    }

    static class CachedItem {
        private final Transformer transformer;
        private final SAXParser saxParser;
        private final MagicSAXFilter magicSAXFilter;

        CachedItem(Transformer transformer, SAXParser saxParser, MagicSAXFilter magicSAXFilter)  {
            this.transformer = transformer;
            this.saxParser = saxParser;
            this.magicSAXFilter = magicSAXFilter;
            XMLDocumentFilter[] filters = { magicSAXFilter };
            try {
                saxParser.setProperty("http://cyberneko.org/html/properties/filters", filters);
            } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public AntiSamySAXScanner(Policy policy) {
        super(policy);
    }

    @Override
    public CleanResults getResults() {
        return null;
    }

    @Override
    public CleanResults scan(String html) throws ScanException {
        return scan(html, this.policy);
    }

    public CleanResults scan(String html, Policy policy) throws ScanException {
       if (html == null) {
           throw new ScanException(new NullPointerException("Null html input"));
       }

       int maxInputSize = this.policy.getMaxInputSize();

       if (html.length() > maxInputSize) {
           addError(ErrorMessageUtil.ERROR_INPUT_SIZE, new Object[] {html.length(), maxInputSize});
           throw new ScanException(errorMessages.get(0));
       }
       
        final StringWriter out = new StringWriter();
        StringReader reader = new StringReader(html);

        CleanResults results = scan(reader, out);
        final String tainted = html;
        Callable<String> cleanCallable = new Callable<String>() {
            public String call() throws Exception {
                return trim(tainted, out.toString());
            }
        };
        return new CleanResults(results.getStartOfScan(), cleanCallable, (DocumentFragment)null, results.getErrorMessages());
    }

    /**
     * Using a SAX parser, can pass Streams for input and output.
     * Use case is a Servlet filter where request or response is large
     * and caller does not need the entire string in memory.
     * @param reader A Reader which can feed the SAXParser a little input at a time
     * @param writer A Writer that can take a little output at a time
     * @return CleanResults where the cleanHtml is null. If a caller wants the HTML as a string,
     *         it must capture the contents of the writer (i.e., use a StringWriter).
     * @throws ScanException When there is a problem encountered
     *         while scanning the HTML.
     */
    public CleanResults scan(Reader reader, Writer writer) throws ScanException {
        try {

            CachedItem candidateCachedItem = cachedItems.poll();
            if (candidateCachedItem == null){
                candidateCachedItem = new CachedItem(getNewTransformer(), getParser(), new MagicSAXFilter(messages));
            }
            
            final CachedItem cachedItem = candidateCachedItem;

            SAXParser parser = cachedItem.saxParser;
            cachedItem.magicSAXFilter.reset(policy);

            long startOfScan = System.currentTimeMillis();

            final SAXSource source = new SAXSource(parser, new InputSource(reader));

            final Transformer transformer = cachedItem.transformer;
            boolean formatOutput = policy.isFormatOutput();
            boolean useXhtml = policy.isUseXhtml();
            boolean omitXml = policy.isOmitXmlDeclaration();

            transformer.setOutputProperty(OutputKeys.INDENT, formatOutput ? "yes" : "no");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXml ? "yes" : "no");
            transformer.setOutputProperty(OutputKeys.METHOD, useXhtml ? "xml" : "html");

            //noinspection deprecation
            final org.apache.xml.serialize.OutputFormat format = getOutputFormat();
            //noinspection deprecation
            final org.apache.xml.serialize.HTMLSerializer serializer = getHTMLSerializer(writer, format);
            
            transformer.transform(source, new SAXResult(serializer));
            errorMessages.clear();
            errorMessages.addAll(cachedItem.magicSAXFilter.getErrorMessages());
            cachedItems.add( cachedItem);
            return new CleanResults(startOfScan, (String)null, (DocumentFragment)null, errorMessages);

        } catch (Exception e) {
            throw new ScanException(e);
        }
    }

     /**
      * Return a new Transformer instance. This is wrapped in a synchronized method because there is
      * no guarantee that the TransformerFactory is thread-safe.
      *
      * @return a new Transformer instance.
      */
     private static synchronized Transformer getNewTransformer()  {
         try {
             return sTransformerFactory.newTransformer();
         } catch (TransformerConfigurationException e) {
             throw new RuntimeException(e);
         }
     }

    private static SAXParser getParser()  {
        try {
            SAXParser parser = new SAXParser();
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
            parser.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
            parser.setFeature("http://apache.org/xml/features/scanner/notify-char-refs", true);
            parser.setFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", true);

            parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
            return parser;
        } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
