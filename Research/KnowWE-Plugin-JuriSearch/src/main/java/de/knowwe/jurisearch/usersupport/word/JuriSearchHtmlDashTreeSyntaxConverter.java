package de.knowwe.jurisearch.usersupport.word;



import de.knowwe.usersupport.poi.DashMarkupHtmlToWikiConverter;
import de.knowwe.usersupport.poi.IHtmlToWikiConverter;


public class JuriSearchHtmlDashTreeSyntaxConverter implements IHtmlToWikiConverter {

	@Override
	public String getWikiText(String htmlText) {
		DashMarkupHtmlToWikiConverter x = new DashMarkupHtmlToWikiConverter();
		htmlText = x.getWikiText(htmlText);
		
		String[] lines = htmlText.split("\n");
		return generateDashTree(lines);
	}
	
	
	public String generateDashTree(String[] lines) {
		StringBuilder docText = new StringBuilder();
		boolean isTree = false;
		for (String line : lines)
		{
			if (line.startsWith("-") && !isTree)
			{
				isTree = true;
				docText.append("%%baum \r\n start \r\n");
			}
			if (!line.startsWith("-") && isTree)
			{
				docText.append("% \r\n");
				isTree = false;
			}

			docText.append(line + "\r\n");
		}
		return docText.toString();
	}
	

}
