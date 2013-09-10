/**
 * Copyright (C) 2012 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.proket.d3web.ue;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Some general utility methods used for the usability evaluation stuff
 * 
 * @author Martina Freiberg
 * @date 05.05.2012
 */
public class UEUtils {

    public static String makeUTF8(final String toConvert) {
        try {
            return new String(toConvert.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Rewrites a long value to a more readable "time" representation: long
     * value --> hh:mm:ss
     *
     * @param millis the long value defining the timespan
     * @return the more readable time representation as String
     */
    public static String getHoursMinutesSecondsFromMilliseconds(long millis) {
        String time = "/";
        if (millis != 0) {
            String format = String.format("%%0%dd", 2);
            millis = millis / 1000;
            String seconds = String.format(format, millis % 60);
            String minutes = String.format(format, (millis % 3600) / 60);
            String hours = String.format(format, millis / 3600);
            time = hours + ":" + minutes + ":" + seconds;
        }

        return time;
    }
    
        /**
     * Parses a given String representation of a date into a Date Object.
     * 
     * @param datestring the String representation of the date
     * @param dateformat the desired date format specification
     * @param locale the desired Locale
     * @return the paresed Date Object
     */
    public static Date parseDatestringToDateObject(
            String datestring, String dateformat, Locale locale) {

        String[] dateparts = datestring.split(" ");
        String dateToParse = dateparts[1] + " " + dateparts[2];

        Date date = null;
        DateFormat sdf = new SimpleDateFormat(dateformat, locale);
        try {
            date = sdf.parse(datestring);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return date;
    }

}
