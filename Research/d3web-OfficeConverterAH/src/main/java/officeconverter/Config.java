package officeconverter;

import java.util.ResourceBundle;

public class Config {
	
	private final static String DEFAULT_CONVERSION_HTML_SUFFIX = ".html";
	private final static boolean DEFAULT_CONVERT_CHARACTER_ENTITIES = false;

	public final static String DEFAULT_ENCODING = "UTF-8";
	private final static String PICTURES_SUBDIR = "pictures";

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
	
	private String htmlSuffix;
	private boolean convertCharacterEntities;
	private String encoding;
	private String embeddedObjectsDir;
	private boolean withLists = false;
	private boolean withImageScaling = false;
	private boolean leaveUmlauts = false;

	public Config(String htmlSuffix, Boolean convertCharacterEntities, String encoding, String embeddedObjectsDir) {
		this.htmlSuffix = htmlSuffix == null ? DEFAULT_CONVERSION_HTML_SUFFIX : htmlSuffix;
		this.convertCharacterEntities = convertCharacterEntities == null ? DEFAULT_CONVERT_CHARACTER_ENTITIES : convertCharacterEntities;
		this.encoding = encoding == null ? DEFAULT_ENCODING : encoding;
		this.embeddedObjectsDir = embeddedObjectsDir == null ? PICTURES_SUBDIR : embeddedObjectsDir;
	}
	
	public Config(String htmlSuffix, Boolean convertCharacterEntities, String encoding, String embeddedObjectsDir, boolean withLists, boolean withImageScaling, boolean leaveUmlauts) {
		this(htmlSuffix, convertCharacterEntities,encoding, embeddedObjectsDir);
		this.withLists = withLists;
		this.withImageScaling = withImageScaling;
		this.leaveUmlauts = leaveUmlauts;
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

}
