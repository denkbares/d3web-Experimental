/**
 * Copyright (C) 2012 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.d3web.output.render;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethodRulebased;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.*;
import de.d3web.core.knowledge.terminology.Rating.*;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.proket.d3web.utils.D3webUtils;
import de.d3web.scoring.HeuristicRating;
import java.util.List;

/**
 * Basic class for rendering the solution panel or explanation component of a
 * d3web dialog
 *
 * @author Alina Coca
 * @date Feb 2013
 */
public class SolutionExplanationClarificationD3webRenderer {

    public String renderExplanationForSolution(Solution solution, Session d3webSession) {

        List<? extends PSMethod> psms = d3webSession.getPSMethods();
        for (PSMethod psm : psms) {
            if (psm instanceof PSMethodRulebased) {
                // we have rule based knowledge, so call rule-based expl. renderer
                return renderExplanationClariRuleBased(solution, d3webSession);

            }
        }
        return "textual rule based explanation n/a";

    }

    //get all the answer options for one question, exept the one witch has been picked 
    public String getOtherAnswers(TerminologyObject quest, Value existing) {
        String other = "<span> ["; // list of answers will be in a span HTML tag
        List<Choice> other_answers = ((QuestionChoice) quest).getAllAlternatives(); // get all answer alternatives for one question
        for (Choice answer : other_answers) {
            if (!answer.toString().equals(existing.toString())) { //add all  answers besides the one witch was picked in the form dialog 
                other += answer.toString() + "/ "; //separate answers by a /
            }
        }
        int last = other.lastIndexOf("/"); // delete last /
        other = other.substring(0, last);
        other += "]</span>"; // close span Tag
        return other;
    }

    //get all the answer options for one question
    public String getAllAnswers(TerminologyObject quest) {
        String answers = "<span> ["; // list of answers will be in a span HTML tag
        List<Choice> other_answers = ((QuestionChoice) quest).getAllAlternatives();// get all answer alternatives for one question
        for (Choice answer : other_answers) {
            answers += answer.toString() + "/ "; // add all answers to the list and separate by /
        }
        int last = answers.lastIndexOf("/"); //delete last /
        answers = answers.substring(0, last);
        answers += " ]</span>"; //close span Tag
        return answers;
    }

