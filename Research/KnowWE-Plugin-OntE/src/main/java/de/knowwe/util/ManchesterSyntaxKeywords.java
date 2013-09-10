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

import java.util.ArrayList;
import java.util.List;

import de.knowwe.kdom.manchester.frame.ClassFrame;
import de.knowwe.kdom.manchester.frame.DataPropertyFrame;
import de.knowwe.kdom.manchester.frame.IndividualFrame;
import de.knowwe.kdom.manchester.frame.ObjectPropertyFrame;

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

	SOME_("SOME_"),

	ONLY("ONLY"),

	ONLY_("ONLY_"),

	ONLYSOME("ONLYSOME"),

	MIN("MIN"),

	MIN_("MIN_"),

	MAX("MAX"),

	MAX_("MAX_"),

	EXACTLY("EXACTLY"),

	EXACTLY_("EXACTLY_"),

	VALUE("VALUE"),

	VALUE_("VALUE_"),

	AND("AND"),

	THAT("THAT"),

	OR("OR"),

	NOT("NOT"),

	INVERSE("INVERSE"),

	SELF("SELF"),

	SUBCLASS_OF("SubClassOf", ClassFrame.class),

	SUPERCLASS_OF("SuperClassOf"),

	EQUIVALENT_TO("EquivalentTo", ClassFrame.class, ObjectPropertyFrame.class, DataPropertyFrame.class),

	EQUIVALENT_CLASSES("EquivalentClasses"),

	EQUIVALENT_PROPERTIES("EquivalentProperties"),

	DISJOINT_WITH("DisjointWith", ClassFrame.class, ObjectPropertyFrame.class, DataPropertyFrame.class),

	INDIVIDUALS("Individuals"),

	DISJOINT_CLASSES("DisjointClasses"),

	DISJOINT_PROPERTIES("DisjointProperties"),

	DISJOINT_UNION_OF("DisjointUnionOf", ClassFrame.class),

	FACTS("Facts", IndividualFrame.class),

	SAME_AS("SameAs", IndividualFrame.class),

	SAME_INDIVIDUAL("SameIndividual"),

	DIFFERENT_FROM("DifferentFrom", IndividualFrame.class),

	DIFFERENT_INDIVIDUALS("DifferentIndividuals"),

	MIN_INCLUSIVE_FACET(">="),

	MAX_INCLUSIVE_FACET("<="),

	MIN_EXCLUSIVE_FACET(">"),

	MAX_EXCLUSIVE_FACET("<"),

	TYPES("Types", IndividualFrame.class),

	TYPE("Type"),

	ANNOTATIONS("Annotations", ClassFrame.class, IndividualFrame.class, ObjectPropertyFrame.class, DataPropertyFrame.class),

	DOMAIN("Domain", ObjectPropertyFrame.class, DataPropertyFrame.class),

	RANGE("Range", ObjectPropertyFrame.class, DataPropertyFrame.class),

	CHARACTERISTICS("Characteristics", ObjectPropertyFrame.class, DataPropertyFrame.class),

	FUNCTIONAL("Functional"),

	INVERSE_FUNCTIONAL("InverseFunctional"),

	SYMMETRIC("Symmetric"),

	TRANSITIVE("Transitive"),

	REFLEXIVE("Reflexive"),

	IRREFLEXIVE("Irreflexive"),

	ASYMMETRIC("Asymmetric"),

	INVERSE_OF("InverseOf", ObjectPropertyFrame.class, DataPropertyFrame.class),

	SUB_PROPERTY_OF("SubPropertyOf", ObjectPropertyFrame.class, DataPropertyFrame.class),

	SUB_PROPERTY_CHAIN("SubPropertyChain", ObjectPropertyFrame.class),

	HAS_KEY("HasKey", ClassFrame.class);

	private String keyword = "";

	private List<Class<?>> context = null;


	private ManchesterSyntaxKeywords(String keyword) {
		this(keyword, null, null);
	}

	private ManchesterSyntaxKeywords(String keyword, Class<?>... context) {
		this.keyword = keyword;
		this.context = new ArrayList<Class<?>>();

		for (int i = 0; i < context.length; i++) {
			if (context[i] != null) {
				this.context.add(context[i]);
			}
		}
	}

	public String getKeyword() {
		return this.keyword;
	}

	public boolean inContext(Class<?> cls) {

		for (Class<?> clazz : context) {
			if (cls.equals(clazz)) {
				return true;
			}
		}
		return false;
	}
}
