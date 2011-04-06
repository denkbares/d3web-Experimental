package de.knowwe.caseTrain;

import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMWarning;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.type.AnonymousTypeInvisible;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;

public abstract class BlockMarkupType extends AbstractType {

	private final String key;
	public static final String START_TAG = "<<";
	public static final String END_TAG = ">>";

	private final BlockMarkupContent content;

	public String getCSSClass() {
		return this.getClass().getSimpleName();
	}

	public BlockMarkupType(String key) {
		this.key = key;
		
		String regex = "^(" + START_TAG + ""
		+ key +":"+ "(.*?)" + END_TAG + ")\\r?\\n";
		
		this.sectionFinder = new RegexSectionFinder(regex, Pattern.DOTALL
				| Pattern.MULTILINE, 1);

		this.setCustomRenderer(new KnowWEDomRenderer<BlockMarkupType>() {

			@Override
			public void render(KnowWEArticle article, Section<BlockMarkupType> sec, UserContext user, StringBuilder string) {
				string.append(KnowWEUtils.maskHTML("<div class='"
						+ sec.get().getCSSClass()
						+ "'>"));
				Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
						article,
						sec,
						KDOMError.class), string);

				Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
						article,
						sec,
						KDOMWarning.class), string);
				DelegateRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("</div>"));

			}
		});


		content = new BlockMarkupContent(key + "Content");

		String regex2 = START_TAG + ""
				+ key + ":" + "(.*?)" + END_TAG;
		content.setSectionFinder(new RegexSectionFinder(regex2, Pattern.DOTALL, 1));
		this.addChildType(content);

		content.addChildType(new Title());

		AnonymousTypeInvisible keytext = new AnonymousTypeInvisible("syntax");
		this.addChildType(keytext);
	}

	public String getKey() {
		return key;
	}

	public void addContentType(Type t) {
		content.addChildType(t);
	}




}

class BlockMarkupContent extends AbstractType {

	private final String css;

	public BlockMarkupContent(String cssClass) {
		this.css = cssClass;
		this.setCustomRenderer(new KnowWEDomRenderer<SubblockMarkup>() {

			@Override
			public void render(KnowWEArticle article, Section<SubblockMarkup> sec, UserContext user, StringBuilder string) {
				string.append(KnowWEUtils.maskHTML("<div class='"
						+ css
						+ "'>"));
				DelegateRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("</div>"));

			}
		});

	}

}
