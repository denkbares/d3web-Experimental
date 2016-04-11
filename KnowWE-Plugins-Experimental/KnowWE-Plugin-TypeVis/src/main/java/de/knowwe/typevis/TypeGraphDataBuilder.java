/*
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
package de.knowwe.typevis;

import java.util.LinkedList;
import java.util.List;

import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.visualization.ConceptNode;
import de.knowwe.visualization.Config;
import de.knowwe.visualization.Edge;
import de.knowwe.visualization.GraphDataBuilder;

/**
 * @author Johanna Latt
 * @created 10.10.2013
 */
public class TypeGraphDataBuilder extends GraphDataBuilder {

    private int depth = 0;

    private String concept;

    // list of all instances of the requested type and its subclasses
    private List<Type> conceptTypes;
    // list of already visited types to prevent endless loops
    private List<Type> visited;

    private Class<?> visTypeClass;
    // whether the class of the requested concept could be found or not
    private boolean couldFindClass = true;

    public TypeGraphDataBuilder(Section<?> section, Config config, LinkToTermDefinitionProvider uriProvider) {
        initialiseData(section, config, uriProvider);
    }

    @Override
    public void selectGraphData() {
        getConceptName();

        findClassOfConcept();
        findChildAndSubclassTypes();

        for (Type t : conceptTypes) {
            insertMainConcept(t);
            // if requested, the successors are added to the source
            if (config.getSuccessors() > 0) {
                addSuccessors(t);
            }
        }
    }

    /**
     * Get the concept name without the package declaration.
     *
     * @created 30.10.2013
     */
    private void getConceptName() {
        String conceptWithPackageDeclaration = config.getConcepts().iterator().next();

        // get concept name without package declaration (if existant)
        String[] parts = conceptWithPackageDeclaration.split("\\.");
        concept = parts[parts.length - 1];
    }

    private void findClassOfConcept() {
        try {
            visTypeClass = Class.forName(config.getConcepts().iterator().next());
        } catch (ClassNotFoundException e) {
            couldFindClass = false;
            e.printStackTrace();
        }
    }

    private void findChildAndSubclassTypes() {
        conceptTypes = new LinkedList<>();
        visited = new LinkedList<>();

        RootType rt = RootType.getInstance();
        if (rt.getName().equals(concept)) conceptTypes.add(rt);

        addAllMatchingConceptTypes(rt);
    }

    /**
     * Recursively finds all Types in the type hierarchy that match the
     * requested type-String or any subclass of it. Those are added to the
     * conceptType-List. Furthermore the predecessors of the requested type and
     * its subclasses are added to the graph.
     *
     * @created 21.10.2013
     */
    private void addAllMatchingConceptTypes(Type type) {
        List<Type> children = type.getChildrenTypes();
        if (children == null) return;

        for (Type child : children) {
            // prevent endless loops
            if (visited.contains(child)) continue;
            visited.add(child);

            // check for inheritance
            if (couldFindClass) {
                Class<?> currentTreeTypeClass = child.getClass();
                if (visTypeClass.isAssignableFrom(currentTreeTypeClass)) {
                    conceptTypes.add(child);
                    // add predecessor
                    addConcept(type, child, NODE_TYPE.INSTANCE);
                }
            }

            if (child.getName().equals(concept) && !conceptTypes.contains(child)) {
                conceptTypes.add(child);
                // add predecessor
                addConcept(type, child, NODE_TYPE.INSTANCE);
            }

            addAllMatchingConceptTypes(child);
        }
    }

    public void insertMainConcept(Type concept) {
        // the main concept is inserted
        ConceptNode conceptNode = new ConceptNode(concept.getName() + concept.hashCode(),
                NODE_TYPE.INSTANCE,
                concept.getName(), concept.getName(), "");
        conceptNode.setRoot(true);
        data.addConcept(conceptNode);
    }

    private void addSuccessors(Type concept) {
        List<Type> children = concept.getChildrenTypes();
        if (children == null) return;
        for (Type child : children) {
            if (excludedNode(child.getName())) {
                continue;
            }

            addConcept(concept, child, NODE_TYPE.INSTANCE);

            depth++;
            if (depth < config.getSuccessors()) {
                addSuccessors(child);
            }
            if (depth == config.getSuccessors()) {
                addOutgoingEdgesSuccessors(child);
            }
            depth--;
        }
    }


    private void addOutgoingEdgesSuccessors(Type concept) {
        List<Type> children = concept.getChildrenTypes();
        if (children == null) return;
        for (Type child : children) {
            if (excludedNode(child.getName())) {
                continue;
            }

            addOuterConcept(concept, child);
        }
    }


    private void addConcept(Type fromType, Type toType, NODE_TYPE type) {
        String fromLabel = fromType.getName();
        String toLabel = toType.getName();
        String fromName = fromLabel + fromType.hashCode();
        String toName = toLabel + toType.hashCode();

        ConceptNode toNode;
        ConceptNode fromNode;

        toNode = data.getConcept(toName);
        if (toNode == null) {
            toNode = new ConceptNode(toName, type, createConceptURL(toLabel), toLabel, "");
            data.addConcept(toNode);
        }
        toNode.setOuter(false);

        fromNode = data.getConcept(fromName);
        if (fromNode == null) {
            fromNode = new ConceptNode(fromName, type, createConceptURL(fromLabel), fromLabel, "");
            data.addConcept(fromNode);
        }
        fromNode.setOuter(false);

        Edge newLineRelationsKey = new Edge(fromNode, "", toNode);
        data.addEdge(newLineRelationsKey);
    }

    private void addOuterConcept(Type fromType, Type toType) {
        String fromLabel = fromType.getName();
        String toLabel = toType.getName();
        String fromName = fromLabel + fromType.hashCode();
        String toName = toLabel + toType.hashCode();

        if (toLabel.equals("")) {
            return;
        }

        boolean nodeIsNew = !data.getConceptDeclarations().contains(new ConceptNode(toName));

        ConceptNode toNode = data.getConcept(toName);
        if (toNode == null) {
            toNode = new ConceptNode(toName, NODE_TYPE.UNDEFINED,
                    createConceptURL(toLabel),
                    toLabel, "");
        }

        ConceptNode fromNode = data.getConcept(fromName);
        if (fromNode == null) {
            fromNode = new ConceptNode(fromName, NODE_TYPE.UNDEFINED,
                    createConceptURL(fromLabel),
                    fromLabel, "");
        }

        Edge edge = new Edge(fromNode, "", toNode);

        boolean edgeIsNew = !data.getAllEdges().contains(edge);

        if (config.isShowOutgoingEdges()) {
            if (nodeIsNew) {
                toNode.setOuter(true);
                data.addConcept(toNode);
            }
            if (edgeIsNew) {
                data.addEdge(edge);
            }
        } else {
            // do not show outgoing edges
            if (!nodeIsNew) {
                // but show if its node is internal one already
                data.addEdge(edge);
            }
        }
    }

}
