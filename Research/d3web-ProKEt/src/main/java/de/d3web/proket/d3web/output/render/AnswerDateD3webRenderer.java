/**
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.d3web.output.render;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;
import javax.servlet.http.HttpSession;

/**
 * Renderer for rendering basic NumAnswers.
 *
 * TODO CHECK: 1) basic properties for answers 2) d3web resulting properties,
 * e.g. is indicated, is shown etc.
 *
 * @author Martina Freiberg @created 16.01.2011
 */
public class AnswerDateD3webRenderer extends AnswerTextD3webRenderer implements AnswerD3webRenderer {

    /**
     * Specifically adapted for rendering NumAnswers
     */
    @Override
    public String renderTerminologyObject(ContainerCollection cc,
            Session d3webSession, Choice choice, TerminologyObject to, TerminologyObject parent, int loc,
            HttpSession httpSession) {

        QuestionDate dq = (QuestionDate) to;

        StringBuilder sb = new StringBuilder();

        // return if the InterviewObject is null
        if (to == null) {
            return "";
        }

        // get the fitting template. In case user prefix was specified, the
        // specific TemplateName is returned, else the base object name.
        StringTemplate st = TemplateUtils.getStringTemplate(
                super.getTemplateName("DateAnswerPure"), "html");

        st.setAttribute("fullId", getID(dq));
        st.setAttribute("realAnswerType", "date");
        st.setAttribute("parentFullId", getID(parent));

        Blackboard bb = d3webSession.getBlackboard();
        Value value = bb.getValue((ValueObject) to);

        if (to.getInfoStore().getValue(BasicProperties.ABSTRACTION_QUESTION)) {
            st.setAttribute("readonly", "true");
        }

        // QContainer indicated
        if (bb.getSession().getKnowledgeBase().getInitQuestions().contains(parent)
                || isIndicated(parent, bb)) {

            // show, if indicated follow up
            if ((D3webUtils.isFollowUpToQCon(to, parent) && isIndicated(to, bb))
                    || (!D3webUtils.isFollowUpToQCon(to, parent))) {
                st.removeAttribute("readonly");
            } else {
                st.setAttribute("readonly", "true");
                // also remove possible set values
                st.removeAttribute("selection");
                st.setAttribute("selection", "");
            }
        } else {
            st.setAttribute("readonly", "true");
            // also remove possible set values
            st.removeAttribute("selection");
            st.setAttribute("selection", "");
        }

        String dateDescription = to.getInfoStore().getValue(ProKEtProperties.DATE_FORMAT);
        boolean dateReverse = false;

       // if date need to be reversed, date description contains a
        // :::reverse at the end (i.e. after the simple date format spec
        if (dateDescription != null && dateDescription.contains(":::reverse")) {
            dateReverse = true;
            dateDescription = dateDescription.replace(":::reverse", "");
        }

        SimpleDateFormat dateFormat = null;

        if (dateDescription != null && !dateDescription.isEmpty()) {
            String[] dateDescSplit = dateDescription.split("OR");
            try {
                dateFormat = new SimpleDateFormat(dateDescSplit[0].trim());
            } catch (IllegalArgumentException e) {
            }
        }
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        }
        dateDescription = dateFormat.toPattern();

        GregorianCalendar dateCal = null;
        if (value != null && UndefinedValue.isNotUndefinedValue(value)
                && !value.equals(Unknown.getInstance())) {

            Date d = ((DateValue) value).getDate();
            dateCal = new GregorianCalendar();
            dateCal.setTime(d);
        }

        String year = null, month = null, day = null, hour = null, minute = null, second = null;

        if (dateCal != null) {
            year = String.valueOf(dateCal.get(Calendar.YEAR));
            month = String.valueOf(dateCal.get(Calendar.MONTH) + 1);
            day = String.valueOf(dateCal.get(Calendar.DAY_OF_MONTH));
            hour = String.valueOf(dateCal.get(Calendar.HOUR_OF_DAY));
            minute = String.valueOf(dateCal.get(Calendar.MINUTE));
            second = String.valueOf(dateCal.get(Calendar.SECOND));
        }

        StringBuilder table = new StringBuilder();
        StringBuilder firstRow = new StringBuilder();
        StringBuilder secondRow = new StringBuilder();
        table.append("<table style=\"width:0px\"><tr>");
        if (dateDescription.contains("d")) {
            firstRow.append("<td>Day:</td>");
            secondRow.append(createDayDropDown(day));
        }

        if (dateDescription.contains("M")) {
            firstRow.append("<td>Month:</td>");
            secondRow.append(createMonthDropDown(month));
        }

        if (dateDescription.contains("y")) {
            if (dateReverse) {
                firstRow.append("<td>Year:</td>");
                secondRow.append(createYearDropDownReverse(year));
            } else {
                firstRow.append("<td>Year:</td>");
                secondRow.append(createYearDropDown(year));
            }
        }

        if (dateDescription.contains("H")) {
            firstRow.append("<td>Hour:</td>");
            secondRow.append(createHourDropDown(hour));
        }

        if (dateDescription.contains("m")) {
            firstRow.append("<td>Minute:</td>");
            secondRow.append(createMinuteDropDown(minute));
        }

        if (dateDescription.contains("s")) {
            firstRow.append("<td>Second:</td>");
            secondRow.append(createSecondDropDown(second));
        }
        table.append(firstRow);
        table.append("</tr><tr>");
        table.append(secondRow);
        table.append("</tr></table>");
        st.setAttribute(
                "dropdown_menu", table.toString());

        sb.append(st.toString());

        super.makeTables(to, to, cc, sb);

        return sb.toString();
    }

    private String createYearDropDownReverse(String selectedValue) {
        return createDropDownOptions(selectedValue, "Year", 2008, 2025);
    }

    private String createYearDropDown(String selectedValue) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return createDropDownOptions(selectedValue, "Year", currentYear, 1900);
    }

    private String createMonthDropDown(String selectedValue) {
        return createDropDownOptions(selectedValue, "Month", 1, 12);
    }

    private String createDayDropDown(String selectedValue) {
        return createDropDownOptions(selectedValue, "Day", 1, 31);
    }

    private String createHourDropDown(String selectedValue) {
        return createDropDownOptions(selectedValue, "Hour", 23);
    }

    private String createMinuteDropDown(String selectedValue) {
        return createDropDownOptions(selectedValue, "Minute", 59);
    }

    private String createSecondDropDown(String selectedValue) {
        return createDropDownOptions(selectedValue, "Second", 59);
    }

    private String createDropDownOptions(String selectedValue, String name, int end) {
        return createDropDownOptions(selectedValue, name, 0, end);
    }

    private String createDropDownOptions(String selectedValue, String name, int start, int end) {
        ArrayList<String> measure = new ArrayList<String>();
        boolean reverse = false;
        if (end < start) {
            reverse = true;
            int temp = start;
            start = end;
            end = temp;
        }
        for (int i = start; i <= end; i++) {
            measure.add(String.valueOf(i));
        }
        if (reverse) {
            Collections.reverse(measure);
        }
        return "<td><select type='" + name + "select'>\n"
                + createDropDownOptionsWithDefault("", selectedValue,
                measure.toArray(new String[]{})) + "<select/></td>";
    }
}
