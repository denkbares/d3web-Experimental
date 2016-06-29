/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.defi.time;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.defi.menu.MenuUtilities;


/**
 * 
 * @author dupke
 * @created 10.02.2013
 */
public class GetPersonalTimeTableAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String user = context.getParameter("user");
		List<Date> dates = new ArrayList<>();
		StringBuilder datesStr = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

		dates.addAll(TimeTableUtilities.getPersonalTimeTable(user));
		for (Date date : dates) {
			datesStr.append(sdf.format(date)).append("#");
		}

		for (int i = dates.size(); i < MenuUtilities.getRootUnits().size(); i++) {
			datesStr.append("-#");
		}

		datesStr.delete(datesStr.length() - 1, datesStr.length());
		context.getWriter().write(datesStr.toString());
	}

}
