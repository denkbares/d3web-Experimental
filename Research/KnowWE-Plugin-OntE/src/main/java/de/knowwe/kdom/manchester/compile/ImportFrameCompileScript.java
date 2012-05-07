package de.knowwe.kdom.manchester.compile;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.knowwe.compile.ImportManager;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.manchester.compile.utils.ImportedOntologyManager;
import de.knowwe.kdom.manchester.frame.ImportFrame;
import de.knowwe.kdom.renderer.OnteRenderingUtils;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.owlapi.OWLAPIKnowledgeUnitCompileScript;

/**
 * 
 * 
 * @author Stefan Mark
 * @created 30.11.2011
 */
public class ImportFrameCompileScript extends OWLAPIKnowledgeUnitCompileScript<ImportFrame> {

	public ImportFrameCompileScript() {
		super(false);
	}

	@Override
	public Set<OWLAxiom> createOWLAxioms(Section<ImportFrame> section, Collection<Message> messages) {

		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		ImportFrame importFrame = section.get();

		if (importFrame.hasImportIRI(section)) {

			Section<? extends Type> importIRISection = importFrame.getImportIRI(section);
			IRI iri = IRI.create(importIRISection.getText());
			try {
				// ... load the ontology
				OWLOntologyManager manager = OWLAPIConnector.getInstance(iri).getManager();
				OWLOntology toImport = manager.loadOntologyFromOntologyDocument(iri);

				// ... only import consistent ontologies ...
				// OWLReasonerFactory factory =
				// OWLAPIConnector.getInstance(iri).getFactory();
				// OWLReasoner reasoner = factory.createReasoner(toImport);

				// handleImportDeclarations(toImport, axioms, section); not
				// necessary

				axioms.addAll(toImport.getAxioms());

				// ... finally add root to known imported ontologies ...
				IRI ontologyIRI = toImport.getOntologyID().getOntologyIRI();

				if (!ImportedOntologyManager.getInstance().isKnown(iri)) {
					ImportedOntologyManager.getInstance().addOntology(section, ontologyIRI);
					ImportedOntologyManager.getInstance().addAxioms(toImport.getAxioms(),
							ontologyIRI);
				}
				manager.removeOntology(toImport);

				// ... and now add the terms to the ReferenceManager ...
				Set<OWLEntity> entities = toImport.getSignature();
				messages.addAll(registerImportedTerminology(entities, section, ontologyIRI));
			}
			catch (OWLOntologyAlreadyExistsException e) {
				messages.add(Messages.warning("The ontology is already imported. This import will be ignored! "
						+ iri));
				return new HashSet<OWLAxiom>();
			}
			catch (OWLOntologyCreationException e) {
				messages.add(Messages.error("Could not load the ontology from: " + iri));
				return new HashSet<OWLAxiom>();
			}
		}
		return axioms;
	}

	@Override
	public void deleteFromOntology(Section<ImportFrame> section) {
		ImportFrame importFrame = section.get();

		Section<? extends Type> importIRISection = importFrame.getImportIRI(section);
		IRI iri = IRI.create(importIRISection.getText());

		// .. remove the import declaration from the ontology
		OWLAPIConnector.getGlobalInstance().removeImport(iri);

		// ... remove registered terms from the ReferenceManager
		iri = ImportedOntologyManager.getInstance().getImportIRIFromSection(section);
		if (iri != null) {
			IncrementalCompiler.getInstance().deregisterImportedTerminology(section);
		}

		// ... and finally from the import ontology manager itself
		ImportedOntologyManager.getInstance().removeOntology(section);
	}

	@Override
	public void insertIntoOntology(Section<ImportFrame> section) {

		ImportFrame importFrame = section.get();
		Section<? extends Type> importIRISection = importFrame.getImportIRI(section);

		String importIRI = importIRISection.getText();
		if (!importIRI.endsWith("#")) {
			importIRI += "#";
		}
		IRI iri = IRI.create(importIRI);

		// .. add the import as an import declaration to the local one
		OWLAPIConnector.getGlobalInstance().addImport(iri);

		ImportManager.clearNewImports();
		ImportManager.clearRemovedImports();
	}

