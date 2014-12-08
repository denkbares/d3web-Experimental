/*
 * Copyright (C) 2014 denkbares GmbH, Germany
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

package de.knowwe.ontology.browser;

import de.knowwe.core.user.UserContext;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.sparql.RenderMode;
import de.knowwe.rdf2go.sparql.SparqlResultNodeRenderer;
import de.knowwe.rdf2go.sparql.TermDefinitionLinkNodeRenderer;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Jochen Reutelshoefer (denkbares GmbH)
 * @created 28.10.14.
 */
public class SparqlResultNodeDragRenderer implements SparqlResultNodeRenderer {
    @Override
    public String renderNode(String text, String variable, UserContext user, Rdf2GoCore core, RenderMode mode) {

        final boolean isValidURI = isURI(text);
        final String reducedURI = Rdf2GoUtils.reduceNamespace(core, text);
        StringBuilder result = new StringBuilder();
        final boolean isConceptNode = isValidURI && reducedURI != text;
        if(isConceptNode && mode.equals(RenderMode.HTML)) {
            String identifier = reducedURI.replace(":", "#");
            result.append("<div style='display:inline;position:static !important;' class='dragSparqlResultNode'>");
            result.append("<div class='termID'>" + identifier + "</div>");
        }
        result.append(new TermDefinitionLinkNodeRenderer().renderNode(text, variable, user, core, mode));
        if(isConceptNode  && mode.equals(RenderMode.HTML)) {
            result.append("</div>");
        }
        return result.toString();
    }

    private boolean isURI(String text) {
        try {
            new URI(text);
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean allowFollowUpRenderer() {
        return false;
    }
}
