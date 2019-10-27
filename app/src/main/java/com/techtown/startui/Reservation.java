package com.techtown.startui;


import android.widget.TableLayout;

public class Reservation implements Comparable<Reservation> {

    private ClassRoomData classRoomData;

    public Reservation() {
        // Basic Constructor
    }
    public Reservation(ClassRoomData classRoomData) {
        this.classRoomData = classRoomData;
    }

    public int getUserId() { return this.classRoomData.getUserId(); }
    public String getUserName() { return this.classRoomData.getUserName(); }
    public String getClassRoom() { return this.classRoomData.getClassRoom(); }
    public int getStartHour() { return this.classRoomData.getStartTimeInHour(); }
    public int getEndHour() { return this.classRoomData.getEndTimeInHour(); }
    public int getNumUsers() { return this.classRoomData.getNumUsers(); }
    public String getUsage() { return this.classRoomData.getUsage(); }

    @Override
    public int compareTo(Reservation target) {
        if(this.getStartHour() > target.getStartHour()) return 1;
        else if(this.getStartHour() < target.getStartHour()) return -1;
        return 0;
    }
}
