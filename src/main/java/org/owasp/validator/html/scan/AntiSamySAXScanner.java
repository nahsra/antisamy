/*
 * Copyright (c) 2007-2011, Arshan Dabirsiaghi, Jason Li
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.parsers.SAXParser;
import org.owasp.validator.html.AsyncResults;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class AntiSamySAXScanner extends AbstractAntiSamyScanner {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    
    private static final Queue<CachedItem> cachedItems = new ConcurrentLinkedQueue<CachedItem>();

    private static final TransformerFactory sTransformerFactory = TransformerFactory.newInstance();

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
            } catch (SAXNotRecognizedException e) {
                throw new RuntimeException(e);
            } catch (SAXNotSupportedException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public AntiSamySAXScanner(Policy policy) {
		super(policy);
	}

	public CleanResults getResults() {
		return null;
	}

	public CleanResults scan(String html) throws ScanException {
	    return scan(html, this.policy);
	}
	
	public CleanResults scan(String html, Policy policy) throws ScanException {
       if (html == null) {
            throw new ScanException(new NullPointerException("Null input"));
        }

       int maxInputSize = this.policy.getMaxInputSize();

       if (html.length() > maxInputSize) {
           addError(ErrorMessageUtil.ERROR_INPUT_SIZE, new Object[] {html.length(), maxInputSize});
           throw new ScanException(errorMessages.get(0));
       }
       
        StringWriter out = new StringWriter();
        StringReader reader = new StringReader(html);

        AsyncResults results = scan(reader, out);
        try
        {
            CachedItem cachedItem = (CachedItem) results.getFuture().get();
            final String cleanHtml = trim(html, out.getBuffer().toString());
            Callable<String> cleanCallable = new Callable<String>() {
                public String call() throws Exception {
                    return cleanHtml;
                }
            };

            errorMessages.clear();
            errorMessages.addAll(cachedItem.magicSAXFilter.getErrorMessages());
            cachedItems.add(cachedItem);
            return new CleanResults(results.getStartOfScan(), cleanCallable, null, errorMessages);
        }
        catch (InterruptedException e)
        {
            throw new ScanException(e);
        }
        catch (ExecutionException e)
        {
            throw new ScanException(e);
        }
	}

	public AsyncResults scan(Reader reader, Writer out) throws ScanException {
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
            final org.apache.xml.serialize.HTMLSerializer serializer = getHTMLSerializer(out, format);
            Future<CachedItem> future = executorService.submit(new Callable<CachedItem>() {
                public CachedItem call() throws TransformerException {
                    transformer.transform(source, new SAXResult(serializer));
                    return cachedItem;
                }
            });

			return new AsyncResults(startOfScan, future);

		} catch (Exception e) {
			throw new ScanException(e);
		}

	}

     /**
      * Return a new Transformer instance. This is wrapped in a synchronized method because there is
      * no guarantee that the TransformerFactory is thread-safe
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
        } catch (SAXNotRecognizedException e) {
            throw new RuntimeException(e);
        } catch (SAXNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
