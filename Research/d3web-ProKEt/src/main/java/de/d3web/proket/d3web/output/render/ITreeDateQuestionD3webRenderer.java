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

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * TODO: make super-class for clari hie?!
 *
 * @author Martina Freiberg @created 22.04.2012
 */
public class ITreeDateQuestionD3webRenderer extends AnswerDateD3webRenderer implements IQuestionD3webRenderer {

    protected static String TT_PROP_ERROR = "<b>Gewählte Antwort widerspricht der aus den Detailfragen hergeleiteten Bewertung.</b> "
            + "<br />Löschen Sie mindestens eine Antwort durch Klick auf den X-Button der jeweiligen Detailfrage, "
            + "wenn Sie eine andere als die bisher hergeleitete Bewertung setzen möchten.";
    

    @Override
    /**
     * Adapted specifically for question rendering
     */
    public String renderTerminologyObject(Session d3webSession, ContainerCollection cc,
            TerminologyObject to, TerminologyObject parent, int loc, HttpSession httpSession,
            HttpServletRequest request) {

      Boolean hidden = to.getInfoStore().getValue(ProKEtProperties.HIDE);
        // return if the InterviewObject is null
        if (to == null || (hidden != null && hidden)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();


        // get the fitting template. In case user prefix was specified, the
        // specific TemplateName is returned, otherwise, the base object name.
        StringTemplate st = TemplateUtils.getStringTemplate(
                super.getTemplateName("ITreeDateQuestion"), "html");

        // set some basic properties
        st.setAttribute("fullId", getID(to));
        st.setAttribute("title", D3webUtils.getTOPrompt(to, loc));
        
        
        // set bonus text: is displayed in auxinfo panel
        String bonustext = 
                to.getInfoStore().getValue(ProKEtProperties.POPUP);
        st.setAttribute("bonusText", bonustext);
        
       
        String dateDescription = to.getInfoStore().getValue(ProKEtProperties.DATE_FORMAT);
        boolean dateReverse = false;

        // if date need to be reversed, date description contains a
        // :::reverse at the end (i.e. after the simple date format spec
        if (dateDescription != null && dateDescription.contains(":::reverse")) {
            dateReverse = true;
            dateDescription = dateDescription.replace(":::reverse", "");
        }

        Blackboard bb = d3webSession.getBlackboard();
        Value value = bb.getValue((ValueObject) to);
        
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
        table.append("<table style=\"width:0px;\" class=\"table-imagebox\"><tr>");
        if (dateDescription.contains("d")) {
            firstRow.append("<td>" + D3webUtils.translateDropdownTitle("D", loc) + "</td>");
            secondRow.append(D3webUtils.createDayDropDown(day));
        }

        if (dateDescription.contains("M")) {
             firstRow.append("<td>" + D3webUtils.translateDropdownTitle("M", loc) + "</td>");
            secondRow.append(D3webUtils.createMonthDropDown(month));
        }

        if (dateDescription.contains("y")) {
            if (dateReverse) {
                 firstRow.append("<td>" + D3webUtils.translateDropdownTitle("Y", loc) + "</td>");
                secondRow.append(D3webUtils.createYearDropDownReverse(year));
            } else {
                firstRow.append("<td>" + D3webUtils.translateDropdownTitle("Y", loc) + "</td>");
                secondRow.append(D3webUtils.createYearDropDown(year));
            }
        }

        if (dateDescription.contains("H")) {
             firstRow.append("<td>" + D3webUtils.translateDropdownTitle("H", loc) + "</td>");
            secondRow.append(D3webUtils.createHourDropDown(hour));
        }

        if (dateDescription.contains("m")) {
             firstRow.append("<td>" + D3webUtils.translateDropdownTitle("Min", loc) + "</td>");
            secondRow.append(D3webUtils.createMinuteDropDown(minute));
        }

        if (dateDescription.contains("s")) {
            firstRow.append("<td>" + D3webUtils.translateDropdownTitle("S", loc) + "</td>");
            secondRow.append(D3webUtils.createSecondDropDown(second));
        }
        table.append(firstRow);
        table.append("</tr><tr>");
        table.append(secondRow);
        table.append("</tr></table>");
        
        st.setAttribute(
                "dropdown", table.toString());
        
        


        // render arrows: --> check whether question has children,
        if(to.getChildren().length > 0){
         st.setAttribute("typeimg", "img/closedArrow.png");
        } else {
            st.setAttribute("typeimg", "img/transpSquare.png");
        }

        /*
         * READ FLOW - AND/OR/Score/Rules verbalization
         */
        // for topmost element, do not render any read flow verbalization
         if (parent.getName().equals("Q000")) {
            st.setAttribute("readimg", "img/transpSquare.png");
        } else if (parent.getInfoStore().getValue(ProKEtProperties.RULETYPE) != null
                && parent.getInfoStore().getValue(ProKEtProperties.RULETYPE).equals(true)) {
            st.setAttribute("readimg", "img/Formula.png");
            st.setAttribute("qtype", "ruletype");
        } else if (parent.getInfoStore().getValue(ProKEtProperties.SCORING) != null
                && parent.getInfoStore().getValue(ProKEtProperties.SCORING).equals(true)) {
            st.setAttribute("readimg", "img/Score.png");
            st.setAttribute("qtype", "scoretype");
        } else if (parent.getInfoStore().getValue(ProKEtProperties.ORTYPE) != null
                && parent.getInfoStore().getValue(ProKEtProperties.ORTYPE).equals(true)) {
            st.setAttribute("readimg", "img/Or.png");
        } else {
            st.setAttribute("readimg", "img/And.png");

        }

        // TODO: render the value i.e. coloring of the question
        // System.out.println(getID(to) + ": " + val);

        st.removeAttribute("tty");
        st.removeAttribute("ttn");
        st.removeAttribute("ttu");
        st.removeAttribute("ttnan");


     

        st.setAttribute("tooltip", TT_PROP_ERROR);

        super.renderChildrenITreeNum(st, d3webSession, cc, to, loc, httpSession, request);

        sb.append(st.toString());

        return sb.toString();
    }
}
