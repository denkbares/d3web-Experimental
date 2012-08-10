package de.d3web.proket.d3web.extension.timedb.tests;
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
import de.d3web.proket.d3web.extension.timedb.DateCalculator;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.*;
import java.text.ParseException;
import junit.framework.TestCase;
import static org.junit.Assert.*;

/**
 * Tests the week functions of the DateCalculator.
 * 
 * @author Martina Freiberg
 * @date Aug 2012 
 */
public class TestDateCalculatorWeekFunctions extends TestCase{

    public TestDateCalculatorWeekFunctions() {
    }


    /**
     * Test the calculation of the full weeks difference of two Dates.
     */
    @Test
    public void testWeekDeltaWithinYear() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        Date start = null;
        Date end = null;

        try {
            start = dateFormat.parse("01.04.2012");
            end = dateFormat.parse("06.04.2012");
            assertTrue(
                    "exp: 0 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    0 == DateCalculator.getWeekDelta(start, end));
            
            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("08.01.2012");
            assertTrue(
                    "exp: 1 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    1 == DateCalculator.getWeekDelta(start, end));

            start = dateFormat.parse("01.02.2012");
            end = dateFormat.parse("28.02.2012");
            assertTrue(
                    "exp: 3 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    3 == DateCalculator.getWeekDelta(start, end));
            
            start = dateFormat.parse("01.02.2012");
            end = dateFormat.parse("29.02.2012");
            assertTrue(
                    "exp: 4 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    4 == DateCalculator.getWeekDelta(start, end));

            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("01.02.2012");
            assertTrue(
                    "exp: 4 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    4 == DateCalculator.getWeekDelta(start, end));
            
            start = dateFormat.parse("01.01.2011");
            end = dateFormat.parse("30.12.2011");
            assertTrue(
                    "exp: 51 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    51 == DateCalculator.getWeekDelta(start, end));
            
            start = dateFormat.parse("01.01.2011");
            end = dateFormat.parse("01.01.2012");
            assertTrue(
                    "exp: 52 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    52 == DateCalculator.getWeekDelta(start, end));
            
            start = dateFormat.parse("01.01.2011");
            end = dateFormat.parse("06.01.2012");
            assertTrue(
                    "exp: 52 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    52 == DateCalculator.getWeekDelta(start, end));

        } catch (ParseException pe) {
        }
    }
    
    /**
     * TESTS whether calculation of full weeks difference of two dates works 
     * correctly.
     */
    @Test
    public void testWeekDeltaOutsideYear() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        Date start = null;
        Date end = null;

        try {
            
            start = dateFormat.parse("01.01.2011");
            end = dateFormat.parse("30.12.2011");
            assertTrue(
                    "exp: 51 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    51 == DateCalculator.getWeekDelta(start, end));
            
            start = dateFormat.parse("01.01.2011");
            end = dateFormat.parse("01.01.2012");
            assertTrue(
                    "exp: 52 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    52 == DateCalculator.getWeekDelta(start, end));
            
            start = dateFormat.parse("01.01.2011");
            end = dateFormat.parse("06.01.2012");
            assertTrue(
                    "exp: 52 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    52 == DateCalculator.getWeekDelta(start, end));

            start = dateFormat.parse("01.01.2011");
            end = dateFormat.parse("07.01.2012");
            assertTrue(
                    "exp: 53 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    53 == DateCalculator.getWeekDelta(start, end));
            
            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("29.12.2012");
            assertTrue(
                    "exp: 51 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    51 == DateCalculator.getWeekDelta(start, end));
            
            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("30.12.2012");
            assertTrue(
                    "exp: 52 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    52 == DateCalculator.getWeekDelta(start, end));
            
            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("05.01.2013");
            assertTrue(
                    "exp: 52 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    52 == DateCalculator.getWeekDelta(start, end));

            start = dateFormat.parse("01.01.2012");
            end = dateFormat.parse("06.01.2013");
            assertTrue(
                    "exp: 53 > act: "+ DateCalculator.getWeekDelta(start, end) +" !",
                    53 == DateCalculator.getWeekDelta(start, end));
            
        } catch (ParseException pe) {
        }
    }
    
   
}
