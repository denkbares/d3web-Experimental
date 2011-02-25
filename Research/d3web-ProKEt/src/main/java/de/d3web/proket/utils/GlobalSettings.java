/**
 * Copyright (C) 2010/2011 Chair of Artificial Intelligence and Applied
 * Informatics Computer Science VI, University of Wuerzburg
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

import de.d3web.proket.data.DialogStrategy;
import de.d3web.proket.data.DialogType;

/**
 * Class for storing and retrieving the global or default dialog settings.
 * Contains the base paths needed by the app, and default settings of dialogs.
 * 
 * @author Martina Freiberg
 * @created 11.10.2010
 */
public class GlobalSettings {

	/*
	 * Let DefaultSettings implement a Singleton template as only one instance
	 * is needed per program flow
	 */
	private static GlobalSettings instance = null;

	public static GlobalSettings getInstance() {
		if (instance == null) {
			return new GlobalSettings();
		}
		return instance;
	}

	private GlobalSettings() {
	}

	/* Paths */
	private String d3webSpecsPath = "/specs/d3web";
	private String prototypeSpecsPath = "/specs/prototypes";
	private String baseSpecsPath = "/specs/";

	private String baseTempPath = "/stringtemp";
	private String htmlTempPath = "/stringtemp/html";
	private String cssTempPath = "/stringtemp/css";

	private String proketPath = "de.d3web.proket.";
	private String d3webRendererPath = "de.d3web.proket.d3web.output.render.";
	private String rendererBasePath = "de.d3web.proket.output.render";

	private String applicationBasePath = "/applicationResources";


	/* File names */
	private String defaultKB = "defaultKB.jar"; // TODO
	private String defaultPrototypeXML = "defaultPrototype.xml"; // TODO


	/* Default dialog settings */
	private DialogType defaultDialogType = DialogType.DEFAULT;
	private DialogStrategy defaultDialogStrategy = DialogStrategy.DEFAULT;
	private String defaultCSSSpec = "default"; // TODO


	/* The getters for retrieving all those values */
	public String getProketPath() {
		return proketPath;
	}

	public String getD3webSpecsPath() {
		return d3webSpecsPath;
	}

	public String getPrototypeSpecsPath() {
		return prototypeSpecsPath;
	}

	public String getBaseSpecsPath() {
		return baseSpecsPath;
	}

	public String getDefaultKbFileName() {
		return defaultKB;
	}

	public String getDefaultPrototypeFileName() {
		return defaultPrototypeXML;
	}

	public DialogType getDefaultDialogType() {
		return defaultDialogType;
	}

	public DialogStrategy getDefaultDialogStrategy() {
		return defaultDialogStrategy;
	}

	public String getDefaultCSSSpec() {
		return defaultCSSSpec;
	}

	public String getBaseTempPath() {
		return baseTempPath;
	}

	public String getHtmlTempPath() {
		return htmlTempPath;
	}

	public String getCssTempPath() {
		return cssTempPath;
	}

	public String getRendererBasePath(){
		return rendererBasePath;
	}

	public String getApplicationBasePath() {
		return applicationBasePath;
	}

	public String getD3webRendererPath() {
		return d3webRendererPath;
	}
}
