package com.example.neighbormaterebuild.model;

import com.google.gson.annotations.SerializedName;

public class Util {

    @SerializedName("field_id")
    private String field_id;

    @SerializedName("name")
    private String name;

    @SerializedName("value")
    private String value;

    public Util() {
    }

    public Util(String field_id, String name, String value) {
        this.field_id = field_id;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getField_id() {
        return field_id;
    }

    public void setField_id(String field_id) {
        this.field_id = field_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
