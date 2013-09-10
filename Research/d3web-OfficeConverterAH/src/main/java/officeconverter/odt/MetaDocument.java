package officeconverter.odt;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class MetaDocument {
	Document doc;

	private String META_GENERATOR;
	private String META_INITIAL_CREATOR;
	private String META_CREATION_DATE;
	private String DC_CREATOR;
	private String DC_DATE;
	private String DC_LANGUAGE;

	public MetaDocument(Document doc) {
		this.doc = doc;
		init();
	}

	private void init() {
		Namespace office = doc.getRootElement().getNamespace("office");
		Namespace meta = doc.getRootElement().getNamespace("meta");
		Namespace dc = doc.getRootElement().getNamespace("dc");

		Element office_meta = doc.getRootElement().getChild("meta", office);
		META_GENERATOR = office_meta.getChildTextNormalize("generator", meta);
		META_INITIAL_CREATOR = office_meta.getChildTextNormalize(
				"initial-creator", meta);
		META_CREATION_DATE = office_meta.getChildTextNormalize("creation-date",
				meta);
		DC_CREATOR = office_meta.getChildTextNormalize("creator", dc);
		DC_DATE = office_meta.getChildTextNormalize("date", dc);
		DC_LANGUAGE = office_meta.getChildTextNormalize("language", dc);

	}

	public String getDC_CREATOR() {
		return DC_CREATOR;
	}

	public void setDC_CREATOR(String dc_creator) {
		DC_CREATOR = dc_creator;
	}

	public String getDC_DATE() {
		return DC_DATE;
	}

	public void setDC_DATE(String dc_date) {
		DC_DATE = dc_date;
	}

	public String getDC_LANGUAGE() {
		return DC_LANGUAGE;
	}

	public void setDC_LANGUAGE(String dc_language) {
		DC_LANGUAGE = dc_language;
	}

	public String getMETA_CREATION_DATE() {
		return META_CREATION_DATE;
	}

	public void setMETA_CREATION_DATE(String meta_creation_date) {
		META_CREATION_DATE = meta_creation_date;
	}

	public String getMETA_GENERATOR() {
		return META_GENERATOR;
	}

	public void setMETA_GENERATOR(String meta_generator) {
		META_GENERATOR = meta_generator;
	}

	public String getMETA_INITIAL_CREATOR() {
		return META_INITIAL_CREATOR;
	}

	public void setMETA_INITIAL_CREATOR(String meta_initial_creator) {
		META_INITIAL_CREATOR = meta_initial_creator;
	}

	public String toString() {
		return "META_GENERATOR" + getMETA_GENERATOR() + "#DC_CREATOR"
				+ getDC_CREATOR();
	}

}
