package de.knowwe.usersupport.poi;

public class DashMarkupHtmlToWikiConverter implements IHtmlToWikiConverter {

	@Override
	public String getWikiText(String htmlText) {
		htmlText = htmlText.replaceAll("\\<li\\>\\<p\\>", "\\<li\\>");
		htmlText = htmlText.replaceAll("\\</p\\>\\</li\\>", "\\</li\\>");

		// bullet lists to wiki-syntax
		htmlText = htmlText.replaceAll("\\<ol\\>", "");
		htmlText = htmlText.replaceAll("\\</ol\\>", "");
		htmlText = htmlText.replaceAll("\\<li\\>", "*");
		htmlText = htmlText.replaceAll("\\</li\\>", "\\<br/\\>");

		// italic
		htmlText = htmlText.replaceAll("\\<i\\>", "''");
		htmlText = htmlText.replaceAll("\\</i\\>", "''");

		// bold
		htmlText = htmlText.replaceAll("\\<b\\>", "__");
		htmlText = htmlText.replaceAll("\\</b\\>", "__");

		htmlText = htmlText.replaceAll("\\<p\\>", "");
		htmlText = htmlText.replaceAll("\\</p\\>", "\\<br/\\>");
		
		htmlText = htmlText.replaceAll("\n", "");
		
		
		htmlText = htmlText.replaceAll("\\<br\\>", "\n");
		htmlText = htmlText.replaceAll("\\<br/\\>", "\n");
		
		return htmlText;
	}

}
