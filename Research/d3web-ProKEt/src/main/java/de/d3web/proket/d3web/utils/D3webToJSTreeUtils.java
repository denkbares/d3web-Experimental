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

        if (!s.getName().contains("000")) {
            bui.append("<li id='" + s.getName() + "'>\n");
            bui.append("<a href='TODO'>" + s.getName() + "</a>\n");
        } else if (s.getName().contains("000")
                && s.getChildren().length > 0) {

            for (TerminologyObject cs : s.getChildren()) {
                Solution2JSTreeHTMLRecurse(cs, bui);
            }
        }

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

        if (!qc.getName().contains("000")) {
            bui.append("<li id='qsnavi_" + qc.getName() + "'>\n");
            bui.append("<a href='" + qc.getName() + "'>" + qc.getName() + "</a>\n");
        } else if (qc.getName().contains("000")
                && qc.getChildren().length > 0) {

            for (TerminologyObject cs : qc.getChildren()) {
                QContainer2JSTreeHTMLRecurse(cs, bui);
            }
        }

        if (qc.getChildren().length > 0) {

            for (TerminologyObject cs : qc.getChildren()) {
                bui.append("<ul>\n");

                QContainer2JSTreeHTMLRecurse(cs, bui);
                bui.append("</ul>\n");
            }

        }
        bui.append("</li>\n");
    }

}
