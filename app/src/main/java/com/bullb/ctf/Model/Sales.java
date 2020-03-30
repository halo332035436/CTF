package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class Sales implements Serializable {
    public static String TYPE_A = "A";
    public static String TYPE_F = "F";
    public static String TYPE_E = "E";
    public static String TYPE_M = "M";


    @SerializedName("A")
    public double A;
    @SerializedName("E")
    public double E;
    @SerializedName("F")
    public double F;
    @SerializedName("M")
    public double M;
}
