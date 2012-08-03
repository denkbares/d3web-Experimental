package de.knowwe.usersupport.html;

import de.uniwue.abstracttools.xml.XmlElement;
import de.uniwue.abstracttools.xml.XmlTree;

public class HtmlDashTreeWikiTextConverter {

	public String convertFromHtml(XmlTree html) {
		XmlElement root = html.getRootElement();
		StringBuilder sb = new StringBuilder();
		writeHtmlElement(root, sb);
		return sb.toString();
	}

	private void writeHtmlElement(XmlElement e, StringBuilder o) {
		while (e != null) {
			String tag = e.getQName().trim();
			if (tag.matches("[hH][0-9]+")) {
				e = writeDashTree(e, o);
			}
			else if (tag.matches("[oO][lL]")) {
				e = writeOrderedList(e, o);
			}
			else if (tag.matches("[uU][lL]")) {
				e = writeUnOrderedList(e, o);
			}
			else if (tag.matches("[pP]")) {
				e = writeParagraph(e, o);
			}
			else if (tag.matches("[bB][rR]")) {
				e = writeLineBreak(e, o);
			}
			else e = handleUnknownTag(e, o);
		}
	}

	private XmlElement writeDashTree(XmlElement e, StringBuilder o) {
		return null;
	}

	private XmlElement writeOrderedList(XmlElement e, StringBuilder o) {
		return null;
	}

	private XmlElement writeUnOrderedList(XmlElement e, StringBuilder o) {
		return null;
	}

	private XmlElement writeParagraph(XmlElement e, StringBuilder o) {
		return null;
	}

	private XmlElement writeLineBreak(XmlElement e, StringBuilder o) {
		return null;
	}

	private XmlElement handleUnknownTag(XmlElement e, StringBuilder o) {
		return null;
	}
}
