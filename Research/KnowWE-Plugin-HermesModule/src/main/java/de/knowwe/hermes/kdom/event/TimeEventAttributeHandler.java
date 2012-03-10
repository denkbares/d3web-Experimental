package de.knowwe.hermes.kdom.event;

import java.util.ArrayList;
import java.util.Collection;

import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;

/**
 * A very simple abstract SubtreeHandler that only executes the crateAttribute()
 * hook if there is no error in its fahters subtree
 * 
 * @author Jochen
 * @created 11.10.2010
 * @param <T>
 */
public abstract class TimeEventAttributeHandler<T extends Type> extends SubtreeHandler<T> {

	@Override
	public Collection<Message> create(Article article, Section<T> s) {
		if (s.getFather().hasErrorInSubtree(article)) {
			return new ArrayList<Message>(0);
		}
		else {
			createAttribute(article, s);
		}
		return null;
	}

	protected abstract Collection<Message> createAttribute(Article article, Section<T> s);

	// @Override
	// public boolean needsToCreate(Article article, Section<T> s) {
	// return super.needsToCreate(article, s)
	// || DashTreeUtils.isChangeInAncestorSubtree(article, s, 1);
	// }
	//
	// @Override
	// public boolean needsToDestroy(Article article, Section<T> s) {
	// return super.needsToDestroy(article, s)
	// || DashTreeUtils.isChangeInAncestorSubtree(article, s, 1);
	//
	// }
}
