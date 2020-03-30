package com.bullb.ctf;

import android.content.Context;
import android.content.SharedPreferences;

import com.bullb.ctf.Utils.SharedUtils;

import java.io.Serializable;


public class ServerPreference implements Serializable {
    public static String SERVER_VERSION_HK = "HK";
    public static String SERVER_VERSION_CN = "CN";

    public static String VERSION_PREFERENCE_NAME = "version";

    public static String SERVER_VERSION = "server_version";

    public static void setServerVersion(Context context, String serverVersion){
        SharedPreferences prefs = context.getSharedPreferences(VERSION_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(SERVER_VERSION, serverVersion);
        e.commit();
    }

    public static String getServerVersion(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(VERSION_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(SERVER_VERSION, SERVER_VERSION_HK);
        return token;
    }

    public static String getServerUrl(Context context){

        if (getServerVersion(context).equals(SERVER_VERSION_CN)){
            return BuildConfig.SERVER_URL_CN;
        }

        return BuildConfig.SERVER_URL_HK;
    }


}
