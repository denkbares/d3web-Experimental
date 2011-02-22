package de.knowwe.kdom.turtle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.semantic.rdf2go.RDF2GoSubtreeHandler;
import de.d3web.we.core.semantic.rdf2go.Rdf2GoCore;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.objects.TermReference;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.SyntaxError;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.kdom.turtle.TurtleMarkup.TurtleObject;
import de.knowwe.kdom.turtle.TurtleMarkup.TurtlePredicate;
import de.knowwe.termObject.RDFResourceType;

public class TurtleRDF2GoCompiler extends RDF2GoSubtreeHandler<TurtleMarkup> {

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<TurtleMarkup> s) {
		if (s.hasErrorInSubtree(article)) {
			return new ArrayList<KDOMReportMessage>(0);
		}

		List<Section<RDFResourceType>> found = new ArrayList<Section<RDFResourceType>>();
		URI subURI = null;
		URI predURI = null;
		URI objURI = null;

		s.findSuccessorsOfType(RDFResourceType.class, found);

		if (found.size() == 3) {
			Section<RDFResourceType> subject = found.get(0);
			Section<RDFResourceType> predicate = found.get(1);
			Section<RDFResourceType> object = found.get(2);

			subURI = subject.get().getURI(subject);
			predURI = predicate.get().getURI(predicate);
			objURI = object.get().getURI(object);
		}
//		else if (found.size() == 2) {
//			Section<RDFResourceType> first = found.get(0);
//			Section<RDFResourceType> second = found.get(1);
//			if (first.get() instanceof TurtlePredicate &&
//					second.get() instanceof TurtleObject) {
//				predURI = first.get().getURI(first);
//				objURI = second.get().getURI(second);
//				getSubject(s);
//
//			}
//			else {
//				return Arrays.asList((KDOMReportMessage) new SyntaxError(
//						"invalid term combination:" + found.size()));
//			}
//		}
		else {
			return Arrays.asList((KDOMReportMessage) new SyntaxError(
					"invalid term combination:" + found.size()));
		}

		List<Statement> statements = new ArrayList<Statement>();
		Statement st = Rdf2GoCore.getInstance().createStatement(subURI, predURI, objURI);
		statements.add(st);
		Rdf2GoCore.getInstance().addStatements(statements, s);

		return new ArrayList<KDOMReportMessage>(0);
	}

	private URI getSubject(Section<TurtleMarkup> s) {
		TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
		boolean b = terminologyHandler.isDefinedTerm(s.getArticle(),
				s.getArticle().getTitle(), KnowWETerm.GLOBAL);
		if (b) {
			Section<? extends TermDefinition> termDefiningSection = terminologyHandler.getTermDefiningSection(
					s.getArticle(), s.getArticle().getTitle(), KnowWETerm.GLOBAL);

			Object termObject = termDefiningSection.get().getTermObject(s.getArticle(),
					termDefiningSection);

			if (termObject instanceof URI) {
				return (URI) termObject;
			}
		}
		return null;

	}

}
