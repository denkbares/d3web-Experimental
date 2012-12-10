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
package de.knowwe.wisskont.refactoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdfs.tripleMarkup.TripleMarkup;
import de.knowwe.wisskont.AssociationBidirMarkup;
import de.knowwe.wisskont.AssociationMarkup;
import de.knowwe.wisskont.CanBidirMarkup;
import de.knowwe.wisskont.CanMarkup;
import de.knowwe.wisskont.ConceptMarkup;
import de.knowwe.wisskont.MustMarkup;

/**
 * 
 * @author jochenreutelshofer
 * @created 05.12.2012
 */
public class ConceptRelationMarkupRefactoring {

	public static void refactor(Article a, UserActionContext user) {
		if (hasConceptDefinition(a)) {
			List<Section<TripleMarkup>> triples = Sections.findSuccessorsOfType(a.getRootSection(),
					TripleMarkup.class);
			if (triples.size() == 0) return;

			List<String> mustObjects = new ArrayList<String>();
			List<String> canObjects = new ArrayList<String>();
			List<String> canBidirObjects = new ArrayList<String>();
			List<String> associationObjects = new ArrayList<String>();
			List<String> associationBidirObjects = new ArrayList<String>();

			Iterator<Section<TripleMarkup>> tripleIter = triples.iterator();
			Map<String, String> replacementMap = new HashMap<String, String>();
			while (tripleIter.hasNext()) {
				Section<TripleMarkup> triple = tripleIter.next();
				Section<? extends SimpleReference> subject = TripleMarkup.getSubject(triple);
				String subjectName = subject.get().getTermName(subject);

				if (subjectName.equals(getConceptName(a))) {
					// if subject matches do
					Section<? extends SimpleReference> predicate = TripleMarkup.getPredicate(triple);
					String predName = predicate.get().getTermName(predicate);
					Section<? extends SimpleReference> object = TripleMarkup.getObject(triple);
					String objectName = object.get().getTermName(object);
					if (predName.equals(MustMarkup.KEY)) {
						mustObjects.add(objectName);
						// will be deleted:
						replacementMap.put(triple.getID(), "");

					}
					else if (predName.equals(CanMarkup.KEY)) {
						canObjects.add(objectName);
						// will be deleted:
						replacementMap.put(triple.getID(), "");

					}
					else if (predName.equals(CanBidirMarkup.KEY)) {
						canBidirObjects.add(objectName);
						// will be deleted:
						replacementMap.put(triple.getID(), "");

					}
					else if (predName.equals(AssociationMarkup.KEY)) {
						associationObjects.add(objectName);
						// will be deleted:
						replacementMap.put(triple.getID(), "");

					}
					else if (predName.equals(AssociationBidirMarkup.KEY)) {
						associationBidirObjects.add(objectName);
						// will be deleted:
						replacementMap.put(triple.getID(), "");

					}

				}
				else {
					tripleIter.remove(); // forget about this triple
				}
			}

			String mustCode = generateCode(MustMarkup.KEY, mustObjects);
			String canCode = generateCode(CanMarkup.KEY, canObjects);
			String canBidirCode = generateCode(CanBidirMarkup.KEY, canBidirObjects);
			String associationCode = generateCode(AssociationMarkup.KEY, associationObjects);
			String associationBidirCode = generateCode(AssociationBidirMarkup.KEY,
					associationBidirObjects);

			String overallCodeBlob = mustCode + canCode + canBidirCode + associationCode
					+ associationBidirCode;

			if (triples.size() == 0) return;
			Section<TripleMarkup> firstTriple = triples.get(0);
			replacementMap.put(firstTriple.getID(), overallCodeBlob);
			try {
				Sections.replaceSections(user, replacementMap);
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * 
	 * @created 05.12.2012
	 * @param key
	 * @param objects
	 * @return
	 */
	private static String generateCode(String key, List<String> objects) {
		if (objects.size() == 0) return "";
		StringBuffer code = new StringBuffer();
		String linebreak = System.getProperty("line.separator");
		code.append(linebreak + key + ": ");
		code.append(Strings.concat(", ", objects));
		code.append(linebreak);
		code.append(linebreak);
		return code.toString();
	}

	/**
	 * 
	 * @created 05.12.2012
	 * @param a
	 * @return
	 */
	private static boolean hasConceptDefinition(Article a) {
		return getConceptName(a) != null;
	}

	private static String getConceptName(Article a) {
		Section<ConceptMarkup> def = Sections.findSuccessor(a.getRootSection(), ConceptMarkup.class);
		if (def != null) {
			Section<SimpleDefinition> termDef = Sections.findSuccessor(def, SimpleDefinition.class);
			return termDef.get().getTermName(termDef);
		}
		return null;
	}
}
