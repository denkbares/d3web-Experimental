
package de.d3web.proket.d3web.extension.timedb;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DateCalculator {

/*  
    public static int getWeekDelta(Date start, Date end) {

        Calendar startC = Calendar.getInstance();
        startC.setTime(start);
        Calendar endC = Calendar.getInstance();
        endC.setTime(end);

        long diffMillis = endC.getTimeInMillis() - startC.getTimeInMillis();
        double diffWeek = diffMillis / 1000D / 60D / 60D / 24D / 7;

        return (int) diffWeek;
    }

    /
    public static int getMonthDelta(Date start, Date end) {

        int weekDelta = getWeekDelta(start, end);
        int diffMonths = weekDelta / 4;

        return diffMonths;
    }

    public static int getMiddleOfMonth(Date date) {
        return 15;
    }


    public static boolean isMiddleOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return ((Integer) cal.get(Calendar.DAY_OF_MONTH)).equals(((Integer) getMiddleOfMonth(date)));
    }

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

     public static int dayOfMonth(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.DAY_OF_MONTH);
    }
   public static int dayOfWeek(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.DAY_OF_WEEK);
    }

  public static int month(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.MONTH) + 1;
    }

      public static int year(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.YEAR);
    }

    public static int hour12(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.HOUR);
    }

       public static int hour24(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static int minute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.MINUTE);
    }

 public static int second(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.SECOND);
    }*/
}
