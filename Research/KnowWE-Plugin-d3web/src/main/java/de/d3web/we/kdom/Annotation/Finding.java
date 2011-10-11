/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.we.kdom.Annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.core.semantic.OwlHelper;
import de.d3web.we.core.semantic.OwlSubtreeHandler;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.d3webModule.D3WebOWLVokab;
import de.d3web.we.kdom.condition.antlr.NOT;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.basicType.QuotedType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.report.SimpleMessageError;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.filter.TypeSectionFilter;
import de.knowwe.kdom.renderer.StyleRenderer;

@SuppressWarnings("unchecked")
public class Finding extends AbstractType {

	@Override
	public void init() {
		this.childrenTypes.add(new NOT());
		FindingComparator findingComparator = new FindingComparator();
		this.childrenTypes.add(findingComparator);
		this.childrenTypes.add(new QuotedType(new FindingQuestion(findingComparator)));
		this.childrenTypes.add(new FindingQuestion(findingComparator));
		this.childrenTypes.add(new QuotedType(new FindingAnswer()));
		this.childrenTypes.add(new FindingAnswer());
		this.sectionFinder = new FindingSectionFinder();
		this.addSubtreeHandler(Priority.HIGH, new FindingSubTreeHandler());
	}

	@Override
	public KnowWEDomRenderer getRenderer() {
		return StyleRenderer.CONDITION;
	}

	private class FindingSubTreeHandler extends OwlSubtreeHandler<Finding> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article,
				Section section) {
			List<KDOMReportMessage> msgs = new ArrayList<KDOMReportMessage>();
			UpperOntology uo = UpperOntology.getInstance();
			IntermediateOwlObject io = new IntermediateOwlObject();
			try {
				Section csection = (Section) section
						.getChildren(
								new TypeSectionFilter(new FindingComparator()
										.getName())).get(0);
				String comparator = ((FindingComparator) csection
						.get()).getComparator(csection);

				Section qsection = Sections.findSuccessor(section, FindingQuestion.class);
				String question = ((FindingQuestion) qsection.get())
						.getQuestion(qsection);

				Section asection = Sections.findSuccessor(section, FindingAnswer.class);
				String answer = ((FindingAnswer) asection.get())
						.getAnswer(asection);

				URI compuri = uo.getHelper().getComparator(comparator);
				URI questionuri = uo.getHelper().createlocalURI(question);
				URI answeruri = uo.getHelper().createlocalURI(answer);
				URI literalinstance = uo.getHelper().createlocalURI(
						section.getTitle() + ".." + section.getID() + ".."
								+ question + comparator + answer);

				ArrayList<Statement> slist = new ArrayList<Statement>();
				try {
					uo.getHelper().attachTextOrigin(literalinstance, section,
							io, D3WebOWLVokab.ANNOTATION);
					slist.add(uo.getHelper().createStatement(literalinstance,
							RDF.TYPE, D3WebOWLVokab.LITERAL));
					slist.add(uo.getHelper().createStatement(literalinstance,
							D3WebOWLVokab.HASINPUT, questionuri));
					slist
							.add(uo.getHelper().createStatement(
									literalinstance, D3WebOWLVokab.HASCOMPARATOR
									,
									compuri));
					slist.add(uo.getHelper().createStatement(literalinstance,
							D3WebOWLVokab.HASVALUE
							, answeruri));
				}
				catch (RepositoryException e) {

					e.printStackTrace();
				}
				io.addAllStatements(slist);
				io.addLiteral(literalinstance);
			}
			catch (IndexOutOfBoundsException e) {
				msgs.add(new SimpleMessageError("Finding without subsections"));
			}
			catch (NullPointerException e) {
				msgs.add(new SimpleMessageError("Nullpointer"));
			}
			KnowWEUtils.storeObject(article, section, OwlHelper.IOO, io);
			return msgs;
		}

	}

	public class FindingSectionFinder implements SectionFinder {

		private final AllTextFinderTrimmed textFinder = new AllTextFinderTrimmed();

		@Override
		public List<SectionFinderResult> lookForSections(String text,
				Section<?> father, Type type) {
			if (text.contains(">") || text.contains("=") || text.contains("<")) {
				if (!text.contains("+=")) { // hack excluding "+="
					return textFinder.lookForSections(
							text, father, type);
				}
			}
			return null;
		}

	}

}
