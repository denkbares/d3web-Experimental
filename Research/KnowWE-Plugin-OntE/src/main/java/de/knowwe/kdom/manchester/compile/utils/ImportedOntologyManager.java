package de.knowwe.kdom.manchester.compile.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.kdom.manchester.frame.ImportFrame;

/**
 *
 *
 * @author Stefan Mark
 * @created 01.12.2011
 */
public class ImportedOntologyManager {

	/**
	 * Stores the only instance of the {@link ImportedOntologyManager}.
	 */
	private static ImportedOntologyManager instance = null;

	private final Map<IRI, Section<ImportFrame>> importedOntologies = new HashMap<IRI, Section<ImportFrame>>();

	private final Map<IRI, Set<OWLAxiom>> importedAxioms = new HashMap<IRI, Set<OWLAxiom>>();

	private final Map<IRI, Set<Section<? extends AbstractType>>> importedTerms = new HashMap<IRI, Set<Section<? extends AbstractType>>>();


	/**
	 * Private constructor due Singleton pattern.
	 */
	private ImportedOntologyManager() {

	}

	/**
	 * Returns an instance of the {@link ImportedOntologyManager}.
	 *
	 * @created 01.12.2011
	 * @return
	 */
	public static synchronized ImportedOntologyManager getInstance() {
		if (instance == null) {
			instance = new ImportedOntologyManager();
		}
		return instance;
	}

	public Map<IRI, Section<ImportFrame>> getImportedOntologies() {
		return importedOntologies;
	}

	public Map<IRI, Set<OWLAxiom>> getImportedAxioms() {
		return importedAxioms;
	}

	public Set<OWLAxiom> getImportedAxiomsForIRI(IRI iri) {
		if (importedAxioms.containsKey(iri)) {
			return importedAxioms.get(iri);
		}
		return Collections.emptySet();
	}

	public IRI getIRIForTerm(String termIdentifier) {
		for (IRI iri : importedTerms.keySet()) {

			Set<Section<? extends AbstractType>> terms = importedTerms.get(iri);
			for (Section<? extends AbstractType> section : terms) {
				if (section.getOriginalText().equals(termIdentifier)) {
					return iri;
				}
			}
		}
		return IRI.generateDocumentIRI();
	}

	public void addOntology(Section<ImportFrame> section, IRI ontologyIRI) {
		if (!importedOntologies.containsKey(ontologyIRI)) {
			if (!isKnown(ontologyIRI)) {
				importedOntologies.put(ontologyIRI, section);
			}
		}
	}

	public void addAxioms(Set<OWLAxiom> axioms, IRI ontologyIRI) {
		if (!importedAxioms.containsKey(ontologyIRI)) {
			importedAxioms.put(ontologyIRI, axioms);
		}
	}

	public void removeOntology(Section<? extends AbstractType> section) {

		IRI iri = getInstance().getImportIRIFromSection(section);
		importedOntologies.remove(iri);
		importedAxioms.remove(iri);
	}

	public void addImportedTerm(IRI iri, Section<? extends AbstractType> section) {
		if (!importedTerms.containsKey(iri)) {
			importedTerms.put(iri, new HashSet<Section<? extends AbstractType>>());
		}
		importedTerms.get(iri).add(section);
	}

	public void removeImportedTerm(IRI iri, Section<? extends AbstractType> section) {
		if (importedTerms.containsKey(iri)) {
			importedTerms.get(iri).remove(section);
			importedAxioms.remove(iri);
		}
	}

	public Set<Section<? extends AbstractType>> getImportedTermsForOntology(IRI iri) {
		if (importedTerms.containsKey(iri)) {
			return importedTerms.get(iri);
		}
		return Collections.emptySet();
	}

	public IRI getImportIRIFromSection(Section<? extends AbstractType> section) {
		for (IRI iri : importedOntologies.keySet()) {
			if (importedOntologies.get(iri).equals(section)) {
				return iri;
			}
		}
		return null;
	}

	/**
	 * Checks weather a given ontology IRI is already imported. Note: The IRI
	 * where the Ontology is imported from is not necessary the Ontology IRI.
	 *
	 * @created 01.12.2011
	 * @param IRI ontologyIRI The IRI of the to import ontology.
	 * @return boolean
	 */
	public boolean isKnown(IRI iri) {

		for (Section<ImportFrame> i : importedOntologies.values()) {
			ImportFrame frame = i.get();

			if (frame.getImportIRI(i).getOriginalText().equals(iri.toString())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks weather a given ontology IRI is already imported.
	 *
	 * @created 01.12.2011
	 * @param IRI ontologyIRI The IRI of the to import ontology.
	 * @return boolean
	 */
	public boolean isKnownImportSection(Section<ImportFrame> section) {
		return importedOntologies.containsValue(section);
	}
}
