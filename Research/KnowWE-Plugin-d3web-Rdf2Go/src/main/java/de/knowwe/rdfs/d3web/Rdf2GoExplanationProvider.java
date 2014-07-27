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

package de.knowwe.rdfs.d3web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;

import de.d3web.strings.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.sparql.utils.SparqlQuery;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

/**
 * Provides explanation utility methods.
 * <p/>
 * Created by Albrecht Striffler (denkbares GmbH) on 22.07.14.
 */
public class Rdf2GoExplanationProvider {

    private final Rdf2GoCore core;
    private final String sessionId;

    public Rdf2GoExplanationProvider(Rdf2GoCore core, String sessionId) {
        this.core = core;
        this.sessionId = sessionId;
    }

    public Collection<Fact> getSources(String objectName) {
        Collection<Fact> facts = new ArrayList<Fact>();
        SparqlQuery query = new SparqlQuery().SELECT("?SourceObjectName ?SourceValue ?Agent ?AgentType")
                .WHERE("<lns:" + sessionId + "> lns:hasFact ?Fact")
                .AND_WHERE("?Fact lns:hasTerminologyObject <lns:" + Strings.encodeURL(objectName) + ">")
                .AND_WHERE("?Fact prov:wasDerivedFrom ?OtherFact")
                .AND_WHERE("?OtherFact lns:hasTerminologyObject ?ObjectUri")
                .AND_WHERE("?ObjectUri rdfs:label ?SourceObjectName")
                .AND_WHERE("?OtherFact lns:hasValue ?SourceValue")
                .AND_WHERE("?OtherFact prov:wasAttributedTo ?Agent")
                .AND_WHERE("?Agent rdf:type ?AgentType");
        QueryResultTable queryRows = core.sparqlSelect(query.toSparql(core), false, 100);
        for (QueryRow queryRow : queryRows) {
            Fact fact = new Fact();
            fact.terminologyObject = queryRow.getValue("SourceObjectName").toString();
            fact.value = clean(queryRow.getValue("SourceValue").toString());
            fact.agent = clean(queryRow.getValue("Agent").toString());
            fact.agentType = clean(queryRow.getValue("AgentType").toString());
            facts.add(fact);
        }
        return facts;
    }

    private String clean(String text) {
        text = Rdf2GoUtils.trimNamespace(core, text);
        text = Rdf2GoUtils.trimDataType(core, text);
        return text;
    }

    public Fact getFact(String objectName) {
        SparqlQuery query = new SparqlQuery().SELECT("?Value ?Agent ?AgentType")
                .WHERE("<lns:" + sessionId + "> lns:hasFact ?Fact")
                .AND_WHERE("?Fact lns:hasTerminologyObject <lns:" + Strings.encodeURL(objectName) + ">")
                .AND_WHERE("?Fact lns:hasValue ?Value")
                .AND_WHERE("?Fact prov:wasAttributedTo ?Agent")
                .AND_WHERE("?Agent rdf:type ?AgentType");
        QueryResultTable queryRows = core.sparqlSelect(query.toSparql(core), false, 100);
        ClosableIterator<QueryRow> iterator = queryRows.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("No Fact found for object name '" + objectName + "'");
        }
        Fact fact = new Fact();
        fact.terminologyObject = objectName;
        QueryRow queryRow = iterator.next();
        fact.value = clean(queryRow.getValue("Value").toString());
        fact.agent = clean(queryRow.getValue("Agent").toString());
        fact.agentType = clean(queryRow.getValue("AgentType").toString());
        if (iterator.hasNext()) {
            System.out.println(fact.value + ", " + fact.agent + ", " + fact.agentType);
            QueryRow next = iterator.next();
            System.out.println(clean(next.getValue("Value").toString()) + ", " + clean(next.getValue("Agent")
                    .toString()) + ", " + clean(next.getValue("AgentType").toString()));
            //throw new IllegalArgumentException("Multiple Facts found for object name '" + objectName + "'");
        }
        return fact;
    }

    public Collection<Fact> getLeafSources(String objectName) {
        HashSet<Fact> leaves = new HashSet<Fact>();
        getLeafSources(getFact(objectName), leaves);
        return leaves;
    }

    private void getLeafSources(Fact currentFact, Set<Fact> leaves) {
        Collection<Fact> sources = getSources(currentFact.terminologyObject);
        if (sources.isEmpty()) {
            leaves.add(currentFact);
            return;
        }
        for (Fact source : sources) {
            getLeafSources(source, leaves);
        }
    }

    public static class Fact {

        public String terminologyObject;
        public String value;
        public String agent;
        public String agentType;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Fact fact = (Fact) o;

            if (agent != null ? !agent.equals(fact.agent) : fact.agent != null) return false;
            if (agentType != null ? !agentType.equals(fact.agentType) : fact.agentType != null) return false;
            if (terminologyObject != null ? !terminologyObject.equals(fact.terminologyObject) : fact.terminologyObject != null) {
                return false;
            }
            if (value != null ? !value.equals(fact.value) : fact.value != null) return false;
            return true;
        }

        @Override
        public int hashCode() {
            int result = terminologyObject != null ? terminologyObject.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            result = 31 * result + (agent != null ? agent.hashCode() : 0);
            result = 31 * result + (agentType != null ? agentType.hashCode() : 0);
            return result;
        }
    }
}