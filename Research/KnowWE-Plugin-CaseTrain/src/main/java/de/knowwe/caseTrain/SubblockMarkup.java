package de.knowwe.caseTrain;

import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.type.AnonymousTypeInvisible;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public class SubblockMarkup extends AbstractType {

	private final String key;

	public static final String START_TAG = "<";
	public static final String END_TAG = ">";

	private final SubBlockMarkupContent content;

	public String getCSSClass() {
		return this.getClass().getSimpleName();
	}

	public SubblockMarkup(String key) {
		this.key = key;

		this.sectionFinder = new RegexSectionFinder("\\r?\\n(" + START_TAG
				+ key + ":" + "(.*?)" + END_TAG + ")\\r?\\n", Pattern.DOTALL, 1);

		this.setCustomRenderer(new KnowWEDomRenderer<SubblockMarkup>() {

			@Override
			public void render(KnowWEArticle article, Section<SubblockMarkup> sec, UserContext user, StringBuilder string) {
				string.append(KnowWEUtils.maskHTML("<div class='"
						+ sec.get().getCSSClass()
						+ "'>"));
				DelegateRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("</div>"));

			}
		});

		content = new SubBlockMarkupContent();
		String regex2 = START_TAG
				+ key + ":" + "(.*?)" + END_TAG;
		content.setSectionFinder(new RegexSectionFinder(regex2, Pattern.DOTALL, 1));
		this.addChildType(content);

		AnonymousTypeInvisible keytext = new AnonymousTypeInvisible("syntax");
		this.addChildType(keytext);
	}

	protected String getKey() {
		return this.key;
	}

	public void addContentType(Type t) {
		content.addChildType(t);
	}

}

class SubBlockMarkupContent extends AbstractType {

}
