package de.d3web.proket.d3web.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import de.d3web.core.knowledge.Resource;

public class AttachmentHandlingD3webUtils {

	public static String getTextfileContentsFromTextfileAttachment(Resource res)
			throws IOException {

		InputStream is = res.getInputStream();
		Writer writer = new StringWriter();

		if (is != null) {
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			}
			finally {
				is.close();
			}
		}
		return writer.toString();
	}
}
