/**
 * Copyright (C) 2013 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.d3web.settings;



/**
 * @author M. Freiberg @date Apr 2013
 */
public class GeneralDialogSettings {

    private static GeneralDialogSettings instance;

    public static GeneralDialogSettings getInstance() {

	if (instance == null) {
	    instance = new GeneralDialogSettings();
	}
	return instance;
    }

    private GeneralDialogSettings() {
    }

    // OWN = user choses filename, ANONYM = system generates random filename that gets encrypted, ANONYM_OWN = user choses basic filename which is encrypted by system
    public enum CaseSaveMode {

	OWN, ANONYM, ANONYM_OWN
    };

    // currently supported login modes: OFF = no login, USRDAT: based
    // on csv tailored textfile, DB: using SQL database connection
    public enum LoginMode {

	OFF, USRDAT, DB
    };
    
    
    
    
    private CaseSaveMode caseSaveMode;
    /*
     * TODO: make enum HERE! The login mode, needed for adding the chosen form
     * of login
     */
    private LoginMode loginMode;
    /*
     * The header / title of the dialog parsed from the d3web XML
     */
    private String header;
    private Boolean debug;
    /*
     * In case we want to internationalize
     */
    private String language = "en";

    public CaseSaveMode getCaseSaveMode() {
	return caseSaveMode;
    }

    public void setCaseSaveMode(CaseSaveMode caseSaveMode) {
	this.caseSaveMode = caseSaveMode;
    }

    public void setLoginMode(LoginMode login) {
	this.loginMode = login;
    }

    public LoginMode getLoginMode() {
	return this.loginMode;
    }

    public String getHeader() {
	return header;
    }

    public void setHeader(String header) {
	this.header = header;
    }

    public void setLanguage(String lang) {
	this.language = lang;
    }

    public String getLanguage() {
	return this.language;
    }

    public Boolean getDebug() {
	return debug;
    }

    public void setDebug(Boolean debug) {
	this.debug = debug;
    }
}
