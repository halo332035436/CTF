package com.bullb.ctf.Model;

import com.bullb.ctf.API.Response.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Thomas on 16/12/2016.
 */

public class Notification {

    @SerializedName("id")
    public String id;
    @SerializedName("type")
    public String type;
    @SerializedName("data")
    public NotificationDetail data;
    @SerializedName("url")
    public String webViewURL;
    @SerializedName("name")
    public String name;
    @SerializedName("details")
    public String details;

    public class NotificationDetail implements Serializable {
        @SerializedName("id")
        public String id;
        @SerializedName("title")
        public String title;
        @SerializedName("description")
        public String details;
    }
}