	/**
	 * Handle optional import declaration within the to import ontology.
	 * 
	 * @created 14.12.2011
	 * @param ontolology
	 * @param axioms
	 * @param section
	 */
	private void handleImportDeclarations(OWLOntology ontolology, Set<OWLAxiom> axioms, Section<ImportFrame> section, IRI ontologyIRI) {
		Set<OWLImportsDeclaration> importDeclarations = ontolology.getImportsDeclarations();
		if (!importDeclarations.isEmpty()) {
			for (OWLImportsDeclaration owlImportsDeclaration : importDeclarations) {
				IRI iri = owlImportsDeclaration.getIRI();
				try {
					if (!ImportedOntologyManager.getInstance().isKnown(iri)) {

						OWLOntologyManager manager = OWLAPIConnector.getInstance(iri).getManager();
						OWLOntology anotherImport = manager.loadOntologyFromOntologyDocument(iri);

						// ... finally add to known imported ontologies ...
						IRI anotherOntologyIRI = anotherImport.getOntologyID().getOntologyIRI();
						ImportedOntologyManager.getInstance().addOntology(section, ontologyIRI);
						ImportedOntologyManager.getInstance().addAxioms(anotherImport.getAxioms(),
								ontologyIRI);
						manager.removeOntology(anotherImport);
						axioms.addAll(anotherImport.getAxioms());

						// ... and now add the terms to the ReferenceManager ...
						Set<OWLEntity> entities = anotherImport.getSignature();
						registerImportedTerminology(entities, section, ontologyIRI);

						handleImportDeclarations(anotherImport, axioms, section, anotherOntologyIRI);
					}
				}
				catch (OWLOntologyCreationException e) {

				}
			}
		}
	}

	/**
	 * Register imported terms to the {@link ReferenceManager} of the
	 * {@link IncrementalCompiler}. Each term is stored as {@link ImportedTerm}.
	 * Simply call the register method from the {@link IncrementalCompiler} for
	 * each term that should be added.
	 * 
	 * @created 01.12.2011
	 * @param terminologyExtension
	 */
	private Collection<Message> registerImportedTerminology(Set<OWLEntity> entities, Section<? extends AbstractType> section, IRI ontologyIRI) {
		IncrementalCompiler compiler = IncrementalCompiler.getInstance();
		Collection<String> entityNames = new LinkedList<String>();

		for (OWLEntity owlEntity : entities) {

			if (owlEntity.getIRI().toString().startsWith(ontologyIRI.toString())) {

				String conceptName = OnteRenderingUtils.getDisplayName(owlEntity);

				// if (owlEntity.getIRI().getFragment() != null) {
				// conceptName = owlEntity.getIRI().getFragment();
				// }
				// else {
				// String path = owlEntity.getIRI().toURI().getPath();
				// conceptName = path.substring(path.lastIndexOf("/") + 1);
				// }

				entityNames.add(conceptName);
				compiler.registerImportedTerminology(section, new TermIdentifier(conceptName));
			}
		}

		Collection<Message> messages = new LinkedList<Message>();
		if (hasDuplicate(entityNames)) {
			messages.add(Messages.warning("Import violates unique name assumption!"));
		}

		if (messages.isEmpty()) { // check only if no violations till now
			Collection<Section<? extends SimpleDefinition>> localTerms = IncrementalCompiler.getInstance().getTerminology().getAllTermDefinitions();
			for (Section<? extends SimpleDefinition> term : localTerms) {
				if (entityNames.contains(term.getText())) {
					messages.add(Messages.warning("Import violates unique name assumption! Some concepts are already defined!"));
					break;
				}
			}
		}

		return messages;
	}

	private static <T> boolean hasDuplicate(Collection<T> list) {
		Set<T> set = new HashSet<T>();
		for (T each : list) {
			if (!set.add(each)) {
				return true;
			}
		}
		return false;
	}

}