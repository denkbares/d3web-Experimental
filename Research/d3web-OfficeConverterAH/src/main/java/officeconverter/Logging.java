package officeconverter;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Logging {

	private static Logger logger;
	
	private static FileHandler logFileHandler;
	
	private static Formatter logFormatter = new Formatter() {
		@Override
		public String format(LogRecord rec) {
			String s = "";

			String t = "<b>" + rec.getLevel().getLocalizedName() + ":</b>";
			if (rec.getLevel().intValue() >= Level.WARNING.intValue())
				s += "<font color='red'>" + t + "</font>";
			else
				s += t;
			
			if (rec.getLevel().intValue() >= Level.INFO.intValue())
				s += "<br>";

			s += formatMessage(rec) +
				"<br>" + System.getProperty("line.separator");
			
			return s;
		}
	};
	
	public static Logger getLogger() {
		if (Logging.logger == null) {
			Logging.logger = Logger.getLogger(Logging.class.getPackage().getName());
			updateFormatters();
		}
		return Logging.logger;
	}
	
	public static void setLogFile(File file) throws Exception {
		if (logFileHandler != null)
			getLogger().removeHandler(logFileHandler);
		logFileHandler = new FileHandler(file.getAbsolutePath());
		if (logFormatter != null)
			logFileHandler.setFormatter(logFormatter);
		getLogger().addHandler(logFileHandler);
	}
	
	public static void setLogFormatter(Formatter f) {
		logFormatter = f;
		updateFormatters();
	}
	
	private static void updateFormatters() {
		for (Handler h : getLogger().getHandlers())
			h.setFormatter(logFormatter);
	}

}
