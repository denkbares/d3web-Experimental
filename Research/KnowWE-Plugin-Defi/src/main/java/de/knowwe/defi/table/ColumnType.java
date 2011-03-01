package de.knowwe.defi.table;

import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;

public class ColumnType extends AbstractType {
	
	public static final StyleRenderer Text_RENDERER = new StyleRenderer("color:rgb(152, 180, 12)");
	
	public ColumnType() {
		this.setSectionFinder(new RegexSectionFinder("SPALTE:(.*?)\r?\n", Pattern.DOTALL,1));
		this.setCustomRenderer(Text_RENDERER);
	}
}
