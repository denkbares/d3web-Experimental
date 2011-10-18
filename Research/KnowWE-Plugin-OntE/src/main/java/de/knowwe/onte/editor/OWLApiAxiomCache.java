/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.onte.editor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.semanticweb.owlapi.model.OWLObject;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;

/**
 *
 *
 * @author Stefan Mark
 * @created 16.10.2011
 */
public class OWLApiAxiomCache {

	public static final String STORE_CACHE = "cache";
	public static final String STORE_EXPLANATION = "explanation";

	/**
	 * Stores witch section in an article is responsible for a certain OWLAxiom.
	 * This can be used to map explanations to the according article and vice
	 * versa.
	 */
	private final WeakHashMap<Section<? extends Type>, Set<OWLObject>> cache;

	/**
	 * Stores for unsat concepts which axioms and section are responsible.
	 */
	private final WeakHashMap<Section<? extends Type>, Set<OWLObject>> explanations;

	/**
	 * The instance of the {@link OWLApiAxiomCache}, implemented as SingleTon to
	 * assure only one cache exists.
	 */
	private static OWLApiAxiomCache instance = null;

	/**
	 * Constructor. Initializes the {@link OWLApiAxiomCache}.
	 */
	private OWLApiAxiomCache() {
		this.cache = new WeakHashMap<Section<? extends Type>, Set<OWLObject>>();
		this.explanations = new WeakHashMap<Section<? extends Type>, Set<OWLObject>>();
	}

	/**
	 * Simple instantiation function (see Singleton pattern).
	 *
	 * @created 17.10.2011
	 * @return {@link OWLApiAxiomCache}
	 */
	public static synchronized OWLApiAxiomCache getInstance() {
		if (instance == null) {
			instance = new OWLApiAxiomCache();
		}
		return instance;
	}

	/**
	 * Returns the stored {@link OWLObject}s for a given section.
	 *
	 * @created 17.10.2011
	 * @param Section<? extends Type> section
	 * @param String type
	 * @return Set<OWlObject> The found objects
	 */
	public Set<OWLObject> getStoredObjects(Section<? extends Type> section, String type) {
		WeakHashMap<Section<? extends Type>, Set<OWLObject>> tmp = getStore(type);

		if (tmp.containsKey(section)) {
			return tmp.get(section);
		}
		return Collections.emptySet();
	}

	/**
	 * Adds a mapping between a section of the KDOm and an OWL axiom to the
	 * axiom cache. If an argument is NULL an {@link IllegalArgumentException}
	 * is thrown.
	 *
	 * @created 17.10.2011
	 * @param Section<? extends Type> section
	 * @param OWLAxiom axiom
	 */
	public void addToCache(Section<? extends Type> section, OWLObject object) {

		if (section == null || object == null) {
			throw new IllegalArgumentException("OWLApiAxiomCache.addToCache: Arguments cannot be null!");
		}

		if(cache.get(section) != null) {
			cache.get(section).add(object);
		}
		else {
			Set<OWLObject> objects = new HashSet<OWLObject>();
			objects.add(object);
			cache.put(section, objects);
		}
	}
	/**
	 * Adds a mapping between a section of the KDOm and an OWL axiom to the
	 * axiom cache. If an argument is NULL an {@link IllegalArgumentException}
	 * is thrown.
	 *
	 * @created 17.10.2011
	 * @param Section<? extends Type> section
	 * @param OWLAxiom axiom
	 */
	public void addToExplanation(Section<? extends Type> section, OWLObject object) {

		if (section == null || object == null) {
			throw new IllegalArgumentException(
					"OWLApiAxiomCache.addToExplanation: Arguments cannot be null!");
		}

		if (explanations.get(section) != null) {
			explanations.get(section).add(object);
		}
		else {
			Set<OWLObject> objects = new HashSet<OWLObject>();
			objects.add(object);
			explanations.put(section, objects);
		}
	}
	/**
	 * Look up the given {@link OWLObject} in the cache. if found, return the
	 * section the {@link OWLObject} belongs to, otherwise NULL.
	 *
	 * @created 17.10.2011
	 * @param object
	 * @return
	 */
	public Section<? extends Type> lookUpSection(OWLObject object, String type) {

		WeakHashMap<Section<? extends Type>, Set<OWLObject>> nodes = getStore(type);

		for (Section<? extends Type> section : nodes.keySet()) {
			Set<OWLObject> objects = nodes.get(section);
			for (OWLObject owlObject : objects) {
				if (owlObject.equals(object)) {
					return section;
				}
			}
		}
		return null;
	}
	/**
	 * Look up the given {@link OWLObject} in the cache. if found, return the
	 * section the {@link OWLObject} belongs to, otherwise NULL.
	 *
	 * @created 17.10.2011
	 * @param object
	 * @return
	 */
	public Section<? extends Type> lookUpSectionPerID(String sectionID, String type) {
		WeakHashMap<Section<? extends Type>, Set<OWLObject>> nodes = getStore(type);

		for (Section<? extends Type> section : nodes.keySet()) {
			if (section.getID().equals(sectionID)) {
				return section;
			}
		}
		return null;
	}

	private WeakHashMap<Section<? extends Type>, Set<OWLObject>> getStore(String type) {
		if (type.equals(STORE_EXPLANATION)) {
			return explanations;
		}
		else {
			return cache;
		}
	}
}
