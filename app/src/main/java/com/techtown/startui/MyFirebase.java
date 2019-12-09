package com.techtown.startui;

import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

class AscendingStartTime implements Comparator<ArrayList<String>> {
    private static int DATE_i = 1;
    private static int TIME_i = 2;
    @Override
    public int compare(ArrayList<String> a, ArrayList<String> b) {
        int date_cmp = a.get(DATE_i).compareTo(b.get(DATE_i));
        return (date_cmp != 0) ? date_cmp : a.get(TIME_i).compareTo(b.get(TIME_i));
    }
}

public class MyFirebase {

    private static final int BLACK = 1;
    private static final int RED   = 0;

    private String rootUID;

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

    public MyFirebase(ClassRoomData classRoomData) {
        this.classRoomData = classRoomData;
        mRef = FirebaseDatabase.getInstance().getReference();
        calRef = mRef.child("calendar");
        userRef = mRef.child("users").child(String.valueOf(classRoomData.getUserId()));
        userRsvnRef = userRef.child("myResvList");
        rsvnRef = mRef.child("reservations");
    }

    private boolean isEmpty(DataSnapshot dsRoot) {
        return dsRoot.hasChild("RBTreeHeader");
    }

    private DataSnapshot findNode(DataSnapshot dsRoot, DataSnapshot fNode, DataSnapshot node) {
        if(isEmpty(dsRoot)) return null; setRsvnDS(dsRoot);
        if(getStartTime(fNode) < getStartTime(node)) {
            if(!isNodeEquals(getLeftNode(node), getNode("nullNode")))
                return findNode(dsRoot, fNode, getLeftNode(node));
        } else if(getStartTime(fNode) > getStartTime(node)) {
            if(!isNodeEquals(getRightNode(node), getNode("nullNode")))
                return findNode(dsRoot, fNode, getRightNode(node));
        } else if(getStartTime(fNode) == getStartTime(node)) {

            return node;
        }
        return null;
    }

    public void insert(DataSnapshot dsRoot, String uidKey, ClassRoomData classRoomData) {
        setRsvnDS(dsRoot);
        long currentStartTime = classRoomData.getStartTime();
        if(!isEmpty(dsRoot)) {
            mRef.child("RBTreeHeader").setValue(uidKey);
            dsRoot.child("RBTreeHeader").getRef().setValue(uidKey);
        } else {
            setRootUID(dsRoot);
            DataSnapshot temp = rsvnDS.child(dsRoot.child("RBTreeHeader").getValue(String.class));
            rsvnRef.child(uidKey).child("rbColor").setValue(RED);
            while(true) {
                if(currentStartTime < getStartTime(temp)) {
                    if(getLeftNode(temp).getKey().equals("nullNode")) {
                        temp.child("leftNode").getRef().setValue(uidKey);
                        getNode(uidKey).child("parentNode").getRef().setValue(temp.getKey());
                        break;
                    } else temp = getLeftNode(temp);
                } else if(currentStartTime >= getStartTime(temp)) {
                    if(getRightNode(temp).getKey().equals("nullNode")) {
                        temp.child("rightNode").getRef().setValue(uidKey);
                        getNode(uidKey).child("parentNode").getRef().setValue(temp.getKey());
                        break;
                    } else temp = getRightNode(temp);
                }
            } // while
            fixTree(dsRoot, uidKey);
        } // if - isEmpty
    }

