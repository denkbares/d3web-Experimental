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
 * @author Martina Freiberg @created 11.10.2010
 */
public class GlobalSettings {

    /*
     * Let DefaultSettings implement a Singleton template as only one instance
     * is needed per program flow
     */
    private static GlobalSettings instance = null;

    public static GlobalSettings getInstance() {
        if (instance == null) {
            instance = new GlobalSettings();
        }
        return instance;
    }

    private GlobalSettings() {
    }

    /*
     * Paths
     */
    private final String d3webSpecsPath = "/specs/d3web";
    private final String prototypeSpecsPath = "/specs/prototypes";
    private final String baseSpecsPath = "/specs/";
    private final String baseTempPath = "/stringtemp";
    private final String htmlTempPath = "/stringtemp/html";
    private final String cssTempPath = "/stringtemp/css";
    private final String proketPath = "de.d3web.proket.";
    private final String d3webRendererPath = "de.d3web.proket.d3web.output.render.";
    private final String rendererBasePath = "de.d3web.proket.output.render";
    private final String applicationBasePath = "/controlcenterResources";
    private final String resourcesPath = "resources/";
    /*
     * File names
     */
    private final String defaultKB = "defaultKB.jar"; // TODO
    private final String defaultPrototypeXML = "defaultPrototype.xml"; // TODO

    /*
     * Default dialog settings
     */
    private final DialogType defaultDialogType = DialogType.DEFAULT;
    private final DialogStrategy defaultDialogStrategy = DialogStrategy.DEFAULT;
    private final String defaultCSSSpec = "default"; // TODO

    /*
     * The folder, where persistence is stored, e.g., case-files
     */
    private String caseFolder = "";
    private String kbImgFolder = "";
    private String servletBasePath;
    private int localeIdent = 0;
    /*
     * Save log files
     */
    private String logBaseFolder = "";
    private String logSubFolder = "";
    
    private boolean initLog = false;
    private String questionCount = "";
    private String uegroup = "";
    private String uesystemtype = "";


    /*
     * The getters for retrieving all those values
     */
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

    public String getRendererBasePath() {
        return rendererBasePath;
    }

    public String getApplicationBasePath() {
        return applicationBasePath;
    }

    public String getD3webRendererPath() {
        return d3webRendererPath;
    }

    /*
     * Some values need to be set at runtime from outside
     */
    public void setCaseFolder(String folderPath) {
        caseFolder = folderPath;
    }

    public String getCaseFolder() {
        return caseFolder;
    }

    public void setKbImgFolder(String path) {
        kbImgFolder = path;
    }

    public String getKbImgFolder() {
        return kbImgFolder;
    }

    public void setServletBasePath(String servletBasePath) {
        this.servletBasePath = servletBasePath;
    }

    public String getServletBasePath() {
        return this.servletBasePath;
    }

    public void setLogBaseFolder(String basefolder) {
        logBaseFolder = basefolder;
    }

    public void setLogSubFolder(String subfolder){
        this.logSubFolder = subfolder;
    }
    
    public String getLogFolder() {
        if(!this.logBaseFolder.equals("") &&
                !this.logSubFolder.equals("")){
            return 
                    logBaseFolder + logSubFolder;
        } else {
            return this.logBaseFolder;
        }
    }

    public int getLocaleIdentifier() {
        return this.localeIdent;
    }

    public void setLocaleIdentifier(int localId) {
        this.localeIdent = localId;
    }

    public boolean initLogged() {
        return this.initLog;
    }

    public void setInitLogged(boolean iLog) {
        this.initLog = iLog;
    }

    // TODO: factor out to dialog/prototype settings
    public void setQuestionCount(String c) {
        this.questionCount = c;
    }

    public String getQuestionCount() {
        return this.questionCount;
    }


    public String getResourcesPath() {
        return this.resourcesPath;
    }
    
    public void setUEGroup(String group){
        this.uegroup = group;
    }
    
    public String getUEGroup(){
        return this.uegroup;
    }
    
    public void setUESystemType(String uestype){
        this.uesystemtype = uestype;
    }
    
    public String getUESystemType(){
        return this.uesystemtype;
    }
}
