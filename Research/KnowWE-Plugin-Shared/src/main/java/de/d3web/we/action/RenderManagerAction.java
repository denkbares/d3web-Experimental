/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.we.action;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.rendering.CustomRenderer;
import de.d3web.we.kdom.rendering.RendererManager;

/**
 * RenderManagerAction. Sets or removes a {@link CustomRenderer} from the
 * {@link RendererManager}. The renderer are given as a pair of
 * renderer:objecttype encoded as JSON.
 * <p>
 * For example:
 * <p>
 * <code>
 * var params = { rm : custom,
 *     types : {
 *         'de.d3web.we.kdom.questionTreeNew.dialog.QuestionDashTreeRenderer'
 *             : 'de.d3web.we.kdom.questionTreeNew.QuestionDashTree',
 *         'de.d3web.we.kdom.questionTreeNew.dialog.DashesPrefixRenderer'
 *             : 'de.d3web.we.kdom.dashTree.DashesPrefix'
 *     }
 * }
 * </code>
 * <p>
 * The {@code rm} parameter declares if the given renderer are removed or added
 * to the {@link RendererManager}. If the renderer should be removed set the
 * {@code rm} parameter to an empty string, otherwise to something you like
 * unlike the empty string.
 *
 * @author smark
 * @since 2010/03/09
 */
public class RenderManagerAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		Map<String, String> parameterMap = context.getParameters();

		Gson gson = new Gson();
		java.lang.reflect.Type mapType = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> gsonFormMap = gson.fromJson(parameterMap.get("types"), mapType);

		String rm = parameterMap.get("rm");

		if (gsonFormMap != null) {

			for (String renderer : gsonFormMap.keySet()) {
				Type type = null;
				CustomRenderer customRenderer = null;

				String t = gsonFormMap.get(renderer);
				List<Type> types = KnowWEEnvironment.getInstance()
						.getAllTypes();
				for (Type Type : types) {
					if (Type.getClass().getName().equals(t)) {
						type = Type;
						break;
					}
				}
				if (type == null) continue;

				if (rm != "") { // set renderer

					try {
						ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
						try {
							customRenderer = (CustomRenderer) Class.forName(
									renderer, true, classLoader).newInstance();
						}
						catch (InstantiationException e) {
							e.printStackTrace();
						}
						catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

					RendererManager.getInstance().setRenderer(type, customRenderer);
				}
				else { // remove renderer
					RendererManager.getInstance().removeRenderer(type);
				}
			}
		}
	}
}
