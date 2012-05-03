package officeconverter.odt;

import org.jdom.Namespace;

public class NamespaceManager {
	public final static Namespace fo = Namespace.getNamespace("fo",
			"urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0");
	public final static Namespace text = Namespace.getNamespace("text",
			"urn:oasis:names:tc:opendocument:xmlns:text:1.0");
	public final static Namespace style = Namespace.getNamespace("style",
			"urn:oasis:names:tc:opendocument:xmlns:style:1.0");
	public final static Namespace office = Namespace.getNamespace("office",
			"urn:oasis:names:tc:opendocument:xmlns:office:1.0");
	public final static Namespace xlink = Namespace.getNamespace("xlink",
			"http://www.w3.org/1999/xlink");
	public final static Namespace draw = Namespace
			.getNamespace("urn:oasis:names:tc:opendocument:xmlns:drawing:1.0");

}
