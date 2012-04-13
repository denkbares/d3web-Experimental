package de.knowwe.usersupport.poi;

import java.util.List;

public class DefaultUserSupportWordImportConfiguration implements IWordImportConfiguration {

	@Override
	public String cleanHTMLLine(String s, List<String> docLines) {
		s = s.replaceAll("\\<li\\>\\<p\\>", "\\<li\\>");
		s = s.replaceAll("\\</p\\>\\</li\\>", "\\</li\\>");

		// bullet lists to wiki-syntax
		s = s.replaceAll("\\<ol\\>", "");
		s = s.replaceAll("\\</ol\\>", "");
		s = s.replaceAll("\\<li\\>", "*");
		s = s.replaceAll("\\</li\\>", "\\<br/\\>");

		// italic
		s = s.replaceAll("\\<i\\>", "''");
		s = s.replaceAll("\\</i\\>", "''");

		// bold
		s = s.replaceAll("\\<b\\>", "__");
		s = s.replaceAll("\\</b\\>", "__");

		s = s.replaceAll("\\<p\\>", "");
		s = s.replaceAll("\\</p\\>", "\\<br/\\>");

		String[] lines = s.split("\\<br/\\>");
		for (String l : lines)
			docLines.add(l);
		return s;
	}

	@Override
	public String createWikiMarkup(List<String> lines) {
		StringBuilder docText = new StringBuilder();
		for (String line : lines)
		{
			docText.append(line + "\r\n");
		}
		return docText.toString();
	}

}
