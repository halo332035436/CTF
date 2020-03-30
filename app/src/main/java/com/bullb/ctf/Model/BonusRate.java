package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class BonusRate implements Serializable {
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

    public double getA() {
        return A;
    }

    public double getF() {
        return F;
    }

    public double getE() {
        return E;
    }

    public double getM() {
        return M;
    }


    public BonusRate(double a, double e, double f, double m){
        this.A = a;
        this.E = e;
        this.F = f;
        this.M = m;
    }
}
