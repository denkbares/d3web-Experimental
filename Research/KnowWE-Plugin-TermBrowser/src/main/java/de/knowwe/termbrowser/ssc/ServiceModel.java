/*
 * Copyright (C) 2014 denkbares GmbH, Germany
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

package de.knowwe.termbrowser.ssc;

/**
 * Created by jochenreutelshofer on 02.05.14.
 */
public class ServiceModel {

    public static final String hasConceptURI = "http://www.denkbares.com/ssc/ds#hasConcept";
    public static final String type = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    public static final String segmentURI = "http://denkbares.com/SemanticServiceCore/TextProcessing#Segment";
    public static final String relevantConceptURI = "http://www.denkbares.com/ssc/ds#hasRelevantConcept";
    public static final String hasScoreURI = "http://www.denkbares.com/ssc/ds#hasScore";
    public static final String pagesFromURI = "http://www.denkbares.com/ssc/ds#fromPage";
    public static final String pagesToURI = "http://www.denkbares.com/ssc/ds#toPage";
    public static final String hasResourceURI = "http://www.denkbares.com/ssc/ds#hasResource";
    public static final String hasTitleURI = "http://www.denkbares.com/ssc/ds#hasTitle";
	public static final String rdfsSubClassOf = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
    public static final String rdfsLabel = "http://www.w3.org/2000/01/rdf-schema#label";
    public static final String skosLabel = "http://www.w3.org/2004/02/skos/core#label";
    public static final String skosPrefLabel = "http://www.w3.org/2004/02/skos/core#prefLabel";
    public static final String skosAltLabel = "http://www.w3.org/2004/02/skos/core#altLabel";
    public static final String skosHiddenLabel = "http://www.w3.org/2004/02/skos/core#hiddenLabel";

    public static final String Component = "http://denkbares.com/SemanticServiceCore#Component";
    public static final String Function = "http://denkbares.com/SemanticServiceCore#Function";
	public static final String Concept = "http://denkbares.com/SemanticServiceCore#Concept";
    public static final String subComponentOf = "http://denkbares.com/SemanticServiceCore#isSubComponentOf";
    public static final String dependsOn = "http://denkbares.com/SemanticServiceCore#dependsOn";
    public static final String requiresComponent = "http://denkbares.com/SemanticServiceCore#requiresComponent";

	public static final String owl = "http://www.w3.org/2002/07/owl#";
	public static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";

    //public static final String LNS = "http://localhost:8080/KnowWE/Wiki.jsp?page=";
}
