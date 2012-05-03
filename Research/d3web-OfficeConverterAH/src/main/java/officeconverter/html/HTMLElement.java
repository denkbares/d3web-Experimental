package officeconverter.html;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.jdom.Attribute;
import org.jdom.Namespace;
import org.jdom.Text;

import officeconverter.Config;
import officeconverter.odt.NamespaceManager;
import officeconverter.odt.Style;
import officeconverter.odt.StyleManager;

public class HTMLElement {

	public class Element {
		private String name;
		private String attribute;

		public Element(String name) {
			this.name = name;
			this.attribute = "";
		}

		public Element(String name, String attribute) {
			this.name = name;
			this.attribute = attribute;
		}

		public String getAttribute() {
			return attribute;
		}

		public void setAttribute(String attribute) {
			this.attribute = attribute;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private Config c;
	private List<Element> elements;

	public HTMLElement(Config c, org.jdom.Element element) {
		this.c = c;
		elements = new LinkedList<Element>();
		init(element);
	}

	private Element getElementName(org.jdom.Element elem) {
		String name = elem.getName().trim().toLowerCase();
		if ("h".equals(name)) {
			return new Element("p");
		} 		
		else if ("image".equals(name)) {
			Element ret = new Element("img");
			ret.setAttribute(
				" src=\"" + elem.getAttributeValue("href", NamespaceManager.xlink) + "\""
			);
			
			org.jdom.Element frame = elem.getParentElement();
			if (frame != null && "frame".equals(frame.getName())) {
				
				String clipping = StyleManager.getInstance().getStyle(frame, Style.STYLE_TYPE_CLIPPING);
				ret.setAttribute(
						ret.getAttribute() + " clip=\"" + clipping + "\"");
				
				String
					y = "0.00cm",
					width = "0.00cm",
					height = "0.00cm",
					zindex = "0.00cm"
				;
				for (int i = 0; i < frame.getAttributes().size(); i++) {
					String tmp = frame.getAttributes().get(i).toString();
					int tmp2 = tmp.length() - 2;
					if (tmp.contains("svg:y"))
						y = tmp.substring(19, tmp2);
					else if (tmp.contains("width"))
						width = tmp.substring(23, tmp2);
					else if (tmp.contains("height"))
						height = tmp.substring(24, tmp2);
					// since the z-index is not important we ignore it
					// otherwise the scaling string is never equal
//					else if (tmp.contains("draw:z-index"))
//						zindex = tmp.substring(26, tmp2);
				}
				String scaling = y + ", " + width + ", " + height + ", " + zindex + "cm";
				ret.setAttribute(
					ret.getAttribute() + " scaling(" + scaling + ")");
			}
			return ret;
		} else if ("table-row".equals(name)) {
			return new Element("tr");
		} else if ("table-cell".equals(name)) {
			return new Element("td");
		} else if ("p".equals(name)) {
			return new Element("p");
		} else if ("span".equals(name)) {
			if (elem.getContentSize() != 0 && elem.getContent(0) instanceof Text) {
				Namespace ns = NamespaceManager.text;
				Attribute stName = elem.getAttribute("style-name", ns);
				if (stName != null) {
					String st = stName.getValue();
					Style s = StyleManager.getInstance().styles.get(st);
					String superduper = s.getStyle(Style.STYLE_TYPE_SUPERDUPER);
					if (superduper != null && superduper.startsWith("super "))
						return new Element("sup");
					if (superduper != null && superduper.startsWith("sub "))
						return new Element("sub");
				}
			}
			return new Element("span");
		} else if ("table".equals(name)) {
			return new Element("table");
		} else if (c.isWithLists() && "list".equals(name)) {
			return new Element("ol");
		} else if (c.isWithLists() && "list-item".equals(name)) {
			return new Element("li");
		}

		return new Element("");
	}

	private void init(org.jdom.Element elem) {

		if ("p".equals(elem.getName()) && "".equals(elem.getTextTrim())
				&& elem.getChildren().isEmpty()) {
			elements.add(new Element("br"));
			return;
		}

		Element main = getElementName(elem);
		if ("".equals(main.getName()))
			return;
		elements.add(main);

		// Background
//		if (!"".equals(StyleManager.getBGColor(elem))) {
//			String bg = "";
//			bg = " style=\"background: " + StyleManager.getBGColor(elem) + "\"";
//			if ("span".equals(main.getName()))
//				main.setAttribute(bg);
//			else
//				elements.add(new Element("span", bg));
//		}

		if (StyleManager.isBold(elem))
			elements.add(new Element("b"));
		if (StyleManager.isItalic(elem))
			elements.add(new Element("i"));
		if (StyleManager.isStriked(elem))
			elements.add(new Element("strike"));
		if (StyleManager.isUnderlined(elem))
			elements.add(new Element("u"));
	}

	private boolean isEmptyTag(String tag) {
		return ("br".equalsIgnoreCase(tag));
	}

	public String getStartTag() {
		StringBuilder buffy = new StringBuilder();
		for (Element elem : elements) {
			buffy.append("<" + elem.getName() + elem.getAttribute());
			if (isEmptyTag((elem.getName()))) {
				buffy.append("/");
			}
			buffy.append(">");
		}
		return buffy.toString();
	}

	public String getEndTag() {
		String res = "";
		ListIterator<Element> it = elements.listIterator(elements.size());
		// Durchlaufe die Liste r�ckw�rts
		while (it.hasPrevious()) {
			String tag = it.previous().getName();
			if (!isEmptyTag(tag))
				res += "</" + tag + ">";

		}
		return res;
	}
}
