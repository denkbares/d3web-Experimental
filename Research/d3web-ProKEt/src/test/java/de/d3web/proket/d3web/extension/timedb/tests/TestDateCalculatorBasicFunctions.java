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
package de.d3web.proket.d3web.extension.timedb.tests;

import de.d3web.proket.d3web.extension.timedb.DateCalculator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test class for checking functionality of DateCalculator's month related
 * functions.
 *
 * @date Aug 2012
 *
 * @author Martina Freiberg
 */
public class TestDateCalculatorBasicFunctions extends TestCase {

    public TestDateCalculatorBasicFunctions() {
    }

    /*
     * TESTS whether the correct number of days in a month are calculated for a
     * given date and its month
     */
    @Test
    public void testSecond() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm.ss");
        Date d1 = null;

        try {
            d1 = dateFormat.parse("01:23:00");
            assertEquals(
                    "exp: 0 != act: " + DateCalculator.second(d1) + " !",
                    0, DateCalculator.second(d1));
            
             d1 = dateFormat.parse("01:23:59");
            assertEquals(
                    "exp: 59 != act: " + DateCalculator.second(d1) + " !",
                    59, DateCalculator.second(d1));
            
             d1 = dateFormat.parse("01:23:25");
            assertEquals(
                    "exp: 25 != act: " + DateCalculator.second(d1) + " !",
                    25, DateCalculator.second(d1));
        } catch (ParseException pe) {
        }
    }

    @Test
    public void testMinute() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm.ss");
        Date d1 = null;

        try {
            d1 = dateFormat.parse("02:00:01");
            assertEquals(
                    "exp: 0 != act: " + DateCalculator.minute(d1) + " !",
                    0, DateCalculator.minute(d1));
            
            d1 = dateFormat.parse("02:18:01");
            assertEquals(
                    "exp: 18 != act: " + DateCalculator.minute(d1) + " !",
                    18, DateCalculator.minute(d1));
            
             d1 = dateFormat.parse("02:59:01");
            assertEquals(
                    "exp: 59 != act: " + DateCalculator.minute(d1) + " !",
                    59, DateCalculator.minute(d1));
        } catch (ParseException pe) {
        }
    }

    @Test
    public void testHour12() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm.ss");
        Date d1 = null;

        try {
            d1 = dateFormat.parse("00:21:42");
            assertEquals(
                    "exp: 0 != act: " + DateCalculator.hour12(d1) + " !",
                    0, DateCalculator.hour12(d1));
            
            d1 = dateFormat.parse("03:21:42");
            assertEquals(
                    "exp: 3 != act: " + DateCalculator.hour12(d1) + " !",
                    3, DateCalculator.hour12(d1));
            
            d1 = dateFormat.parse("11:21:42");
            assertEquals(
                    "exp: 11 != act: " + DateCalculator.hour12(d1) + " !",
                    11, DateCalculator.hour12(d1));
        } catch (ParseException pe) {
        }
    }

    @Test
    public void testHour24() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH.mm.ss");
        Date d1 = null;

        try {
             d1 = dateFormat.parse("00:21:42");
            assertEquals(
                    "exp: 0 != act: " + DateCalculator.hour24(d1) + " !",
                    0, DateCalculator.hour24(d1));
            
            d1 = dateFormat.parse("08:21:42");
            assertEquals(
                    "exp: 8 != act: " + DateCalculator.hour24(d1) + " !",
                    8, DateCalculator.hour24(d1));
            
             d1 = dateFormat.parse("12:21:42");
            assertEquals(
                    "exp: 12 != act: " + DateCalculator.hour24(d1) + " !",
                    12, DateCalculator.hour24(d1));
            
             d1 = dateFormat.parse("23:21:42");
            assertEquals(
                    "exp: 23 != act: " + DateCalculator.hour24(d1) + " !",
                    23, DateCalculator.hour24(d1));
        } catch (ParseException pe) {
        }
    }

    @Test
    public void testDayOfMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date d1 = null;

        try {
            d1 = dateFormat.parse("01.01.2012");
            assertEquals(
                    "exp: 1 > act: " + DateCalculator.dayOfMonth(d1) + " !",
                    1, DateCalculator.dayOfMonth(d1));
            // february normal years
            d1 = dateFormat.parse("02.02.2011");
            assertEquals(
                    "exp: 2 > act: " + DateCalculator.dayOfMonth(d1) + " !",
                    2, DateCalculator.dayOfMonth(d1));
            d1 = dateFormat.parse("28.02.2010");
            assertEquals(
                    "exp: 28 > act: " + DateCalculator.dayOfMonth(d1) + " !",
                    28, DateCalculator.dayOfMonth(d1));
           // february in leap year -> 29 exists
            d1 = dateFormat.parse("29.02.2012");
            assertEquals(
                    "exp: 29 > act: " + DateCalculator.dayOfMonth(d1) + " !",
                    29, DateCalculator.dayOfMonth(d1));
            // february in normal year -> 29.2. equals 1.3.
            d1 = dateFormat.parse("29.02.2011");
            assertEquals(
                    "exp: 1 > act: " + DateCalculator.dayOfMonth(d1) + " !",
                    1, DateCalculator.dayOfMonth(d1));
            d1 = dateFormat.parse("31.03.2007");
            assertEquals(
                    "exp: 31 > act: " + DateCalculator.dayOfMonth(d1) + " !",
                    31, DateCalculator.dayOfMonth(d1));
            

        } catch (ParseException pe) {
        }
    }

    @Test
    public void testDayOfWeek() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date d1 = null;

        try {
            // sunday
            d1 = dateFormat.parse("01.01.2012");
            assertEquals(
                    "exp: 1 != act: " + DateCalculator.dayOfWeek(d1) + " !",
                    1, DateCalculator.dayOfWeek(d1));

            // monday
            d1 = dateFormat.parse("02.01.2012");
            assertEquals(
                    "exp: 2 != act: " + DateCalculator.dayOfWeek(d1) + " !",
                    2, DateCalculator.dayOfWeek(d1));

            // tuesday
            d1 = dateFormat.parse("03.01.2012");
            assertEquals(
                    "exp: 3 != act: " + DateCalculator.dayOfWeek(d1) + " !",
                    3, DateCalculator.dayOfWeek(d1));

            // wednesday
            d1 = dateFormat.parse("04.01.2012");
            assertEquals(
                    "exp: 4 != act: " + DateCalculator.dayOfWeek(d1) + " !",
                    4, DateCalculator.dayOfWeek(d1));

            // thursday
            d1 = dateFormat.parse("05.01.2012");
            assertEquals(
                    "exp: 5 != act: " + DateCalculator.dayOfWeek(d1) + " !",
                    5, DateCalculator.dayOfWeek(d1));

            // friday
            d1 = dateFormat.parse("06.01.2012");
            assertEquals(
                    "exp: 6 != act: " + DateCalculator.dayOfWeek(d1) + " !",
                    6, DateCalculator.dayOfWeek(d1));

            // saturday
            d1 = dateFormat.parse("07.01.2012");
            assertEquals(
                    "exp: 7 != act: " + DateCalculator.dayOfWeek(d1) + " !",
                    7, DateCalculator.dayOfWeek(d1));
        } catch (ParseException pe) {
        }
    }

    @Test
    public void testMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date d1 = null;

        try {
            d1 = dateFormat.parse("01.01.2012");
            assertEquals(
                    "exp: 1 != act: " + DateCalculator.month(d1) + " !",
                    1, DateCalculator.month(d1));

            d1 = dateFormat.parse("01.02.2012");
            assertEquals(
                    "exp: 2 != act: " + DateCalculator.month(d1) + " !",
                    2, DateCalculator.month(d1));

            d1 = dateFormat.parse("01.03.2012");
            assertEquals(
                    "exp: 3 != act: " + DateCalculator.month(d1) + " !",
                    3, DateCalculator.month(d1));

            d1 = dateFormat.parse("01.04.2012");
            assertEquals(
                    "exp: 4 != act: " + DateCalculator.month(d1) + " !",
                    4, DateCalculator.month(d1));

            d1 = dateFormat.parse("01.05.2012");
            assertEquals(
                    "exp: 5 != act: " + DateCalculator.month(d1) + " !",
                    5, DateCalculator.month(d1));

            d1 = dateFormat.parse("01.06.2012");
            assertEquals(
                    "exp: 6 != act: " + DateCalculator.month(d1) + " !",
                    6, DateCalculator.month(d1));

            d1 = dateFormat.parse("01.07.2012");
            assertEquals(
                    "exp: 7 != act: " + DateCalculator.month(d1) + " !",
                    7, DateCalculator.month(d1));

            d1 = dateFormat.parse("01.08.2012");
            assertEquals(
                    "exp: 8 != act: " + DateCalculator.month(d1) + " !",
                    8, DateCalculator.month(d1));

            d1 = dateFormat.parse("01.09.2012");
            assertEquals(
                    "exp: 9 != act: " + DateCalculator.month(d1) + " !",
                    9, DateCalculator.month(d1));

            d1 = dateFormat.parse("01.10.2012");
            assertEquals(
                    "exp: 10 != act: " + DateCalculator.month(d1) + " !",
                    10, DateCalculator.month(d1));

            d1 = dateFormat.parse("01.11.2012");
            assertEquals(
                    "exp: 11 != act: " + DateCalculator.month(d1) + " !",
                    11, DateCalculator.month(d1));

            d1 = dateFormat.parse("01.12.2012");
            assertEquals(
                    "exp: 12 != act: " + DateCalculator.month(d1) + " !",
                    12, DateCalculator.month(d1));


        } catch (ParseException pe) {
        }
    }

    @Test
    public void testYear() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date d1 = null;

        try {
            d1 = dateFormat.parse("01.01.2012");
            assertEquals(
                    "exp: 2012 != act: " + DateCalculator.year(d1) + " !",
                    2012, DateCalculator.year(d1));

            d1 = dateFormat.parse("01.01.2000");
            assertEquals(
                    "exp: 2000 != act: " + DateCalculator.year(d1) + " !",
                    2000, DateCalculator.year(d1));

            d1 = dateFormat.parse("01.01.1999");
            assertEquals(
                    "exp: 1999 != act: " + DateCalculator.year(d1) + " !",
                    1999, DateCalculator.year(d1));

            d1 = dateFormat.parse("01.01.1970");
            assertEquals(
                    "exp: 1970 != act: " + DateCalculator.year(d1) + " !",
                    1970, DateCalculator.year(d1));

            d1 = dateFormat.parse("01.01.1900");
            assertEquals(
                    "exp: 1900 != act: " + DateCalculator.year(d1) + " !",
                    1900, DateCalculator.year(d1));

            d1 = dateFormat.parse("01.01.999");
            assertEquals(
                    "exp: 999 != act: " + DateCalculator.year(d1) + " !",
                    999, DateCalculator.year(d1));
        } catch (ParseException pe) {
        }
    }
}
