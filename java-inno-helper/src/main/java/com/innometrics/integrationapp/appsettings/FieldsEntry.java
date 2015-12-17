package com.innometrics.integrationapp.appsettings;


import java.util.Map;

public class FieldsEntry {

    private Map<String, Object> fieldSettings;
    private String valueRef;
    private String value;
    private String fieldName;
    private String type;
    private Boolean required;
    private String srcType;

    public void setFieldSettings(Map<String, Object> fieldSettings) {
        this.fieldSettings = fieldSettings;
    }

    public void setValueRef(String valueRef) {
        this.valueRef = valueRef;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Map<String, Object> getFieldSettings() {
        return fieldSettings;
    }

    public String getValueRef() {
        return valueRef;
    }

    public Object getValue() {
        return value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getType() {
        return type;
    }

    public Boolean getRequired() {
        return required;
    }

    public String getSrcType() {
        return srcType;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

}
