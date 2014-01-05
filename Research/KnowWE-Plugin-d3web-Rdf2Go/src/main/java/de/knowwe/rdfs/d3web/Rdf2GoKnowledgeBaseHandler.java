package de.knowwe.rdfs.d3web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.strings.Strings;
import de.d3web.we.knowledgebase.D3webCompiler;
import de.d3web.we.object.D3webTermDefinition;
import de.d3web.we.reviseHandler.D3webHandler;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.rdf2go.Rdf2GoCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class Rdf2GoKnowledgeBaseHandler extends D3webHandler<D3webTermDefinition<?>> {

	@Override
	public Collection<Message> create(D3webCompiler compiler, Section<D3webTermDefinition<?>> section) {

		if (section.hasErrorInSubtree()) return Messages.noMessage();

		Collection<Rdf2GoCompiler> rdf2Gocompilers = Rdf2GoD3webUtils.getRdf2GoCompilers(compiler,
				section);
		for (Rdf2GoCompiler rdf2GoCompiler : rdf2Gocompilers) {

			addKnowledgeBaseInfo(rdf2GoCompiler, compiler, section);

			addChildrenIndexes(rdf2GoCompiler, compiler);
		}

		return Messages.noMessage();
	}

	/**
	 * Since the order and parents of the TerminologyObjects can change during
	 * the compilation (due to multiple definitions), we add these informations
	 * after the compilation of the TerminologyObjects and do it only once.
	 * 
	 * @param rdf2GoCompiler
	 * 
	 * @created 10.06.2013
	 */
	private void addChildrenIndexes(Rdf2GoCompiler rdf2GoCompiler, D3webCompiler compiler) {
		KnowledgeBase knowledgeBase = D3webUtils.getKnowledgeBase(compiler);
		Collection<TerminologyObject> allTerminologyObjects = knowledgeBase.getManager().getAllTerminologyObjects();
		for (NamedObject terminologyObject : allTerminologyObjects) {
			addNamedObjectChildrenIndexes(rdf2GoCompiler, compiler, terminologyObject);
		}
	}

	private void addNamedObjectChildrenIndexes(Rdf2GoCompiler rdf2GoCompiler, D3webCompiler compiler, NamedObject namedObject) {
		String externalForm = Rdf2GoD3webUtils.getIdentifierExternalForm(namedObject);
		Rdf2GoCore core = rdf2GoCompiler.getRdf2GoCore();
		URI termIdentifierURI = core.createlocalURI(externalForm);

		List<Statement> statements = new ArrayList<Statement>();

		TerminologyObject[] parents = new TerminologyObject[0];
		if (namedObject instanceof TerminologyObject) {
			parents = ((TerminologyObject) namedObject).getParents();
		}
		else if (namedObject instanceof Choice) {
			parents = new TerminologyObject[] { ((Choice) namedObject).getQuestion() };
		}
		for (TerminologyObject parent : parents) {
			String parentExternalForm = Rdf2GoD3webUtils.getIdentifierExternalForm(parent);
			int index = -1;
			if (namedObject instanceof TerminologyObject) {
				TerminologyObject[] children = parent.getChildren();
				for (int i = 0; i < children.length; i++) {
					if (children[i] == namedObject) {
						index = i;
					}
				}
			}
			else if (namedObject instanceof Choice) {
				index = ((QuestionChoice) parent).getAllAlternatives().indexOf(namedObject);
			}
			// should not happen
			if (index == -1) continue;

			BlankNode indexNode = core.createBlankNode();
			URI hasIndexInfoURI = core.createlocalURI("hasIndexInfo");
			Rdf2GoUtils.addStatement(core,
					termIdentifierURI, hasIndexInfoURI, indexNode, statements);
			URI hasIndexURI = core.createlocalURI("hasIndex");
			Literal indexLiteral = core.createDatatypeLiteral(
					Integer.toString(index), XSD._int);
			Rdf2GoUtils.addStatement(core,
					indexNode, hasIndexURI, indexLiteral, statements);
			URI indexOfURI = core.createlocalURI("isIndexOf");
			URI parentIdentifierURI = core.createlocalURI(
					parentExternalForm);
			Rdf2GoUtils.addStatement(core,
					indexNode, indexOfURI, parentIdentifierURI, statements);
		}

		core.addStatements(compiler, Rdf2GoUtils.toArray(statements));

	}

	private void addKnowledgeBaseInfo(Rdf2GoCompiler rdf2GoCompiler, D3webCompiler compiler, Section<D3webTermDefinition<?>> section) {
		String kbName = D3webUtils.getKnowledgeBase(compiler).getId();
		Rdf2GoCore core = rdf2GoCompiler.getRdf2GoCore();
		URI articleNameURI = core.createlocalURI(
				Strings.encodeURL(section.getTitle()));

		URI articleURI = core.createlocalURI("Article");

		URI kbNameURI = core.createlocalURI(Strings.encodeURL(kbName));
		URI definesURI = core.createlocalURI(
				"defines");

		// Subject: lns:ArticleName Predicate: lns:defines Object:
		// lns:KbName
		core.addStatements(compiler, core.createStatement(
				articleNameURI, definesURI, kbNameURI));

		// Subject: lns:ArticleName Predicate: rdf:type Object:
		// lns:Article
		core.addStatements(compiler, core.createStatement(articleNameURI,
				RDF.type, articleURI));
	}

}
