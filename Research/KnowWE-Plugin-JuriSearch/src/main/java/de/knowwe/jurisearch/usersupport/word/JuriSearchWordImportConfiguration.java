package de.knowwe.jurisearch.usersupport.word;

import java.util.List;

import de.knowwe.usersupport.poi.IWordImportConfiguration;

public class JuriSearchWordImportConfiguration implements IWordImportConfiguration {

	@Override
	public String cleanHTMLLine(String s, List<String> docLines) {
		s = s.replaceAll("\\<p\\>FRAGE\\</p\\>", "FRAGE\\<br/\\>");
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
