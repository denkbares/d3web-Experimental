package de.knowwe.kdom.manchester.types;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.IncrementalTermReference;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.KnowWETerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;

public class PredefinedTermReference extends IncrementalTermReference<String> {

	@SuppressWarnings("unchecked")
	final KnowWEDomRenderer<PredefinedTermReference> REF_RENDERER =
			new ToolMenuDecoratingRenderer<PredefinedTermReference>(new StyleRenderer(
					"color:rgb(25, 180, 120)"));

	public PredefinedTermReference() {
		super(String.class);
		this.setCustomRenderer(REF_RENDERER);
		this.setSectionFinder(new SectionFinder() {

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {

				String trimmed = text.trim();

				if (IncrementalCompiler.getInstance().getTerminology().isPredefinedObject(trimmed)) {

					int currentStart = text.indexOf(trimmed);
					List<SectionFinderResult> results = new ArrayList<SectionFinderResult>();
					results.add(new SectionFinderResult(currentStart, text.length()));
					return results;
				}

				return null;
			}
		});
	}

	@Override
	public String getTermIdentifier(Section<? extends KnowWETerm<String>> s) {
		return s.getOriginalText();
	}

	@Override
	public String getTermObjectDisplayName() {
		return "IRI";
	}
}