    private void fixTree(DataSnapshot dsRoot, String uidKey) {
        while(getColor(getParentNode(uidKey)) == RED) {
            DataSnapshot uncle;
            if(isNodeEquals(getParentNode(uidKey), getLeftNode(getParentNode(getParentNode(uidKey))))) {
                uncle = getRightNode(getParentNode(getParentNode(uidKey)));
                if(!isNodeEquals(uncle, getNode("nullNode")) && getColor(uncle) == RED) {
                    getParentNode(uidKey).child("rbColor").getRef().setValue(BLACK);
                    uncle.child("rbColor").getRef().setValue(BLACK);
                    getParentNode(getParentNode(uidKey)).child("rbColor").getRef().setValue(RED);
                    uidKey = getParentNode(getParentNode(uidKey)).getKey();
                    continue;
                }
                if(isNodeEquals(getNode(uidKey), getRightNode(getParentNode(uidKey)))) {
                    uidKey = getParentNode(uidKey).getKey();
                    rotateLeft(getNode(uidKey));
                }
                getParentNode(uidKey).child("rbColor").getRef().setValue(BLACK);
                getParentNode(getParentNode(uidKey)).child("rbColor").getRef().setValue(RED);
                rotateRight(getParentNode(getParentNode(uidKey)));
            } else {
                uncle = getLeftNode(getParentNode(getParentNode(uidKey)));
                if(!isNodeEquals(uncle, getNode("nullNode")) && getColor(uncle) == RED) {
                    getParentNode(uidKey).child("rbColor").getRef().setValue(BLACK);
                    uncle.child("rbColor").getRef().setValue(BLACK);
                    getParentNode(getParentNode(uidKey)).child("rbColor").getRef().setValue(RED);
                    uidKey = getParentNode(getParentNode(uidKey)).getKey();
                    continue;
                }
                if(isNodeEquals(getNode(uidKey), getLeftNode(getParentNode(uidKey)))) {
                    uidKey = getParentNode(uidKey).getKey();
                    rotateRight(getNode(uidKey));
                }
                getParentNode(uidKey).child("rbColor").getRef().setValue(BLACK);
                getParentNode(getParentNode(uidKey)).child("rbColor").getRef().setValue(RED);
                rotateLeft(getParentNode(getParentNode(uidKey)));
            }
        }
        rsvnRef.child(dsRoot.child("RBTreeHeader").getValue(String.class)).child("rbColor").setValue(BLACK);
    }

    private void rotateLeft(DataSnapshot node) {
        if(!isNodeEquals(getParentNode(node), getNode("nullNode"))) {
            if(isNodeEquals(node, getLeftNode(getParentNode(node))))
                getParentNode(node).child("leftNode").getRef().setValue(getRightNode(node).getKey());
            else
                getParentNode(node).child("rightNode").getRef().setValue(getRightNode(node).getKey());
            getRightNode(node).child("parentNode").getRef().setValue(getParentNode(node).getKey());
            node.child("parentNode").getRef().setValue(getRightNode(node));
            if(!isNodeEquals(getLeftNode(getRightNode(node)), getNode("nullNode")))
                getLeftNode(getRightNode(node)).child("parentNode").getRef().setValue(node.getKey());
            node.child("rightNode").getRef().setValue(getLeftNode(getRightNode(node)).getKey());
            getParentNode(node).child("leftNode").getRef().setValue(node.getKey());
        } else {
            DataSnapshot right = getRightNode(rootUID);
            getNode(rootUID).child("rightNode").getRef().setValue(getLeftNode(right));
            getLeftNode(right).child("parentNode").getRef().setValue(rootUID);
            getNode(rootUID).child("parentNode").getRef().setValue(right.getKey());
            right.child("leftNode").getRef().setValue(rootUID);
            right.child("parentNode").getRef().setValue("nullNode");
            rootUID = right.getKey();
        }
    }

    private void rotateRight(DataSnapshot node) {
        if(!isNodeEquals(getParentNode(node), getNode("nullNode"))) {
            if(isNodeEquals(node, getLeftNode(getParentNode(node))))
                getParentNode(node).child("leftNode").getRef().setValue(getLeftNode(node).getKey());
            else
                getParentNode(node).child("rightNode").getRef().setValue(getLeftNode(node).getKey());
            getLeftNode(node).child("rightNode").getRef().setValue(getParentNode(node).getKey());
            node.child("parentNode").getRef().setValue(getLeftNode(node).getKey());
            if(!isNodeEquals(getRightNode(getLeftNode(node)), getNode("nullNode")))
                getRightNode(getLeftNode(node)).child("parentNode").getRef().setValue(node.getKey());
            node.child("leftNode").getRef().setValue(getRightNode(getLeftNode(node)).getKey());
            getParentNode(node).child("rightNode").getRef().setValue(node.getKey());
        } else {
            DataSnapshot left = getLeftNode(rootUID);
            getNode(rootUID).child("leftNode").getRef().setValue(getRightNode(getLeftNode(rootUID)));
            getRightNode(left).child("parentNode").getRef().setValue(rootUID);
            getNode(rootUID).child("parentNode").getRef().setValue(left.getKey());
            left.child("rightNode").getRef().setValue(rootUID);
            left.child("parentNode").getRef().setValue("nullNode");
            rootUID = left.getKey();
        }
    }

