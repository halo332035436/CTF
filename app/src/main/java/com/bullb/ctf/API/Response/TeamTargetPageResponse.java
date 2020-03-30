package com.bullb.ctf.API.Response;


import com.bullb.ctf.Model.Department;
import com.bullb.ctf.Model.DepartmentSales;
import com.bullb.ctf.Model.DepartmentTarget;
import com.bullb.ctf.Model.Parent;
import com.bullb.ctf.Model.Ranks;
import com.bullb.ctf.Model.User;
import com.bullb.ctf.Model.UserSale;
import com.bullb.ctf.Model.UserTarget;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by oscar on 7/1/16.
 */
public class TeamTargetPageResponse extends BaseResponse{
    @SerializedName("department_targets")
    public ArrayList<DepartmentTarget> department_targets;
    @SerializedName("department_sales")
    public ArrayList<DepartmentSales> departmentSales;
    @SerializedName("users")
    public ArrayList<User> users;
    @SerializedName("ranks")
    public Ranks ranks;
    @SerializedName("parent")
    public Parent parent;
    @SerializedName("descendants")
    public ArrayList<Department> descendants;
    @SerializedName("children")
    public ArrayList<Department> children;

}