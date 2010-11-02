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

package de.d3web.proket.input.d3web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.interviewmanager.CurrentQContainerFormStrategy;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;
import de.d3web.plugin.JPFPluginManager;
import de.d3web.proket.data.DialogStrategy;
import de.d3web.proket.utils.FileUtils;

/**
 * Util methods for the binding of d3web to the ProKEt system.
 * 
 * @author Martina Freiberg Johannes Mitlmeier
 */
public class D3webUtils {

	/**
	 * Create a d3web session with a given knowledge base. Per default creates a
	 * session for a multiple-question dialog.
	 * 
	 * @created 11.10.2010
	 * @param kb the d3web knowledge base
	 * @return the Session
	 */
	public static Session createSession(KnowledgeBase kb) {
		return createSession(kb, DialogStrategy.NEXTFORM);
	}

	/**
	 * Creates a new D3webSession for a a given KnowledgeBase and a certain
	 * DialogType.
	 * 
	 * @param kb d3web KnowledgeBase to be used.
	 * @param dt the Dialog Type
	 * @return A new D3webSession with the KnowledgeBase associated.
	 */
	public static Session createSession(KnowledgeBase kb, DialogStrategy ds) {

		// if DialogType given
		if (ds != null) {
			if (ds == DialogStrategy.NEXTUAQUESTION) { // one question dialog
				return SessionFactory.createSession(
						null, kb, new NextUnansweredQuestionFormStrategy(), new Date());
			}
			else if (ds == DialogStrategy.NEXTFORM) { // questionnaire based
				return SessionFactory.createSession(
						null, kb, new CurrentQContainerFormStrategy(), new Date());
			}
		}

		// per default returns questionnaire based standard questionnaires
		return SessionFactory.createSession(
				null, kb, new CurrentQContainerFormStrategy(), new Date());
	}

	/**
	 * Creates a new D3webSession for a given KnowledgeBase (by filename).
	 * 
	 * @param kbFilename File name of the KnowledgeBase to be used. The file is
	 *        searched in /resources/kb or at runtime in WEB-INF/classes/kb. The
	 *        suffix ".jar" can be omitted.
	 * @return A new D3webSession with the KnowledgeBase associated.
	 */
	public static Session createSession(String kbFilename) {
		return createSession(getKnowledgeBase(kbFilename), DialogStrategy.DEFAULT);
	}

	/**
	 * Util method: Get a String representation of the questions and solutions
	 * of a given KnowledgeBase for debugging.
	 * 
	 * @param kb KnowledgeBase to be debugged.
	 * @return A String containing a textual representation of the
	 *         KnowledgeBase's questions and solutions.
	 */
	public static String debug(KnowledgeBase kb) {
		StringBuilder sb = new StringBuilder();
		debugRecurse(kb.getRootQASet(), sb, "");
		return sb.toString();
	}

	/**
	 * The recursive util method needed to assemble a String representation of
	 * the questions and solutions of a knowledge base.
	 * 
	 * @created 11.10.2010
	 * @param root The recursion start
	 * @param sb a StringBuilder
	 * @param indention indentation String
	 */
	private static void debugRecurse(TerminologyObject root, StringBuilder sb,
			String indention) {

		// go through all children
		for (TerminologyObject element : root.getChildren()) {

			// append indentation and element name
			sb.append(indention).append(element.getName());

			// also append Choices of Questions
			if ((element instanceof QuestionChoice)) {
				List<Choice> alternatives = ((QuestionChoice) element)
						.getAllAlternatives();
				sb.append(" (");
				for (Choice choice : alternatives) {
					sb.append(choice.getName()).append(", ");
				}
				sb.append(")\n");

				// in case a Container/QASet is given, recursion goes on
			} else if ((element instanceof QContainer)
					|| (element instanceof QASet)) {
				sb.append("\n");
				debugRecurse(element, sb, indention + "\t");
			}
		}
	}

	/**
	 * Discretize a rating with no limits/discretization values given, i.e.
	 * using the defaults defined in this method.
	 * 
	 * @created 11.10.2010
	 * @param value The value to be discretized
	 * @return String an abstract/discretized value
	 */
	public static String discretizeRating(double value) {

		// some reasonable default discretizing values
		List<Double> limits = new ArrayList<Double>();
		limits.add(49.0);
		limits.add(79.0);
		limits.add(100.0);

		// and some default discretized (abstract) values
		List<String> returnValues = new ArrayList<String>();
		returnValues.add("rating-low");
		returnValues.add("rating-medium");
		returnValues.add("rating-high");

		return discretizeRating(limits, returnValues, value);
	}

	/**
	 * Discretize a rating value with given limits and discretization values.
	 * 
	 * @param limits Ordered list of limits starting from the lowest, where a
	 *        single value of x means the rating has to be <= x.
	 * @param returnValues Return values based on the limits.
	 * @param value Value to be discretized.
	 * @return null on negative input value, the returnValue corresponding to
	 *         the lowest limit value higher than the value to check.
	 */
	public static String discretizeRating(List<Double> limits,
			List<String> returnValues, double value) {
		if (value < 0) {
			return null;
		}
		for (int i = 0; i < limits.size(); i++) {
			if (value <= limits.get(i)) {
				return returnValues.get(i);
			}
		}

		// return the highest value by default
		return returnValues.get(returnValues.size() - 1);
	}

	/**
	 * Util method to get all jpf-plugin-files of a given folder. Needed for the
	 * PluginManager to work.
	 * 
	 * @param folder Folder to operate in.
	 * @return List of the JPF plugins in the given folder.
	 */
	public static File[] getAllJPFPlugins(File folder) {
		List<File> fileList = new ArrayList<File>();

		for (File file : folder.listFiles()) {
			if (file.getName().contains("-jpf-plugin")) {
				fileList.add(file);
			}
		}
		return fileList.toArray(new File[fileList.size()]);
	}

