/*
 * Copyright (C) 2009 Chair of Aimport java.util.regex.Pattern;

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
Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.knowwe.casetrain.type.general;

import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMNotice;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.KDOMWarning;
import de.d3web.we.kdom.report.MessageRenderer;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.type.AnonymousTypeInvisible;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.casetrain.util.Utils;

/**
 * 
 * A simple Type to capture a section of a page using a start/end-tag and a
 * keyword. Content-types can be added. Error messages are rendered as summary
 * at the top of the block.
 * 
 * @author Jochen
 * @created 06.04.2011
 */
public abstract class BlockMarkupType extends AbstractType {

	// Warnings are only rendered in message block
	//	@Override
	//	public MessageRenderer getWarningRenderer() {
	//		return new MessageRenderer() {
	//
	//			@Override
	//			public String preRenderMessage(KDOMReportMessage m, UserContext user) {
	//				return "";
	//			}
	//
	//			@Override
	//			public String postRenderMessage(KDOMReportMessage m, UserContext user) {
	//				return "";
	//			}
	//		};
	//	}

	@Override
	public MessageRenderer getNoticeRenderer() {
		return new MessageRenderer() {

			@Override
			public String preRenderMessage(KDOMReportMessage m, UserContext user) {
				return "";
			}

			@Override
			public String postRenderMessage(KDOMReportMessage m, UserContext user) {
				return "";
			}
		};
	}

	//	@Override
	//	public MessageRenderer getErrorRenderer() {
	//		return new MessageRenderer() {
	//
	//			@Override
	//			public String preRenderMessage(KDOMReportMessage m, UserContext user) {
	//				return "";
	//			}
	//
	//			@Override
	//			public String postRenderMessage(KDOMReportMessage m, UserContext user) {
	//				return "";
	//			}
	//		};
	//	}

	private final String key;
	public static final String START_TAG = "<<";
	public static final String END_TAG = ">>";

	private final BlockMarkupContent content;

	public String getCSSClass() {
		return this.getClass().getSimpleName();
	}

	public BlockMarkupType(String key) {
		this.key = key;

		String regex = "^\\s*(" + START_TAG + ""
		+ key +":"+ "(.*?)" + END_TAG + ")\\r?\\n";

		this.sectionFinder = new RegexSectionFinder(regex, Pattern.DOTALL
				| Pattern.MULTILINE, 1);

		this.setCustomRenderer(new KnowWEDomRenderer<BlockMarkupType>() {

			@SuppressWarnings("unchecked")
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

				Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
						article,
						sec,
						KDOMNotice.class), string);

				// TODO Delegation renders PlainText around collapsebox!
				Section<BlockMarkupContent> con =
					Sections.findSuccessor(sec, BlockMarkupContent.class);
				con.get().getRenderer().render(article, con, user, string);
				//				DelegateRenderer.getInstance().render(
				//						article, sec,
				//						user, string);
				string.append(KnowWEUtils.maskHTML("</div>"));

			}
		});


		content = new BlockMarkupContent(key + "Content");

		// TODO: reuse regex above
		String regex2 = START_TAG + ""
		+ key + ":" + "(.*?)" + END_TAG;
		content.setSectionFinder(new RegexSectionFinder(regex2, Pattern.DOTALL, 1));
		this.addChildType(content);

		// TODO Title needed?
		//		content.addChildType(new Title());

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
