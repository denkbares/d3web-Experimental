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

package de.d3web.we.kdom.condition.antlr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

import de.d3web.KnOfficeParser.DefaultLexer;
import de.d3web.KnOfficeParser.complexcondition.ComplexConditionSOLO;
import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.core.semantic.OwlHelper;
import de.d3web.we.core.semantic.OwlSubtreeHandler;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.d3webModule.D3WebOWLVokab;
import de.d3web.we.kdom.ExpandedSectionFinderResult;
import de.d3web.we.kdom.condition.old.Disjunct;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.basicType.RoundBracedType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.report.SimpleMessageError;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.renderer.StyleRenderer;

public class ComplexFinding extends AbstractType {

	@Override
	protected void init() {
		this.childrenTypes.add(new RoundBracedType(this));
		this.childrenTypes.add(new OrOperator());
		this.sectionFinder = new AllTextSectionFinder();
		this.childrenTypes.add(new Disjunct());
		this.addSubtreeHandler(new ComplexFindingSubtreeHandler());
	}

	/**
	 * Only for XclRelation Highlighting.
	 */
	public KnowWEDomRenderer getBackgroundColorRenderer(String color) {
		return StyleRenderer.getRenderer(StyleRenderer.CONDITION.getCssStyle(), color);
	}

	private class ComplexFindingSubtreeHandler extends OwlSubtreeHandler<ComplexFinding> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<ComplexFinding> s) {
			IntermediateOwlObject io = new IntermediateOwlObject();
			List<KDOMReportMessage> msgs = new ArrayList<KDOMReportMessage>();
			try {
				UpperOntology uo = UpperOntology.getInstance();
				URI complexfinding = uo.getHelper().createChildOf(D3WebOWLVokab.COMPLEXFINDING
						,
						uo.getHelper().createlocalURI(
								s.getTitle() + ".." + s.getID()));
				io.addLiteral(complexfinding);
				List<Section<? extends Type>> children = s.getChildren();
				for (Section<?> current : children) {
					if (current.get() instanceof Disjunct) {
						IntermediateOwlObject iohandler = (IntermediateOwlObject) KnowWEUtils.getStoredObject(
								article, current, OwlHelper.IOO);
						for (URI curi : iohandler.getLiterals()) {
							Statement state = uo.getHelper().createStatement(
									complexfinding, D3WebOWLVokab.HASDISJUNCTS
									, curi);
							io.addStatement(state);
							iohandler.removeLiteral(curi);
						}
						io.merge(iohandler);
					}
				}
			}
			catch (RepositoryException e) {
				msgs.add(new SimpleMessageError(e.getMessage()));
			}
			// return io;
			// SemanticCore.getInstance().addStatements(io, s);
			KnowWEUtils.storeObject(article, s, OwlHelper.IOO, io);
			return msgs;
		}

	}

	public class ComplexFindingANTLRSectionFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
			InputStream stream = new ByteArrayInputStream(text.getBytes());
			ANTLRInputStream input = null;
			try {
				input = new ANTLRInputStream(stream);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			DefaultLexer lexer = new DefaultLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			ComplexConditionSOLO parser = new ComplexConditionSOLO(tokens);
			ConditionKDOMBuilder builder = new ConditionKDOMBuilder();
			parser.setBuilder(builder);
			try {
				parser.complexcondition();
			}
			catch (RecognitionException e) {
				e.printStackTrace();
			}
			ExpandedSectionFinderResult s = builder.peek();
			if (s == null) {
				return null;
			}
			List<SectionFinderResult> list = new ArrayList<SectionFinderResult>();
			list.add(s);
			return list;
		}
	}

}
