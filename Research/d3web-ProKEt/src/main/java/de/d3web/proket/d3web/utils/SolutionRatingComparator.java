/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.utils;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.scoring.HeuristicRating;
import de.d3web.scoring.inference.PSMethodHeuristic;
import de.d3web.xcl.inference.PSMethodXCL;
import java.util.Collection;
import java.util.Comparator;

/**
 * Tailored Comparator Class for comparing two d3web case file objects as to
 * which one was created earlier by using the creation date as stored within the
 * d3web case file
 *
 * @author Martina Freiberg @date Dec 2012
 */
public class SolutionRatingComparator implements Comparator<Solution> {

    private Session d3webs;

    public SolutionRatingComparator(Session d3webs) {
        this.d3webs = d3webs;
    }

    @Override
    public int compare(Solution s1, Solution s2) {

        Blackboard bb = d3webs.getBlackboard();
        /*
         * get scoring, dependent on applied problem solving method
         */
        Double score1 = 0.0;
        Collection<PSMethod> contributingPSMethods =
                bb.getContributingPSMethods(s1);
        for (PSMethod psmethod : contributingPSMethods) {
            if (psmethod.getClass().equals(PSMethodHeuristic.class)) {
                score1 = ((HeuristicRating) bb.getRating(s1)).getScore();
                break;
            } // preparation for x
            else if (psmethod.equals(PSMethodXCL.class)) {
                System.out.println("xcl scoring");
                break;
            }
        }

        Double score2 = 0.0;
        contributingPSMethods =
                bb.getContributingPSMethods(s2);
        for (PSMethod psmethod : contributingPSMethods) {
            if (psmethod.getClass().equals(PSMethodHeuristic.class)) {
                score2 = ((HeuristicRating) bb.getRating(s2)).getScore();
                break;
            } // preparation for x
            else if (psmethod.equals(PSMethodXCL.class)) {
                System.out.println("xcl scoring");
                break;
            }
        }

        return score2.compareTo(score1);
    }
}
