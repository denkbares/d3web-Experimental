package de.knowwe.rdf2go;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.ontoware.rdf2go.model.Statement;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.user.UserContext;

public class RDF2GoStatementCacheViewer extends AbstractHTMLTagHandler {

	public RDF2GoStatementCacheViewer() {
		super("statementcache");
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {
		Map<String, WeakHashMap<Section<? extends Type>, List<Statement>>> statementCache = Rdf2GoCore.getInstance().getStatementCache();

		StringBuffer buffy = new StringBuffer();

		for (String string : statementCache.keySet()) {
			buffy.append("\"" + string + "\":");
			buffy.append("<br>");
			WeakHashMap<Section<? extends Type>, List<Statement>> weakHashMap = statementCache.get(string);
			for (Section<?> sec : weakHashMap.keySet()) {
				buffy.append(sec.getID());
				buffy.append(":");
				for (Statement statement : weakHashMap.get(sec)) {
					buffy.append(statement.toString());
				}
			}
			buffy.append("<br>");

		}

		return buffy.toString();
	}

}
