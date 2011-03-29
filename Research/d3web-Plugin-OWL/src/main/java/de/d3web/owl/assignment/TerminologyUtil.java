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
package de.d3web.owl.assignment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;

/**
 *
 * @author Sebastian Furth
 * @created Mar 29, 2011
 */
public class TerminologyUtil {

	// Just for convenience and code beautification
	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	// All instances of TerminologyUtil
	private static Map<KnowledgeBase, TerminologyUtil> instances = new WeakHashMap<KnowledgeBase, TerminologyUtil>();

	/**
	 * Returns an instance of TerminologyUtil for a specific @Link{KnowledgeBase}
	 * <b>If no instances exists, a new one will be created.</b>
	 *
	 * @created Mar 29, 2011
	 * @param kb the underlying KnowledgeBase
	 * @return an instance of TerminologyUtil for the KnowledgeBase
	 */
	public static TerminologyUtil getInstance(KnowledgeBase kb) {
		TerminologyUtil instance = instances.get(kb);
		if (instance == null) {
			instance = new TerminologyUtil(kb);
			instances.put(kb, instance);
		}
		return instance;
	}

	/* The underlying knowledge base */
	private final KnowledgeBase kb;

	/* Caches to avoid multiple searches */
	private final Map<OWLNamedIndividual, Solution> solutionCache = new HashMap<OWLNamedIndividual, Solution>();
	private final Map<OWLNamedIndividual, Question> questionCache = new HashMap<OWLNamedIndividual, Question>();
	private final Map<OWLNamedIndividual, QContainer> qcontainerCache = new HashMap<OWLNamedIndividual, QContainer>();

	/* Ensure non-instantiability */
	private TerminologyUtil(KnowledgeBase kb) {
		if (kb == null) {
			throw new NullPointerException();
		}
		this.kb = kb;
	}

	public Set<Solution> getSolutionsFor(Set<OWLNamedIndividual> individuals) {
		return findTerminologyObjects(individuals, kb.getManager().getSolutions(), solutionCache);
	}

	public Set<Question> getQuestionsFor(Set<OWLNamedIndividual> individuals) {
		return findTerminologyObjects(individuals, kb.getManager().getQuestions(), questionCache);
	}

	public Set<QContainer> getQContainersFor(Set<OWLNamedIndividual> individuals) {
		return findTerminologyObjects(individuals, kb.getManager().getQContainers(),
				qcontainerCache);
	}

	private <T extends TerminologyObject> Set<T> findTerminologyObjects(Set<OWLNamedIndividual> individuals, List<T> terminologyObjects, Map<OWLNamedIndividual, T> cache) {
		Set<T> results = new HashSet<T>();
		for (OWLNamedIndividual individual : individuals) {
			// check the cache
			T result = cache.get(individual);
			// if necessary, search in the kb
			if (result == null) {
				// extract the name from the IRI
				String name = extract(individual);
				for (T temp : terminologyObjects) {
					if (temp.getName().equalsIgnoreCase(name)) {
						result = temp;
						// add TerminologyObject to the cache
						cache.put(individual, temp);
						break;
					}
				}
			}
			// add the found TerminologyObject to the results
			if (result != null) {
				results.add(result);
			}
			else {
				logger.severe("Unable to find a corresponding TerminologyObject for IRI: "
						+ individual);
			}
		}
		return results;
	}

	private String extract(OWLNamedIndividual individual) {
		String fragment = individual.getIRI().getFragment();
		if (fragment != null) {
			return fragment.replaceAll("_", " ");
		}
		logger.warning("Fragment is null for IRI: " + individual);
		return individual.getIRI().toString();
	}

}
