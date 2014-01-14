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
import de.d3web.we.knowledgebase.KnowledgeBaseType;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.compile.packaging.PackageCompileType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.ontology.compile.OntologyCompiler;
import de.knowwe.ontology.compile.OntologyHandler;
import de.knowwe.rdf2go.Rdf2GoCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

public class Rdf2GoKnowledgeBaseHandler extends OntologyHandler<KnowledgeBaseType> {

	@Override
	public Collection<Message> create(OntologyCompiler compiler, Section<KnowledgeBaseType> section) {

		if (section.hasErrorInSubtree()) return Messages.noMessage();

		Section<PackageCompileType> compileSection = Sections.findSuccessor(section,
				PackageCompileType.class);
		D3webCompiler d3webCompiler = Compilers.getCompiler(compileSection, D3webCompiler.class);

		addKnowledgeBaseInfo(compiler, d3webCompiler, section);

		addChildrenIndexes(compiler, d3webCompiler, section);

		return Messages.noMessage();
	}

	/**
	 * Since the order and parents of the TerminologyObjects can change during
	 * the compilation (due to multiple definitions), we add these informations
	 * after the compilation of the TerminologyObjects and do it only once.
	 * 
	 * @param rdf2GoCompiler
	 * @param section
	 * 
	 * @created 10.06.2013
	 */
	private void addChildrenIndexes(Rdf2GoCompiler rdf2GoCompiler, D3webCompiler d3webCompiler, Section<KnowledgeBaseType> section) {
		KnowledgeBase knowledgeBase = D3webUtils.getKnowledgeBase(d3webCompiler);
		Collection<TerminologyObject> allTerminologyObjects = knowledgeBase.getManager().getAllTerminologyObjects();
		for (NamedObject terminologyObject : allTerminologyObjects) {
			addNamedObjectChildrenIndexes(rdf2GoCompiler, d3webCompiler, terminologyObject, section);
		}
	}

	private void addNamedObjectChildrenIndexes(Rdf2GoCompiler rdf2GoCompiler, D3webCompiler d3webCompiler, NamedObject namedObject, Section<KnowledgeBaseType> section) {
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

		core.addStatements(section, Rdf2GoUtils.toArray(statements));

	}

	private void addKnowledgeBaseInfo(Rdf2GoCompiler rdf2GoCompiler, D3webCompiler compiler, Section<KnowledgeBaseType> section) {
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
		core.addStatements(section, core.createStatement(
				articleNameURI, definesURI, kbNameURI));

		// Subject: lns:ArticleName Predicate: rdf:type Object:
		// lns:Article
		core.addStatements(section, core.createStatement(articleNameURI,
				RDF.type, articleURI));
	}

	@Override
	public void destroy(OntologyCompiler compiler, Section<KnowledgeBaseType> section) {
		compiler.getRdf2GoCore().removeStatementsForSection(section);
	}

}
