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
public class TestDateCalculatorMonthFunctions extends TestCase {

    public TestDateCalculatorMonthFunctions() {
    }

    /*
     * TESTS whether the correct number of days in a month are calculated for a
     * given date and its month
     */
    @Test
    public void testGetNumberOfMonthDays() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date d1 = null;

        try {
            d1 = dateFormat.parse("01.01.2012");
            assertEquals(
                    "exp: 31 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    31, DateCalculator.getNumberOfMonthDays(d1));
            // february normal years
            d1 = dateFormat.parse("01.02.2011");
            assertEquals(
                    "exp: 28 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    28, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.02.2010");
            assertEquals(
                    "exp: 28 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    28, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.02.2009");
            assertEquals(
                    "exp: 28 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    28, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.02.2007");
            assertEquals(
                    "exp: 28 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    28, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.02.1987");
            assertEquals(
                    "exp: 28 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    28, DateCalculator.getNumberOfMonthDays(d1));
            // leap years
            d1 = dateFormat.parse("01.02.2012");
            assertEquals(
                    "exp: 29 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    29, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.02.2000");
            assertEquals(
                    "exp: 29 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    29, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.02.1988");
            assertEquals(
                    "exp: 29 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    29, DateCalculator.getNumberOfMonthDays(d1));
            // remaining normal months
            d1 = dateFormat.parse("01.03.2012");
            assertEquals(
                    "exp: 31 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    31, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.04.2012");
            assertEquals(
                    "exp: 30 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    30, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.05.2012");
            assertEquals(
                    "exp: 31 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    31, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.06.2012");
            assertEquals(
                    "exp: 30 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    30, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.07.2012");
            assertEquals(
                    "exp: 31 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    31, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.08.2012");
            assertEquals(
                    "exp: 31 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    31, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.09.2012");
            assertEquals(
                    "exp: 301 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    30, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.10.2012");
            assertEquals(
                    "exp: 31 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    31, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.11.2012");
            assertEquals(
                    "exp: 30 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    30, DateCalculator.getNumberOfMonthDays(d1));
            d1 = dateFormat.parse("01.12.2012");
            assertEquals(
                    "exp: 31 > act: " + DateCalculator.getNumberOfMonthDays(d1) + " !",
                    31, DateCalculator.getNumberOfMonthDays(d1));

        } catch (ParseException pe) {
        }
    }

    /*
     * TESTS if it is correctly calculated for each month and potential day
     * variation if the given date is the end of the given month or not.
     */
    @Test
    public void testIsEndOfMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        Date date = null;

        try {
            // JANUARY
            date = dateFormat.parse("01.01.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("30.01.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("31.01.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));

            // FEBRUARY
            // Feb: Leap Year Case - 2012 was leap year so 29th is end of month
            date = dateFormat.parse("01.02.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("27.02.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("28.02.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("29.02.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));

            // February normal: 2011 was no leap year so 28th is end of month
            date = dateFormat.parse("27.02.2011");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("28.02.2011");
            assertTrue(
                    "exp: true > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("29.02.2011");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));

            // MARCH
            date = dateFormat.parse("01.03.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("30.03.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("31.01.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));

            // APRIL
            date = dateFormat.parse("01.04.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("29.04.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("30.04.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));

            // MAI
            date = dateFormat.parse("01.05.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("30.05.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("31.05.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));

            // JUNE
            date = dateFormat.parse("01.06.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("29.06.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("30.06.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));


            // JULY
            date = dateFormat.parse("01.07.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("30.07.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("31.07.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));


            // AUGUST
            date = dateFormat.parse("01.08.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("30.08.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("31.08.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));

            // SEPTEMBER
            date = dateFormat.parse("01.09.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("29.09.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("30.09.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));

            // OCTOBER
            date = dateFormat.parse("01.10.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("30.10.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("31.10.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));

            // NOVEMBER
            date = dateFormat.parse("01.1.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("29.11.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("30.11.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));


            // DECEMBER
            date = dateFormat.parse("01.12.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("30.12.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));
            date = dateFormat.parse("31.12.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.isEndOfMonth(date) + " !",
                    DateCalculator.isEndOfMonth(date));

        } catch (ParseException pe) {
        }
    }

    /*
     * Tests of for each month, the middle day (always 15) is returned
     * correctly.
     */
    @Test
    public void testGetMiddleOfMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date d1 = null;

        try {
            d1 = dateFormat.parse("01.01.2012");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            // february normal years
            d1 = dateFormat.parse("01.02.2011");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.02.2010");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.02.2009");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.02.2007");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.02.1987");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            // leap years
            d1 = dateFormat.parse("01.02.2012");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.02.2000");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.02.1988");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            // remaining normal months
            d1 = dateFormat.parse("01.03.2012");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.04.2012");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.05.2012");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.06.2012");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.07.2012");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.08.2012");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.09.2012");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.10.2012");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.11.2012");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.12.2012");
            assertEquals(
                    "exp: 15 > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    15, DateCalculator.getMiddleOfMonth(d1));

        } catch (ParseException pe) {
        }
    }
    
    /**
     * Tests correct functionality of testIsMiddleOfMonth function, i.e. whether it
     * is correctly returned whether a given date is the middle of the month
     * or not
     */
    @Test
    public void testIsMiddleOfMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date d1 = null;

        try {
            d1 = dateFormat.parse("01.01.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("14.01.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("16.01.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("30.01.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("31.01.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("15.01.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("01.02.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("14.02.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("16.02.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("28.02.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("29.02.2012");
            assertFalse(
                    "exp: false > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("15.02.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("15.03.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("15.04.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("15.05.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("15.06.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("15.07.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("15.08.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("15.09.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("15.10.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("15.11.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            d1 = dateFormat.parse("15.12.2012");
            assertTrue(
                    "exp: true > act: " + DateCalculator.getMiddleOfMonth(d1) + " !",
                    DateCalculator.isMiddleOfMonth(d1));
            

        } catch (ParseException pe) {
        }
    }

    /*
     * TESTS whether the full months difference between two dates is calculated
     * correctly WITHIN one year.
     */
    @Test
    public void testMonthDeltaWithinYear() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        Date start = null;
        Date end = null;

        try {
            start = dateFormat.parse("30.01.2012");
            end = dateFormat.parse("03.02.2012");
            assertTrue(
                    "exp: 0 > act: " + DateCalculator.getMonthDelta(start, end) + " !",
                    0 == DateCalculator.getMonthDelta(start, end));

            
            start = dateFormat.parse("01.04.2012");
            end = dateFormat.parse("06.04.2012");
            assertTrue(
                    "exp: 0 > act: " + DateCalculator.getMonthDelta(start, end) + " !",
                    0 == DateCalculator.getMonthDelta(start, end));

            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("01.04.2012");
            assertTrue(
                    "exp: 3 > act: " + DateCalculator.getMonthDelta(start, end) + " !",
                    3 == DateCalculator.getMonthDelta(start, end));

            start = dateFormat.parse("15.02.2012");
            end = dateFormat.parse("15.03.2012");
            assertTrue(
                    "exp: 1 > act: " + DateCalculator.getMonthDelta(start, end) + " !",
                    1 == DateCalculator.getMonthDelta(start, end));

            // 48 weeks over here
            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("02.12.2012");
            assertTrue(
                    "exp: 12 > act: " + DateCalculator.getMonthDelta(start, end) + " !",
                    12 == DateCalculator.getMonthDelta(start, end));

            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("15.12.2012");
            assertTrue(
                    "exp: 12 > act: " + DateCalculator.getMonthDelta(start, end) + " !",
                    12 == DateCalculator.getMonthDelta(start, end));

            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("30.12.2012");
            assertTrue(
                    "exp: 13 > act: " + DateCalculator.getMonthDelta(start, end) + " !",
                    13 == DateCalculator.getMonthDelta(start, end));

        } catch (ParseException pe) {
        }
    }

    /*
     * TESTS whether the full months difference between two dates is calculated
     * correctly OVER the bounds of a year.
     */
    @Test
    public void testMonthDeltaOverYear() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        Date start = null;
        Date end = null;

        try {
            // 48 weeks over here
            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("02.12.2012");
            assertTrue(
                    "exp: 12 > act: " + DateCalculator.getMonthDelta(start, end) + " !",
                    12 == DateCalculator.getMonthDelta(start, end));

            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("15.12.2012");
            assertTrue(
                    "exp: 12 > act: " + DateCalculator.getMonthDelta(start, end) + " !",
                    12 == DateCalculator.getMonthDelta(start, end));

            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("30.12.2012");
            assertTrue(
                    "exp: 13 > act: " + DateCalculator.getMonthDelta(start, end) + " !",
                    13 == DateCalculator.getMonthDelta(start, end));

            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("26.01.2013");
            assertTrue(
                    "exp: 14 > act: " + DateCalculator.getMonthDelta(start, end) + " !",
                    13 == DateCalculator.getMonthDelta(start, end));

            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("27.01.2013");
            assertTrue(
                    "exp: 14 > act: " + DateCalculator.getMonthDelta(start, end) + " !",
                    14 == DateCalculator.getMonthDelta(start, end));

        } catch (ParseException pe) {
        }
    }
}
