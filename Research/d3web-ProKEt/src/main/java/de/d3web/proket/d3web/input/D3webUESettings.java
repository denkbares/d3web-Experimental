/**
 * Copyright (C) 2012 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.d3web.input;

import de.d3web.proket.d3web.ue.JSONLogger;

/**
 * Class for storing Usability Extension Settings, e.g. paths, properties etc
 * as defined in the d3web-specification XMLs.
 * TODO: check if we need to create such settings for each user or if it is
 * sufficient to have one web application wide instance for all users as with
 * d3web settings.
 * 
 * @author Martina Freiberg
 * @Date 25.08.2012
 */
public class D3webUESettings {
    
    private static String SEPARATOR = System.getProperty("file.separator");
    
    public enum UEQ {NONE, SUS, OWN}
    
    private JSONLogger logger = null;
    private String uegroupID = "";
    private boolean logging = false;
    private boolean feedbackform = false;
    private boolean study = false;
    private String analysisOutputPath = SEPARATOR;
    private String logfilesPath = SEPARATOR + "LOGS/";
    private UEQ uequestionnaire = UEQ.NONE;
    
    private static D3webUESettings instance;
    
    public static D3webUESettings getInstance(){
        if (instance==null){
            instance = new D3webUESettings();
        } return instance;
    }
    
    private  D3webUESettings(){
    }
    
    
    public boolean isFeedbackform() {
        return feedbackform;
    }

    public void setFeedbackform(boolean feedbackform) {
        this.feedbackform = feedbackform;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public boolean isStudy() {
        return study;
    }

    public void setStudy(boolean study) {
        this.study = study;
    }

    public String getUegroupID() {
        return uegroupID;
    }

    public void setUegroupID(String uegroupID) {
        this.uegroupID = uegroupID;
    }

    public String getAnalysisOutputPath() {
        return analysisOutputPath;
    }

    public void setAnalysisOutputPath(String analysisOutputPath) {
        this.analysisOutputPath = analysisOutputPath;
    }

    public String getLogfilesPath() {
        return logfilesPath;
    }

    public void setLogfilesPath(String logfilesPath) {
        this.logfilesPath = logfilesPath;
    }

    public UEQ getUequestionnaire() {
        return uequestionnaire;
    }

    public void setUequestionnaire(UEQ uequestionnaire) {
        this.uequestionnaire = uequestionnaire;
    }

    public void setLogger(JSONLogger logger) {
        this.logger = logger;
    }

    public JSONLogger getLogger() {
        return this.logger;
    }

    
    
    
}
