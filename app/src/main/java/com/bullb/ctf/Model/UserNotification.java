package com.bullb.ctf.Model;

import com.google.gson.annotations.SerializedName;

public class UserNotification {
    @SerializedName("pivot")
    public Pivot pivot;

    @SerializedName("id")
    public String id;
    @SerializedName("title")
    public String title;
    @SerializedName("message")
    public String message;

    public class Pivot{

    }
}
