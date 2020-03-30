package com.bullb.ctf.API;

public class FormData {
    public enum FormDataType{
        TEXT, FILE
    }

    public String name;
    public String value;
    public FormDataType type;

    public FormData(String name, String value){
        this.name = name;
        this.value = value;
        this.type = FormDataType.TEXT;
    }

    public FormData(String name, String value, FormDataType type){
        this.name = name;
        this.value = value;
        this.type = type;
    }
}
