/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.d3web.proket.d3web.utils;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Solution;

/**
 *
 * @author Martina Freiberg @date September 2012
 */
public class D3webToJSTreeUtils {

    public static String getJSTreeHTMLFromD3webSolutions(KnowledgeBase kb) {

        StringBuilder bui = new StringBuilder();

        TerminologyObject roots = kb.getRootSolution();
        if(roots.getName().contains("000")){
            roots = roots.getChildren()[0];
        }
        Solution2JSTreeHTMLRecurse(roots, bui);

        return bui.toString();
    }

    public static String getJSTreeHTMLFromD3webQuestionnaires(KnowledgeBase kb) {

        StringBuilder bui = new StringBuilder();

        TerminologyObject root = kb.getRootQASet();
        QContainer2JSTreeHTMLRecurse(root, bui);

        return bui.toString();
    }

    private static void Solution2JSTreeHTMLRecurse(TerminologyObject s, StringBuilder bui) {

        bui.append("<li id='" + s.getName() + "'>\n");
        bui.append("<a href='TODO'>" + s.getName() + "</a>\n");

        if (s.getChildren().length > 0) {

            for (TerminologyObject cs : s.getChildren()) {
                bui.append("<ul>\n");

                Solution2JSTreeHTMLRecurse(cs, bui);
                bui.append("</ul>\n");
            }

        }
        bui.append("</li>\n");
    }

    private static void QContainer2JSTreeHTMLRecurse(TerminologyObject qc, StringBuilder bui) {

        bui.append("<li>\n");
        bui.append("<a href='TODO'>" + qc.getName() + "</a>\n");

        if (qc.getChildren().length > 0) {

            for (TerminologyObject cqcon : qc.getChildren()) {

                if (cqcon instanceof QContainer) {
                    bui.append("<ul>\n");

                    QContainer2JSTreeHTMLRecurse(cqcon, bui);

                    bui.append("</ul>\n");
                }
            }
        }
        bui.append("</li>\n");
    }

    public static void main(String[] args) {
        KnowledgeBase kb = new KnowledgeBase();
        Solution roots = new Solution(kb, "Root Solution");
        Solution cr1 = new Solution(roots, "Child 1 of Root");
        Solution cr2 = new Solution(roots, "Child 2 of Root");
        Solution cr21 = new Solution(cr2, "Child 1 of Root Child2");
        kb.setRootSolution(roots);

        System.out.println(getJSTreeHTMLFromD3webSolutions(kb));
        
        QContainer rootQC = new QContainer(kb, "Root QContainer");
        QContainer c1 = new QContainer(rootQC, "Child 1 of Root QC");
        QContainer c2 = new QContainer(rootQC, "Child 2 of Root QC");
        QContainer c11 = new QContainer(c1, "Child of Child 1");
        kb.setRootQASet(rootQC);
        
        System.out.println(getJSTreeHTMLFromD3webQuestionnaires(kb));
    }
}