    private void transplant(DataSnapshot target, DataSnapshot with) {
        if(isNodeEquals(getParentNode(target), getNode("nullNode"))){
            rootUID = with.getKey();
            mRef.child("RBTreeHeader").setValue(rootUID);
        } else if(isNodeEquals(target, getLeftNode(getParentNode(target)))) {
            getParentNode(target).child("leftNode").getRef().setValue(with.getKey());
        } else {
            getParentNode(target).child("rightNode").getRef().setValue(with.getKey());
        }
        with.child("parentNode").getRef().setValue(getParentNode(target).getKey());
    }

    public boolean delete(DataSnapshot dsRoot, DataSnapshot z) {
        setRootUID(dsRoot);
        if((z = findNode(dsRoot, z, getNode(rootUID))) == null) return false;
        DataSnapshot x, y = z;
        int y_original_color = getColor(y);
        if(isNodeEquals(getLeftNode(z), getNode("nullNode"))) {
            x = getRightNode(z);
            transplant(z, getRightNode(z));
        } else if(isNodeEquals(getRightNode(z), getNode("nullNode"))) {
            x = getLeftNode(z);
            transplant(z, getLeftNode(z));
        } else {
            y = treeMinimum(getRightNode(z));
            y_original_color = getColor(y);
            x = getRightNode(y);
            if(isNodeEquals(getParentNode(y), z))
                x.child("parentNode").getRef().setValue(y.getKey());
            else {
                transplant(y, getRightNode(y));
                y.child("rightNode").getRef().setValue(getRightNode(z).getKey());
                getRightNode(y).child("parentNode").getRef().setValue(y.getKey());
            }
            transplant(z, y);
            y.child("leftNode").getRef().setValue(getLeftNode(z).getKey());
            getLeftNode(y).child("parentNode").getRef().setValue(y.getKey());
            y.child("rbColor").getRef().setValue(getColor(z));
        }
        if(y_original_color == BLACK)
            deleteFixup(x);
        return true;
    }

    private void deleteFixup(DataSnapshot x) {
        while(!isNodeEquals(x, getNode(rootUID)) && getColor(x) == BLACK) {
            if(isNodeEquals(x, getLeftNode(getParentNode(x)))) {
                DataSnapshot w = getRightNode(getParentNode(x));
                if(getColor(w) == RED) {
                    w.child("rbColor").getRef().setValue(BLACK);
                    getParentNode(x).child("rbColor").getRef().setValue(RED);
                    rotateLeft(getParentNode(x));
                    w = getRightNode(getParentNode(x));
                }
                if(getColor(getLeftNode(w)) == BLACK && getColor(getRightNode(w)) == BLACK) {
                    w.child("rbColor").getRef().setValue(RED);
                    x = getParentNode(x);
                    continue;
                } else if(getColor(getRightNode(w)) == BLACK) {
                    getLeftNode(w).child("rbColor").getRef().setValue(BLACK);
                    w.child("rbColor").getRef().setValue(RED);
                    rotateRight(w);
                    w = getRightNode(getParentNode(x));
                }
                if(getColor(getRightNode(w)) == RED){
                    w.child("rbColor").getRef().setValue(getColor(getParentNode(x)));
                    getParentNode(x).child("rbColor").getRef().setValue(BLACK);
                    getRightNode(w).child("rbColor").getRef().setValue(BLACK);
                    rotateLeft(getParentNode(x));
                    x = getNode(rootUID);
                }
            } else {
                DataSnapshot w = getLeftNode(getParentNode(x));
                if(getColor(w) == RED) {
                    w.child("rbColor").getRef().setValue(BLACK);
                    getParentNode(x).child("rbColor").getRef().setValue(RED);
                    rotateRight(getParentNode(x));
                    w = getLeftNode(getParentNode(x));
                }
                if(getColor(getRightNode(w)) == BLACK && getColor(getLeftNode(w)) == BLACK) {
                    w.child("rbColor").getRef().setValue(RED);
                    x = getParentNode(x);
                    continue;
                } else if(getColor(getLeftNode(w)) == BLACK) {
                    getRightNode(w).child("rbColor").getRef().setValue(BLACK);
                    w.child("rbColor").getRef().setValue(RED);
                    rotateLeft(w);
                    w = getLeftNode(getParentNode(x));
                }
                if(getColor(getLeftNode(w)) == RED) {
                    w.child("rbColor").getRef().setValue(getColor(getParentNode(x)));
                    getParentNode(x).child("rbColor").getRef().setValue(BLACK);
                    getLeftNode(w).child("rbColor").getRef().setValue(BLACK);
                    rotateRight(getParentNode(x));
                    x = getNode(rootUID);
                }
            }
        }
        x.child("rbColor").getRef().setValue(BLACK);
    }

