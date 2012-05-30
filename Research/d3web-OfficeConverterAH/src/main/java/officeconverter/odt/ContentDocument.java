package officeconverter.odt;


import java.util.List;


import officeconverter.Config;
import officeconverter.html.HTMLElement;
import officeconverter.utils.Unicode2Html;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;


public class ContentDocument {
	Config c;
	Document doc;

	Element scripts;
	Element font_face_decls;
	Element body;
	Element text;

	String html;

	public ContentDocument(Document doc, Config c) {
		this.doc = doc;
		this.c = c;
		init();
	}

	private void init() {

		Element docRoot = doc.getRootElement();
		scripts = docRoot.getChild("scripts", NamespaceManager.office);
		font_face_decls = docRoot.getChild("font-face-decls",
				NamespaceManager.office);
		StyleManager.getInstance().addStyles(
				docRoot.getChild("automatic-styles", NamespaceManager.office)
						.getChildren("style", NamespaceManager.style));
		body = docRoot.getChild("body", NamespaceManager.office);
		this.text = body.getChild("text", NamespaceManager.office);

		List<Element> elements = this.text.getContent();
		StringBuilder buffy = new StringBuilder();
		convert(elements, buffy);
		html = buffy.toString();
	}

	private String recode(String html) {
		try {
			return Unicode2Html.getHtmlStringWithCheckOnISOControls(html);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String convert(List<Element> elements, StringBuilder buffy) {
		for (Object elem : elements) {
			if (elem instanceof Text) {
				buffy.append(recode(((Text) elem).getText()));
			} else if (elem instanceof Element) {
				HTMLElement htmlElement = new HTMLElement(c, (Element) elem);
				buffy.append(htmlElement.getStartTag());
				if (((Element) elem).getContent() == null || ((Element) elem).getContent().isEmpty()) {
					buffy.append(recode(((Element) elem).getTextTrim()));
				} else {
					convert(((Element) elem).getContent(), buffy);
				}
				buffy.append(htmlElement.getEndTag());
			}
		}

		return "";
	}

	public String getHtml() {
		return html;
	}

}
