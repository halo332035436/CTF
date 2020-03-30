package com.bullb.ctf.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.bullb.ctf.Login.LoginActivity;
import com.bullb.ctf.Model.BonusesData;
import com.bullb.ctf.Model.RewardData;
import com.bullb.ctf.Model.SalesData;
import com.bullb.ctf.Model.SelfManagementData;
import com.bullb.ctf.Model.UserTargetData;
import com.bullb.ctf.Model.User;
import com.google.gson.Gson;

import java.io.Serializable;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by oscarlaw on 29/9/16.
 */
public class SharedPreference implements Serializable {
    public static String KEY_PREFERENCE_NAME = "key";
    public static String USER_PREFERENCE_NAME = "user";
    public static String VERSION_PREFERENCE_NAME = "version";
    public static String SELF_MANAGEMENT_PREFERENCE_NAME = "self_management";
    public static String BONUSES_PREFERENCE_NAME = "bonus";
    public static String REWARS_PREFERENCE_NAME = "rewards";
    public static String SALES_PREFERENCE_NAME = "sales";
    public static String TARGET_PREFERENCE_NAME = "sales";


    public static String PRIVATE_KEY = "private_key";
    public static String PUBLIC_KEY = "public_key";


    public static String USER = "user";
    public static String UUID = "uuid";
    public static String CHANNEL_ID = "channel_id";
    public static String TOKEN = "token";
    public static String VERSION = "version";
    public static String SELF_MANAGEMENT = "self_management";
    public static String SUPERVISOR = "supervisor";
    public static String BONUSES = "bonuses";
    public static String REWARDS = "rewards";
    public static String SALES = "sales";
    public static String TARGET = "target";
    public static String NOTIFICATION_UNREAD = "notification_unread";
    public static String NOTIFICATION_UNREAD_COUNT = "notification_unread_count";

    public static String VIEWABLE_ROOT_INDEX = "viewable_root_index";


    //hk / cn
    public static String SERVER_VERSION = "server_version";


    public static String RANDOM_PW = "random_pw";


    public static void setPrivateKey(Context context,String privateKey){
        SharedPreferences prefs = context.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(PRIVATE_KEY, privateKey);
        e.commit();
    }

