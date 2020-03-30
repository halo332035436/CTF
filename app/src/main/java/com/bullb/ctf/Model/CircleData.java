package com.bullb.ctf.Model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class CircleData implements Serializable {
    public static String TYPE_MONEY = "money";
    public static String TYPE_TEXT = "text";

    public String description;
    public String data;
    public String type;

    /**
     * @param description title
     * @param data target value
     * @param type unit ($)
     */
    public CircleData(String description, String data, String type){
        this.description = description;
        this.data = data;
        this.type = type;
    }
}
