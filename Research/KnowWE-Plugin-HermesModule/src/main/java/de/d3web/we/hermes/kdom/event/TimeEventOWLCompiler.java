package de.d3web.we.hermes.kdom.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.d3web.we.core.semantic.rdf2go.DefaultURIContext;
import de.d3web.we.core.semantic.rdf2go.RDF2GoSubtreeHandler;
import de.d3web.we.core.semantic.rdf2go.Rdf2GoCore;
import de.d3web.we.hermes.TimeEvent;
import de.d3web.we.hermes.TimeStamp;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.contexts.ContextManager;
import de.d3web.we.kdom.contexts.DefaultSubjectContext;
import de.d3web.we.kdom.report.KDOMReportMessage;

public class TimeEventOWLCompiler extends RDF2GoSubtreeHandler<TimeEventNew> {

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<TimeEventNew> s) {

		TimeEvent event = TimeEventNew.createTimeEvent(s);
		if (s.hasErrorInSubtree(article) || event == null) {
			return new ArrayList<KDOMReportMessage>(0);
		}
		List<Statement> io = new ArrayList<Statement>();
		try {

			ArrayList<Statement> slist = new ArrayList<Statement>();

			/* creating all the URIs for the resources */
			String localID = s.getTitle() + "_" + s.getID();
			URI localURI = Rdf2GoCore.getInstance().createlocalURI(localID);

			URI timeEventURI = Rdf2GoCore.getInstance().createlocalURI("Ereignis");

			// Putting the TimeEventURI in a context, so it can be found by
			// subtypes
			DefaultURIContext uc = new DefaultURIContext();
			uc.setSubjectURI(localURI);
			ContextManager.getInstance().attachContext(s, uc);

			DefaultSubjectContext sc = new DefaultSubjectContext(localID);
			ContextManager.getInstance().attachContext(s, sc);

			// handle date infos
			createDateTripels(event.getTime(), slist, localURI);

			// add textorigin
			Rdf2GoCore.getInstance().attachTextOrigin(localURI, s, io);

			// handle description
			String description = event.getDescription();
			if (description != null) {
				Literal descriptionURI = Rdf2GoCore.getInstance().createLiteral(description);
				slist.add(Rdf2GoCore.getInstance().createStatement(localURI,
						Rdf2GoCore.getInstance().createlocalURI("hasDescription"), descriptionURI));
			}

			// handle title
			String title = event.getTitle();
			Literal titleURI = Rdf2GoCore.getInstance().createLiteral(title);
			slist.add(Rdf2GoCore.getInstance().createStatement(localURI,
					Rdf2GoCore.getInstance().createlocalURI("hasTitle"), titleURI));

			// handle importance
			Integer importance = event.getImportance();
			if (importance != null) {
				Literal importanceURI = Rdf2GoCore.getInstance().createDatatypeLiteral(
						importance.toString(), "xsd:int");
				slist.add(Rdf2GoCore.getInstance().createStatement(localURI,
						Rdf2GoCore.getInstance().createlocalURI("hasImportance"), importanceURI));
			}

			// handle sources
			List<String> sourceStrings = event.getSources();
			List<Literal> sourceURIs = new ArrayList<Literal>();
			for (String source : sourceStrings) {
				sourceURIs.add(Rdf2GoCore.getInstance().createLiteral(source));
			}
			for (Literal sURI : sourceURIs) {
				slist.add(Rdf2GoCore.getInstance().createStatement(localURI,
						Rdf2GoCore.getInstance().createlocalURI("hasSource"), sURI));
			}

			io.add(Rdf2GoCore.getInstance().createStatement(localURI, RDF.type, timeEventURI));
			io.addAll(slist);

		}
		catch (ModelRuntimeException e) {
			e.printStackTrace();
		}

		Rdf2GoCore.getInstance().addStatements(io, s);
		return new ArrayList<KDOMReportMessage>(0);
	}

	private void createDateTripels(TimeStamp timeStamp, ArrayList<Statement> slist, URI localURI) throws ModelRuntimeException {
		if (timeStamp != null) {
			Literal dateText = Rdf2GoCore.getInstance().createLiteral(timeStamp.getEncodedString());

			Literal dateStart = Rdf2GoCore.getInstance().createDatatypeLiteral(
					timeStamp.getStartPoint().getInterpretableTime() + "", "xsd:double");

			Literal dateEnd = null;
			if (timeStamp.getEndPoint() != null) {

				dateEnd = Rdf2GoCore.getInstance().createDatatypeLiteral(
						timeStamp.getEndPoint().getInterpretableTime() + "", "xsd:double");
			}

			slist.add(Rdf2GoCore.getInstance().createStatement(localURI,
					Rdf2GoCore.getInstance().createlocalURI("hasStartDate"), dateStart));
			if (dateEnd != null) {
				slist.add(Rdf2GoCore.getInstance().createStatement(localURI,
						Rdf2GoCore.getInstance().createlocalURI("hasEndDate"), dateEnd));
			}
			slist.add(Rdf2GoCore.getInstance().createStatement(localURI,
					Rdf2GoCore.getInstance().createlocalURI("hasDateDescription"), dateText));
		}
	}

}
