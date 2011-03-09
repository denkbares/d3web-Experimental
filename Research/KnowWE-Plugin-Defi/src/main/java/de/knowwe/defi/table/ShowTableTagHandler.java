package de.knowwe.defi.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.table.Table;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.user.UserContext;

public class ShowTableTagHandler extends AbstractTagHandler{

	public ShowTableTagHandler() {
		super("Befragungstabelle");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {
		String id = parameters.get("id");
		if(id == null) {
			return "Error: no table id specified!";
		}
		Section<DefineTableMarkup> myTable = findTableToShow(article, id);
		if(myTable != null) {
			return renderTable(myTable, userContext);
		}
		else {
			return "no table definition found for specified id: "+id;
		}
		
	}

	private String renderTable(Section<DefineTableMarkup> myTable, UserContext user) {
		Section<Table> table = Sections.findSuccessor(myTable,Table.class);
		StringBuilder string = new StringBuilder();
		DelegateRenderer.getInstance().render(myTable.getArticle(), table.getFather(),
				user, string);
		return string.toString();
	}

	private Section<DefineTableMarkup> findTableToShow(KnowWEArticle article, String id) {
		Collection<KnowWEArticle> articles = KnowWEEnvironment.getInstance().getArticleManager(article.getWeb()).getArticles();
		for (KnowWEArticle knowWEArticle : articles) {
			List<Section<DefineTableMarkup>> tables = new ArrayList<Section<DefineTableMarkup>>();
			Sections.findSuccessorsOfType(knowWEArticle.getSection(), DefineTableMarkup.class,
					tables);
			for (Section<DefineTableMarkup> table : tables) {
				String tableID = table.get().getAnnotation(table, "id");
				if(tableID != null) {
					if (tableID.equals(id)) {
						return table;
					}
				}
				
			}
		}
		return null;
	}

}
