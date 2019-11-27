package com.mobiletemple.photopeople.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private Editor editor;
    // Shared pref file name
    private static final String PREF_NAME = "PhotopeolpePref";

    // All Shared Preferences Keys
    public static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_WORKING_HOURS_ADDED = "is_working_hours_added";
    // Notification Count
    private static final String NOTIFICATION_COUNT = "notification_count";

    public static final String KEY_VERIFY_EMAIL = "verify_email";
    public static final String KEY_FORGET_EMAIL = "forget_email";
    //login
    public static final String KEY_EMAIL = "emp_email";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_FREE_JOBSTATUS = "jobstatus";

    public static final String KEY_NAME = "first_name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_DESIGNATION = "designation";
    public static final String KEY_IMAGE = "profile_image";
    public static final String KEY_COVERPIC = "cover_image";
    public static final String KEY_IMAGECOUNT = "imageCount";
    public static final String KEY_USERID= "user_id";
    public static final String KEY_FREELANCERTYPE= "freelancertype";

    public static final String KEY_STARTDATE= "startdate";
    public static final String KEY_ENDDATE= "enddate";
    public static final String KEY_FREEE_TYPE= "freelancertype";
    public static final String KEY_LATITUDE = "ip_latitude";
    public static final String KEY_LONGITUDE = "ip_longitude";
    public static final String KEY_LOCATION = "location";


    public static final String KEY_MYLATITUDE = "MYip_latitude";
    public static final String KEY_MYLONGITUDE = "MYip_longitude";
    public static final String KEY_MYCOUNTRY = "country";

    // Constructor
    public SessionManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLoginSession(String email, String id, String name, String user_type, String mobile,
                                String image,String freelancertype, String imageCount) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USERID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_USER_TYPE, user_type);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_IMAGE, image);
        editor.putString(KEY_FREELANCERTYPE,freelancertype);
        editor.putString(KEY_IMAGECOUNT,imageCount);

        editor.commit();
    }

    // Get stored session data
    public HashMap<String, String> getLoginSession() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, ""));
        user.put(KEY_USERID, pref.getString(KEY_USERID, ""));
        user.put(KEY_NAME, pref.getString(KEY_NAME, ""));
        user.put(KEY_USER_TYPE, pref.getString(KEY_USER_TYPE, ""));
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, ""));
        user.put(KEY_DESIGNATION, pref.getString(KEY_DESIGNATION, ""));
        user.put(KEY_IMAGE, pref.getString(KEY_IMAGE, ""));
        user.put(KEY_FREELANCERTYPE, pref.getString(KEY_FREELANCERTYPE, ""));
        user.put(KEY_IMAGECOUNT, pref.getString(KEY_IMAGECOUNT, ""));

        return user;
    }

    // Clear session details
    public void logoutUser() {
        editor.clear();
        editor.commit();
    }


    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, false);
    }

    public void setFirstTimeLaunch(boolean b) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, b);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean checkWorkingHours() {
        return pref.getBoolean(IS_WORKING_HOURS_ADDED, false);
    }



    public int getNotificationCount() {
        return pref.getInt(NOTIFICATION_COUNT, 0);
    }

    public void setNotificationCount(int count) {
        editor.putInt(NOTIFICATION_COUNT, count);
        editor.commit();
    }


    public void setImageSession(String picurl)
    {
        editor.putString(KEY_IMAGE, picurl);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getImageSession() {
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_IMAGE, pref.getString(KEY_IMAGE, ""));
        return user;
    }


    public void setMyLatLong(String lat,String lng,String country)
    {
        editor.putString(KEY_MYLATITUDE, lat);
        editor.putString(KEY_MYLONGITUDE, lng);
        editor.putString(KEY_MYCOUNTRY, country);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getMyLatLong() {
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_MYLONGITUDE, pref.getString(KEY_MYLONGITUDE, ""));
        user.put(KEY_MYLATITUDE, pref.getString(KEY_MYLATITUDE, ""));
        user.put(KEY_MYCOUNTRY, pref.getString(KEY_MYCOUNTRY, ""));

        return user;
    }

    public void setCoverPicSession(String picurl1)
    {
        editor.putString(KEY_COVERPIC, picurl1);
        editor.commit();
    }

    public HashMap<String, String> getCoverPicSession() {
        HashMap<String, String> user = new HashMap<>();

        user.put(KEY_COVERPIC, pref.getString(KEY_COVERPIC, ""));
        return user;
    }

    public void setuserid(String userId)
    {
        editor.putString(KEY_USERID, userId);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getuserId() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_USERID, pref.getString(KEY_USERID, ""));
        return user;
    }





    public void setFilter(String startdate,String enddate,String location,String latitude,String longitude,String freetype)
    {
        editor.putString(KEY_STARTDATE, startdate);
        editor.putString(KEY_ENDDATE, enddate);
        editor.putString(KEY_LATITUDE, latitude);
        editor.putString(KEY_LONGITUDE, longitude);
        editor.putString(KEY_LOCATION, location);
        editor.putString(KEY_FREEE_TYPE, freetype);

        editor.commit();
    }

    public HashMap<String, String> getFilter()
    {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_STARTDATE, pref.getString(KEY_STARTDATE, ""));
        user.put(KEY_ENDDATE, pref.getString(KEY_ENDDATE, ""));
        user.put(KEY_LOCATION, pref.getString(KEY_LOCATION, ""));
        user.put(KEY_LONGITUDE, pref.getString(KEY_LONGITUDE, ""));
        user.put(KEY_LATITUDE, pref.getString(KEY_LATITUDE, ""));
        user.put(KEY_FREEE_TYPE, pref.getString(KEY_FREEE_TYPE, ""));

        return user;
    }




}