package de.knowwe.defi.table;

import de.d3web.we.kdom.table.NullTableAttributesProvider;
import de.d3web.we.kdom.table.Table;

public class TableTemplateType extends Table {

	public TableTemplateType() {
		super(new NullTableAttributesProvider());
		this.injectTableCellContentChildtype(new InputFieldCellContent());
	}

}