    private DataSnapshot treeMinimum(DataSnapshot subTreeRoot) {
        while(!isNodeEquals(getLeftNode(subTreeRoot), getNode("nullNode"))) {
            subTreeRoot = getLeftNode(subTreeRoot);
        }
        return subTreeRoot;
    }

    private boolean isNodeEquals(DataSnapshot a, DataSnapshot b) {
        if(a == null || b == null) return false;
        return a.getKey().equals(b.getKey());
    }
    public DataSnapshot getNode(String uidKey) { return rsvnDS.child(uidKey); }
    private DataSnapshot getParentNode(DataSnapshot n) { return rsvnDS.child(n.child("parentNode").getValue(String.class)); }
    private DataSnapshot getParentNode(String uidKey) { return getParentNode(getNode(uidKey)); }
    private DataSnapshot getLeftNode(DataSnapshot n) { return rsvnDS.child(n.child("leftNode").getValue(String.class)); }
    private DataSnapshot getLeftNode(String uidKey) { return getLeftNode(getNode(uidKey)); }
    private DataSnapshot getRightNode(DataSnapshot n) { return rsvnDS.child(n.child("rightNode").getValue(String.class)); }
    private DataSnapshot getRightNode(String uidKey) { return getRightNode(getNode(uidKey)); }
    private long getStartTime(DataSnapshot n) { return n.child("startTime").getValue(Long.class); }
    private long getStartTime(String uidKey) { return getStartTime(getNode(uidKey)); }
    private long getEndTime(DataSnapshot n) { return n.child("endTime").getValue(Long.class); }
    private int getColor(DataSnapshot n) { return n.child("rbColor").getValue(Integer.class); }
    private int getColor(String uidKey) { return getColor(getNode(uidKey)); }
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
    private DataSnapshot getFavClassRoomDS(DataSnapshot dsRoot, ClassRoomData classRoomData) {
        return getUserSnapshot(dsRoot, classRoomData).child("myFavList");
    }
    private void setRsvnDS(DataSnapshot dsRoot) { rsvnDS = dsRoot.child("reservations"); }
    private void setCalDS(DataSnapshot dsRoot) { calDS = dsRoot.child("calendar"); }
    private void setRootUID(DataSnapshot dsRoot) { rootUID = dsRoot.child("RBTreeHeader").getValue(String.class); }
    private String pad0toHour(int hr) {
        return (hr < 10) ? ("0" + hr) : String.valueOf(hr);
    }
    private String getUIDkey() { return UUID.randomUUID().toString(); }
    private HashMap<String, Object> getNewReservationObject(String uidKey, ClassRoomData classRoomData) {
        HashMap<String, Object> rsvnPush = new HashMap<>();
        HashMap<String, Object> rsvnInfo = new HashMap<>();
        rsvnInfo.put("userId", classRoomData.getUserId());
        rsvnInfo.put("userName", classRoomData.getUserName());
        rsvnInfo.put("phoneNumber", classRoomData.getPhoneNumber());
        rsvnInfo.put("classRoom", classRoomData.getClassRoom());
        rsvnInfo.put("startTime", classRoomData.getStartTime());
        rsvnInfo.put("endTime", classRoomData.getEndTime());
        rsvnInfo.put("numUsers", classRoomData.getNumUsers());
        rsvnInfo.put("usage", classRoomData.getUsage());
        rsvnInfo.put("rbColor", 1);
        rsvnInfo.put("leftNode", "nullNode");
        rsvnInfo.put("rightNode", "nullNode");
        rsvnInfo.put("parentNode", "nullNode");
        rsvnPush.put(uidKey, rsvnInfo);
        return rsvnPush;
    }

