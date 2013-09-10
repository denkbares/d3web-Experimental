/**
 * KnowWE Metatool
 * Copyright (C) 2011 Alex Legler
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package de.knowwe.metatool;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A parser context that is held during parsing and contains settings
 * and parser messages to exchange between the MetaTool and the caller
 * 
 * @author Alex Legler
 */
public class ParserContext {
	private List<ParserMessage> parserMessages;
	private Map<String, String> settings;
	
	public ParserContext() {
		parserMessages = new LinkedList<ParserMessage>();
		settings = new HashMap<String, String>();
	}
	
	public void addParserMessage(ParserMessage msg) {
		parserMessages.add(msg);
	}
	
	public List<ParserMessage> getParserMessages() {
		return Collections.unmodifiableList(parserMessages);
	}
	
	public List<ParserMessage> getParserMessages(ParserMessage.Type type) {
		List<ParserMessage> matches = new LinkedList<ParserMessage>();
		
		for (ParserMessage msg : getParserMessages()) {
			if (msg != null && msg.getType().equals(type)) {
				matches.add(msg);
			}
		}
		
		return Collections.unmodifiableList(matches);
	}
	
	public void setSetting(String settingName, String value) {
		settings.put(settingName, value);
	}
	
	/**
	 * Returns the value of a setting, or a default value
	 * @param settingName The setting to retrieve
	 * @param deflt Default value to use if it isn't set
	 * @return The setting, or null
	 */
	public String getSetting(String settingName, String deflt) {
		String value = settings.get(settingName);
		
		if (value == null && deflt != null) {
			return deflt;
		} else {
			return value;
		}
	}
	
	public static class ParserMessage {
		
		public enum Type {
			Warning, Error;
		}
		
		String message;
		Type type;
		int line;
		
		public ParserMessage(String message, Type type, int line) {
			this.message = message;
			this.type = type;
			this.line = line;
		}
		
		public ParserMessage(String message, Type type) {
			this(message, type, -1);
		}
		
		public String getMessage() {
			return message;
		}
		
		public Type getType() {
			return type;
		}
		
		public int getLine() {
			return line;
		}
	}
}
