/**
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
package de.d3web.proket.d3web.input;

import java.util.List;
import java.util.Vector;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.proket.data.Answer;
import de.d3web.proket.data.D3webDialog;
import de.d3web.proket.data.DialogTree;
import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.data.Question;
import de.d3web.proket.data.Questionnaire;

/**
 * Util Methods for transforming d3web elements in IDialogObjects and vice
 * versa.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class D3webConversionUtils {

	/**
	 * Transforms d3web Choices from a ChoiceQuestion into proket-answers and
	 * assigns them to the given IDialogObject
	 * 
	 * @created 11.10.2010
	 * @param object the object that needs to filled with d3web answers
	 * @param choiceQuestion the d3web-question providing the choices
	 */
	private static void createAnswersFromAlternatives(IDialogObject object,
			QuestionChoice choiceQuestion) {

		// get the answer choices from d3web
		List<Choice> alternatives = choiceQuestion.getAllAlternatives();
		Vector<String> vec = new Vector<String>();

		// put them into Vector
		for (Choice alternative : alternatives) {
			vec.add(alternative.getName());
		}

		// delegate...
		createAnswersFromVector(object, vec);
	}

	/**
	 * Util method called from createAnswersFromAlternatives
	 * 
	 * @created 11.10.2010
	 * @param object the IDialogObject the answers are later assigned to
	 * @param answers the answers vector containing the names of d3web answers
	 */
	private static void createAnswersFromVector(IDialogObject object,
			Vector<String> answers) {

		for (int i = 0; i < answers.size(); i++) {
			Answer child = new Answer();
			// add question id to keep answer-ids unique
			child.setId(object.getId() + "-answer-" + String.valueOf(i)); // prefix

			if (!answers.get(i).equals("")) {
				child.setTitle(answers.get(i));
			}

			// define parent and child relation
			child.setParent(object);
			object.addChild(child);
		}
	}

	/**
	 * Get an IDialogObject for a given InterviewObject in a given Session and
	 * the provided root parent element that it should be append to.
	 * 
	 * @created 11.10.2010
	 * @param d3webSession the specified d3web session
	 * @param child the d3web Interview Object
	 * @param rootParent the root parent
	 * @return
	 */
	public static IDialogObject getDialogObject(Session d3webSession,
			InterviewObject child, IDialogObject rootParent) {

		// calling the actual transformation method with setting the inclusion
		// of children to true per default
		return getDialogObject(d3webSession, child, rootParent, true);
	}

	/**
	 * Transforms a {@link InterviewObject} from d3web to an
	 * {@link IDialogObject}.
	 * 
	 * @param d3websource
	 *            d3web given {@link InterviewObject}
	 * @return A {@link IDialogObject} representation of the source object.
	 */
	public static IDialogObject getDialogObject(Session d3webSession,
			InterviewObject d3websource, IDialogObject rootDialogObject, boolean includeChildren) {

		Blackboard bb = d3webSession.getBlackboard();
		
		// return if the InterviewObject is null
		if (d3websource == null) {
			return null;
		}

		IDialogObject dialogobject = null;
		// get the class name of the d3web object
		String className = d3websource.getClass().getName();

		// remove package name in getting the substring until and including the
		// last dot before the class name
		className = className.replaceFirst(".*\\.", "");

		boolean nextLevelIncludeChildren = includeChildren;
		if (className.equals("QuestionOC")) {
			dialogobject = new Question();
			dialogobject.setType("oc");
			transferGeneralAttributes(d3websource, dialogobject);
			createAnswersFromAlternatives(dialogobject, (QuestionChoice) d3websource);
		} else if (className.equals("QuestionMC")) {
			dialogobject = new Question();
			dialogobject.setType("mc");
			// in d3web dialogs we need send buttons for all mc questions
			dialogobject.getInheritableAttributes().setSendButton(true);
			transferGeneralAttributes(d3websource, dialogobject);
			createAnswersFromAlternatives(dialogobject, (QuestionChoice) d3websource);
		} else if (className.equals("QuestionYN")) {
			dialogobject = new Question();
			dialogobject.setType("yn");
			transferGeneralAttributes(d3websource, dialogobject);
			Vector<String> vec = new Vector<String>();
			vec.add("");
			createAnswersFromVector(dialogobject, vec);
		} else if (className.equals("QuestionNum")) {
			dialogobject = new Question();
			dialogobject.setType("num");
			// in d3web dialogs we need send buttons for all num questions
			dialogobject.getInheritableAttributes().setSendButton(true);
			transferGeneralAttributes(d3websource, dialogobject);
			Vector<String> vec = new Vector<String>();
			vec.add("");
			createAnswersFromVector(dialogobject, vec);
		} else if (className.equals("QuestionText")) {
			dialogobject = new Question();
			dialogobject.setType("text");
			// create answers
			transferGeneralAttributes(d3websource, dialogobject);
			Vector<String> vec = new Vector<String>();
			vec.add("");
			createAnswersFromVector(dialogobject, vec);
		} else if (className.equals("QContainer")) {
			dialogobject = new Questionnaire();
			transferGeneralAttributes(d3websource, dialogobject);
			// nextLevelIncludeChildren = false;
			// TODO: why? Questionnaires could also contain children...
		}

		// return if nothing important happend and object could not be built
		if (dialogobject == null) {
			return null;
		}

		// set root parent
		// if (object.getParent() == null) {
		dialogobject.setParent(rootDialogObject);
		// }
		dialogobject.getInheritableAttributes().compileInside();

		// process children
		if (includeChildren) {
			TerminologyObject[] children = d3websource.getChildren();
			if (children != null && children.length > 0) {
				Vector<IDialogObject> parsedChildren = new Vector<IDialogObject>();
				for (TerminologyObject child : children) {
					if (!(child instanceof InterviewObject)) {
						continue;
					}
					// IDialogObject parsedChild = getDialogObject(d3webSession,
					// (InterviewObject) child, rootParent,
					// nextLevelIncludeChildren);
					IDialogObject parsedChild = getDialogObject(d3webSession,
							(InterviewObject) child, dialogobject,
							nextLevelIncludeChildren);
					if (parsedChild != null) {
						// double link
						parsedChild.setParent(dialogobject);
						parsedChildren.add(parsedChild);
					}
				}
				dialogobject.setChildren(parsedChildren);
			}
		}

		return dialogobject;
	}

	/**
	 * Transfer the ID ad Title from a d3web InterviewObject into an
	 * IDialogObject
	 * 
	 * @created 11.10.2010
	 * @param source the d3web Interview Object
	 * @param object the IDialogObject
	 */
	private static void transferGeneralAttributes(InterviewObject source,
			IDialogObject object) {
		if (object == null) {
			return;
		}
		object.setId(source.getId());
		object.setTitle(source.getName());
	}

	/**
	 * Retrieves the complete DialogTree (internal representation of a dialog)
	 * based on a given knowledge base and a d3web session.
	 * 
	 * @created 13.10.2010
	 * @param kb the knowledge base, the tree needs to be loaded from
	 * @param ses the session
	 * @return the internal dialog representation DialogTree
	 */
	public static DialogTree getEntireTreeFromKB(KnowledgeBase kb, Session ses) {
	
		DialogTree tree = new DialogTree(); // create new tree
		
		// use D3webDialog class for specifying the root element
		IDialogObject root = null;

		try {
			root = D3webDialog.class.newInstance();
		}
		catch (NullPointerException e) {
			return null;
		}
		catch (InstantiationException e) {
			return null;
		}
		catch (IllegalAccessException e) {
			return null;
		}
		if (root == null) {
			return null;
		}

		// get root QASet from the knowledge base
		QASet rootd3web = kb.getRootQASet();

		// set some properties of the root object
		((D3webDialog) root).setd3webRoot(rootd3web);
		root.setId("rootd3web"); // TODO better ID needed some day maybe
		root.setParent(null); // rootiest root does not need a parent

		// retrieve and add the children to the root, recursive adding
		// children's children and so on.
		// TODO some day handle and break potential cyclic recursions
		Vector<IDialogObject> children = new Vector<IDialogObject>();
		for (QASet childQASet : kb.getManager().getQContainers()) {
			if (!childQASet.getName().equals("Q000")) {
				IDialogObject ido = getDialogObject(ses, childQASet, root, true);
				children.add(ido);
			}
		}
		root.setChildren(children);

		// add the root to the tree and print for debugging
		tree.setRoot(root);
		return tree;
	}

	/**
	 * Retrieves a {@link KnowledgeBase} that is to some extend equivalent to
	 * the {@link DialogTree} given. Currently only transfers {@link Question}s
	 * and their {@link Answer}s.
	 * 
	 * TODO not yet finished. Needed???
	 * 
	 * @param dialogTree {@link DialogTree} to convert.
	 * @return Converted {@link KnowledgeBase}.
	 */
	/*
	 * public static KnowledgeBase getKnowledgeBase(DialogTree dialogTree) { try
	 * { InitPluginManager.init();
	 * 
	 * KnowledgeBaseManagement kbm = KnowledgeBaseManagement .createInstance();
	 * QASet root = kbm.getKnowledgeBase().getRootQASet();
	 * 
	 * QContainer questionContainer = kbm.createQContainer( "QuestionContainer",
	 * root);
	 * 
	 * List<IDialogObject> dialogItems = dialogTree.asList();
	 * 
	 * // loop all questions for (IDialogObject dialogObject : dialogItems) { if
	 * (!(dialogObject instanceof Question)) { continue; }
	 * 
	 * // child answers Vector<IDialogObject> answers =
	 * dialogObject.getChildren();
	 * 
	 * String questionType = (String) StringUtils.firstNonNull( ((Question)
	 * dialogObject).getInheritableAttributes()
	 * .compileInside().getAnswerType(), ((Question) dialogObject).getType(),
	 * answers.get(0) .getType()); if (questionType == null) { continue; }
	 * 
	 * // create question de.d3web.core.knowledge.terminology.Question
	 * newQuestion = null; boolean createChoices = false; // Date, MC, Num, OC,
	 * Text, YN, ZC if (questionType.equalsIgnoreCase("MC")) { newQuestion =
	 * kbm.createQuestionMC( dialogObject.getFullId(), dialogObject.getTitle(),
	 * questionContainer, new String[] {}); createChoices = true; } else if
	 * (questionType.equalsIgnoreCase("OC")) { newQuestion =
	 * kbm.createQuestionOC( dialogObject.getFullId(), dialogObject.getTitle(),
	 * questionContainer, new String[] {}); createChoices = true; } // TODO
	 * create all types
	 * 
	 * if (newQuestion == null) { continue; }
	 * 
	 * if (createChoices) { List<Choice> choiceList = new ArrayList<Choice>();
	 * for (IDialogObject answer : answers) { choiceList.add(new
	 * Choice(answer.getTitle())); } ((QuestionChoice)
	 * newQuestion).setAlternatives(choiceList); }
	 * 
	 * }
	 * 
	 * // Define the init questionnaire kbm.getKnowledgeBase().setInitQuestions(
	 * Arrays.asList(questionContainer));
	 * 
	 * return kbm.getKnowledgeBase(); } catch (IOException e) { return null; } }
	 */


}
