/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.kdom.bibtex.verbalizer;

import java.io.IOException;
import java.util.Map;

import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;

import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexString;
import bibtex.parser.ParseException;

import de.d3web.we.biolog.utils.BiologUtils;
import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderManager.RenderingFormat;

/**
 * @author Fabian Haupt
 * 
 */
public class BibtexHtmlRenderer implements BibTexRenderer {

	public static String newline = System.getProperty("line.separator");
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seede.d3web.we.kdom.bibtex.verbalizer.BibTexRenderer#
	 * getSupportedClassesForVerbalization()
	 */
	@Override
	public Class[] getSupportedClassesForVerbalization() {
		Class[] supported = { BibtexEntry.class };
		return supported;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.d3web.we.kdom.bibtex.verbalizer.BibTexRenderer#
	 * getSupportedRenderingTargets()
	 */
	@Override
	public RenderingFormat[] getSupportedRenderingTargets() {
		RenderingFormat[] formats = { RenderingFormat.HTML };
		return formats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderer#verbalize(java.lang
	 * .Object,
	 * de.d3web.we.kdom.bibtex.verbalizer.BibTexRenderManager.RenderingFormat,
	 * java.util.Map)
	 */
	@Override
	public Object render(Object o, RenderingFormat targetFormat,
			Map<String, Object> parameter) {
		StringBuffer buffy = new StringBuffer();
		BibtexEntry bte = (BibtexEntry) o;
		if (bte.getEntryKey() == null)
			bte.setEntryKey(Integer.toString(bte.hashCode()));
		SimpleBibTeXParser sparser = new SimpleBibTeXParser();
		BibTex bt;
		buffy.append("<p>");
		buffy.append("<table border=\"0\">");
		try {
			bt = sparser.parseBibTeX(bte.toString());
			
			///////////////////////////////////////////////////////
			if (bt.getTitle() != null) {
				
				buffy.append("<tr>");
				
				buffy.append("<a name=\"" + bt.getBibtexKey() + "\"></a>");
				
				// Title
				buffy.append(	"<strong>"
								+ bt.getTitle()
								+ "</strong></tr>");
				
				// Authors
				buffy.append("<tr><td>Author(s)</td>");
				if (bt.getAuthor() != null)
					buffy.append("<td>" + bt.getAuthor() + "</td>");
				buffy.append("</tr>");

				// in: Journal=Agrarwirtschaft, number=2, volume=49, year=2000
				buffy.append("<tr><td>in:</td><td>");
				if (bt.getJournal() != null)
					buffy.append(bt.getJournal() + ", ");
				
				if (bt.getNumber() != null)
					buffy.append(bt.getNumber() + ", ");
				
				if (bt.getVolume() != null)
					buffy.append(bt.getVolume() + ", ");
				
				if (bt.getYear() != null)
					buffy.append(bt.getYear());
				
				if ( buffy.charAt(buffy.length()-1) == ',' )
					buffy.deleteCharAt(buffy.length()-1);
				
				buffy.append("</td></tr>");
				
				// keywords
				BibtexString field;
				if ((field = (BibtexString)bte.getFieldValue("keywords")) != null) {
					buffy.append("<tr><td>keywords</td><td>");
					String con = field.getContent();
					for (String cur:con.split(","))
						buffy.append(cur + ",");
					
					buffy.deleteCharAt(buffy.length()-1);
					
					buffy.append("</td>");
				}
				
				// abstract
				if (bt.getAbstract() != null)
					buffy.append("<tr><td>abstract:</td><td>" + bt.getAbstract() + "</td></tr>");

				// downloadlink
				buffy.append("<a href=\"BibTexDownload.jsp?nodeID="
								+ parameter.get("nodeID")
								+ "&bibID="
								+ bte.getEntryKey()
								+ "&"
								+ KnowWEAttributes.TOPIC
								+ "="
								+ parameter.get("topic")
								+ "&filename=bibtex.bib\"><img width=16 height=16 title=\"Bibtex download\" src=\"KnowWEExtension/images/bibtex.png\" /></a>");
			}
		} catch (ParseException e) {
			buffy.append(e.toString());
		} catch (IOException e) {
			buffy.append(e.toString());
		}
		buffy.append("</table>");
		buffy.append("</p>");
		return BiologUtils.replaceBibTeX(buffy.toString());
	}

}
