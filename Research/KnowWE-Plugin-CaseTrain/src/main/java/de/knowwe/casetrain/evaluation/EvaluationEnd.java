/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.casetrain.evaluation;

import de.knowwe.casetrain.type.Closure;
import de.knowwe.casetrain.type.general.SubblockMarkup;
import de.knowwe.casetrain.type.general.SubblockMarkupContent;
import de.knowwe.casetrain.type.general.Title;
import de.knowwe.casetrain.util.Utils;
import de.knowwe.core.compile.DefaultGlobalCompiler;
import de.knowwe.core.compile.DefaultGlobalCompiler.DefaultGlobalScript;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.CompilerMessage;

/**
 * 
 * @author Johannes Dienst
 * @created 20.05.2011
 */
public class EvaluationEnd extends SubblockMarkup {

	public EvaluationEnd() {
		super("EvaluationEnd");
		this.addContentType(new Title());

		this.addCompileScript(new DefaultGlobalScript<Closure>() {

			@Override
			public void compile(DefaultGlobalCompiler compiler, Section<Closure> s) throws CompilerMessage {

				// Section<Title> title = Sections.findSuccessor(s,
				// Title.class);
				// if (title == null) {
				// messages.add(new MissingTitleError(Title.TITLE));
				// }

				Section<SubblockMarkupContent> plain =
						Sections.successor(s, SubblockMarkupContent.class);
				if (plain.getText() == null || plain.getText().trim().equals("")) {
					throw new CompilerMessage(
							Utils.missingContentWarning(EvaluationEnd.class.getSimpleName()));
				}

			}

		});
	}

	@Override
	public String getCSSClass() {
		return "Ie";
	}

}
