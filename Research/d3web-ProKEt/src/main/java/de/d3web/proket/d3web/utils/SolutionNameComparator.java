/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.utils;

import de.d3web.core.knowledge.terminology.Solution;
import java.util.Comparator;

/**
 * Tailored Comparator Class for comparing two d3web case file objects as to
 * which one was created earlier by using the creation date as stored within the
 * d3web case file
 *
 * @author Martina Freiberg @date Dec 2012
 */
public class SolutionNameComparator implements Comparator<Solution> {

    @Override
    public int compare(Solution s1, Solution s2) {
     
      return s1.getName().compareTo(s2.getName());
    }

   
}
