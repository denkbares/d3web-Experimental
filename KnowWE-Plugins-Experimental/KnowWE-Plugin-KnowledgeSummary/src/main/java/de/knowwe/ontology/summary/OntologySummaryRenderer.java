package de.knowwe.ontology.summary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;

import com.denkbares.semanticcore.TupleQueryResult;
import com.denkbares.utils.Log;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.ontology.compile.OntologyCompiler;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

/**
 * OntologySummaryRenderer counts and shows all classes and properties of an ontology
 * @author Veronika Sehne on 30.04.2014.
 */
public class OntologySummaryRenderer extends DefaultMarkupRenderer {

	@Override
	public void renderContents(Section<?> section, UserContext user, RenderResult result) {
		OntologyCompiler compiler = Compilers.getCompiler(section, OntologyCompiler.class);
		if(compiler != null) {
			de.knowwe.rdf2go.Rdf2GoCore core = compiler.getRdf2GoCore();
			String classQuery = "SELECT ?x WHERE { ?x rdf:type rdfs:Class." +
					"}";

			String propertyQuery = "SELECT ?x WHERE { ?x rdf:type rdf:Property." +
					"}";
			String localNamespace = core.getLocalNamespace();
			TupleQueryResult classResultTable = core.sparqlSelect(classQuery);
			TupleQueryResult propertyResultTable = core.sparqlSelect(propertyQuery);

			List<String> classes = new ArrayList<>();
			List<String> properties = new ArrayList<>();

			for (BindingSet row : classResultTable) {
				Value x = row.getValue("x");
				classes.add(x.stringValue());
			}

			for (BindingSet row : propertyResultTable) {
				Value x = row.getValue("x");
				properties.add(x.stringValue());
			}

			Iterator<String> classesIterator = classes.iterator();
			while (classesIterator.hasNext()) {
				String s = classesIterator.next();
				if (!s.startsWith(localNamespace)) {
					classesIterator.remove();
				}
			}

			Iterator<String> propertiesIterator = properties.iterator();
			while (propertiesIterator.hasNext()) {
				String s = propertiesIterator.next();
				if (!s.startsWith(localNamespace)) {
					propertiesIterator.remove();
				}
			}

			result.append("Classes: " + classes.size());
			for (String s : classes) {
				result.appendHtml("<br>");
				if (s.startsWith(localNamespace)) {
					s = Rdf2GoUtils.trimNamespace(core, s);
				}
				result.append("\t" + s);
			}

			result.appendHtml("<br>");

			result.append("Properties: " + properties.size());
			for (String s : properties) {
				result.appendHtml("<br>");
				if (s.startsWith(localNamespace)) {
					s = Rdf2GoUtils.trimNamespace(core, s);
				}
				result.append("\t" + s);
			}
		} else {
			Log.warning("no ontology found!");
		}

	}
}
