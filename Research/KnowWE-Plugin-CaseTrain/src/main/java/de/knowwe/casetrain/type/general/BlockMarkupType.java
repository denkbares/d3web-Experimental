/*
 * Copyright (C) 2009 Chair of Aimport java.util.regex.Pattern;
 * 
 * import de.d3web.we.kdom.AbstractType; import de.d3web.we.kdom.Article; import
 * de.d3web.we.kdom.Section; import de.d3web.we.kdom.Type; import
 * de.d3web.we.kdom.rendering.DelegateRenderer; import
 * de.d3web.we.kdom.rendering.KnowWEDomRenderer; import
 * de.d3web.we.kdom.report.KDOMError; import
 * de.d3web.we.kdom.report.KDOMWarning; import
 * de.d3web.we.kdom.sectionFinder.RegexSectionFinder; import
 * de.d3web.we.kdom.type.AnonymousTypeInvisible; import
 * de.d3web.we.user.UserContext; import de.d3web.we.utils.KnowWEUtils; Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.knowwe.casetrain.type.general;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.MessageRenderer;
import de.knowwe.kdom.AnonymousTypeInvisible;

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

	@Override
	public MessageRenderer getMessageRenderer(Message.Type messageType) {
		if (Message.Type.INFO.equals(messageType)) return null;
		return super.getMessageRenderer(messageType);
	}

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
				+ key + ":" + "(.*?)" + END_TAG + ")\\r?\\n";

		this.setSectionFinder(new RegexSectionFinder(regex, Pattern.DOTALL
				| Pattern.MULTILINE, 1));

		this.setRenderer(new BlockMarkupTypeRenderer());

		content = new BlockMarkupContent(key + "Content");

		// TODO: reuse regex above
		String regex2 = START_TAG + ""
				+ key + ":" + "(.*?)" + END_TAG;
		content.setSectionFinder(new RegexSectionFinder(regex2, Pattern.DOTALL, 1));
		this.addChildType(content);

		// TODO Title needed?
		// content.addChildType(new Title());

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
