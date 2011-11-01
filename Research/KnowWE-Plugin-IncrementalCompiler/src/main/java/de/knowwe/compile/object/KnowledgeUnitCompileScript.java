package de.knowwe.compile.object;

import java.util.Collection;

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;

public interface KnowledgeUnitCompileScript<T extends Type> {

	public Collection<Section<TermReference>> getAllReferencesOfKnowledgeUnit(Section<? extends KnowledgeUnit<T>> section);

	public void insertIntoRepository(Section<T> section);

	public void deleteFromRepository(Section<T> section);
}
