package officeconverter;

import java.util.ResourceBundle;

public class Config {
	
	// ----------------------------------------------------------------

	private static String serviceHost;
	private static int servicePort;
	private static String serviceProgramDir;
	private static long serviceWaitStartDelayMS;

	static {
		ResourceBundle bundle = ResourceBundle.getBundle("officeconverter");

		serviceHost = bundle.getString("openOfficeServerHost");
		servicePort = Integer.valueOf(bundle.getString("openOfficeServerPort"));
		serviceProgramDir = bundle.getString("openOffice2Dir");
		serviceWaitStartDelayMS = Long.parseLong(bundle.getString("openOfficeServerWaitStartDelayMS"));
	}

	public static String getServiceHost() {
		return serviceHost;
	}

	public static int getServicePort() {
		return servicePort;
	}

	public static String getServiceProgramDir() {
		return serviceProgramDir;
	}

	public static long getServiceWaitStartDelayMS() {
		return serviceWaitStartDelayMS;
	}
	
	// ----------------------------------------------------------------
	
	private String htmlSuffix = ".html";
	private String encoding = "UTF-8";
	private String embeddedObjectsDir = "pictures";
	private boolean convertCharacterEntities = false;
	private boolean withLists = false;
	private boolean withImageScaling = false;
	private boolean leaveUmlauts = false;
	private boolean withHeadlineDepths = false;

	public Config() { /* use setters */ }

	public Config setHtmlSuffix(String htmlSuffix) {
		this.htmlSuffix = htmlSuffix;
		return this;
	}

	public Config setConvertCharacterEntities(boolean convertCharacterEntities) {
		this.convertCharacterEntities = convertCharacterEntities;
		return this;
	}

	public Config setEncoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public Config setEmbeddedObjectsDir(String embeddedObjectsDir) {
		this.embeddedObjectsDir = embeddedObjectsDir;
		return this;
	}

	public Config setWithLists(boolean withLists) {
		this.withLists = withLists;
		return this;
	}

	public Config setWithImageScaling(boolean withImageScaling) {
		this.withImageScaling = withImageScaling;
		return this;
	}

	public Config setLeaveUmlauts(boolean leaveUmlauts) {
		this.leaveUmlauts = leaveUmlauts;
		return this;
	}

	public Config setWithHeadlineDepths(boolean withHeadlineDepths) {
		this.withHeadlineDepths = withHeadlineDepths;
		return this;
	}

	public String getHtmlSuffix() {
		return htmlSuffix;
	}

	public boolean isConvertCharacterEntities() {
		return convertCharacterEntities;
	}
	
	public boolean leaveUmlauts() {
		return leaveUmlauts;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getEmbeddedObjectsDir() {
		return embeddedObjectsDir;
	}
	
	public boolean isWithLists() {
		return withLists;
	}

	public boolean isWithImageScaling() {
		return withImageScaling;
	}

	public boolean isWithHeadlineDepths() {
		return withHeadlineDepths;
	}

}
