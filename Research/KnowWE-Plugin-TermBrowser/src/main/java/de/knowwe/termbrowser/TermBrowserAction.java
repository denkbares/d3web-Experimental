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

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.compile.PackageCompiler;
import de.knowwe.core.compile.packaging.PackageCompileType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.core.utils.PackageCompileLinkToTermDefinitionProvider;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;
import de.knowwe.termbrowser.util.TermBrowserUtils;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Jochen Reutelshöfer
 * @created 10.12.2012
 */
public class TermBrowserAction extends AbstractAction {

    @Override
    public void execute(UserActionContext context) throws IOException {
        String result = perform(context);
        if (result != null && context.getWriter() != null) {
            context.setContentType("text/html; charset=UTF-8");
            context.getWriter().write(result);
        }

    }

    public static Rdf2GoCompiler getCompiler(String master) {
        ArticleManager articleManager = Environment.getInstance().getArticleManager(
                Environment.DEFAULT_WEB);
        Rdf2GoCompiler compiler = null;
        Collection<PackageCompiler> compilers = Compilers.getCompilers(articleManager,
                PackageCompiler.class);
        for (PackageCompiler ontologyCompiler : compilers) {
            Section<? extends PackageCompileType> compileSection = ontologyCompiler.getCompileSection();
            Section<DefaultMarkupType> defaultMarkup = Sections.ancestor(compileSection,
                    DefaultMarkupType.class);
            if ((defaultMarkup.getText().contains(master) || defaultMarkup.getTitle().equals(master))
                    && ontologyCompiler instanceof Rdf2GoCompiler) {
                compiler = (Rdf2GoCompiler) ontologyCompiler;
            }
        }
        return compiler;
    }

    private String perform(UserActionContext context) {
        String command = context.getParameter("command");
        String term = context.getParameter("term");
        String type = context.getParameter("type");
        if (type.equals("undefined")) {
            type = "";
        }
        String label = context.getParameter("label");
        if (label == null || label.equals("undefined")) {
            label = "";
        }
        String master = TermBrowserMarkup.getCurrentTermbrowserMarkupMaster(context);

		/*
         * treat case when the semantic-autocompletion slot sends full URIs
		 */
        if (term != null && term.startsWith("http:")) {
            Rdf2GoCore core = getCompiler(master).getRdf2GoCore();
            term = Rdf2GoUtils.reduceNamespace(core, term);
            term = term.replace(":", "#");
        }

        if (term != null) {
            if (command.equals("searched")) {
                // update ranking weights
                TermSetManager.getInstance().termSearched(context, createTermIdentifier(term, type, label));
            } else if (command.equals("remove")) {
                // removes this concept from the list
                TermSetManager.getInstance().clearTerm(context, createTermIdentifier(term, type, label));
            } else if (command.equals("expand")) {
                // adds all sub-concepts of a concept to the list
                TermSetManager.getInstance().expandTerm(context, createTermIdentifier(term, type, label));
            } else if (command.equals("addParent")) {
                // adds all sub-concepts of a concept to the list
                TermSetManager.getInstance().addParentTerm(context, createTermIdentifier(term, type, label));
            } else if (command.equals("collapse")) {
                // removes all sub-concepts of a concepts from the list
                TermSetManager.getInstance().collapseTerm(context, createTermIdentifier(term, type, label));
            } else if (command.equals("collapseGraph")) {
                // stores user's collapse state on server
                TermSetManager.getInstance().collapseGraph(context);
            } else if (command.equals("openGraph")) {
                // stores user's collapse state on server
                TermSetManager.getInstance().openGraph(context);
            } else if (command.equals("toggleGraph")) {
                // stores user's collapse state on server
                TermSetManager.getInstance().toggleGraph(context);
            } else if (command.equals("collapseList")) {
                // stores user's collapse state on server
                TermSetManager.getInstance().collapseList(context);
            } else if (command.equals("openList")) {
                // stores user's collapse state on server
                TermSetManager.getInstance().openList(context);
            } else if (command.equals("clear")) {
                // clears the entire concept list/tree
                TermSetManager.getInstance().clearList(context);
            } else if (command.equals("toggle")) {
                // toggles the the collapse state of list/tree
                TermSetManager.getInstance().toggleCollapse(context);
            } else if (command.equals("open")) {
                // opens the page for this concept
                // is handled by a link
            }
        }
        LinkToTermDefinitionProvider linkProvider = null;
        if (master == null) {
            // TODO: completely remove dependency to IncrementalCompiler
            try {
                linkProvider = (LinkToTermDefinitionProvider) Class.forName(
                        "de.knowwe.compile.utils.IncrementalCompilerLinkToTermDefinitionProvider")
                        .newInstance();
            } catch (Exception e) {
                linkProvider = new LinkToTermDefinitionProvider() {
                    @Override
                    public String getLinkToTermDefinition(Identifier name, String masterArticle) {
                        return null;
                    }
                };
            }
        } else {
            linkProvider = new PackageCompileLinkToTermDefinitionProvider();
        }
        boolean abbreviationFlag = TermBrowserMarkup.getCurrentTermbrowserMarkupPrefixAbbreviationFlag(context);
        return RenderResult.unmask(new TermBrowserRenderer(context,
                linkProvider, master, abbreviationFlag).renderTermBrowser(), context);
    }


    private BrowserTerm createTermIdentifier(String term, String type, String label) {
        // TODO: caution: this will break if identifier names contain '#' !!
        String[] split = term.split("#");
        for (int i = 0; i < split.length; i++) {
            split[i] = Strings.unquote(split[i]);
        }

        // build type abbr.
        if (type != null && type.startsWith("http:")) {
            type = TermBrowserUtils.abbreviateTypeNameForURI(type);
        }
        return new BrowserTerm(type, label, split);
    }


}
