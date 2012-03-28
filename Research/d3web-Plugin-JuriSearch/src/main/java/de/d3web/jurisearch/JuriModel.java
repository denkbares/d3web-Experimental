/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.jurisearch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.terminology.info.Property;

/**
 * 
 * @author grotheer
 * @created 09.03.2012
 */
public class JuriModel implements KnowledgeSlice {

	public static final Property<Boolean> DUMMY = Property.getProperty("dummy", Boolean.class);

	public final static KnowledgeKind<JuriModel> KNOWLEDGE_KIND = new KnowledgeKind<JuriModel>(
			"JuriModel", JuriModel.class);

	private final Set<JuriRule> rules;

	public JuriModel() {
		rules = new HashSet<JuriRule>();
	}

	public JuriModel(Collection<JuriRule> c) {
		this();
		rules.addAll(c);
	}

	public void addRule(JuriRule r) {
		rules.add(r);
	}

	public void removeRule(JuriRule r) {
		rules.remove(r);
	}

	public void addRules(Collection<JuriRule> c) {
		rules.addAll(c);
	}

	public void removeRules(Collection<JuriRule> c) {
		rules.removeAll(c);
	}

	public Set<JuriRule> getRules() {
		return rules;
	}
}
