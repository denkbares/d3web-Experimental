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
package de.knowwe.defi.time;

import de.knowwe.core.compile.DefaultGlobalCompiler;
import de.knowwe.core.compile.DefaultGlobalCompiler.DefaultGlobalScript;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.AllTextFinder;
import de.knowwe.core.report.CompilerMessage;
import de.knowwe.kdom.sectionFinder.LineSectionFinder;

public class TimeTableTemplateContentType extends AbstractType {

	public TimeTableTemplateContentType() {
		this.setSectionFinder(AllTextFinder.getInstance());
		this.addChildType(new TimeTableLine());
	}

	class TimeTableLine extends AbstractType {

		TimeTableLine() {
			this.setSectionFinder(LineSectionFinder.getInstance());
			this.addChildType(new NumberOfDaysT());
		}
	}
}

class NumberOfDaysT extends AbstractType {

	public NumberOfDaysT() {
		this.setSectionFinder(AllTextFinder.getInstance());
		this.addCompileScript(new NumberOfDaysChecker());
	}

	class NumberOfDaysChecker extends DefaultGlobalScript<NumberOfDaysT> {

		@Override
		public void compile(DefaultGlobalCompiler compiler, Section<NumberOfDaysT> s) throws CompilerMessage {
			String numberText = s.getText().trim();
			try {
				int number = Integer.parseInt(numberText);
				if (number < 0) throw new NumberFormatException();
			}
			catch (NumberFormatException e) {
				throw CompilerMessage.error("Invalid Number: " + numberText);
			}

		}
	}
}
