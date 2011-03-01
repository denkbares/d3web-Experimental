/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.we.kdom.table.attributes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Priority;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.message.CreateRelationFailed;
import de.d3web.we.kdom.table.Table;
import de.d3web.we.kdom.table.TableCellContent;
import de.d3web.we.kdom.table.TableLine;
import de.d3web.we.object.QuestionReference;
import de.d3web.we.object.QuestionnaireReference;
import de.d3web.we.object.SolutionReference;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;

/**
 * 
 * !!! Caution !!!!
 * 
 * This markup will be replaced soon, do not spend time on it
 * 
 * !!! Caution !!!!
 * 
 * 
 * AttributeTable which allows to specifiy additional information for
 * NamedObjects. Therefore the data is added to the MMInfoStorage.
 * 
 * It is not necessary to specify the header of the table, because it is
 * generated automatically.
 * 
 * The TerminologyObject is specified in the first column. The name of the
 * property in the second, the third is ignored and the data/content in the
 * fourth;
 * 
 * @author Sebastian Furth
 * @created 28/10/2010
 */
public class AttributeTable extends Table {

	public AttributeTable() {
		super(new AttributeTableAttributesProvider());
		childrenTypes.add(0, new AttributeTableLine());
		this.setCustomRenderer(new AttributeTableRenderer());
		this.addSubtreeHandler(Priority.LOWEST, new AttributeTableSubTreeHandler());
	}

	public class AttributeTableSubTreeHandler extends D3webSubtreeHandler<AttributeTable> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<AttributeTable> s) {

			Collection<KDOMReportMessage> msg = new LinkedList<KDOMReportMessage>();

			List<Section<AttributeTableTempType>> tempTypes = new LinkedList<Section<AttributeTableTempType>>();
			Sections.findSuccessorsOfType(s, AttributeTableTempType.class, tempTypes);
			KnowledgeBase kb = getKB(article);

			for (Section<AttributeTableTempType> tempType : tempTypes) {

				TerminologyObject namedObject = findNamedObject(kb, tempType.getOriginalText());

				// Create MMInfo
				if (namedObject != null) {
					createMMInfo(namedObject, tempType);
				}

				// Set correct TermReference Type
				if (namedObject instanceof Solution) {
					tempType.setType(new SolutionReference());
				}
				else if (namedObject instanceof Question) {
					tempType.setType(new QuestionReference());
				}
				else if (namedObject instanceof QContainer) {
					tempType.setType(new QuestionnaireReference());
				}
				else {
					msg.add(new CreateRelationFailed("Unable to find a terminology object named \""
							+ tempType.getOriginalText() + "\""));
				}

			}

			return msg;
		}

		private void createMMInfo(TerminologyObject namedObject, Section<AttributeTableTempType> tempType) {
			Section<TableLine> line = Sections.findAncestorOfType(tempType, TableLine.class);
			List<Section<TableCellContent>> cells = new LinkedList<Section<TableCellContent>>();
			Sections.findSuccessorsOfType(line, TableCellContent.class, cells);
			if (cells.size() == 4) {
				String subject = cells.get(1).getOriginalText().trim();
				Property<Object> property = Property.getUntypedProperty(subject);
				// String title = cells.get(2).getOriginalText().trim();
				String data = cells.get(3).getOriginalText().trim();
				namedObject.getInfoStore().addValue(property, data);
			}
			else {
				Logger.getLogger(this.getClass().getName()).warning(
						"Failed to add MMInfo to \"" + namedObject);
			}

		}

		private TerminologyObject findNamedObject(KnowledgeBase kb, String name) {
			// Is there a Question with this name?
			TerminologyObject namedObject = kb.getManager().searchQuestion(name);
			if (namedObject == null) {
				// Or a Solution?
				namedObject = kb.getManager().searchSolution(name);
				if (namedObject == null) {
					// Or a QContainer?
					namedObject = kb.getManager().searchQContainer(name);
				}
			}
			return namedObject;
		}
	}
}
