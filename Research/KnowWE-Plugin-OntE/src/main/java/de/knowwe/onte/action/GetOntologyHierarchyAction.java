package de.knowwe.onte.action;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.owlapi.query.OWLApiQueryEngine;

/**
 * Loads from the ontology for a given entity type the complete hierarchy.
 * Possible entities are OWLClass, OWLObjectProperty and OWLDataProperty.
 *
 * @author Stefan Mark
 * @created 14.12.2011
 */
public class GetOntologyHierarchyAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		OWLApiQueryEngine engine = new OWLApiQueryEngine(new SimpleShortFormProvider());
		LinkedHashMap<OWLEntity, Set<OWLEntity>> hierarchy = new LinkedHashMap<OWLEntity, Set<OWLEntity>>();

		OWLEntity topEntity = null;
		StringBuilder json = new StringBuilder();
		String classification = context.getParameter("classification");

		OWLDataFactory factory = OWLAPIConnector.getGlobalInstance().getManager().getOWLDataFactory();

		if (classification.equals("OWLClass")) {
			topEntity = factory.getOWLThing();
			engine.getInferredClassHierarchie(hierarchy, (OWLClass) topEntity, null);
		}
		else if (classification.equals("OWLObjectProperty")) {
			topEntity = factory.getOWLTopObjectProperty();
			engine.getInferredObjectPropertyHierarchy(hierarchy, (OWLObjectProperty) topEntity,
					null);
		}
		else if (classification.equals("OWLDataProperty")) {
			topEntity = factory.getOWLTopDataProperty();
			engine.getInferredDataPropertyHierarchy(hierarchy, (OWLDataProperty) topEntity, null);
		}
		else if (classification.equals("OWLIndividual")) {

		}

		json.append("{");

		if (classification.equals("OWLClass")) {
			json.append("\"optionalRoot\" : { ");
			constructOptionalUnsatNodes(json, classification);
			json.append("},");
		}

		constructJSONString(hierarchy, topEntity, json, classification);
		json.append("}");

		context.getWriter().write(json.toString());
	}

	/**
	 * Creates the JSON string used to communicate with the client.
	 *
	 * @created 02.12.2011
	 * @param hierarchy
	 * @param owlClass
	 * @param json
	 */
	public void constructJSONString(Map<OWLEntity, Set<OWLEntity>> hierarchy, OWLEntity owlClass, StringBuilder json, String type) {
		json.append("\"name\" : \"").append(owlClass.getIRI().getFragment()).append("\",\n");
		json.append("\"type\" : \"").append(type).append("\"");

		if (hierarchy.containsKey(owlClass)) {
			Set<OWLEntity> children = hierarchy.get(owlClass);

			if (!children.isEmpty()) {
				json.append(",\n\"children\" : [");

				Iterator<OWLEntity> it = children.iterator();
				while (it.hasNext()) {
					OWLEntity child = it.next();

					json.append("{\n");
					constructJSONString(hierarchy, child, json, type);
					json.append("}");

					if (it.hasNext()) {
						json.append(",\n");
					}
				}
				json.append("]");
			}
		}
	}

	private void constructOptionalUnsatNodes(StringBuilder json, String classification) {

		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		OWLReasoner reasoner = connector.getReasoner();

		Node<OWLClass> unsatNodes = reasoner.getUnsatisfiableClasses();
		if (!unsatNodes.getEntitiesMinusBottom().isEmpty()) {

			StringBuilder nothingConcepts = new StringBuilder();
			nothingConcepts.append("\"name\" : \"Nothing\", \"type\" : \"")
					.append(classification).append("\", \"children\" : [");

			Iterator<OWLClass> it = unsatNodes.getEntitiesMinusBottom().iterator();
			while (it.hasNext()) {
				OWLClass owlClass = it.next();
				nothingConcepts.append("{");
				nothingConcepts.append("\"name\" : \"").append(owlClass.getIRI().getFragment()).append(
						"\",");
				nothingConcepts.append("\"type\" : \"").append(classification).append("\",");
				nothingConcepts.append("\"color\" : \"#FF0000\"");
				nothingConcepts.append("}");
				if (it.hasNext()) {
					nothingConcepts.append(",");
				}
			}
			nothingConcepts.append("]");
			json.append(nothingConcepts);
		}
	}
}
