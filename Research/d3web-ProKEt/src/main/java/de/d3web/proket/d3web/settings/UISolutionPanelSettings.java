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
package de.d3web.proket.d3web.settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing all UI Properties of a productive KBS as specified in the
 * XML, e.g. definition of CSS, header text etc etc
 * UISettings are parsed from the specification and are the same for each user
 * given that he uses the same dialog. They are distinct for each distinct
 * dialog however.
 * 
 * @author Martina Freiberg
 * @date 29/08/2012
 */
public class UISolutionPanelSettings {
 
    public enum SolutionRange{SINGLE, MULTIPLE}; 
    
    public enum Dynamics{STATIC, INTERACTIVE};
    
    public enum RatingGranularity{ABSTRACT, PRECISE};
    
    public enum SolutionSorting{ALPHABETICAL, CATEGORICAL, CATEGALPHA};
    
    public enum SolutionStructuring{LISTING, TABLE};
    
    public enum ExplanationType{NONE, TREEMAP, TABLE, CLARI, TEXTUAL, RULEGRAPH};
    
    public enum SolutionDepth{ALL, ESTABLISHED, SUGGESTED, EXCLUDED};
    
    
    
    private List solutionDepths;
    
    private List ratingGranularity;
    
    
    private ExplanationType explanationType;
    
    private SolutionSorting solutionSorting;
    
    private SolutionStructuring solutionStructuring;
    
    private SolutionRange solutionRange;
    
    private Dynamics dynamics;
    
    
    
    // singleton constructor stuff
    private static UISolutionPanelSettings instance = null;
    
    public static UISolutionPanelSettings getInstance() {
        if (instance == null) {
            instance = new UISolutionPanelSettings();
        }
        return instance;
    }

    private UISolutionPanelSettings() {
    }
    

     public void setExplanationType(ExplanationType type) {
        this.explanationType = type;
    }

    public ExplanationType getExplanationType() {
        return this.explanationType;
    }
    
     public SolutionSorting getSolutionSorting() {
        return solutionSorting;
    }

    public void setSolutionSorting(SolutionSorting solSorting) {
        this.solutionSorting = solSorting;
    }
    
     public void setSolutionDepths(List depth){
        this.solutionDepths = depth;
    }
    
    public List getSolutionDepths(){
        return this.solutionDepths;
    }

    public SolutionStructuring getSolutionStructuring() {
        return solutionStructuring;
    }

    public void setSolutionStructuring(SolutionStructuring solutionStructuring) {
        this.solutionStructuring = solutionStructuring;
    }

    public SolutionRange getSolutionRange() {
        return solutionRange;
    }

    public void setSolutionRange(SolutionRange solutionRange) {
        this.solutionRange = solutionRange;
    }

    public List getRatingGranularity() {
        return ratingGranularity;
    }

    public void setRatingGranularity(List ratingGranularity) {
        this.ratingGranularity = ratingGranularity;
    }

    public Dynamics getDynamics() {
        return dynamics;
    }

    public void setDynamics(Dynamics dynamics) {
        this.dynamics = dynamics;
    }

   
    
    
    public boolean getShowAbstractSolRating(){
        if(this.ratingGranularity.contains(
                RatingGranularity.ABSTRACT.toString())){
            return true;
        }
        
        return false;
    }
    
    public boolean getShowPreciseSolRating(){
        if(this.ratingGranularity.contains(
                RatingGranularity.PRECISE.toString())){
            return true;
        }
        
        return false;
    }
}
