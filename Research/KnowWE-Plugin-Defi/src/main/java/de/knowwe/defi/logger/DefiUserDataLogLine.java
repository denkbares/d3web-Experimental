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
package de.knowwe.defi.logger;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author dupke
 * @created 31.07.2013
 */
public class DefiUserDataLogLine {

	/** login **/
	private Date firstLogin;
	private Date lastLogin;
	private int numberOfLogins;
	private int numberOfTimeouts;
	/** page **/
	private int numberOfVisitedPages;
	private HashMap<Integer, Integer> numberOfVisitedPagesPerUnit;
	private int numberOfRatedPages;
	private HashMap<Integer, Integer> numberOfRatedPagesPerUnit;
	private HashMap<String, Integer> numberOfVisitsPerPage;
	private boolean externalPagesVisited;
	private int numberOfExternalPagesVisited;
	/** forum **/
	private int numberOfComments;
	private HashMap<String, Integer> numberOfCommentsPerPage;
	private int numberOfOpenedDiscussions;
	private HashMap<String, Integer> numberOfOpenedDiscussionsPerUnit;
	/** emergencyplan **/
	private boolean emergencyPlanBuilt;
	private Date emergencyPlanLastChange;
	private boolean emergencyPlanPrinted;
	private Date emergencyPlanPrintedOnDate;
	/** feedback **/
	private List<String> answers;
	private Date firstSave;
	private Date lastSave;
	private List<String> TextboxAnswers;
	private List<Date> datesOfTextboxAnswers;
	/** readbutton **/
	private List<String> readbuttonAnswers;
	private List<Date> datesOfReadbuttonAnswers;
}
