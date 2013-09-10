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
package de.d3web.proket.data;

/**
 * The possible styles a dialog can implement
 *
 * @author Martina Freiberg @created 08.10.2010 TODO think about correct and
 * sufficient types
 */
public enum DialogType {
    
    /*
     * Pub consultation dialog or medical diagnosis clarification
     */
    CLARIFICATION ("Clarfication"),
    /*
     * e.g. in JuriSearch, Clarification modules
     */
    FREECHOICE ("Freechoice"),
    SINGLEFORM ("SingleForm"),
    /*
     * the JuriSearch Clarification style
     */
    ITREE ("ITree"),
     /*
     * The Standard form based dialog, only for documentatn, i.e. without
     * solution panel
     */
    STANDARD ("Standard"),
    /*
     * A consultation questionary, i.e. like Standard style but with solution
     * panel
     */
    QUESTIONARYCONS ("QuestionaryCons"),
    EURAHS("EuraHS");
    
    
    private String stringrep;

    private DialogType(String s) {
        stringrep = s;
    }

    @Override
    public String toString(){
        return this.stringrep;
    }
}
