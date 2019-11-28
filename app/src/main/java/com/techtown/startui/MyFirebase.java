package com.techtown.startui;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class FBnode {

    FBnode left, right;
    ClassRoomData classRoomData;
    byte color;

    /* Constructor */
    public FBnode(ClassRoomData classRoomData) { this( classRoomData, null, null ); }

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
    private DatabaseReference mRef;
    private DatabaseReference userRef;
    private DatabaseReference userRsvnRef;
    private DatabaseReference calRef;
    private DatabaseReference rsvnRef;

    static {
        nullNode = new FBnode(new ClassRoomData());
        nullNode.left = nullNode;
        nullNode.right = nullNode;
    }

    public MyFirebase(ClassRoomData classRoomData) {
        this.header = new FBnode(classRoomData);
        this.header.left = nullNode;
        this.header.right = nullNode;
        mRef = FirebaseDatabase.getInstance().getReference();
        calRef = mRef.child("calendar");
        userRef = mRef.child("users").child(String.valueOf(classRoomData.getUserId()));
        userRsvnRef = userRef.child("myResvList");
        rsvnRef = mRef.child("reservation");
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
            this.current = (classRoomData.getStartTime() < current.classRoomData.getStartTime()) ? this.current.left : this.current.right;
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

    //

}
