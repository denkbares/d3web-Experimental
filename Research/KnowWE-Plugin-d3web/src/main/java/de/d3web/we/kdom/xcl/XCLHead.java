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

package de.d3web.we.kdom.xcl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

import de.d3web.we.core.semantic.DefaultURIContext;
import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.core.semantic.OwlHelper;
import de.d3web.we.core.semantic.OwlSubtreeHandler;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.basic.LineBreak;
import de.d3web.we.kdom.contexts.ContextManager;
import de.d3web.we.kdom.contexts.DefaultSubjectContext;
import de.d3web.we.kdom.decisionTree.SolutionID;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.logging.Logging;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.utils.Patterns;

public class XCLHead extends AbstractType {

	public static final String KEY_SOLUTION_NAME = "solutionName";

	@Override
	protected void init() {
		this.sectionFinder = new XCLHeadSectionFinder();
		SolutionID sID = new SolutionID();
		sID.setCustomRenderer(SolutionIDHighlightingRenderer.getInstance());
		this.childrenTypes.add(sID);
		this.childrenTypes.add(new LineBreak());
		this.addSubtreeHandler(new XCLHeadSubtreeHandler());
		this.addSubtreeHandler(new XCLHeadOWLSubTreeHandler());

	}

	private class XCLHeadSubtreeHandler extends D3webSubtreeHandler<XCLHead> {


		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<XCLHead> s) {

			Section<?> father = s.getFather();

			if (!father.get().getClass().equals(XCList.class)) {
				Logging.getInstance().log(Level.WARNING, "Expected different fathertype: XCList");
				return null;
			}

			String string = s.getOriginalText().trim();

			if (string.startsWith("\"")) {
				string = string.substring(1, string.length() - 1);
			}

			DefaultURIContext context = new DefaultURIContext(string);
			ContextManager.getInstance().attachContext(father, context);
			// store name of solution, so it is available for DCProperty-Sections
			KnowWEUtils.storeObject(article, father, KEY_SOLUTION_NAME, string);

			return null;

		}

	}

	public class XCLHeadSectionFinder implements SectionFinder {

		private final Pattern pattern;

		public XCLHeadSectionFinder() {
			pattern = Pattern.compile("^[\\t ]*(" + Patterns.D3IDENTIFIER + ")[\\t ]*\\{");
		}

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
			if (text.length() == 0) return null;

			Matcher matcher = pattern.matcher(text);

			if (matcher.lookingAt()) {
				List<SectionFinderResult> result = new ArrayList<SectionFinderResult>(1);
				result.add(new SectionFinderResult(matcher.start(1), matcher.end(1)));

				return result;
			}
			else {
				return null;
			}

		}
	}

	private class XCLHeadOWLSubTreeHandler extends OwlSubtreeHandler<XCLHead> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<XCLHead> section) {
			IntermediateOwlObject io = new IntermediateOwlObject();
			DefaultSubjectContext sol = (DefaultSubjectContext) ContextManager.getInstance()
					.getContext(section, DefaultSubjectContext.CID);

			Section<?> father = section.getFather();
			String string = section.getOriginalText().trim();

			if (string.startsWith("\"")) {
				string = string.substring(1, string.length() - 1);
			}
			DefaultURIContext context = new DefaultURIContext(string);
			ContextManager.getInstance().attachContext(father, context);

			String solution = sol != null ? sol.getSubject() : null;
			if (solution != null) {
				UpperOntology uo = SemanticCoreDelegator.getInstance().getUpper();

				try {
					URI solutionuri = uo.getHelper().createlocalURI(solution);
					io.addStatement(uo.getHelper().createStatement(solutionuri,
							RDF.TYPE, OwlHelper.SOLUTION));
				}
				catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			SemanticCoreDelegator.getInstance().addStatements(io, section);
			return null;
		}

	}

}
