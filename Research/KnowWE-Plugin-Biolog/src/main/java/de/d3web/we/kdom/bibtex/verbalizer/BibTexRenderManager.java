/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.we.kdom.bibtex.verbalizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Adaption of the VerbalisationManager from d3web-verbalizer for Bibtex entries ..
 * @author fhaupt
 * @date nov 09
 * 
 */
public class BibTexRenderManager {

	//the standard priorities to choose, if several verbalizers could perform the same rendering
	public static final int PRIORITY_HIGH = 1000;
	public static final int PRIORITY_MEDIUM = 500;
	public static final int PRIORITY_LOW = 100;
	private static final int PRIORITY_MIN = 0;
	
	private static String[] specialStrings =  {"\n", "\r",
		".", ",", ";", "!", "@", "|", "#", "~",
		"{", "}", "[", "]", "(", ")", 
		"<", ">", "=", 
		"-", "+", "*", "/",
		"WENN", "IF",
		"DANN", "THEN",
		"UND", "AND",
		"ODER", "OR",
		"NICHT", "NOT",
		"VERBERGE", "HIDE",
		"AUSSER", "EXCEPT",
		"UNBEKANNT", "UNKNOWN",
		"SOFORT", "INSTANT", 
		"MINMAX",
		"KONTEXT", "CONTEXT",
		"SOWIE",
		"ENTFERNEN", "DELETE",
		"IN",
		"ALLE", "ALL",
		"SET",
		"&REF"};
	
	/**
	 * possible types of targets, to which can be rendered by a verbalizer
	 */
	public enum RenderingFormat {
		HTML, SWRCOWL, PLAIN_TEXT
	};

	/**
	 * The singleton instance of VerbalizationManager
	 */
	private static BibTexRenderManager instance;

	/**
	 * A list of Verbalizers registered in the Manager
	 */
	private ArrayList<BibTexRenderer> registeredRenderers = new ArrayList<BibTexRenderer>();

	/**
	 * This Hashmap saves the priorities of the Verbalizers
	 */
	private Map<BibTexRenderer, Integer> priorityHash;

	/**
	 * private constructor for singleton instantiation
	 */
	private BibTexRenderManager() {
		priorityHash = new HashMap<BibTexRenderer, Integer>();
		register(new DefaultRenderer(), PRIORITY_MIN);
		// register(new RuleVerbalizer(), PRIORITY_LOW);
		register(new BibtexHtmlRenderer());
		register(new BibtexSWRCRenderer());
	}

	/**
	 * Returns the singleton instance of VerbalizationManager. Lazy
	 * instantiation on demand.
	 * 
	 * @return the singleton instance of VerbalizationManager
	 */
	public static BibTexRenderManager getInstance() {
		if (instance == null) {
			instance = new BibTexRenderManager();

		}
		return instance;
	}

	/**
	 * registers a verbalizer in the Manager with medium Priority
	 * 
	 * @param verbalizer
	 *            the verbalizer to be registered
	 */
	public void register(BibTexRenderer verbalizer) {
		register(verbalizer, PRIORITY_MEDIUM);
	}

	/**
	 * registers a verbalizer in the Manager with given priority
	 * 
	 * @param verbalizer
	 *            the verbalizer to be registered
	 */
	public void register(BibTexRenderer verbalizer, int priority) {
		registeredRenderers.add(verbalizer);
		priorityHash.put(verbalizer, priority);
		Collections.sort(registeredRenderers, new VerbalizerComparator());
	}

	/**
	 * removes a verbalizer from the Manager
	 * 
	 * @param verbalizer
	 *            the verbalizer to be deregistered
	 */
	public void deregister(BibTexRenderer verbalizer) {
		registeredRenderers.remove(verbalizer);
		priorityHash.remove(verbalizer);
	}

	/**
	 * returns the priority of v in the VerbalizationManager, -1, if not
	 * registered
	 * 
	 * @param v
	 * @return the priority of v in the VerbalizationManager, -1, if not
	 *         registered
	 */
	public int getPriority(BibTexRenderer v) {
		if (!priorityHash.containsKey(v)) {
			return -1;
		}
		return priorityHash.get(v);
	}

	public void setPriority(BibTexRenderer v, int newPriority) {
		priorityHash.put(v, newPriority);
	}

	private static boolean containsSpecialStrings(String s) {
		for (int i = 0; i < specialStrings.length; i++) {
			if (s.contains(specialStrings[i])) {
				return true;
			}
		}
		return false;
	}
	
	public static String quoteIfNecessary(String s) {
		if (containsSpecialStrings(s)) {
			return "\"" + s + "\"";
		}
		return s;
	}
	
	/**
	 * Returns a verbalization (String representation) of the given object in
	 * the target format without using additional parameters by calling an appropriate Verbalizer
	 * 
	 * 
	 * @param o
	 *            the Object to be verbalized
	 * @param targetFormat
	 *            The output format of the verbalization (HTML/XML/PlainText...)
	 * @return A String representation of given object o in the target format
	 */
	public Object render(Object o, RenderingFormat renderingTargetFormat) {
		return render(o, renderingTargetFormat, null);
	}

	/**
	 * Returns a verbalization (String representation) of the given object in
	 * the target format using additional parameters by calling an appropriate Verbalizer. 
	 * 
	 * 
	 * @param o
	 *            the Object to be verbalized
	 * @param targetFormat
	 *            The output format of the verbalization (HTML/XML/PlainText...)
	 * @param parameter
	 *            additional parameters used to adapt the verbalization (e.g.,
	 *            singleLine, etc...)
	 * @return A String representation of given object o in the target format
	 */
	public Object render(Object o, RenderingFormat renderingTargetFormat, Map<String, Object> parameter) {
		// go through the verbalizers (sorted by priorities)
		for (BibTexRenderer v : registeredRenderers) {

			// can handle v the class of this object?
			boolean classCanBeHandled = false;
			for (Class c : v.getSupportedClassesForVerbalization()) {
				if (c.isInstance(o))
					classCanBeHandled = true;
			}
			// if the class cant be handled try next verbalizer
			if (!classCanBeHandled)
				continue;

			// class can be handled, now check, if the renderingType is
			// supported by this verbalizer
			if (Arrays.asList(v.getSupportedRenderingTargets()).contains(renderingTargetFormat)) {
				return v.render(o, renderingTargetFormat, parameter);
			}
			// else continue with next verbalization
		}

		// this should never happen, as the DefaultVerbalizer can handle
		// everything!
		return null;
	}

	/**
	 * A private comparator, that compares Verbalizers: compares the priority of
	 * both verbalizers in the VerbalizerManager
	 * 
	 * @author lemmerich
	 * 
	 */
	private class VerbalizerComparator implements Comparator<BibTexRenderer> {

		@Override
		public int compare(BibTexRenderer o1, BibTexRenderer o2) {
			int priorityV1 = getPriority(o1);
			int priorityV2 = getPriority(o2);
			return priorityV2 - priorityV1;
		}

	}

}
