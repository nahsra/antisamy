package org.owasp.validator.html.scan;

import org.apache.xml.serialize.ElementState;
import org.apache.xml.serialize.HTMLdtd;
import org.apache.xml.serialize.OutputFormat;
import org.owasp.validator.html.InternalPolicy;

import java.io.IOException;
import java.io.Writer;

@SuppressWarnings("deprecation")
public class ASHTMLSerializer extends org.apache.xml.serialize.HTMLSerializer {

	private boolean encodeAllPossibleEntities;

    public ASHTMLSerializer(Writer w, OutputFormat format, InternalPolicy policy) {
		super(w, format);
		this.encodeAllPossibleEntities = policy.isEntityEncodeIntlCharacters();
	}
	
	protected String getEntityRef(int charToPrint) {
		if(encodeAllPossibleEntities || Constants.big5CharsToEncode.indexOf(charToPrint) != -1)
			return super.getEntityRef(charToPrint);
		return null;
	}

	public void endElementIO(String namespaceURI, String localName,
			String rawName) throws IOException {
		
		ElementState state;

		// Works much like content() with additions for closing
		// an element. Note the different checks for the closed
		// element's state and the parent element's state.
		_printer.unindent();
		state = getElementState();

        if (state.empty)
			_printer.printText('>');
		// This element is not empty and that last content was
		// another element, so print a line break before that
		// last element and this element's closing tag.
		// [keith] Provided this is not an anchor.
		// HTML: some elements do not print closing tag (e.g. LI)
		if (rawName == null || !HTMLdtd.isOnlyOpening(rawName)) {
			if (_indenting && !state.preserveSpace && state.afterElement)
				_printer.breakLine();
			// Must leave CData section first (Illegal in HTML, but still)
			if (state.inCData)
				_printer.printText("]]>");
			_printer.printText("</");
			_printer.printText(state.rawName);
			_printer.printText('>');
		}
		
		// Leave the element state and update that of the parent
		// (if we're not root) to not empty and after element.
		state = leaveElementState();
		// Temporary hack to prevent line breaks inside A/TD
		if (rawName == null
				|| (!rawName.equalsIgnoreCase("A") && !rawName
						.equalsIgnoreCase("TD")))

			state.afterElement = true;
		state.empty = false;
		if (isDocumentState())
			_printer.flush();
	}

}
