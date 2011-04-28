package de.d3web.proket.d3web.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

public class Base64CoDec {

	private final static DateFormat MY_DF = new SimpleDateFormat("yyyyMMddHHmmss");
	private final static Random MY_R = new Random();
	private final static String MY_S = new String();

	private final static Base64 MY_BASE = new Base64();

	public static String getCode() {
		String res = "";

		// we start with date
		String base = MY_DF.format(new Date());

		// permutate
		char[] basePerm = new char[base.length()];
		char[] permPos = new char[base.length()];
		for (int i = 0; i < base.length(); i++) {
			while (true) {
				int r = MY_R.nextInt(base.length());
				if (basePerm[r] == 0) {
					permPos[i] = (char) r;
					basePerm[r] = base.charAt(i);
					break;
				}
			}
		}

		// base 64
		String s1 = MY_BASE.encodeAsString(new String(permPos).getBytes());
		String s2 = MY_BASE.encodeAsString(new String(basePerm).getBytes());

		// interweave
		for (int i = 0; i < s1.length(); i++) {
			res += s1.charAt(i);
			res += s2.charAt(i);
		}

		return res.substring(0, res.length() - 2);
	}

	public static Date getDate(String code) throws IOException {

		// de-interweave
		String s1 = "";
		String s2 = "";
		for (int i = 0; i < code.length(); i += 2) {
			s1 += code.charAt(i);
			s2 += code.charAt(i + 1);
		}

		// de-base64
		s1 = new String(MY_BASE.decode(s1 + "="));
		s2 = new String(MY_BASE.decode(s2 + "="));

		// de-permutate
		char[] parse = new char[s2.length()];
		for (int i = 0; i < s1.length(); i++) {
			int pos = (int) s1.charAt(i);
			parse[i] = s2.charAt(pos);
		}

		// parse date
		Date res = null;
		try {
			res = MY_DF.parse(new String(parse));
		} catch (ParseException e) {
			// no valid code
		}

		return res;

	}

	public static String getPlainString(String code) throws IOException {
		String s = new String(Base64.decodeBase64(code.getBytes()));
		return s;
	}

	public static void main(String[] args) throws IOException {
		String s = getCode();

		System.out.println(s);
		System.out.println(getDate(s));
	}

}
