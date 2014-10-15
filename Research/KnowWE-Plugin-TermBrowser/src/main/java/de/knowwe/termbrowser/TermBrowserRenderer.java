/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.termbrowser;

import de.d3web.collections.PartialHierarchyTree;
import de.d3web.collections.PartialHierarchyTree.Node;
import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.termbrowser.autocompletion.WikiPageCompletionProvider;
import de.knowwe.termbrowser.ssc.ServiceModel;

import java.util.*;

/**
 * @author Jochen Reutelshöfer
 * @created 10.12.2012
 */
public class TermBrowserRenderer {

    public static final int THRESHOLD_MAX_TERM_NUMBER = 15;
    private static boolean zebra = false;

    private final UserContext user;
    private final de.knowwe.core.utils.LinkToTermDefinitionProvider linkProvider;
    private final String master;
    private TermBrowserHierarchy hierarchy = null;
    private boolean hierarchyPrefixAbbreviation = true;

    /**
     *
     */
    public TermBrowserRenderer(UserContext user, de.knowwe.core.utils.LinkToTermDefinitionProvider linkProvider, String master, boolean prefixAbbreviation) {
        this.user = user;
        this.linkProvider = linkProvider;
        this.master = master;
        this.hierarchyPrefixAbbreviation = prefixAbbreviation;
        hierarchy = new TermBrowserHierarchy(user);
    }

/*
    private OntologyCompletionProviderConfig getOntologyCompletionProviderConfig(Rdf2GoCore core, UserContext user) {
        final String ssc = "http://denkbares.com/SemanticServiceCore#";
        Collection<String> classes = Arrays.asList(new String[]{"http://denkbares.com/SemanticServiceCore#Wire", "http://denkbares.com/SemanticServiceCore#Unit", "http://denkbares.com/ILME#LRU", "http://denkbares.com/SemanticServiceCore#CableBundle", "http://denkbares.com/SemanticServiceCore#ConcreteComponent", "http://denkbares.com/SemanticServiceCore#HydraulicHose", "http://denkbares.com/SemanticServiceCore#Socket", "http://denkbares.com/SemanticServiceCore#UIComponent", "http://denkbares.com/ILME#STTE", "http://denkbares.com/SemanticServiceCore#Equipment", "http://denkbares.com/SemanticServiceCore#Machine", "http://denkbares.com/SemanticServiceCore#TestEquipment", "http://denkbares.com/SemanticServiceCore#MachineType", "http://denkbares.com/SemanticServiceCore#Plug", "http://denkbares.com/SemanticServiceCore#Pin", "http://denkbares.com/SemanticServiceCore#LogicalComponent", "http://denkbares.com/SemanticServiceCore#Malfunction", "http://denkbares.com/SemanticServiceCore#Context", "http://denkbares.com/SemanticServiceCore#Location", "http://denkbares.com/SemanticServiceCore#Prototype", "http://denkbares.com/SemanticServiceCore#Function"});
        Collection<String> textProperties =
                Arrays.asList(ServiceModel.skosPrefLabel, ServiceModel.skosAltLabel, ServiceModel.skosHiddenLabel, ServiceModel.skosLabel, ServiceModel.rdfsLabel);
        Collection<String> altLanguages = Arrays.asList("de", "en");
        List<String> titleProperties = Arrays.asList(ServiceModel.skosPrefLabel, ServiceModel.skosAltLabel, ServiceModel.rdfsLabel);
        String displayLanguage = "de";
        return new OntologyCompletionProviderConfig(classes, "<" + ssc + "type>",
                titleProperties, textProperties, displayLanguage, altLanguages);
    }
*/
    public String renderTermBrowser() {
        RenderResult string = new RenderResult(user);
        string.appendHtml("<div class='termbrowserframe'>");

        {
            string.appendHtml("<div class='termbrowserheader'>");

            // show collapse button, headline and clear button
            String toolTipCollapse = "Liste der Begriffe verbergen";
            String toolTipOpen = "Liste der Begriffe aufklappen";
            String toolTipClear = "Liste der Begriffe leeren";
            boolean collapsed = TermSetManager.getInstance().listIsCollapsed(user);
            String minusStyle = "";
            String plusStyle = "";
            String clearStyle = "float:right;";
            if (collapsed) {
                minusStyle = "display:none;";
                clearStyle += "display:none;";
            } else {
                plusStyle = "display:none;";
            }
            string.appendHtml("<span title='" + toolTipOpen + "' style='float:left;" + plusStyle
                    + "' class='ui-icon ui-icon-triangle-1-e showList hoverAction'></span>");
            string.appendHtml("<span title='" + toolTipCollapse + "' style='float:left;"
                    + minusStyle
                    + "' class='ui-icon ui-icon-triangle-1-s hideList hoverAction'></span>");

            // set box header title
            String title = "Benutzte Begriffe";
            String markupTitle = TermBrowserMarkup.getCurrentTermbrowserMarkupTitle(user);
            if (markupTitle != null) {
                title = markupTitle;
            }
            string.appendHtml("<span class='toggleList'>" + title + ":</span>");
            string.appendHtml("<span title='" + toolTipClear + "' style='float:left;" + clearStyle
                    + "' class='ui-icon ui-icon-minus clearList hoverAction'></span>");

            if (TermBrowserMarkup.getCurrentTermbrowserMarkupSearchSlotFlag(user)) {
                renderSearchSlot(string);
            }

			/*
             * render semantic autocompletion search slot
			 */
            boolean renderSemanticAutocompletionSlot = false;
            {

                /*
                 * init semantic autocompletion
				 */


                final Section<TermBrowserMarkup> termBrowserMarkup = TermBrowserMarkup.getTermBrowserMarkup(user);
               /* off
                final Collection<OntologyCompiler> compilers = Compilers.getCompilers(termBrowserMarkup, OntologyCompiler.class);
                if (compilers == null || compilers.size() == 0) return "No Ontology Compiler not found!";
                final Rdf2GoCore core = compilers.iterator().next().getRdf2GoCore();

                // CompletionProvider for the ontology
                OntologyCompletionProviderConfig config = getOntologyCompletionProviderConfig(core, user);
                RepositoryAdapter adapter = new Rdf2GoCoreRepositoryAdapter(core);
                CompletionProvider ontologyCompletionProvider = new OntologyCompletionProvider(adapter, config);

                // CompletionProvider for wiki pages
                CompletionProvider pageCompletionProvider = new WikiPageCompletionProvider();

                // init autocompletion
                SemanticAutocompletionManager.initializeSemanticAutocompletion(new ArrayList<Constraint>(), termBrowserMarkup, new CompletionProvider[]{ontologyCompletionProvider, pageCompletionProvider});

                renderSemanticAutocompletionSlot = true;

                */

				/*
				 * html from SemanticAutocompletionType
				 */
                if (renderSemanticAutocompletionSlot) {
                    string.appendHtml("<div>");
                    string.appendHtml("<span class='semanticautocompletionmaster' style='display:none'>");
                    string.append(master);
                    string.appendHtml("</span>");
                    string.appendHtml("<input sectionId='" + termBrowserMarkup.getID() + "' type='text' name='Semantic Autocompletion' value='' class='semanticautocompletion' ");
                    string.appendHtml("id='semanticautocompletion-");
                    string.appendHtml(Integer.toString(termBrowserMarkup.hashCode()));
                    string.appendHtml("' />");
                    string.appendHtml("</div>");
                }
            }

            int browserSize = THRESHOLD_MAX_TERM_NUMBER;

            int userSpecifiedSize = TermBrowserMarkup.getCurrentTermBrowserMarkupSize(user);
            // value 0 is when user has not set size annotation
            if (userSpecifiedSize != 0) {
                browserSize = userSpecifiedSize;
            }

            // render term list
            PartialHierarchyTree<RatedTerm> ratedTermTreeTop = TermSetManager.getInstance().getRatedTermTreeTop(
                    user,
                    browserSize);

            renderTermTree(string, ratedTermTreeTop, collapsed, linkProvider, master);
            string.appendHtml("</div>");
        }
        string.appendHtml("</div>");
        return string.toStringRaw();
    }

