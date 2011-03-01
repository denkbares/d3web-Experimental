package de.knowwe.defi.table;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.sectionFinder.AllTextSectionFinder;

public class DefineTableContentType extends AbstractType {
	
	public DefineTableContentType(){
		this.setSectionFinder(new AllTextSectionFinder());
		this.addChildType(new ColumnType());
		
	}
}
