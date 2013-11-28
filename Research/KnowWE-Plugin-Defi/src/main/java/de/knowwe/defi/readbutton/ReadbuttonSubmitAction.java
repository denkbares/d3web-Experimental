/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.defi.readbutton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.event.EventManager;
import de.knowwe.defi.event.DefiPageRatedEvent;
import de.knowwe.defi.logger.DefiPageRateEventLogger;

/**
 * @author dupke
 */
public class ReadbuttonSubmitAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		// Check file, make it if not exists, return if error occurs
		if (!DefiPageRateEventLogger.checkRateLogFile()) return;

		// Fire page rate event
		Map<String, String> rateInfos = context.getParameters();
		String id = rateInfos.get("id");
		String title = context.getTitle();
		String user = context.getUserName();
		String date = (new SimpleDateFormat("dd.MM.yyyy HH:mm")).format((new Date()));
		String realvalue = rateInfos.get("realvalue");
		String value = rateInfos.get("value");
		String label = rateInfos.get("label");
		String discussed = rateInfos.get("discussed");
		String closed = rateInfos.get("closed");
		EventManager.getInstance().fireEvent(
				new DefiPageRatedEvent(id, title, user, date, realvalue, value, label, discussed,
						closed));
	}

}