    /**
     * @param string
     * @created 02.10.2013
     */
    private void renderSearchSlot(RenderResult string) {
        string.appendHtml("<div class='ui-widget searchBox'>");
        {
            // search field
            string.appendHtml("<table><tr>");
            {
                string.appendHtml("<td>");
                string.appendHtml("<label for='conceptSearch' style='font-weight:normal;padding-right:0;font: 83%/140% Verdana,Arial,Helvetica,sans-serif;;'>Suche: </label>");
                string.appendHtml("</td>");
                string.appendHtml("<td><input id='conceptSearch' size='15' value='' /></td>");

            }
            string.appendHtml("</tr></table>");
        }
        string.appendHtml("</div>");

        string.appendHtml("<script>" +
                "jq$(document).ready(function() {" +
                // "$(function() {" +
                " var availableTags = [" +
                generateTermnames() +
                "];" +
                "jq$( \"#conceptSearch\" ).autocomplete({" +
                "source: availableTags," +
                "select: function( event, ui ) {" +
                "updateTermBrowser(event,ui);" +
                "}," +
                "});" +
                "});" +
                "</script>");

    }

    /**
     * @return
     * @created 10.12.2012
     */
    private String generateTermnames() {

        Collection<Identifier> allTermDefinitions = hierarchy.getAllTerms();
        StringBuilder builder = new StringBuilder();

        if (allTermDefinitions != null) {

            for (Identifier name : allTermDefinitions) {
                builder.append("\"").append(name.toExternalForm().replaceAll("\"", "")).append("\",\n");
            }
        }

        return builder.toString();
    }

