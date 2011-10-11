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

package de.d3web.we.kdom.condition.old;

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
import de.d3web.we.kdom.condition.antlr.AndOperator;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.basicType.RoundBracedType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.report.SimpleMessageError;
import de.knowwe.core.utils.KnowWEUtils;

public class Disjunct extends AbstractType {

	@Override
	public void init() {
		this.sectionFinder = new AllTextFinderTrimmed();
		this.childrenTypes.add(new RoundBracedType(this));
		this.childrenTypes.add(new AndOperator());
		this.childrenTypes.add(new Conjunct());
		this.addSubtreeHandler(new DisjunctSubTreeHandler());
	}

	private class DisjunctSubTreeHandler extends OwlSubtreeHandler<Disjunct> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Disjunct> s) {

			List<KDOMReportMessage> msgs = new ArrayList<KDOMReportMessage>();
			IntermediateOwlObject io = new IntermediateOwlObject();
			try {
				UpperOntology uo = UpperOntology.getInstance();

				URI compositeexpression = uo.getHelper().createlocalURI(
						s.getTitle() + ".." + s.getID());
				io.addStatement(uo.getHelper().createStatement(
						compositeexpression, RDF.TYPE,
						D3WebOWLVokab.DISJUNCTION));
				io.addLiteral(compositeexpression);
				List<Section<? extends Type>> children = s.getChildren();
				for (Section current : children) {
					if (current.get() instanceof Conjunct) {
						IntermediateOwlObject iohandler = (IntermediateOwlObject) KnowWEUtils
								.getStoredObject(article, current, OwlHelper.IOO);
						for (URI curi : iohandler.getLiterals()) {
							Statement state = uo.getHelper().createStatement(
									compositeexpression,
									D3WebOWLVokab.HASDISJUNCTS, curi);
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
			KnowWEUtils.storeObject(article, s, OwlHelper.IOO, io);

			return msgs;
		}

	}

}
