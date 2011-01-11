package de.knowwe.kdom.turtle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;

import de.d3web.we.core.semantic.rdf2go.RDF2GoSubtreeHandler;
import de.d3web.we.core.semantic.rdf2go.Rdf2GoCore;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.SyntaxError;
import de.knowwe.termObject.RDFResourceType;

public class TurtleRDF2GoCompiler extends RDF2GoSubtreeHandler<TurtleMarkup>{

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<TurtleMarkup> s) {
		if(s.hasErrorInSubtree(article)) {
			return new ArrayList<KDOMReportMessage>(0);
		}
			
		List<Section<RDFResourceType>> found = new ArrayList<Section<RDFResourceType>>();
		
		
		s.findSuccessorsOfType(RDFResourceType.class, found);
		
		
		if(found.size() != 3) {
			return Arrays.asList((KDOMReportMessage) new SyntaxError(
			"expected 3 objects - found:"+found.size()));
		}
		
		Section<RDFResourceType> subject = found.get(0);
		Section<RDFResourceType> predicate = found.get(1);
		Section<RDFResourceType> object = found.get(2);
		
		List<Statement> statements = new ArrayList<Statement>();
		Statement st = Rdf2GoCore.getInstance().createStatement(subject.get().getURI(subject), predicate.get().getURI(predicate), object.get().getURI(object));
		statements.add(st);
		Rdf2GoCore.getInstance().addStatements(statements, s);
		
		return new ArrayList<KDOMReportMessage>(0);
	}

}