    private void renderTermTree(RenderResult string, PartialHierarchyTree<RatedTerm> tree, boolean collapsed, LinkToTermDefinitionProvider linkProvider, String master) {

        String style = "";
        if (collapsed) {
            style = "display:none;";
        }
        string.appendHtml("<div id='droppable' style='" + style + "' class='termlist'>");

        Node<RatedTerm> root = tree.getRoot();
        List<Node<RatedTerm>> roots = root.getChildrenSortedDefault();
        if (roots.size() == 0) {
            string.appendHtml("<span style='padding-left: 75px;'>-keine-</span>");
        }
        for (Node<RatedTerm> rootConcept : roots) {

            renderConceptSubTree(rootConcept, 0, string, linkProvider, master);
        }
        string.appendHtml("</div>");

    }

    private void renderConceptSubTree(Node<RatedTerm> rootConcept, int level, RenderResult string, LinkToTermDefinitionProvider linkProvider, String master) {
        string.append("\n"); // append newline into html-code from time to time
        // to avoid jspwiki bug
        renderConcept(rootConcept, level, string, linkProvider, master);
        level += 1;
        List<de.d3web.collections.PartialHierarchyTree.Node<RatedTerm>> childrenSorted = rootConcept.getChildrenSortedDefault();
        for (Node<RatedTerm> node : childrenSorted) {
            renderConceptSubTree(node, level, string, linkProvider, master);
        }

    }

    private void renderConcept(Node<RatedTerm> t, int depth, RenderResult string, LinkToTermDefinitionProvider linkProvider, String master) {

        Identifier term = t.getData().getTerm();
        String lineStyleClass = "zebraline";
        if (!zebra) {
            zebra = true;
        } else {
            lineStyleClass = "zebraline-white";
            zebra = false;
        }

        String url = linkProvider.getLinkToTermDefinition(term, master);
        if (url == null) {
            url = KnowWEUtils.getURLLink(term.getLastPathElement());
        }
        // baseUrl + name;
        String divStyle = "";
        string.appendHtml("<div id='draggable' style='"
                + "'  class='termline "
                + lineStyleClass
                + "'>");
        {
            // table with 2 columns: term name and action buttons (visible hover
            // only)
            string.appendHtml("<table style='table-layout:fixed'>");
            {
                String conceptRowStyle = "entryLine";
                if (TermSetManager.getInstance().getLatestAddedTerm(user) != null
                        && TermSetManager.getInstance().getLatestAddedTerm(user).equals(term)) {
                    conceptRowStyle = "entryLine-highlighted";
                }
                string.appendHtml("<tr height='23px' class='" + conceptRowStyle + "'>");

                // calculate indents div for hierarchy visualization
                string.appendHtml(createIndent(depth));

                // append html code for the actions that can be performed for
                // each term
                {
                    string.appendHtml("<td style='min-width: 16px; padding:0px;'>");
                    insertActionButtonsPre(string, url, divStyle, t);
                    string.appendHtml("</td>");
                }

                // render actual line content
                {
                    string.appendHtml("<td style='width:90%;padding-left:2px;' class='termbrowser'>");

                    // using different font style depending on current hierarchy
                    // depth
                    if (url != null) {
                        string.appendHtml("<a href=\"" + url + "\">");
                    }
                    string.appendHtml("<div class='termname' title='" + term.toExternalForm()
                            + "' style='display:inline;"
                            + createStyle(depth)
                            + "'>");


                    // insert exact term name to be used for action calls
                    string.appendHtml("<div class='termID' >");
                    string.append(term.toExternalForm());
                    string.appendHtml("</div>");

                    // insert term name
                    String label = prepareDisplayLabel(t, term);

                    // add label for display
                    if(term instanceof BrowserTerm
                            && !Strings.isBlank(((BrowserTerm) term).getType())) {
                        string.appendHtml("<div class='typename'  style='display:inline;'>");
                        string.append(((BrowserTerm) term).getType());
                        string.appendHtml("</div>");

                    }

                    string.appendHtml(label);
                    string.appendHtml("</div>");
                    if (url != null) {
                        string.appendHtml("</a>");
                    }

                    string.appendHtml("</td>");
                }

                // append html code for the actions that can be performed for
                // each term
                {
                    string.appendHtml("<td style='min-width: 48px;max-width: 48px;padding:0px;'>");
                    insertActionButtonsPost(string, url, divStyle, term, depth);
                    string.appendHtml("</td>");
                }
                string.appendHtml("</tr>");
            }
            string.appendHtml("</table>");
        }
        string.appendHtml("</div>");
    }

