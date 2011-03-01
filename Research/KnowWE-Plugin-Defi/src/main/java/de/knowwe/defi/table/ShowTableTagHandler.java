package de.knowwe.defi.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;

public class ShowTableTagHandler extends AbstractTagHandler{

	public ShowTableTagHandler() {
		super("Befragungstabelle");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, KnowWEUserContext userContext, Map<String, String> parameters) {
		String id = parameters.get("id");
		if(id == null) {
			return "Error: no table id specified!";
		}
		Section<DefineTableMarkup> myTable = findTableToShow(article, id);
		if(myTable != null) {
			return renderTable(myTable);
		}
		else {
			return "no table definition found for specified id: "+id;
		}
		
	}

	private String renderTable(Section<DefineTableMarkup> myTable) {
		List<Section<ColumnType>> cols = new ArrayList<Section<ColumnType>>();
		Sections.findSuccessorsOfType(myTable, ColumnType.class, cols);
		if(cols.size() == 0) {
			return "no colums defined in table-defintions!";
		}
		StringBuffer buffy = new StringBuffer();
		buffy.append(KnowWEUtils.maskHTML("<table>"));
		buffy.append(KnowWEUtils.maskHTML("<tr>"));
		for (Section<ColumnType> section : cols) {
			buffy.append(KnowWEUtils.maskHTML("<td>"));
			buffy.append(KnowWEUtils.maskHTML(section.getOriginalText()));
			buffy.append(KnowWEUtils.maskHTML("</td>"));
		}
		for(int i = 0; i < cols.size(); i++) {
			buffy.append(KnowWEUtils.maskHTML("<td>"));
			buffy.append(KnowWEUtils.maskHTML("<input type='text'/>"));
			buffy.append(KnowWEUtils.maskHTML("</td>"));
		}
		buffy.append(KnowWEUtils.maskHTML("</tr>"));
		buffy.append(KnowWEUtils.maskHTML("</table>"));
		buffy.append("<input type='button' name='ok' value='ok' />");
		return buffy.toString();
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
					if(tableID == id) {
						return table;
					}
				}
				
			}
		}
		return null;
	}

}
