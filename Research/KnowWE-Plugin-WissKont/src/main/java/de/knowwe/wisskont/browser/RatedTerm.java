/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.wisskont.browser;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.wisskont.util.HierarchyNode;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 12.04.2013
 */
public class RatedTerm implements HierarchyNode<RatedTerm> {

	public static final RatedTerm ROOT = new RatedTerm("ROOT");

	private final String term;
	private double value;

	@Override
	public String toString() {
		return term + " (" + value + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RatedTerm) {
			RatedTerm other = (RatedTerm) obj;
			return other.term.equals(this.term);
		}
		return false;
	}

	/**
	 * 
	 */
	public RatedTerm(String term) {
		this.term = term;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getTerm() {
		return term;
	}

	public RatedTerm(String term, double v) {
		this(term);
		value = v;
	}

	@Override
	public int compareTo(RatedTerm o) {
		return term.compareTo(o.term);
	}

	@Override
	public boolean isSubNodeOf(RatedTerm node) {
		if (node.equals(RatedTerm.ROOT)) return false;
		String baseUrl = Rdf2GoCore.getInstance().getLocalNamespace();

		String thisConceptURLString = Strings.encodeURL(this.term);
		String thisURL = baseUrl + thisConceptURLString;
		URI thisURI = new URIImpl(thisURL);

		String otherConceptURLString = Strings.encodeURL(node.term);
		String otherURL = baseUrl + otherConceptURLString;
		URI otherURI = new URIImpl(otherURL);

		return MarkupUtils.isSubConceptOf(thisURI, otherURI);
	}

	/**
	 * 
	 * @created 12.04.2013
	 * @param increment
	 */
	public void incrValue(Double increment) {
		value += increment;
	}

}
