package officeconverter.odt;

import org.jdom.Element;
import org.jdom.Namespace;

public class Style {
	
	Element style;
	String name;
	String parent_style_name;

	public static class StyleType {

		private String nodeName = null;

		private String attributeName = null;
		private Namespace attributNamespace = null;

		StyleType(String nodeName, String attributeName,
				Namespace attributNamespace) {
			super();
			this.nodeName = nodeName;
			this.attributeName = attributeName;
			this.attributNamespace = attributNamespace;
		}

	}

	public final static StyleType STYLE_TYPE_WEIGHT = new StyleType(
			"text-properties", "font-weight", NamespaceManager.fo); // bold

	public final static StyleType STYLE_TYPE_FONT_STYLE = new StyleType(
			"text-properties", "font-style", NamespaceManager.fo); // italic

	public final static StyleType STYLE_TYPE_UNDERLINE = new StyleType(
			"text-properties", "text-underline-style", NamespaceManager.style); // solid

	public final static StyleType STYLE_TYPE_BACKGROUND = new StyleType(
			"text-properties", "background-color", NamespaceManager.fo); // #ff0000

	public final static StyleType STYLE_TYPE_CROSSING_OUT = new StyleType(
			"text-properties", "text-line-through-type", NamespaceManager.style); // double
	
	public final static StyleType STYLE_TYPE_SUPERDUPER = new StyleType(
			"text-properties", "text-position", NamespaceManager.style); // super XX%

	/**
	 * clipping information associated to an image
	 */
	public final static StyleType STYLE_TYPE_CLIPPING = new StyleType(
			"graphic-properties", "clip", NamespaceManager.fo);
	// fo:clip="rect(5.375cm
	// 2.944cm
	// 5.909cm
	// 3.447cm)"

	public Style(Element style) {
		this.style = style;
		init();
	}

	private void init() {
		name = style.getAttribute("name", NamespaceManager.style).getValue();
	}

	public String getName() {
		if (name == null || "".equals(name)) {
			throw new RuntimeException("Style has no name!");
		}
		return name;
	}

	public String getParent_style_name() {
		return parent_style_name;
	}

	public boolean hasParent() {
		return ((parent_style_name != null) && (!"".equals(parent_style_name)));
	}

	public boolean hasStyle(StyleType styleType, String value) {
		Element child = style.getChild(styleType.nodeName,
				NamespaceManager.style);
		if (child == null) {
			return false;
		}
		String ret = child.getAttributeValue(styleType.attributeName,
				styleType.attributNamespace, "");
		if ("".equals(ret)) {
			return false;
		}
		return (ret.equals(value));

	}

	public String getStyle(StyleType styleType) {
		Element child = style.getChild(styleType.nodeName,
				NamespaceManager.style);
		if (child == null) {
			return "";
		}
		String ret = child.getAttributeValue(styleType.attributeName,
				styleType.attributNamespace, "");
		if ("".equals(ret)) {
			return "";
		}
		return (ret);
	}

}
