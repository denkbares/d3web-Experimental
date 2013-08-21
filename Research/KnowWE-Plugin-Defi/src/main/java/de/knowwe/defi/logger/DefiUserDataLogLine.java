// /*
// * Copyright (C) 2013 University Wuerzburg, Computer Science VI
// *
// * This is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation; either version 3 of
// * the License, or (at your option) any later version.
// *
// * This software is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this software; if not, write to the Free
// * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
// * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
// */
// package de.knowwe.defi.logger;
//
// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
//
// /**
// * A log line for all the data about a user.
// *
// * @author dupke
// * @created 31.07.2013
// */
// public class DefiUserDataLogLine {
//
// /** login **/
// // private Date firstLogin;
// // private Date lastLogin;
// // private int numberOfLogins;
// // private int numberOfTimeouts;
// /** page **/
// // private int numberOfVisitedPages;
// // private HashMap<Integer, Integer> numberOfVisitedPagesPerUnit;
// private int numberOfRatedPages;
// private HashMap<Integer, Integer> numberOfRatedPagesPerUnit;
// // private HashMap<String, Integer> numberOfVisitsPerPage;
// private boolean externalPagesVisited;
// private int numberOfExternalPagesVisited;
// /** forum **/
// // private int numberOfComments;
// // private HashMap<String, Integer> numberOfCommentsPerPage; in
// // commentlogger topic hinzufügen
// private int numberOfOpenedDiscussions;
// private HashMap<String, Integer> numberOfOpenedDiscussionsPerUnit;
// /** emergencyplan **/
// private boolean emergencyPlanBuilt;
// private Date emergencyPlanLastChange;
// private boolean emergencyPlanPrinted;
// private Date emergencyPlanPrintedOnDate;
// /** feedback **/
// private List<String> answers;
// private Date firstSave;
// private Date lastSave;
// private List<String> TextboxAnswers;
// private List<Date> datesOfTextboxAnswers;
// /** readbutton **/
// private List<String> readbuttonAnswers;
// private List<Date> datesOfReadbuttonAnswers;
// private final static String NO_DATA = "--";
// /** Separator S used in pagelog **/
// private final String S = DefiSessionEventLogger.getSeparator();
// /** Regex to find sessionlogline **/
// private final String MATCH_ME = "^((?!" + S + ").)+" + S + "((?!" + S +
// ").)+" + S + "((?!" + S
// + ").)+" + S + "((?!" + S + ").)+" + S + "((?!" + S + ").)+" + S + "((?!" + S
// + ").)+$";
//
// public DefiUserDataLogLine(String s) {
// if (!s.matches(MATCH_ME)) throw new IllegalArgumentException(
// "line has not the correct syntax.");
//
// String[] parts = s.split(S);
// firstLogin = parts[0];
// lastLogin = parts[1];
// loginTime = parts[2];
// logOutDate = parts[3];
// logOutTime = parts[4];
// timeout = parts[5];
// }
//
// public DefiSessionLogLine() {
// user = NO_DATA;
// loginDate = NO_DATA;
// loginTime = NO_DATA;
// logOutDate = NO_DATA;
// logOutTime = NO_DATA;
// timeout = NO_DATA;
// }
// }
