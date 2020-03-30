package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class Department implements Serializable {
    public static String USER_TYPE_A = "A";
    public static String USER_TYPE_B = "B";
    public static String USER_TYPE_C = "C";


    @SerializedName("id")
    public String id;
    @SerializedName("long_description")
    public String long_description;
//    @SerializedName("short_description")
//    public String short_description;
    @SerializedName("properties")
    public String properties;
    @SerializedName("department_targets")
    public ArrayList<DepartmentTarget> department_targets;
    @SerializedName("department_sales")
    public ArrayList<DepartmentSales> departmentSales;
    @SerializedName("manager_id")
    public String manager_id;

    public String getLong_description() {
        if (long_description == null){
            return "";
        }
        return long_description;
    }

//    public String getShort_description() {
//        if (short_description == null){
//            return "";
//        }
//        return short_description;
//    }


    public int getProperties() {
        if (isNumeric(properties)){
            return Integer.valueOf(properties);
        }
        else{
            return 6;
        }
    }

    public boolean isNumeric(String s){
        if(s.matches("\\d+(?:\\.\\d+)?")) {
            return true;
        } else {
            return false;
        }
    }
}