    public String writeReservation(ClassRoomData classRoomData) {
        String key = getUIDkey();
        userRsvnRef.child(key).setValue(true); getDateRef(classRoomData).child(key).setValue(true);
        rsvnRef.updateChildren(getNewReservationObject(key, classRoomData));
        return key;
    }
    public void deleteReservation(DataSnapshot dataSnapshot, ClassRoomData classRoomData) {
        DataSnapshot rsvnUserDS = getUserRsvnSnapshot(dataSnapshot, classRoomData);
        DataSnapshot dateDS = getDateDS(classRoomData, dataSnapshot);
        Calendar cal = Calendar.getInstance();
        for(DataSnapshot ds : rsvnUserDS.getChildren()) {
            DataSnapshot startTimeDS = rsvnDS.child(ds.getKey()).child("startTime");
            cal.setTimeInMillis(startTimeDS.getValue(Long.class));
            if(cal.get(Calendar.YEAR) == classRoomData.getYear() &&
                cal.get(Calendar.MONTH) == classRoomData.getMonth() &&
                cal.get(Calendar.DAY_OF_MONTH) == classRoomData.getDate() &&
                cal.get(Calendar.HOUR_OF_DAY) + 1 == classRoomData.getStartTimeMsToHour()) {

                delete(dataSnapshot, ds);

                if(rsvnUserDS.getChildrenCount() <= 1)
                    mRef.child("RBTreeHeader").removeValue();

                rsvnDS.child(ds.getKey()).getRef().removeValue();
                dateDS.child(ds.getKey()).getRef().removeValue();
                ds.getRef().removeValue();

                break;
            }
        }
    }
    public long countReservationForCalendar(MyDateObj mdo, int date, DataSnapshot dsRoot) {
        int yr = mdo.getCalendar().get(Calendar.YEAR);
        int mth = mdo.getCalendar().get(Calendar.MONTH) + 1;
        return getDateDS(yr, mth, date, dsRoot).getChildrenCount();
    }
    public void readReservationForTable(DataSnapshot dataSnapshot, ArrayList<ArrayList<ClassRoomData>> cdListParam, HashMap<String, Integer> crMapParam) {
        this.cdList = cdListParam; this.crMap = crMapParam;
        setRsvnDS(dataSnapshot); setCalDS(dataSnapshot);
        DataSnapshot dateRef = getDateDS(classRoomData, dataSnapshot);
        for(DataSnapshot rsvnSnapshot : dateRef.getChildren()) {
            DataSnapshot tmpRef = rsvnDS.child(rsvnSnapshot.getKey());
            ClassRoomData crData = new ClassRoomData((Calendar) classRoomData.getCalendar().clone());
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
    public ArrayList<ArrayList<String>> readReservationForUser(DataSnapshot dataSnapshot) {
        DataSnapshot userRsvnDS = getUserRsvnSnapshot(dataSnapshot, this.classRoomData);
        ArrayList<ArrayList<String>> rsvnList = new ArrayList<>(); setRsvnDS(dataSnapshot);
        for(DataSnapshot ds : userRsvnDS.getChildren()) {
            DataSnapshot currentRsvnDS = rsvnDS.child(ds.getKey());
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(currentRsvnDS.child("startTime").getValue(Long.class));
            ClassRoomData crData = new ClassRoomData(cal);
            crData.setStartTime(currentRsvnDS.child("startTime").getValue(Long.class));
            crData.setEndTime(currentRsvnDS.child("endTime").getValue(Long.class));
            rsvnList.add(new ArrayList<String>()); int i = rsvnList.size() - 1;
            rsvnList.get(i).add(currentRsvnDS.child("classRoom").getValue(String.class));
            rsvnList.get(i).add(crData.getYear() + "/" + pad0toHour(crData.getMonth() + 1) + "/" + pad0toHour(crData.getDate()));
            rsvnList.get(i).add(pad0toHour(crData.getStartTimeMsToHour()) + ":00~" + pad0toHour(crData.getEndTimeMsToHour()) + ":00");
            rsvnList.get(i).add(String.valueOf(currentRsvnDS.child("numUsers").getValue(Integer.class)));
            rsvnList.get(i).add(currentRsvnDS.child("usage").getValue(String.class));
        }
        Collections.sort(rsvnList, new AscendingStartTime());
        return rsvnList;
    }
    public void writeFavoriteClassRoom(String favCR) {
        if(favCR != null) {
            userRef.child("myFavList").child(favCR).setValue(true);
        }
    }
    public ArrayList<String> readFavoriteClassRoom(DataSnapshot dataSnapshot) {
        DataSnapshot favDS = getFavClassRoomDS(dataSnapshot, this.classRoomData);
        ArrayList<String> favList = new ArrayList<>();
        for(DataSnapshot ds : favDS.getChildren())
            favList.add(ds.getKey());
        return favList;
    }
    public void deleteFavoriteClassRoom(String favCR) {
        if(favCR != null) {
            userRef.child("myFavList").child(favCR).removeValue();
        }
    }

}
