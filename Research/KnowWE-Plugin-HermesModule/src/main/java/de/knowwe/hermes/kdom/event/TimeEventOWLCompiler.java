package de.knowwe.hermes.kdom.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.knowwe.core.contexts.ContextManager;
import de.knowwe.core.contexts.DefaultSubjectContext;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.hermes.TimeEvent;
import de.knowwe.hermes.TimeStamp;
import de.knowwe.rdf2go.DefaultURIContext;
import de.knowwe.rdf2go.RDF2GoSubtreeHandler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class TimeEventOWLCompiler extends RDF2GoSubtreeHandler<TimeEventNew> {

	private static final URI HASTOPIC = Rdf2GoCore.getInstance().createURI(
			Rdf2GoCore.getInstance().getLocalNamespace(),
			"hasTopic");

	private static final URI TEXTORIGIN = Rdf2GoCore.getInstance().createURI(
			Rdf2GoCore.getInstance().getLocalNamespace(),
			"TextOrigin");
	private static final URI HASNODE = Rdf2GoCore.getInstance().createURI(
			Rdf2GoCore.getInstance().getLocalNamespace(),
			"hasNode");

	@Override
	public Collection<Message> create(Article article, Section<TimeEventNew> s) {

		TimeEvent event = TimeEventNew.createTimeEvent(s);
		if (s.hasErrorInSubtree(article) || event == null) {
			return new ArrayList<Message>(0);
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
			attachTextOrigin(localURI, s, io);

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

		Rdf2GoCore.getInstance().addStatements(s, Rdf2GoUtils.toArray(io));
		return new ArrayList<Message>(0);
	}

	/**
	 * Attaches a TextOrigin Node to a Resource. It's your duty to make sure the
	 * Resource is of the right type if applicable (eg attachto RDF.TYPE
	 * RDF.STATEMENT)
	 * 
	 * @param attachto The Resource that will be annotated bei the TO-Node
	 * @param source The source section that should be used
	 * @param io the ex-IntermediateOwlObject (now List<Statements> that should
	 *        collect the statements
	 */
	private void attachTextOrigin(Resource attachto, Section<?> source, List<Statement> io) {
		BlankNode to = Rdf2GoCore.getInstance().createBlankNode();
		io.addAll(createTextOrigin(source, to));
		io.add(Rdf2GoCore.getInstance().createStatement(attachto, RDFS.isDefinedBy, to));
	}

	private List<Statement> createTextOrigin(Section<?> source, Resource to) {
		ArrayList<Statement> io = new ArrayList<Statement>();
		io.add(Rdf2GoCore.getInstance().createStatement(to, RDF.type, TEXTORIGIN));
		io.add(Rdf2GoCore.getInstance().createStatement(to, HASNODE,
				Rdf2GoCore.getInstance().createLiteral(source.getID())));
		io.add(Rdf2GoCore.getInstance().createStatement(to, HASTOPIC,
				Rdf2GoCore.getInstance().createlocalURI(source.getTitle())));
		return io;
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
