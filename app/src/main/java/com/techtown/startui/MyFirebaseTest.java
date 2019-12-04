package com.techtown.startui;

import com.google.firebase.database.DataSnapshot;

import java.util.Map;

public class MyFirebaseTest {

    private final byte RED = 0;
    private final byte BLACK = 1;
    private DataSnapshot nil;
    private DataSnapshot root;
    private boolean isInit;

    public MyFirebaseTest() {

        this.isInit = true;

    }

    private void initByDS(DataSnapshot dsRoot) {
        this.nil = dsRoot.child("reservations").child("NILNODE");
        this.nil.child("startTime").getRef().setValue((long)(-1));
        this.nil.child("leftNode").child("NILNODE").getRef().setValue(false);
        this.nil.child("rightNode").child("NILNODE").getRef().setValue(false);
        this.nil.child("rbColor").getRef().setValue(BLACK);

        if(this.isInit) {
            this.root = this.nil;
            this.isInit = false;
        }
    }

    private long getST(DataSnapshot node) {
        return node.child("startTime").getValue(Long.class);
    }
    private DataSnapshot getLeftDS(DataSnapshot node) {
        return node.child("leftNode").getChildren().iterator().next();
    }
    private String getLeft(DataSnapshot node) {
        return getLeftDS(node).getKey();
    }
    private DataSnapshot getRightDS(DataSnapshot node) {
        return node.child("rightNode").getChildren().iterator().next();
    }
    private String getRight(DataSnapshot node) {
        return getRightDS(node).getKey();
    }
    private void setColor(DataSnapshot node, byte color) {
        node.child("rbColor").getRef().setValue(color);
    }

    private DataSnapshot findNode(DataSnapshot dsRoot, DataSnapshot searchNode, DataSnapshot node) {
        initByDS(dsRoot); if(this.root == this.nil) return null;
        if(getST(searchNode) < getST(node)) {
            if(!getLeft(node).equals(this.nil.getKey()))
                return findNode(dsRoot, searchNode, getLeftDS(node));
        } else if(getST(searchNode) > getST(node)) {
            if(!getRight(node).equals(this.nil.getKey()))
                return findNode(dsRoot, searchNode, getRightDS(node));
        } else if(getST(searchNode) == getST(node))
            return node;
        return null;
    }

    private void insert(DataSnapshot dsRoot, DataSnapshot node) {
        initByDS(dsRoot);
        DataSnapshot temp = this.root;
        if(this.root == this.nil) {
            this.root = node;
            setColor(node, BLACK);
        } else {
            setColor(node, RED);
            while(true) {
                if(getST(node) < getST(temp)) {
                    if(getLeft(temp).equals(nil.getKey())) {
                        getLeftDS(temp).getRef().setValue(node.getValue());
                        break;
                    } else {
                        temp = getLeftDS(temp);
                    }
                } else if(getST(node) >= getST(temp)) {
                    if(getRight(temp).equals(nil.getKey())) {
                        getRightDS(temp).getRef().setValue(node.getValue());
                        break;
                    } else {
                        temp = getRightDS(temp);
                    }
                }
            }
            fixTree(node);
        }
    }

    private void fixTree(DataSnapshot node) {
        // TODO: Find out how to get parent datasnapshot
    }

}
