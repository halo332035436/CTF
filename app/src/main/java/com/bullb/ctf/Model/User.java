package com.bullb.ctf.Model;

import android.content.Context;
import android.content.SharedPreferences;

import com.bullb.ctf.Utils.SharedUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.UUID;

import static android.R.attr.type;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class User implements Serializable {
    public static String USER_TYPE_A = "A";
    public static String USER_TYPE_B = "B";
    public static String USER_TYPE_C = "C";


    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("unit")
    public String unit;
    @SerializedName("type")
    public String type;
    @SerializedName("title")
    public String title;
    @SerializedName("department_id")
    public String department_id;
    @SerializedName("email")
    public String email;
    @SerializedName("entry_date")
    public String entry_date;
    @SerializedName("supervisor_id")
    public String supervisor_id;
    @SerializedName("state")
    public String state;
    @SerializedName("phone")
    public String phone;
    @SerializedName("address")
    public String address;
    @SerializedName("user_targets")
    public ArrayList<UserTarget> user_targets;
    @SerializedName("user_sales")
    public ArrayList<UserSale> user_sales;
    @SerializedName("department")
    public Department department;
    @SerializedName("viewable_root_department")
    public Department viewable_root_department;
    @SerializedName("icon_url")
    private String icon_url;
    @SerializedName("ratio_a")
    public double ratio_a;
    @SerializedName("ratio_e")
    public double ratio_e;
    @SerializedName("ratio_f")
    public double ratio_f;
    @SerializedName("unread_notifications_count")
    public int unread_notifications_count;
    @SerializedName("user_role")
    public UserRole userRole;
    @SerializedName("viewable_root_departments")
    public ArrayList<Department> viewable_root_departments;

    public Department getDepartment(){
        if (viewable_root_department != null){
            return viewable_root_department;
        }
        else {
            return department;
        }
    }


    public String getLongDepartmentName(){

        if (department == null || department.long_description == null){
            if (viewable_root_department != null){
                if (viewable_root_department.long_description != null){
                    return viewable_root_department.long_description;
                }
                else
                    return "";
            }
            return "";
        }
        else{
            return department.long_description;
        }
    }


//    public String getShortDepartmentName(){
//        if (viewable_root_department != null){
//            if (viewable_root_department.short_description != null){
//                return viewable_root_department.short_description;
//            }
//            else
//                return "";
//        }
//
//        if (department == null || department.long_description == null){
//            return "";
//        }
//        else{
//            return department.short_description;
//        }
//    }


    public String getAddress() {
        if (address == null)
            return "";
        return address;
    }


    public String getIconUrl() {
        return icon_url;
    }

    public User(String id){
        this.name = id;
        this.id = id;
        this.unit = "hehe";
        this.type = type;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public ArrayList<Department> getViewable_root_departments() {
        if(viewable_root_departments==null||viewable_root_departments.size()==0){
            ArrayList<Department> temp = new ArrayList<>();
            temp.add(viewable_root_department);
            return temp;
        }else {
            return viewable_root_departments;
        }
    }

    public UserTarget getTargetByType(String type) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String now = format1.format(cal.getTime());

        if(user_targets == null || user_targets.size() <= 0){
            return null;
        }
        for(UserTarget target: user_targets){
            try {
                if(target.type.equals(type) && SharedUtils.isSameYear(now,target.from_date)){
                    return target;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
