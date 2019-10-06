package com.techtown.startui;

import java.util.Calendar;

public class MyDateObj {

    private Calendar cal;
    private int year;
    private int month;
    private int firstDayOfWeek;
    private int lastDayOfMonth;

    public MyDateObj() {

        this.cal = Calendar.getInstance();
        setMyDateObj(this.cal.get(Calendar.YEAR), this.cal.get(Calendar.MONTH));

    }

    public void setCalendarDayOfMth(int dayOfMth) { this.cal.set(Calendar.DAY_OF_MONTH, dayOfMth); }

    public Calendar getCalendar() { return this.cal; }

    public int getFirstDayOfWeek() { return this.firstDayOfWeek; }

    public int getLastDayOfMonth() { return this.lastDayOfMonth; }

    public void addMonth(int incr) { if(incr != 0) setMyDateObj(this.year, this.month + incr); }

    private void setMyDateObj(int yr, int mth) {

        this.cal.set(Calendar.YEAR, this.year = yr);
        this.cal.set(Calendar.MONTH, this.month = mth);
        this.cal.set(Calendar.DAY_OF_MONTH, 1);

        this.firstDayOfWeek = this.cal.get(Calendar.DAY_OF_WEEK);
        this.lastDayOfMonth = this.cal.getActualMaximum(Calendar.DAY_OF_MONTH);

    }

}