    public static String getPrivateKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String privateKey = prefs.getString(PRIVATE_KEY, null);
        return privateKey;
    }

    public static void setPublicKey(Context context,String publicKey){
        SharedPreferences prefs = context.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(PUBLIC_KEY, publicKey);
        e.commit();
    }

    public static String getPublicKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String publicKey = prefs.getString(PUBLIC_KEY, null);
        return publicKey;
    }


    public static void setUUID(Context context,String uuid){
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(UUID, uuid);
        e.commit();
    }

    public static String getUUID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String uuid = prefs.getString(UUID, null);
        if (uuid == null || uuid.isEmpty()){
            uuid = java.util.UUID.randomUUID().toString();
            setUUID(context,uuid);
        }
        return uuid;
    }

    public static void setChannelId(Context context,String channelId){
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(CHANNEL_ID, channelId);
        e.commit();
    }

    public static String getChannelId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String channelId = prefs.getString(CHANNEL_ID, null);
        return channelId;
    }


    public static void setUser(User user, Context context){
        Gson gson = new Gson();
        String valueString;
        if (user != null) {
            valueString = gson.toJson(user);
        }
        else {
            valueString = null;
        }
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(USER, valueString);
        e.commit();
    }

    public static User getUser(Context context) {
        String resultString = null;

        try {
            SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
            resultString = prefs.getString(USER, null);
        } catch (Exception e){
            Log.e("Error", String.valueOf(e));
        }

        if (resultString == null) {
            NullPointerException e = new NullPointerException("User object is null");
            Log.e("Error", String.valueOf(e), e);
            return null;
        }

        Gson gson = new Gson();
        User result = gson.fromJson(resultString, User.class);
        return result;
    }


    public static void setRandomPw(Context context, String pw){
        SharedPreferences prefs = context.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(RANDOM_PW, pw);
        e.commit();

    }

    public static String getRandomPw(Context context){
        SharedPreferences prefs = context.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String pw = prefs.getString(RANDOM_PW, null);
        if (pw == null){
            pw = SharedUtils.generateRandomPassword(context);
            setRandomPw(context,pw);
        }
        return pw;
    }


    public static void setToken(Context context, String token){
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(TOKEN, token);
        e.commit();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(TOKEN, null);
        return token;
    }

    public static void setVersion(Context context, int version){
        SharedPreferences prefs = context.getSharedPreferences(VERSION_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putInt(VERSION, version);
        e.commit();
    }

    public static int getVersion(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(VERSION_PREFERENCE_NAME, Context.MODE_PRIVATE);
        int version = prefs.getInt(VERSION, 0);
        return version;
    }


    public static void setSelfManagement(Context context, SelfManagementData data){
        Gson gson = new Gson();
        String valueString;
        if (data != null) {
            valueString = gson.toJson(data);
        }
        else {
            valueString = null;
        }
        SharedPreferences prefs = context.getSharedPreferences(SELF_MANAGEMENT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(SELF_MANAGEMENT, valueString);
        e.commit();
    }

    public static SelfManagementData getSelfManagement(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SELF_MANAGEMENT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String resultString = prefs.getString(SELF_MANAGEMENT, null);
        if (resultString == null){
            return null;
        }
        else {
            Gson gson = new Gson();
            SelfManagementData result = gson.fromJson(resultString, SelfManagementData.class);
            return result;
        }
    }


    public static void setRewards(RewardData rewardData, Context context){
        Gson gson = new Gson();
        String valueString;
        if (rewardData != null) {
            valueString = gson.toJson(rewardData);
        }
        else {
            valueString = null;
        }
        SharedPreferences prefs = context.getSharedPreferences(REWARS_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(REWARDS, valueString);
        e.commit();
    }


    public static RewardData getRewards(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(REWARS_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String resultString = prefs.getString(REWARDS, null);
        if (resultString == null){
            return null;
        }
        else {
            Gson gson = new Gson();
            RewardData result = gson.fromJson(resultString, RewardData.class);
            return result;
        }
    }

    public static void setTarget(UserTargetData userTargetData, Context context){
        Gson gson = new Gson();
        String valueString;
        if (userTargetData != null) {
            valueString = gson.toJson(userTargetData);
        }
        else {
            valueString = null;
        }
        SharedPreferences prefs = context.getSharedPreferences(TARGET_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(TARGET, valueString);
        e.commit();
    }


    public static UserTargetData getTarget(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(TARGET_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String resultString = prefs.getString(TARGET, null);
        if (resultString == null){
            return null;
        }
        else {
            Gson gson = new Gson();
            UserTargetData result = gson.fromJson(resultString, UserTargetData.class);
            return result;
        }
    }

    public static void setBonuses(BonusesData bonuses, Context context){
        Gson gson = new Gson();
        String valueString;
        if (bonuses != null) {
            valueString = gson.toJson(bonuses);
        }
        else {
            valueString = null;
        }
        SharedPreferences prefs = context.getSharedPreferences(BONUSES_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(BONUSES, valueString);
        e.commit();
    }


    public static BonusesData getBonuses(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(BONUSES_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String resultString = prefs.getString(BONUSES, null);
        if (resultString == null){
            return null;
        }
        else {
            Gson gson = new Gson();
            BonusesData result = gson.fromJson(resultString, BonusesData.class);
            return result;
        }
    }


    public static void setSales(SalesData salesData, Context context){
        Gson gson = new Gson();
        String valueString;
        if (salesData != null) {
            valueString = gson.toJson(salesData);
        }
        else {
            valueString = null;
        }
        SharedPreferences prefs = context.getSharedPreferences(SALES_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(SALES, valueString);
        e.commit();
    }


    public static SalesData getSales(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SALES_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String resultString = prefs.getString(SALES, null);
        if (resultString == null){
            return null;
        }
        else {
            Gson gson = new Gson();
            SalesData result = gson.fromJson(resultString, SalesData.class);
            return result;
        }
    }


    public static void setSupervisor(User user, Context context){
        Gson gson = new Gson();
        String valueString;
        if (user != null) {
            valueString = gson.toJson(user);
        }
        else {
            valueString = null;
        }
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(SUPERVISOR, valueString);
        e.commit();
    }


    public static User getSupervisor(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String resultString = prefs.getString(SUPERVISOR, null);
        if (resultString == null){
            return null;
        }
        else {
            Gson gson = new Gson();
            User result = gson.fromJson(resultString, User.class);
            return result;
        }
    }


    public static void setNotificationUnread(Context context, boolean unRead){
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean(NOTIFICATION_UNREAD, unRead);
        e.commit();
    }

    public static boolean getNotificationUnread(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean token = prefs.getBoolean(NOTIFICATION_UNREAD, false);
        return token;
    }


    public static void setNotificationUnreadCount(Context context, int unReadCount){
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putInt(NOTIFICATION_UNREAD_COUNT, unReadCount);
        e.commit();
        //update badge
        ShortcutBadger.applyCount(context.getApplicationContext(), unReadCount);

    }

    public static int getNotificationUnreadCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        int unreadCount = prefs.getInt(NOTIFICATION_UNREAD_COUNT, 0);
        return unreadCount;
    }

    public static void setViewableRootIndex(Context context, int index){
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putInt(VIEWABLE_ROOT_INDEX, index);
        e.commit();
    }

    public static int getViewableRootIndex(Context context){
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFERENCE_NAME, Context.MODE_PRIVATE);
        int index = prefs.getInt(VIEWABLE_ROOT_INDEX, 0);
        return index;
    }


    public static void clearStoredData(Context context){
        setSelfManagement(context, null);
        setUser(null,context);
        setSupervisor(null, context);
        setBonuses(null, context);
        setRewards(null, context);
        setSales(null, context);
        setTarget(null, context);
    }



    public static void logout(Context context){
        setUser(null,context);
        setToken(context, null);
        setSelfManagement(context, null);
        setSupervisor(null, context);
        setBonuses(null, context);
        setRewards(null, context);
        setSales(null, context);
        setTarget(null, context);
        setNotificationUnread(context, false);
        setNotificationUnreadCount(context, 0);
        setViewableRootIndex(context,0);

        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        context.startActivity(intent);
        ((Activity)context).finishAffinity();

    }


    public void callLogoutApi(Context context){


    }




}
