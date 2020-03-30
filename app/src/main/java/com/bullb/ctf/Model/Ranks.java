package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class Ranks implements Serializable {
    @SerializedName("A")
    public String A;
    @SerializedName("E")
    public String E;
    @SerializedName("F")
    public String F;
    @SerializedName("M")
    public String M;
    @SerializedName("")
    public String all;

    public String getA() {
        if (A == null)
            return "-";
        return A;
    }

    public String getE() {
        if (E == null)
            return "-";
        return E;
    }

    public String getF() {
        if (F == null)
            return "-";
        return F;
    }
    public String getM() {
        if (M == null)
            return "-";
        return M;
    }

    public String getAll() {
        if (all == null)
            return "-";
        return all;
    }

}