    private String renderExplanationClariRuleBased(Solution sol, Session d3webSession) {
        String text = "<div class=\"details\">Teildiagnosen:<br />"; //string for the content of the Calarification Popup
        String test = ""; //String for the relevant answers for a solution
        List<TerminologyObject> deriObjectsForSolutions = D3webUtils.getDerivationObjectsPSMRulesFor(sol, d3webSession); 
        
        //build string with relevant solutions for solution and make link to their own clarification dialog
        for (TerminologyObject object : deriObjectsForSolutions) {
            if (object instanceof Solution) {
                text += "<div id=\"" + object.getName() + "_" + sol.getName() + "_solText\" class=\"solText linkstyle cell\" onclick=\"openFirstCloseSecond('" + object.getName() + "' , '" + sol.getName() + "');\"> \n"
                        + "\n"
                        + object.getName() + "\n"
                        + "</div>";
            }
        }
        text += "</div>";
        
        //get Blackbord and solution rating
        Blackboard bb = d3webSession.getBlackboard();
        State state = bb.getRating(sol).getState();
        Double score = ((HeuristicRating) bb.getRating(sol)).getScore(); //rating for solution

        //get all the relevant rules for solution
        List<TerminologyObject> deriObjects = D3webUtils.getDerivationObjectsPSMRulesFor(sol, d3webSession);

        //build string with relevant answers for solution
        for (TerminologyObject rule : deriObjects) {
            Value qvalue = d3webSession.getBlackboard().getValue((ValueObject) rule);
            test += rule.getName() + qvalue + ";";
        }

        //get containers and a list of the answerd questions
        List<QContainer> allContainers = d3webSession.getKnowledgeBase().getManager().getQContainers();
        List<Question> questionList = bb.getAnsweredQuestions();

        //go all the container
        for (QContainer container : allContainers) {
            //only if container not root
            if (!container.getName().equals("Q000")) {
                text += "<br /><div class=\"container\"><b>" + container.toString() + "</b></div>";
                //for all the questions in one container
                for (TerminologyObject cont : container.getChildren()) {

                    if (cont instanceof Question) {

                        if (questionList.contains(cont)) {//if Question in container is in list (answerd questions) then print question + answer;
                            Value answer = bb.getValue((ValueObject) cont);//if answers is relevant for solutions, add green, red or yellow border
                            if (state.equals(Rating.State.ESTABLISHED) && test.contains(cont.toString() + answer.toString())) {
                                //add div with two span tags: Question : answers [alternative answers]
                                text += "<div class=\"quest\"><b>" + cont.toString() + ": </b><span style=\"border: 2px solid #7CF56E\">" + answer.toString() + "</span>" + getOtherAnswers(cont, answer) + "</div>";
                            } else if ((state.equals(Rating.State.EXCLUDED) && test.contains(cont.toString() + answer.toString()))) {
                                text += "<div class=\"quest\"><b>" + cont.toString() + ": </b><span style=\"border: 2px solid #EB4242\">" + answer.toString() + "</span>" + getOtherAnswers(cont, answer) + "</div>";
                            } else if ((state.equals(Rating.State.SUGGESTED) && test.contains(cont.toString() + answer.toString()))) {
                                text += "<div class=\"quest\"><b>" + cont.toString() + ": </b><span style=\"border: 2px solid #EBF707\">" + answer.toString() + "</span>" + getOtherAnswers(cont, answer) + "</div>";
                            } else {
                                text += "<div class=\"quest\"><b>" + cont.toString() + ": </b><span>" + answer.toString() + "</span>" + getOtherAnswers(cont, answer) + "</div>";
                            }

                            //two more for-loops for questions triggerd by specific answers
                            for (TerminologyObject child : cont.getChildren()) {
                                Value a = bb.getValue((ValueObject) child);
                                if (state.equals(Rating.State.ESTABLISHED) && test.contains(child.toString() + a.toString())) {
                                    text += "<div class=\"quest\" ><b>" + child.toString() + ": </b><span style=\"border: 2px solid #7CF56E\">" + a.toString() + "</span>" + getOtherAnswers(child, a) + "</div>";
                                } else if ((state.equals(Rating.State.EXCLUDED) && test.contains(child.toString() + a.toString()))) {
                                    text += "<div class=\"quest\"><b>" + child.toString() + ": </b><span style=\"border: 2px solid #EB4242\">" + a.toString() + "</span>" + getOtherAnswers(child, a) + "</div>";
                                } else if ((state.equals(Rating.State.SUGGESTED) && test.contains(child.toString() + a.toString()))) {
                                    text += "<div class=\"quest\"><b>" + child.toString() + ": </b><span style=\"border: 2px solid #EBF707\">" + a.toString() + "</span>" + getOtherAnswers(child, a) + "</div>";
                                } else {
                                    text += "<div class=\"quest\"><b>" + child.toString() + ": </b><span>" + a.toString() + "</span>" + getOtherAnswers(child, a) + "</div>";
                                }

                                for (TerminologyObject kid : child.getChildren()) {
                                    Value ans = bb.getValue((ValueObject) kid);
                                    if (state.equals(Rating.State.ESTABLISHED) && test.contains(kid.toString() + ans.toString())) {
                                        text += "<div class=\"quest\"><b>" + kid.toString() + ": </b><span style=\"border: 2px solid #7CF56E\">" + ans.toString() + "</span>" + getOtherAnswers(kid, ans) + "</div>";
                                    } else if (state.equals(Rating.State.EXCLUDED) && test.contains(kid.toString() + ans.toString())) {
                                        text += "<div class=\"quest\"><b>" + kid.toString() + ": </b><span style=\"border: 2px solid #EB4242\">" + ans.toString() + "</span>" + getOtherAnswers(kid, ans) + "</div>";
                                    } else if ((state.equals(Rating.State.SUGGESTED) && test.contains(kid.toString() + ans.toString()))) {
                                        text += "<div class=\"quest\"><b>" + kid.toString() + ": </b><span style=\"border: 2px solid #EBF707\">" + ans.toString() + "</span>" + getOtherAnswers(kid, ans) + "</div>";
                                    } else {
                                        text += "<div class=\"quest\"><b>" + kid.toString() + ": </b><span>" + ans.toString() + "</span>" + getOtherAnswers(kid, ans) + "</div>";
                                    }
                                }
                            }

                        } else { //if the question in the container is not in the list of answerd questions add all answers aternatives
                            text = text + "<div class=\"quest\"><b>" + cont.toString() + ": </b>" + getAllAnswers(cont) + "</div>";
                        }
                    } else {//if container is empty or another contains an other container
                        text = text + "<br /><div class=\"container\"><b>" + container.toString() + " : " + "No Question in this container" + "</b>";
                    }
                }

            }

        }
        return score + "|" + text; //return score and clarification content
    }
}