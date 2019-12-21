package com.princedev.bimbel.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveDataPreference {

    private static final String PREF_NI= "ni";
    private static final String PREF_PASSWORD= "password";
    private static final String PREF_STATUS= "status";
    private static final String PREF_CLASSROOM= "classroom";
    private static final String PREF_NAME= "name";

    private static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    //Menghapus data
    public static void clearAllPref(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }

    //Untuk Menyimpan data
    public static void setUserNI(Context ctx, String userNI)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_NI, userNI);
        editor.commit();
    }

    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_NAME, userName);
        editor.commit();
    }

    public static void setUserPassword(Context ctx, String userPassword)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PASSWORD, userPassword);
        editor.commit();
    }

    public static void setUserStatus(Context ctx, String userStatus)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_STATUS, userStatus);
        editor.commit();
    }

    public static void setUserClass(Context ctx, String userClass)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_CLASSROOM, userClass);
        editor.commit();
    }


    //Untuk mengambil data
    public static String getUserNI(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_NI, "");
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_NAME, "");
    }

    public static String getUserPassword(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_PASSWORD, "");
    }

    public static String getUserStatus(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_STATUS, "");
    }

    public static String getUserClass(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_CLASSROOM, "");
    }


}
