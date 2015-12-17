package com.innometrics.integrationapp.model;

/**
 * Created by killpack on 30.11.15.
 */
public class Dirty {
    transient boolean  dirty =true;

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
