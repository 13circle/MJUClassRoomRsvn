package com.techtown.startui;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

class FBnode {

    FBnode left, right;
    ClassRoomData classRoomData;
    byte color;

    /* Constructor */
    public FBnode(ClassRoomData classRoomData) { this( null, null, null ); }

    /* Constructor */
    public FBnode(ClassRoomData classRoomData, FBnode left, FBnode right) {
        this.left = left;
        this.right = right;
        this.classRoomData = classRoomData;
        color = 1;
    }

}

public class MyFirebase {

    private FBnode current;
    private FBnode parent;
    private FBnode grand;
    private FBnode great;
    private FBnode header;
    private static FBnode nullNode;
    private static final byte BLACK = 1;
    private static final byte RED   = 0;
    private ClassRoomData classRoomData;
    private DatabaseReference mRef;
    private DatabaseReference userRef;
    private DatabaseReference userRsvnRef;
    private DatabaseReference calRef;
    private DatabaseReference rsvnRef;
    private DataSnapshot calDS;
    private DataSnapshot rsvnDS;
    private ArrayList<ArrayList<ClassRoomData>> cdList;
    private HashMap<String, Integer> crMap;

    static {
        nullNode = new FBnode(null);
        nullNode.left = nullNode;
        nullNode.right = nullNode;
    }

    public MyFirebase(ClassRoomData classRoomData) {
        this.header = new FBnode(classRoomData);
        this.header.left = nullNode;
        this.header.right = nullNode;
        this.classRoomData = classRoomData;
        mRef = FirebaseDatabase.getInstance().getReference();
        calRef = mRef.child("calendar");
        userRef = mRef.child("users").child(String.valueOf(classRoomData.getUserId()));
        userRsvnRef = userRef.child("myResvList");
        rsvnRef = mRef.child("reservations");
    }

    public boolean isEmpty() { return this.header.right == nullNode; }

    public void makeEmpty() { this.header.right = nullNode; }

    public void insert(ClassRoomData classRoomData) {
        this.current = this.parent = this.grand = this.header;
        nullNode.classRoomData = classRoomData;
        while(!current.classRoomData.getClassRoom().equals(classRoomData.getClassRoom())) {
            this.great = this.grand;
            this.grand = this.parent;
            this.parent = this.current;
            this.current = (classRoomData.getStartTime() < this.current.classRoomData.getStartTime()) ? this.current.left : this.current.right;
            if(this.current.left.color == RED && this.current.right.color == RED)
                handleReorient(classRoomData.getStartTime());
        }
        if(this.current != nullNode) return;
        this.current = new FBnode(classRoomData, nullNode, nullNode);
        if(classRoomData.getStartTime() < this.parent.classRoomData.getStartTime())
            this.parent.left = this.current;
        else
            this.parent.right = this.current;
        handleReorient(classRoomData.getStartTime());
    }

    private void handleReorient(long startTime) {
        this.current.color = RED;
        this.current.left.color = this.current.right.color = BLACK;
        if(this.parent.color == RED) {
            this.grand.color = RED;
            if(startTime < this.grand.classRoomData.getStartTime() != startTime < this.parent.classRoomData.getStartTime())
                this.parent = rotate(startTime, this.grand);
            this.current = rotate(startTime, this.great);
            this.current.color = BLACK;
        }
        this.header.right.color = BLACK;
    }

    private FBnode rotate(long startTime, FBnode parent) {
        if(startTime < parent.classRoomData.getStartTime())
            return parent.left = startTime < parent.left.classRoomData.getStartTime() ? rotateWithLeftChild(parent.left) : rotateWithRightChild(parent.left);
        else
            return parent.right = startTime < parent.right.classRoomData.getStartTime() ? rotateWithLeftChild(parent.right) : rotateWithRightChild(parent.right);
    }

    private FBnode rotateWithLeftChild(FBnode k2) {
        FBnode k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        return k1;
    }

    private FBnode rotateWithRightChild(FBnode k1) {
        FBnode k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        return k2;
    }

    public int countNodes() { return countNodes(this.header.right); }

    private int countNodes(FBnode r) {
        if(r == nullNode) return 0;
        int l = 1;
        l += countNodes(r.left);
        l += countNodes(r.right);
        return l;
    }

    public boolean search(long startTime) { return search(header.right, startTime); }

    private boolean search(FBnode r, long startTime) {
        boolean found = false;
        while ((r != nullNode) && !found) {
            long rStartTime = r.classRoomData.getStartTime();
            if (startTime < rStartTime) r = r.left;
            else if (startTime > rStartTime) r = r.right;
            else { found = true; break; }
            found = search(r, startTime);
        }
        return found;
    }

