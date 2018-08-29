/*
 * Copyright (C) 2014 think-further.de
 */
package de.knowwe.defi.provider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.wiki.WikiPage;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.providers.VersioningFileProvider;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * VersioningFileProvider that encrypts the data before persisting them.
 *
 * @author Sebastian Furth
 * @created 07.11.16
 */
public class CryptoVersioningFileProvider extends VersioningFileProvider {

	private static final String CRYPT_START = "--- crypto ----\n\n";

	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String KEY = "ICD-Crypt-Key_UNSECURE";
	private static final BASE64Encoder ENCODER = new BASE64Encoder();
	private static BASE64Decoder DECODER = new BASE64Decoder();

	@Override
	public synchronized String getPageText(String page, int version) throws ProviderException {
		String pageText = super.getPageText(page, version);
		if (pageText.startsWith(CRYPT_START)) {
			pageText = base64decode(pageText.substring(CRYPT_START.length()));
			return xorMessage(pageText, KEY);
		}
		return pageText;
	}

	@Override
	public synchronized void putPageText(WikiPage page, String text) throws ProviderException {
		text = xorMessage(text, KEY);
		super.putPageText(page, CRYPT_START + base64encode(text));
	}

	private String base64encode(String text) {
		try {
			return ENCODER.encode(text.getBytes(DEFAULT_ENCODING));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	private String base64decode(String text) {
		try {
			return new String(DECODER.decodeBuffer(text), DEFAULT_ENCODING);
		} catch (IOException e) {
			return null;
		}
	}

	private static String xorMessage(String message, String key) {
		try {
			if (message == null || key == null) return null;

			char[] keys = key.toCharArray();
			char[] mesg = message.toCharArray();

			int ml = mesg.length;
			int kl = keys.length;
			char[] newmsg = new char[ml];

			for (int i = 0; i < ml; i++) {
				newmsg[i] = (char)(mesg[i] ^ keys[i % kl]);
			}
			return new String(newmsg);
		} catch (Exception e) {
			return null;
		}
	}

}
