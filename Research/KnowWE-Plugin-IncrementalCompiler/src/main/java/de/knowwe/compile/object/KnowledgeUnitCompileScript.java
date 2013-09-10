package de.knowwe.compile.object;

import java.util.Collection;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;

public interface KnowledgeUnitCompileScript<T extends Type> {

	public Collection<Section<? extends Term>> getAllReferencesOfKnowledgeUnit(Section<? extends KnowledgeUnit> section);

	public Collection<Section<? extends Term>> getExternalReferencesOfKnowledgeUnit(Section<? extends KnowledgeUnit> section);

	public void insertIntoRepository(Section<T> section);

	public void deleteFromRepository(Section<T> section);
}
