package officeconverter.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class DiskUtils {

	public static void copyFile(File in, File out) throws Exception {
		FileChannel sourceChannel = new FileInputStream(in).getChannel();
		FileChannel destinationChannel = new FileOutputStream(out).getChannel();
		sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		// or
		// destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		sourceChannel.close();
		destinationChannel.close();
	}
	
	public static byte[] loadPageFromDisk(String file) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try {
			URL url = new URL(file);
			InputStream in = url.openStream();
			int len;
			byte b[] = new byte[100];
			while ((len = in.read(b)) != -1) {
				byteOut.write(b, 0, len);
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteOut.toByteArray();
	}

	public static void savePageToDisk(File file, String text,
			String encoding) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), encoding));
			out.write(text);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void savePageToDisk(String fileName, byte[] text) {
		try {
			OutputStream out = new BufferedOutputStream(new FileOutputStream(
					fileName));
			for (int i = 0; i < text.length; i++) {
				out.write(text[i]);
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Document loadXMLFile(String filename) {
		try {
			File file = new File(filename);
			SAXBuilder parser = new SAXBuilder();
			return parser.build(file);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.getLocalizedMessage();
		}
		return null;

	}
}
