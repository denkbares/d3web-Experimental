package de.knowwe.defi.table;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.sectionFinder.AllTextSectionFinder;

public class DefineTableContentType extends DefaultAbstractKnowWEObjectType{
	
	public DefineTableContentType(){
		this.setSectionFinder(new AllTextSectionFinder());
		this.addChildType(new ColumnType());
		
	}
}
