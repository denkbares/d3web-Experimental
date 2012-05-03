package officeconverter.odt;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

public class StylesDocument {
	Document doc;

	public StylesDocument(Document doc) {
		this.doc = doc;
		init();
	}

	private void init() {
		StyleManager.getInstance().addStyles(
				(List<Element>)
				doc.getRootElement()
						.getChild("styles", NamespaceManager.office)
						.getChildren("style", NamespaceManager.style));

	}

}
