package officeconverter.odt;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


import officeconverter.odt.Style.StyleType;

import org.jdom.Element;

public class StyleManager {

	private static StyleManager instance;

	public /* was private */ Map<String, Style> styles;

	private StyleManager() {
		styles = new HashMap<String, Style>();
	}

	public synchronized static StyleManager getInstance() {
		if (instance == null) {
			instance = new StyleManager();
		}
		return instance;
	}

	public void addStyles(List<Element> styleElements) {
		for (Element styleElem : styleElements) {
			Style style = new Style(styleElem);
			styles.put(style.getName(), style);
		}
	}

	public boolean hasStyle(Element elem, StyleType styleType, String value) {
		String style = elem.getAttributeValue("style-name",
				NamespaceManager.text);
		if (style == null || "".equals(style))
			return false;
		return hasStyle(style, styleType, value);
	}

	private boolean hasStyle(String style, StyleType styleType, String value) {
		Style newStyle = styles.get(style);
		if (newStyle == null)
			return false;
		else if (newStyle.hasStyle(styleType, value))
			return true;
		else if (newStyle.hasParent())
			return hasStyle(newStyle.getParent_style_name(), styleType, value);
		else
			return false;
	}

	public String getStyle(Element elem, StyleType styleType) {
		String style = elem.getAttributeValue("style-name",
				NamespaceManager.text);
		if (style == null || "".equals(style)) {
			style = elem.getAttributeValue("style-name", NamespaceManager.draw);
		}
		if (style == null || "".equals(style))
			return "";
		return hasStyle(style, styleType);
	}

	private String hasStyle(String style, StyleType styleType) {
		Style newStyle = styles.get(style);
		if (!"".equals(newStyle.getStyle(styleType)))
			return newStyle.getStyle(styleType);
		else {
			if (newStyle.hasParent())
				hasStyle(newStyle.getParent_style_name(), styleType);
		}
		return "";
	}

	public static boolean isBold(Element elem) {
		return StyleManager.getInstance().hasStyle(elem,
				Style.STYLE_TYPE_WEIGHT, "bold");
	}

	public static boolean isItalic(Element elem) {
		return StyleManager.getInstance().hasStyle(elem,
				Style.STYLE_TYPE_FONT_STYLE, "italic");
	}

	public static boolean isStriked(Element elem) {
		return StyleManager.getInstance().hasStyle(elem,
				Style.STYLE_TYPE_CROSSING_OUT, "double");
	}

	public static boolean isUnderlined(Element elem) {
		return StyleManager.getInstance().hasStyle(elem,
				Style.STYLE_TYPE_UNDERLINE, "solid");
	}

	public static String getBGColor(Element elem) {
		return StyleManager.getInstance().getStyle(elem,
				Style.STYLE_TYPE_BACKGROUND);
	}

}
