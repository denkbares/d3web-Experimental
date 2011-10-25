/*
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

package de.knowwe.util;

/**
 * Some keywords of the Manchester OWL syntax.
 *
 * @author Stefan Mark
 * @created 17.10.2011
 */
public enum ManchesterSyntaxKeywords {

	ONTOLOGY("Ontology"),

	IMPORT("Import"),

	PREFIX("Prefix"),

	CLASS("Class"),

	OBJECT_PROPERTY("ObjectProperty"),

	DATA_PROPERTY("DataProperty"),

	INDIVIDUAL("Individual"),

	DATATYPE("Datatype"),

	ANNOTATION_PROPERTY("AnnotationProperty"),

	SOME("SOME"),

	ONLY("ONLY"),

	ONLYSOME("ONLYSOME"),

	MIN("MIN"),

	MAX("MAX"),

	EXACTLY("EXACTLY"),

	VALUE("VALUE"),

	AND("AND"),

	THAT("THAT"),

	OR("OR"),

	NOT("NOT"),

	INVERSE("INVERSE"),

	SELF("SELF"),

	SUBCLASS_OF("SubClassOf"),

	SUPERCLASS_OF("SuperClassOf"),

	EQUIVALENT_TO("EquivalentTo"),

	EQUIVALENT_CLASSES("EquivalentClasses"),

	EQUIVALENT_PROPERTIES("EquivalentProperties"),

	DISJOINT_WITH("DisjointWith"),

	INDIVIDUALS("Individuals"),

	DISJOINT_CLASSES("DisjointClasses"),

	DISJOINT_PROPERTIES("DisjointProperties"),

	DISJOINT_UNION_OF("DisjointUnionOf"),

	FACTS("Facts"),

	SAME_AS("SameAs"),

	SAME_INDIVIDUAL("SameIndividual"),

	DIFFERENT_FROM("DifferentFrom"),

	DIFFERENT_INDIVIDUALS("DifferentIndividuals"),

	MIN_INCLUSIVE_FACET(">="),

	MAX_INCLUSIVE_FACET("<="),

	MIN_EXCLUSIVE_FACET(">"),

	MAX_EXCLUSIVE_FACET("<"),

	TYPES("Types"),

	TYPE("Type"),

	ANNOTATIONS("Annotations"),

	DOMAIN("Domain"),

	RANGE("Range"),

	CHARACTERISTICS("Characteristics"),

	FUNCTIONAL("Functional"),

	INVERSE_FUNCTIONAL("InverseFunctional"),

	SYMMETRIC("Symmetric"),

	TRANSITIVE("Transitive"),

	REFLEXIVE("Reflexive"),

	IRREFLEXIVE("Irreflexive"),

	ASYMMETRIC("Asymmetric"),

	INVERSE_OF("InverseOf"),

	SUB_PROPERTY_OF("SubPropertyOf"),

	SUB_PROPERTY_CHAIN("SubPropertyChain"),

	HAS_KEY("HasKey");


	private String keyword = "";

	private ManchesterSyntaxKeywords(String keyword) {
		this.keyword = keyword;
	}

	public String getKeyword() {
		return this.keyword;
	}
}