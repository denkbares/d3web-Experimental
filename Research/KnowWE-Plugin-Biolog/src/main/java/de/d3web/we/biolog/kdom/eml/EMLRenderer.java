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

package de.d3web.we.biolog.kdom.eml;

import java.util.ArrayList;
import java.util.Collection;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.xml.AbstractXMLObjectType;
import de.d3web.we.kdom.xml.XMLContent;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;

/**
 * Renders the EML-data to a table-format.
 * 
 * @author smark
 */
public class EMLRenderer extends KnowWEDomRenderer {

	@Override
	public void render(KnowWEArticle article, Section sec,
			KnowWEUserContext user, StringBuilder string) {
		if(sec.getObjectType() instanceof EMLXMLType) {
			Section<AbstractXMLObjectType> s = sec;
			
			Section<? extends AbstractXMLObjectType> methodSection = AbstractXMLObjectType.findSubSectionOfTag("methods", s);
			
			if(methodSection != null) {
				this.renderCommonData(s, string, article, user);
				
				Object[][] tblCreator = {{"Nachname", "surName", "individualName"},
						{"Vorname", "givenName", "individualName"},
						{"Anrede", "salutation", "individualName"},
						{"E-Mail", "electronicMailAddress", "creator"},
						{"Organisation", "organizationName", "creator"},
						{"Organisation Strasse", "deliveryPoint", "creator"},
						{"Organisation PLZ", "postalCode", "creator"},
						{"Organisation Ort", "city", "creator"}};
				
				this.renderPersonDetails(s, string, article, user, tblCreator, "creator", 2);
				
				Object[][] tblContact = {{"Nachname", "surName", "individualName"},
						{"Vorname", "givenName", "individualName"},
						{"Anrede", "salutation", "individualName"},
						{"E-Mail", "electronicMailAddress", "contact"},
						{"Organisation", "organizationName", "contact"},
						{"Organisation Strasse", "deliveryPoint", "contact"},
						{"Organisation PLZ", "postalCode", "contact"},
						{"Organisation Ort", "city", "contact"}};
								
				this.renderPersonDetails(s, string, article, user, tblContact, "contact", 3);
				
				this.renderMethods(s, string, article, user);
			}else {
				string.append("no method section found");
			}
		}
	}

	/**
	 * 
	 * @param section
	 * @param string
	 * @param article
	 * @param user
	 */
	private void renderCommonData( Section<? extends AbstractXMLObjectType> section,
			StringBuilder string,
			KnowWEArticle article,
			KnowWEUserContext user){
		
		Object[][] tblColumns = {{"Zweck", "purpose", "dataset"},
				{"Projekt", "title", "project"},
				{"Titel", "title", "dataset"},
				{"Abstract", "abstract", "dataset"},
				{"Keywords", "keyword", "keywordSet"}};
		
		StringBuilder eml = new StringBuilder();
		eml.append("<h2>1. Allgemeine Angaben</h2>");
		eml.append("<table class=\"eml\"><tbody>");
		
		for(Object[] element : tblColumns){
			eml.append("<tr><td class=\"eml-highlight\">"
					+ element[0] + "</td><td class=\"eml-normal\">");
			
			Collection<Section<? extends AbstractXMLObjectType>> tags = new ArrayList<Section<? extends AbstractXMLObjectType>>();
			AbstractXMLObjectType.findSubSectionsOfTag(element[1].toString(), section, tags);
					
			for(Section<? extends AbstractXMLObjectType> tag : tags){
				Section<? extends AbstractXMLObjectType> father = AbstractXMLObjectType.getXMLFatherElement( tag );
				String fatherTag = AbstractXMLObjectType.getTagName( father );
				
				if(fatherTag.equals(element[2])) {
					Section<? extends XMLContent> xml = tag.findChildOfType(XMLContent.class);
					if( xml != null ){
					    eml.append(xml.getOriginalText());
					}
					eml.append(", ");
				}
			}
			if(eml.toString().endsWith(", ")){
				eml.delete( eml.lastIndexOf(", "), eml.length() );
			}
			eml.append("</td></tr>");
		}
		
		eml.append("</tbody></table>");
		string.append(KnowWEUtils.maskHTML( eml.toString() ));
	}
	
	/**
	 * 
	 * @param section
	 * @param string
	 * @param article
	 * @param user
	 */
	private void renderPersonDetails(Section<? extends AbstractXMLObjectType> section,
			StringBuilder string,
			KnowWEArticle article,
			KnowWEUserContext user,
			Object[][] tblColumns,
			String tblName,
			int num){
		
		StringBuilder eml = new StringBuilder();
		eml.append("<h2> " + num + ". " + (tblName.equals("creator") ? "Author" : "Kontakt") + "</h2>");
		eml.append("<table class=\"eml\"><tbody>");
		
		Collection<Section<? extends AbstractXMLObjectType>> tags = new ArrayList<Section<? extends AbstractXMLObjectType>>();
		AbstractXMLObjectType.findSubSectionsOfTag(tblName, section, tags);
		
		for(Object[] element : tblColumns){
			eml.append("<tr>");
			eml.append("<td class=\"eml-highlight\" style=\"width:20%;\">" + element[0] + "</td>");
			
			for(Section<? extends AbstractXMLObjectType> tag : tags){
			
				eml.append("<td class=\"eml-normal\">");
				Section<? extends AbstractXMLObjectType> c = AbstractXMLObjectType.findSubSectionOfTag(element[1].toString(), tag);
				if(c != null ){
					Section<? extends AbstractXMLObjectType> father = AbstractXMLObjectType.getXMLFatherElement( c );
					String fatherTag = AbstractXMLObjectType.getTagName( father );
						
					if(fatherTag.equals(element[2])) {
						Section<? extends XMLContent> xml = c.findChildOfType(XMLContent.class);
						if( xml != null ){
						    eml.append(xml.getOriginalText());
						}
						eml.append(", ");
					}
					if(eml.toString().endsWith(", ")){
						eml.delete( eml.lastIndexOf(", "), eml.length() );
					}
				}
				eml.append("</td>");
			}
			eml.append("</tr>");
		}
		
		eml.append("</tbody></table>");
		string.append(KnowWEUtils.maskHTML( eml.toString() ));
	}
	