    private String prepareDisplayLabel(Node<RatedTerm> t, Identifier term) {
        String label = getLabel(term);
        if (hierarchyPrefixAbbreviation) {
            Node<RatedTerm> parent = t.getParent();
            if (parent != null) {
                Identifier parentID = parent.getData().getTerm();
                String parentLabel = getLabel(parentID);
                if (label.startsWith(parentLabel)) {
                    label = label.substring(parentLabel.length());
                }
            }
        }
        // insert linebreaks for camelCase
        label = addCamlCaseLinebreaks(label);
        return label.trim();
    }

    private String addCamlCaseLinebreaks(String label) {

        int length = label.length();
        int i = 0;
        while (i < length) {

            if (Character.isLowerCase(label.charAt(i))) {
                if (i + 1 < length) {

                    char nextChar = label.charAt(i + 1);
                    boolean nextIsUpper = Character.isUpperCase(nextChar);
                    if (nextIsUpper) {
                        label = label.substring(0, i + 1) + "<wbr>" + label.substring(i + 1);
                        length += 5;
                        i += 5;

                    }
                }
            }
            i++;
        }

        return label;
    }

    private String getLabel(Identifier term) {
        String[] identifierParts = term.getPathElements();
        String label = term.toExternalForm();
        if (identifierParts.length == 2) {
            label = identifierParts[1];
        }

        label = label.replaceAll("\"", "");
        label = label.replaceAll("_", " ");

        return label;
    }

    /**
     * @param string
     * @param url
     * @param divStyle
     * @created 19.04.2013
     */
    private void insertActionButtonsPost(RenderResult string, String url, String divStyle, Identifier term, int level) {
        string.appendHtml("<table style='table-layout:fixed'>");
        string.appendHtml("<tr>");
        {
            insertAddParentButton(string, divStyle, term, level);
            insertObjectInfoLinkButton(string, divStyle, term);
            insertRemoveButton(string, divStyle);

        }
        string.appendHtml("</tr></table>");
    }

    /**
     * @param string
     * @param url
     * @param divStyle
     * @created 19.04.2013
     */
    private void insertActionButtonsPre(RenderResult string, String url, String divStyle, Node<RatedTerm> term) {
        boolean allChildrenShown = allChildrenShown(term);

        string.appendHtml("<table style='padding:0px;table-layout:fixed'>");
        string.appendHtml("<tr>");
        {
            if (allChildrenShown) {
                if (!(hierarchy.getChildren(term.getData().getTerm()).size() == 0)) {
                    insertCollapseButton(string, divStyle);
                }
            } else {
                if (!(hierarchy.getChildren(term.getData().getTerm()).size() == 0)) {

                    insertExpandButton(string, divStyle);
                }
            }
        }
        string.appendHtml("</tr></table>");
    }

    /**
     * @param term
     * @return
     * @created 03.05.2013
     */
    private boolean allChildrenShown(Node<RatedTerm> term) {
        List<Identifier> childrenConcepts = hierarchy.getChildren(term.getData().getTerm());
        for (Identifier childTerm : childrenConcepts) {
            if (!term.getChildren().contains(
                    new Node<RatedTerm>(new RatedTerm(childTerm)))) {
                return false;
            }
        }
        return true;
    }

