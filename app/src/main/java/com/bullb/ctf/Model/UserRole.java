package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class UserRole implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("type")
    private String type;
    @SerializedName("title")
    private String title;
    @SerializedName("detail")
    private ArrayList<KeyValue> detail;
    @SerializedName("ctf_role")
    private String ctfRole;


    public ArrayList<KeyValue> getDetail() {
        return detail;
    }
}
