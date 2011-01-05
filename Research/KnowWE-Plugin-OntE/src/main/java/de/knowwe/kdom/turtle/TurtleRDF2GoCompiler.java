package de.knowwe.kdom.turtle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.basic.PlainText;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.SyntaxError;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.d3web.we.kdom.type.AnonymousType;
import de.knowwe.kdom.turtle.TurtleMarkup.TurtleObject;
import de.knowwe.kdom.turtle.TurtleMarkup.TurtlePredicate;
import de.knowwe.kdom.turtle.TurtleMarkup.TurtleSubject;

public class TurtleRDF2GoCompiler extends GeneralSubtreeHandler<TurtleMarkup>{

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<TurtleMarkup> s) {
		if(s.hasErrorInSubtree(article)) {
			return new ArrayList<KDOMReportMessage>(0);
		}
			
		
		List<Section<? extends KnowWEObjectType>> children = s.getChildren();
		List<Section<? extends KnowWEObjectType>> childrenTmp = new ArrayList<Section<? extends KnowWEObjectType>>(children);
		ListIterator<Section<? extends KnowWEObjectType>> iterator = childrenTmp.listIterator();
		while(iterator.hasNext()) {
			Section<? extends KnowWEObjectType> section = iterator.next();
			if(section.get() instanceof AnonymousType || section.get() instanceof PlainText) {
				iterator.remove();
			}
		}
		
		if(childrenTmp.size() != 3) {
			return Arrays.asList((KDOMReportMessage) new SyntaxError(
			"expected 3 objects - found:"+childrenTmp.size()));
		}
		
		Section<? extends KnowWEObjectType> subject = childrenTmp.get(0);
		Section<? extends KnowWEObjectType> predicate = childrenTmp.get(1);
		Section<? extends KnowWEObjectType> object = childrenTmp.get(2);
		
		
		
		return null;
	}

}