    private DatabaseReference getDateRef(FBnode r) {
        return calRef.child(r.classRoomData.getYear() + "_"
                + (r.classRoomData.getMonth() + 1)).child(String.valueOf(r.classRoomData.getDate()));
    }
    private DataSnapshot getDateDS(FBnode r, DataSnapshot dsRoot) {
        setCalDS(dsRoot);
        return calDS.child(r.classRoomData.getYear() + "_"
                + (r.classRoomData.getMonth() + 1)).child(String.valueOf(r.classRoomData.getDate()));
    }
    // TODO: Must be replaced to FBnode version later
    private DatabaseReference getDateRef(ClassRoomData classRoomData) {
        return calRef.child(classRoomData.getYear() + "_"
                + (classRoomData.getMonth() + 1)).child(String.valueOf(classRoomData.getDate()));
    }
    private DataSnapshot getDateDS(ClassRoomData classRoomData, DataSnapshot dsRoot) {
        setCalDS(dsRoot);
        return calDS.child(classRoomData.getYear() + "_"
                + (classRoomData.getMonth() + 1)).child(String.valueOf(classRoomData.getDate()));
    }
    private DataSnapshot getDateDS(int yr, int mth, int date, DataSnapshot dsRoot) {
        setCalDS(dsRoot);
        return calDS.child(yr + "_" + mth).child(String.valueOf(date));
    }

    private DataSnapshot getUserSnapshot(DataSnapshot dsRoot, ClassRoomData classRoomData) {
        return dsRoot.child("users").child(String.valueOf(classRoomData.getUserId()));
    }
    private DataSnapshot getUserRsvnSnapshot(DataSnapshot dsRoot, ClassRoomData classRoomData) {
        return getUserSnapshot(dsRoot, classRoomData).child("myResvList");
    }
    private void setRsvnDS(DataSnapshot dsRoot) { rsvnDS = dsRoot.child("reservations"); }
    private void setCalDS(DataSnapshot dsRoot) { calDS = dsRoot.child("calendar"); }

    private void triggerRead() { mRef.child("trigger").setValue(true); }

    // TODO: Read/Write functions version control [v1:Basic write to FB (has ClassRoomData as a param) / v2:Implementing RBT in FB (No param - search in FB)]
    public void writeReservation(ClassRoomData classRoomData) {
        String key = userRsvnRef.push().getKey();
        userRsvnRef.child(key).setValue(true); getDateRef(classRoomData).child(key).setValue(true);
        rsvnRef.child(key).child("userId").setValue(classRoomData.getUserId());
        rsvnRef.child(key).child("userName").setValue(classRoomData.getUserName());
        rsvnRef.child(key).child("phoneNumber").setValue(classRoomData.getPhoneNumber());
        rsvnRef.child(key).child("classRoom").setValue(classRoomData.getClassRoom());
        rsvnRef.child(key).child("startTime").setValue(classRoomData.getStartTime());
        rsvnRef.child(key).child("endTime").setValue(classRoomData.getEndTime());
        rsvnRef.child(key).child("numUsers").setValue(classRoomData.getNumUsers());
        rsvnRef.child(key).child("usage").setValue(classRoomData.getUsage());
    }
    public long countReservationForCalendar(MyDateObj mdo, int date, DataSnapshot dsRoot) {
        int yr = mdo.getCalendar().get(Calendar.YEAR);
        int mth = mdo.getCalendar().get(Calendar.MONTH) + 1;
        return getDateDS(yr, mth, date, dsRoot).getChildrenCount();
    }
    public void readReservationForTable(DataSnapshot dataSnapshot, ArrayList<ArrayList<ClassRoomData>> cdListParam, HashMap<String, Integer> crMapParam) {
        triggerRead(); this.cdList = cdListParam; this.crMap = crMapParam;
        setCalDS(dataSnapshot); setRsvnDS(dataSnapshot);
        DataSnapshot dateRef = getDateDS(classRoomData, dataSnapshot);
        for(DataSnapshot rsvnSnapshot : dateRef.getChildren()) {
            DataSnapshot tmpRef = rsvnDS.child(rsvnSnapshot.getKey());
            ClassRoomData crData = new ClassRoomData((Calendar) classRoomData.getCalendar().clone());
            Log.e("Reserved ID", String.valueOf(tmpRef.child("userId").getValue(Integer.class)));
            crData.setUserId(tmpRef.child("userId").getValue(Integer.class));
            crData.setUserName(tmpRef.child("userName").getValue(String.class));
            crData.setClassRoom(tmpRef.child("classRoom").getValue(String.class));
            crData.setStartTime(tmpRef.child("startTime").getValue(Long.class));
            crData.setEndTime(tmpRef.child("endTime").getValue(Long.class));
            crData.setNumUsers(tmpRef.child("numUsers").getValue(Integer.class));
            crData.setUsage(tmpRef.child("usage").getValue(String.class));
            cdList.get(crMap.get(crData.getClassRoom())).add(crData);
        }
    }
    public void readReservationForUser(ClassRoomData classRoomData) {
        //
    }

}
