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
package de.d3web.proket.d3web.extension.timedb;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Utility class for returning some legal/juristic specific date calculations
 * such as:<br/> - WeekDelta: difference of 2 dates in weeks<br/> - MonthDelta:
 * difference of 2 dates in months<br/> - EndOfMonth: 31 for Jan, 28/29 for Feb,
 * 31 for March...<br/> - MiddleOfMonth: always the 15th
 *
 * @author Martina Freiberg @date Aug 2012
 */
public class DateCalculator {

    /**
     * Returns the full number of weeks between two points in time. Thereby,
     * only full weeks are calculated, i.e. 5 days count as "0 weeks" and 4
     * weeks and 3 days count as "4 weeks".
     *
     * @param start Start point in time
     * @param end End point in time
     * @return the number of full weeks inbetween start and end
     */
    public static int getWeekDelta(Date start, Date end) {

        Calendar startC = Calendar.getInstance();
        startC.setTime(start);
        Calendar endC = Calendar.getInstance();
        endC.setTime(end);

        long diffMillis = endC.getTimeInMillis() - startC.getTimeInMillis();
        double diffWeek = diffMillis / 1000D / 60D / 60D / 24D / 7;

        return (int) diffWeek;
    }

    /**
     * Returns the number of months between two points in time. Thereby, months
     * are counted as "4 weeks" or "28 days" respectively. Only full months are
     * counted. I.e., 27 days are "0 months", 6 weeks and 3 days are "1
     * months"...
     *
     * @param start Start point in time
     * @param end End point in time
     * @return the number of full months inbetween start and end
     */
    public static int getMonthDelta(Date start, Date end) {

        int weekDelta = getWeekDelta(start, end);
        int diffMonths = weekDelta / 4;

        return diffMonths;
    }

    /**
     * Returns the number correspondation of the middle of the month of a given
     * date. In juristic/legal cases per default always the 15th.
     *
     * @param date The date, for the month of which to return the middle.
     * @return the middle of month number i.e. the 15th
     */
    public static int getMiddleOfMonth(Date date) {
        return 15;
    }

    /**
     * Checks whether the given date denots the middle of a month. Thereby, all
     * months equally have the 15th as their middle, thus no matter which month
     * is contained in the given date, it is always only the middle of the
     * month, if it is the 15th.
     *
     * @param date the Date to check
     * @return true if the given date denotes the middle of the respective
     * month, i.e. = the 15th.
     */
    public static boolean isMiddleOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return ((Integer) cal.get(Calendar.DAY_OF_MONTH)).equals(((Integer) getMiddleOfMonth(date)));
    }

    /**
     * Checks whether the given date denotes the end of month. Therefore, it is
     * checked, whether the given date equals the number of days in the
     * respective month.
     *
     * @param date The date to be checked.
     * @return true if the given date is the end of a month.
     */
    public static boolean isEndOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        switch (cal.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                return 31 == dayOfMonth(date);
            case Calendar.FEBRUARY:
                if (((GregorianCalendar)cal).isLeapYear(year(date))){
                    return 29 == dayOfMonth(date);
                } else {
                    return 28 == dayOfMonth(date);
                }
            case Calendar.MARCH:
                return 31 == dayOfMonth(date);
            case Calendar.APRIL:
                return 30 == dayOfMonth(date);
            case Calendar.MAY:
                return 31 == dayOfMonth(date);
            case Calendar.JUNE:
                return 30 == dayOfMonth(date);
            case Calendar.JULY:
                return 31 == dayOfMonth(date);
            case Calendar.AUGUST:
                return 31 == dayOfMonth(date);
            case Calendar.SEPTEMBER:
                return 30 == dayOfMonth(date);
            case Calendar.OCTOBER:
                return 31 == dayOfMonth(date);
            case Calendar.NOVEMBER:
                return 30 == dayOfMonth(date);
            case Calendar.DECEMBER:
                return 31 == dayOfMonth(date);
        }
        return false;
    }

    /**
     * Returns the number of days in the month of the given date. That is, the
     * last day (which corresponds also to the number of days per month) of a
     * month is returned, e.g., 31 for Jan, 30 for Apr, 31 for Dec.
     *
     * @param date The date to analyze
     * @return The number of days in the month of the given date.
     */
    public static int dayOfMonth(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Returns the day of week as integer representation. That is, Sunday = 1,
     * Monday = 2... Saturday = 7
     *
     * @param date The date to analyze
     * @return The day of week as Integer
     */
    public static int dayOfWeek(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Returns the month as a number for a given date, i.e. 1 for January, 2 for
     * February...
     *
     * @param date The date to analyze
     * @return The month number
     */
    public static int month(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * Returns the year of a given date
     *
     * @param date The date to analyze
     * @return The year
     */
    public static int year(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.YEAR);
    }

    /**
     * Returns the hour of a given date. Here, the 12-hour clock is assumed,
     * i.e. midnight and noon return 0, 10 am and 10pm both return 10 and so on.
     *
     * @param date The date to analyze
     * @return The hour of the date in 12h clock representation
     */
    public static int hour12(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.HOUR);
    }

    /**
     * Returns the hour of a given date. Here, the 24-hour clock is assumed,
     * i.e. midnight returns0, noon returns 12, 10 am returns 10 and 10pm
     * returns 22 and so on.
     *
     * @param date The date to analyze
     * @return The hour of the date in 24h clock representation
     */
    public static int hour24(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Returns the minute of a given date.
     *
     * @param date The date to analyze
     * @return The minute of the date
     */
    public static int minute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.MINUTE);
    }

    /**
     * Returns the second of a given date.
     *
     * @param date The date to analyze
     * @return The minute of the date
     */
    public static int second(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.SECOND);
    }
}