    private static void insertCollapseButton(RenderResult string, String divStyle) {
        // expand concept, i.e. add all children to this list
        string.appendHtml("<td style='"
                + divStyle
                + "' class='termbrowser'><span class='ui-icon ui-icon-minus collapseConcept hoverAction' title='Unterbegriffe aus Liste entfernen' style='display:none;'></span></td>");
    }

    /**
     * @param string
     * @param divStyle
     * @created 03.05.2013
     */
    private void insertExpandButton(RenderResult string, String divStyle) {
        // expand concept, i.e. add all children to this list
        string.appendHtml("<td style='"
                + divStyle
                + "' class='termbrowser'><span class='ui-icon ui-icon-plus expandConcept' title='Unterbegriff in diese Liste aufnehmen'></span></td>");
    }

    /**
     * @param string
     * @param divStyle
     * @created 03.05.2013
     */
    private void insertRemoveButton(RenderResult string, String divStyle) {
        // delete concept from list
        string.appendHtml("<td style='"
                + divStyle
                + "' class='termbrowser'><span class='ui-icon ui-icon-circle-close removeConcept hoverAction' title='Begriff aus dieser Liste herausnehmen' style='display:none;'></span></td>");
    }

    /**
     * @param string
     * @param divStyle
     * @created 03.05.2013
     */
    private void insertAddParentButton(RenderResult string, String divStyle, Identifier term, int level) {
        if (level == 0) {

            List<Identifier> parentConcepts = hierarchy.getParents(term);
            if (parentConcepts.size() > 0) {

                // add parent concept to list
                string.appendHtml("<td style='"
                        + divStyle
                        + "' class='termbrowser'><span class='ui-icon ui-icon-arrowreturnthick-1-n addParentConcept hoverAction' title='Oberbegriff dieser Liste hinzufügen' style='display:none;'></span></td>");
            } else {
                // to have a fixed layout of the remaining button
                addDummyTableCell(string, 16);
            }
        } else {
            // to have a fixed layout of the remaining button
            addDummyTableCell(string, 16);
        }
    }

    /**
     * @param string
     * @param divStyle
     * @created 03.05.2013
     */
    private void insertObjectInfoLinkButton(RenderResult string, String divStyle, Identifier term) {
        // Wiki.jsp?page=ObjectInfoPage&termIdentifier="Damaged idle speed system"&objectname="Damaged idle speed system"
        // String encodedTerm = term.toString(); // maskTermForHTML(term);
        // String identifier = encodedTerm;
        // String objectName = encodedTerm;
        // String closingQuote = "";
        // if (term.contains("#")) {
        // String[] elements = term.split("#");
        // objectName = elements[1];
        // closingQuote = "\"";
        // identifier = identifier.replace("#", "#\"");
        // }
        // String encodedIdenifier = Strings.encodeURL(identifier);
        String linkToObjectInfoPage = KnowWEUtils.getURLLinkToObjectInfoPage(term);
        // String linkURL = "Wiki.jsp?page=ObjectInfoPage&termIdentifier="
        // + encodedIdenifier
        // + closingQuote + "&objectname=\"" + objectName + "\"";
        string.appendHtml("<td style='"
                + divStyle
                + "' class='termbrowser'><a href='"
                + linkToObjectInfoPage
                + "' ><span class='ui-icon ui-icon-info objectInfoLink hoverAction' title='Zur Info-Seite des Begriffs' style='display:none;'></span></a></td>");
    }

    /**
     * @param i
     * @created 03.06.2013
     */
    private static void addDummyTableCell(RenderResult string, int i) {

        string.appendHtml("<td style='' class='termbrowser'><span style='background-position: -96px -224px;' class='ui-icon' style='display:none;'></span></td>");

    }

    /**
     * @param depth
     * @return
     * @created 15.04.2013
     */
    private static String createStyle(int depth) {

        String result = "";

        if (depth == 0) {
            result += "font-weight:bold;";
        }
        if (depth == 1) {
            result += "font-size:100%;";
        }
        if (depth == 2) {
            result += "font-size:80%;";
        }
        if (depth == 3) {
            result += "font-size:60%;";
        }
        if (depth == 4) {
            result += "font-size:50%;";
        }

        return result;
    }

    private static String createIndent(int count) {
        int width = count * 15;
        String padding = "padding:0.01em;display:block;float:left;min-width:" + width + "px;";

        String result = "<td style='" + padding + "'></td>";

        return result;
    }

}
