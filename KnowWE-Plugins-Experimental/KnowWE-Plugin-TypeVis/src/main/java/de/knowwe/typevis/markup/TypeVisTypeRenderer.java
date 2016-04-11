package de.knowwe.typevis.markup;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.PackageCompileLinkToTermDefinitionProvider;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.typevis.TypeGraphDataBuilder;
import de.knowwe.visualization.Config;

public class TypeVisTypeRenderer extends DefaultMarkupRenderer {

	@Override
	public void renderContents(Section<?> section, UserContext user, RenderResult string) {

		Config config = new Config();
		config.readFromSection(Sections.cast(section, DefaultMarkupType.class));

		TypeGraphDataBuilder builder = new TypeGraphDataBuilder(section, config, new PackageCompileLinkToTermDefinitionProvider());
		builder.render(string);
	}

}
