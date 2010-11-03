/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.proket.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

/**
 * Managing global settings for ProKEt.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 */
public class ProKEtSettings {
	private static final ProKEtSettings INSTANCE = new ProKEtSettings();

	public static ProKEtSettings getInstance() {
		return INSTANCE;
	}

	private final boolean debug = false;
	private final String filename = "data/settings.xml";

	private Properties properties = null;

	private ProKEtSettings() {
		properties = new Properties();
		try {
			properties.loadFromXML(new FileInputStream(filename));
		} catch (InvalidPropertiesFormatException ex) {
			JOptionPane.showMessageDialog(null,
					"Das Format der settings.xml ist ungültig.");
		} catch (FileNotFoundException ex) {
			// no real error. We will create that file on first write access
			// then automatically.
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null,
					"IO error: " + ex.getLocalizedMessage());
		}
	}

	/**
	 * Retrieve a setting's value by name.
	 * 
	 * @param name
	 *            Name of the setting.
	 * @param defaultValue
	 *            Value to be used in case the setting was not set to a concrete
	 *            value
	 * @return Value of the setting if set, defaultValue otherwise
	 */
	public String get(String name, String defaultValue) {
		String result = properties.getProperty(name);
		if (result == null) {
			result = defaultValue;
		}
		if (debug) {
			// saving default
			set(name, result);
			save();
		}
		return result;
	}

	/*
	 * public Boolean getBoolean(String name, Boolean defaultValue) { String
	 * value = properties.getProperty(name); return StringUtils.getValue(value,
	 * defaultValue); }
	 */

	public int getInt(String name, int defaultValue) {
		try {
			return Integer.parseInt(get(name, String.valueOf(defaultValue)));
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

	/**
	 * Save the settings currently encapsulated in this object.
	 * 
	 * @return true on success, false otherwise
	 */
	public boolean save() {
		try {
			properties.storeToXML(new FileOutputStream(filename),
					"Settings for ReservationsSoftware");
		} catch (IOException ex) {
			Logger.getLogger(ProKEtSettings.class.getName()).log(Level.SEVERE,
					null, ex);
			return false;
		}
		return true;
	}

	public boolean set(String name, String value) {
		setQueue(name, value);
		return save();
	}

	/**
	 * Set a setting to a value, but don't write it to the settings file yet.
	 * Call the save method to finish a transaction.
	 * 
	 * @param name
	 *            Setting's name
	 * @param value
	 *            Setting's value
	 */
	public void setQueue(String name, String value) {
		if (debug) {
			System.out.println("Setting \"" + name + "\" to \"" + value + "\"");
		}
		properties.setProperty(name, value);
	}
}
