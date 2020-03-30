package com.bullb.ctf.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import com.bullb.ctf.R;

import java.util.Locale;

/**
 * Created by oscarlaw on 28/7/16.
 */
public class LanguageUtils {
    public static String LANGUAGE_PREFERENCE_NAME = "language";

    public static String LANGUAGE = "language";
//    public final static int ENGLISH_POS = 2;
    public final static int TRADITIONAL_CHINESE = 1;
    public final static int SIMPLIFIED_CHINESE = 0;

    public static void setLanguage(int language, Context context){
        try{
            SharedPreferences prefs = context.getSharedPreferences(LanguageUtils.LANGUAGE_PREFERENCE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor e = prefs.edit();
            e.putInt(LanguageUtils.LANGUAGE, language);
            e.commit();
        } catch (Exception e){
            Log.e("Error", "setLanguage: ", e);
        }
    }

    public static int getLanguage(Context context) {
        try{
            SharedPreferences prefs = context.getSharedPreferences(LanguageUtils.LANGUAGE_PREFERENCE_NAME, Context.MODE_PRIVATE);
            int result = prefs.getInt(LanguageUtils.LANGUAGE, -1);
            return result;
        } catch (Exception e){
            Log.e("Error", "getLanguage: ", e);
        }
        return SIMPLIFIED_CHINESE;
    }

    public static Context setDefaultLanguage(Context context){
        int lang = getLanguage(context);
        if (lang == -1) {
            Locale current = context.getResources().getConfiguration().locale;
            if (current.toString().contentEquals("zh_CN")) {
                lang = SIMPLIFIED_CHINESE;
            }
            else if (current.toString().contentEquals("zh_HK") || current.toString().contentEquals("zh_TW") || current.toString().contains("zh_HK")){
                lang = TRADITIONAL_CHINESE;
            }
//            else if (current.toString().contentEquals("en")){
//                lang = ENGLISH_POS;
//            }
            else {
                lang = SIMPLIFIED_CHINESE;
            }
            LanguageUtils.setLanguage(lang, context);
        }


        Locale locale = getLocaleById(lang);

        return changeLocale(context, locale);
    }

    public static Locale getLocaleById(int id) {
        switch(id) {
            case SIMPLIFIED_CHINESE:
                return new Locale("zh","CN");
            case TRADITIONAL_CHINESE:
                return new Locale("zh","HK");
//            case ENGLISH_POS:
//                locale = Locale.ENGLISH;
//                break;
            default:
                return new Locale("zh","CN");
        }
    }

    public static Context changeLocale(Context context) {
        Locale locale = getLocaleById(LanguageUtils.getLanguage(context));
        return changeLocale(context, locale);
    }


    // lang 0: eng , 1: zh-hant
    public static Context changeLocale(Context context, Locale locale) {
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }

    public static String getLanguageName(Context context, int lang){
        if (lang == SIMPLIFIED_CHINESE)
            return context.getString(R.string.sim_chinese);
        else if (lang == TRADITIONAL_CHINESE)
            return context.getString(R.string.trad_chinese);
//        else if (lang == ENGLISH_POS)
//            return context.getString(R.string.english);
        else
            return context.getString(R.string.sim_chinese);
    }



    public static String getLanguageSimbol(Context context) {
        int langNum = getLanguage(context);
        String result = "";
        switch (langNum){
//            case ENGLISH_POS:
//                result = "en";
//                break;
            case TRADITIONAL_CHINESE:
//                result = "zh_Hant";
                result = "zh-HK";
                break;
            case SIMPLIFIED_CHINESE:
//                result = "zh_Hans";
                result = "zh-CN";
                break;
            default:
//                result = "zh_Hans";
                result = "zh-CN";
        }

        return result;
    }

}
