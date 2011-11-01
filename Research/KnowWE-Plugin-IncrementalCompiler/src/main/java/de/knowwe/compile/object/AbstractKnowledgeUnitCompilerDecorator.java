package de.knowwe.compile.object;

import java.util.Collection;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.plugin.Plugins;

public class AbstractKnowledgeUnitCompilerDecorator extends AbstractType
		implements KnowledgeUnitCompileScript {

	private KnowledgeUnitCompileScript compileScript = null;

	public AbstractKnowledgeUnitCompilerDecorator() {
//		Extension[] exts = PluginManager.getInstance().getExtensions(
//				Plugins.EXTENDED_PLUGIN_ID,
//				Plugins.EXTENDED_POINT_COMPILESCRIPT);
//		for (Extension extension : exts) {
//			String parameter = extension.getParameter("scope");
//			String thisClassName = this.getClass().getCanonicalName();
//			if (parameter.equals(thisClassName)) {
//				Object o = extension.getSingleton();
//				if (o instanceof KnowledgeUnitCompileScript) {
//					compileScript = (((KnowledgeUnitCompileScript) o));
//				}
//			}
//		}
	}

	@Override
	public Collection getAllReferencesOfKnowledgeUnit(Section section) {
		return compileScript.getAllReferencesOfKnowledgeUnit(section);
	}

	@Override
	public void insertIntoRepository(Section section) {
		compileScript.insertIntoRepository(section);

	}

	@Override
	public void deleteFromRepository(Section section) {
		compileScript.deleteFromRepository(section);
	}

}
