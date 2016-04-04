package com.innometrics.integrationapp.model;

import java.io.Serializable;

/**
 * Created by killpack on 30.11.15.
 */
public class Dirty implements Serializable {
    transient boolean isDirty =true;

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        this.isDirty = dirty;
    }
}