	/**
	 * Retrieves the best solutions a {@link Session} contains at this moment.
	 * Thereby, "best" is defined by a minimum rating a solution must have and a
	 * maximum number of solutions to be returned in total.
	 * 
	 * TODO maybe this method needs refactoring as to return other sets of
	 * solutions?!
	 * 
	 * @param session {@link Session} to operate on.
	 * @param minRating Minimum {@link Rating} a {@link Solution} must have to
	 *        be included in the returned list.
	 * @param maxCount Maximum number of {@link Solution}s to include in the
	 *        returned list.
	 * @return List of the best {@link Solution}s according to the parameters
	 *         given. If there are established {@link Solution}s all of them are
	 *         returned regardless of the limit parameters. They only affect the
	 *         list if there are only indicated {@link Solution}s.
	 */
	public static List<Solution> getBestSolutions(final Session session,
			Number minRating, int maxCount) {

		List<Solution> result = new LinkedList<Solution>();

		// get established and suggested solutions
		List<Solution> established = session.getBlackboard().getSolutions(
				State.ESTABLISHED);
		List<Solution> suggested = session.getBlackboard().getSolutions(
				State.SUGGESTED);

		// if established solutions, just return them not regarding any limits
		if (established.size() > 0) {
			return established;
		}

		// if no suggested solutions either, return empty list
		// TODO so other solutions than estab/sugg are ignored! --> rework
		if (suggested.size() == 0) {
			return result;
		}

		// sort suggested solutions according to a comparator that sorts
		// solution objects by their rating
		Collections.sort(suggested, new Comparator<Solution>() {

			@Override
			public int compare(Solution o1, Solution o2) {
				if (o1 == null && o2 == null) {
					return 0;
				}
				if (o2 == null) {
					return 1;
				}
				if (o1 == null) {
					return -1;
				}
				return session.getBlackboard().getRating(o1)
						.compareTo(session.getBlackboard().getRating(o2));
			}
		});

		// limit for the number of solutions: minimum of either maxCount or
		// suggested.
		int limit = Math.min(maxCount, suggested.size());

		// cut off
		for (int i = 0; i < limit; i++) {
			result.add(suggested.get(limit - 1 - i)); // upside-down
		}
		return result;
	}

	/**
	 * Retrieve a {@link KnowledgeBase} by its file name. Search is performed in
	 * /WEB-INF/classes/kb. The trailing ".jar" can be omitted.
	 * 
	 * @param kbFilename File name of the {@link KnowledgeBase}.
	 * @return The {@link KnowledgeBase} denoted by the given name, null if no
	 *         such {@link KnowledgeBase} exists.
	 */
	public static KnowledgeBase getKnowledgeBase(String kbFilename) {

		// add .jar if it's not already there
		if (!kbFilename.endsWith(".jar")) {
			kbFilename += ".jar";
		}

		File kbFile;
		File libPath;
		try {
			// Paths here are relative to the WEB-INF/classes folder!!!
			// from the /specs/d3web folder
			kbFile = FileUtils.getResourceFile("/specs/d3web/" + kbFilename);
			// from the /lib folder
			libPath = FileUtils.getResourceFile("/../lib");
		} catch (FileNotFoundException e1) {
			return null;
		}

		// initialize PluginManager
		File[] files = null;
		files = getAllJPFPlugins(libPath);
		JPFPluginManager.init(files);
		PersistenceManager persistenceManager = PersistenceManager.getInstance();

		// try to load knowledge base
		try {
			return persistenceManager.load(kbFile);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Retrieve all {@link Question}s of a {@link KnowledgeBase} as a string.
	 * TODO: needed?
	 * 
	 * @param kb {@link KnowledgeBase} to operate on.
	 * @return A string with one {@link Question} per line. The questions are
	 *         represented by their name.
	 */
	public static String getQuestionsAsString(KnowledgeBase kb) {
		StringBuilder sb = new StringBuilder();
		List<Question> questions = kb.getQuestions();
		for (Question question : questions) {
			sb.append(question.getName()).append("\n");
		}
		return sb.toString();
	}

	/**
	 * Retrieve solutions from a {@link Session}. Thereby solutions are
	 * filtered. First we try to get only established ones, if there are none
	 * established we want to get the suggested ones TODO maybe we just should
	 * get all the solutions and order them accordingly!
	 * 
	 * @param session {@link Session} to operate on.
	 * @return If there is established {@link Solution}s, a list of those is
	 *         returned. Otherwise this function returns a (possibly empty) list
	 *         of suggested {@link Solution}.
	 */
	public static List<Solution> getSolutionsEstabSugg(Session session) {

		List<Solution> result =
				session.getBlackboard().getSolutions(State.ESTABLISHED);
		if (result.size() == 0) {
			return session.getBlackboard().getSolutions(State.SUGGESTED);
		}
		return result;
	}

	/**
	 * Retrieves a {@link TerminologyObject} from a {@link KnowledgeBase} by its
	 * given ID.
	 * 
	 * @param session The {@link Session} to operate on.
	 * @param id The target ID.
	 * @return The {@link TerminologyObject} with the given ID in the
	 *         {@link Session}'s {@link KnowledgeBase}, or null if no such
	 *         {@link TerminologyObject} exists.
	 */
	public static TerminologyObject getTerminologyObjectByID(
			Session session, String id) {

		// get all questions
		List<Question> questions = session.getKnowledgeBase().getQuestions();
		for (TerminologyObject question : questions) {

			// if ids match, return the question
			if (question.getId().equals(id)) {
				return question;
			}
		}
		return null;
	}
}
