/**
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.d3web.ue.analyze;

/**
 * Basic class for analyzing JSON content Operating on multiple files.
 *
 * @author Martina Freiberg @date 28/11/2011
 */
public class JSONGlobalAnalyzer extends JSONAnalyzer {

    public JSONGlobalAnalyzer(String uppermostdir) {
        super(uppermostdir);
    }
    
    /*
     * Retrieve the total number of stored cases.
     * This is not simply the total number of stored JSON logfiles, but we 
     * have to factor out resumed cases and count those as one... (TODO?!)
     * 
     * @return the number of cases
     * 
     */
    public int getTotalNumberOfCases() {
        return (jsonreader.retrieveAllLogfilesAsJSON(getRootDir()).size());
    }
    
    /**
     * Retrieve the total number of successful cases.
     * Thereby, a successful case means cases, where all questions had been
     * answered and thus start and end values of time are provided
     * 
     * @return the number of successful cases
     */
    public int getTotalNumberOfSuccessfulCases(){
        return 0;
    }
    
    
    public static void main(String[] args){
        JSONGlobalAnalyzer globa = new JSONGlobalAnalyzer("DEFAULT-DATA");
        System.out.println(globa.getTotalNumberOfCases());
    }
}
