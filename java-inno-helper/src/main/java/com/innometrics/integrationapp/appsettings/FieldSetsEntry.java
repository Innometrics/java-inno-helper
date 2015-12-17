package com.innometrics.integrationapp.appsettings;

import java.util.List;

public class FieldSetsEntry {
    private String setName;
    private List<FieldsEntry> fields;

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public List<FieldsEntry> getFields() {
        return fields;
    }

    public void setFields(List<FieldsEntry> fields) {
        this.fields = fields;
    }
}