	/**
	 * 
	 * @param section
	 * @param string
	 * @param article
	 * @param user
	 */
	private void renderMethods(Section<? extends AbstractXMLObjectType> section,
			StringBuilder string,
			KnowWEArticle article,
			KnowWEUserContext user){
		
		StringBuilder eml = new StringBuilder();
		eml.append("<h2> 4. Methoden </h2>");
		eml.append("<table class=\"eml\"><tbody>");
		
		eml.append("<tr><td colspan=\"5\" class=\"eml-highlight\">4.1 Angewendete Methoden</td></tr>");
		
		Collection<Section<? extends AbstractXMLObjectType>> tags = new ArrayList<Section<? extends AbstractXMLObjectType>>();
		AbstractXMLObjectType.findSubSectionsOfTag("description", section, tags);
		
		for(Section<? extends AbstractXMLObjectType> tag : tags){
			Section<? extends AbstractXMLObjectType> father = AbstractXMLObjectType.getXMLFatherElement( tag );
			String fatherTag = AbstractXMLObjectType.getTagName( father );
				
			if(fatherTag.equals("methodStep")) {
				addTD(tag, eml, new String[]{"description"}, null);
			}
		}
		
		eml.append("<tr><td colspan=\"2\" class=\"eml-highlight\">4.2 Sampling</td></tr>");
		
		tags = new ArrayList<Section<? extends AbstractXMLObjectType>>();
		AbstractXMLObjectType.findSubSectionsOfTag("samplingDescription", section, tags);
		
		for(Section<? extends AbstractXMLObjectType> tag : tags){
			Section<? extends AbstractXMLObjectType> father = AbstractXMLObjectType.getXMLFatherElement( tag );
			String fatherTag = AbstractXMLObjectType.getTagName( father );
				
			if(fatherTag.equals("sampling")) {
				addTD(tag, eml, new String[]{"samplingDescription"}, null);
			}
		}
		
		eml.append("<tr><td colspan=\"2\" class=\"eml-highlight\">4.3 Umfang der Untersuchung</td></tr>");

		String[] p = {"studyExtent", "description"};
		addTD(section, eml, p, null);
			
		eml.append("<tr><td colspan=\"2\" class=\"eml-highlight\">4.3.1 zeitlicher Umfang der Untersuchung</td></tr>");
		
		p = new String[]{"temporalCoverage", "beginDate", "calendarDate"};
		addTD(section, eml, p, "Beginn");
		
		p = new String[]{"temporalCoverage", "endDate", "calendarDate"};
		addTD(section, eml, p, "Ende");
					
		eml.append("<tr><td colspan=\"2\" class=\"eml-highlight\">4.3.2 räumlicher Umfang der Untersuchung</td></tr>");
		
		p = new String[]{"coverage","geographicCoverage","description"};
		addTD(section, eml, p, "Koordinatensystem");

		p = new String[]{"coverage","geographicCoverage","BoundingCoordinates","northBoundingCoordinate"};
		addTD(section, eml, p, "Nord (max.Y)");

		p = new String[]{"coverage","geographicCoverage","BoundingCoordinates","southBoundingCoordinate"};
		addTD(section, eml, p, "Süd (min Y)");

		p = new String[]{"coverage","geographicCoverage","BoundingCoordinates","eastBoundingCoordinate"};
		addTD(section, eml, p, "Ost (max X)");

		p = new String[]{"coverage","geographicCoverage","BoundingCoordinates","westBoundingCoordinate"};
		addTD(section, eml, p, "West (min X)");
		
		eml.append("</tbody></table>");
		string.append(KnowWEUtils.maskHTML( eml.toString() ));
	}
	/**
	 * 
	 * @param string
	 * @param path
	 * @param tdName
	 */
	private void addTD(Section<? extends AbstractXMLObjectType> section,
			StringBuilder string, String[] path, String tdName){
		
		String s = getTagContent(section, path);
		if( s != null && s != "") {
			if( tdName != null ){
		        string.append("<tr><td class=\"eml-highlight\">" + tdName + "</td><td class=\"eml-normal\">" + s + "</td></tr>");
			} else {
				string.append("<tr><td class=\"eml-normal\" colspan=\"2\">" + s + "</td></tr>");
			}
		}
	}
	
	
	/**
	 * 
	 * @param s
	 * @param father
	 * @param tag
	 * @return
	 */
	private String getTagContent(Section<? extends AbstractXMLObjectType> s, String[] path){
		String txt = null;
		Section<? extends AbstractXMLObjectType> tmpTag = null;
		
		for (String string : path) {
			if( tmpTag == null ) {
				tmpTag = s;
			}
			tmpTag = AbstractXMLObjectType.findSubSectionOfTag( string, tmpTag);
			if( tmpTag == null )return null;
		}

		if(tmpTag != null) {
			Section<? extends XMLContent> xml = tmpTag.findChildOfType(XMLContent.class);
			if( xml != null) {
				txt = xml.getOriginalText();
			}
		}
		if( txt != null ) {
			return txt;
		}
		return "";
	}
	
}
