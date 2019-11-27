package com.mobiletemple.photopeople.constant;

/**
 * Created by gourav on 15/10/17.
 */

public class Constants {
    // Login Type
    public static final int FB_LOGIN_TYPE = 1;
    public static final int GOOGLE_LOGIN_TYPE = 2;
    public static final int MANAUAL_LOGIN_TYPE = 3;

    // User Type
    public static final String STUDIO_TYPE = "1";
    public static final String FREELANCER_TYPE = "2";
    public static final String ENDUSER_TYPE = "3";

    public static final int PERMISSION_REQUEST = 100;
    // Travel By
    public static final String OWN_CAR = "1";
    public static final String OWN_BIKE = "2";
    public static final String RENT_CAR = "3";
    public static final String TAXI = "4";
    public static final String PUBLIC_TRANSPORT = "5";

    // fREELANCER tYPE
//    public static final String CANDID_PHOTOGRAPHER = "1";
//    public static final String TRADITIONAL_PHOTOGRAPHER = "2";
//    public static final String CANDID_VIDEOGRAPHER = "3";
//    public static final String TRADITIONAL_VIDEOGRAPHER = "4";
//    public static final String HELICAM = "5";
//    public static final String DESIGNER = "6";
//    public static final String VIDEO_EDITOR = "7";
//    public static final String LIVE_PRINT = "8";

    public static final int CANDID_PHOTOGRAPHER = 1;
    public static final int TRADITIONAL_PHOTOGRAPHER = 2;
    public static final int CANDID_VIDEOGRAPHER = 3;
    public static final int TRADITIONAL_VIDEOGRAPHER = 4;
    public static final int HELICAM = 5;
    public static final int DESIGNER = 6;
    public static final int VIDEO_EDITOR = 7;
    public static final int LIVE_PRINT = 8;

    // Event Type
    public static final String BIRTHDAY = "Birthday";
    public static final String ENGAGEMENT = "Engagement";
    public static final String WEDDING = "Wedding";
    public static final String PREWEDDING = "Pre Wedding";
    public static final String MATERNITY = "Maternity";
    public static final String KIDS = "Kids";

    // Freelancer quote status
    public static final String F_SEND_BY_STUDIO = "1";
    public static final String F_ACCEPT_BY_FREELANCER = "2";
    public static final String F_PROCESS_FREELANCER = "3";
    public static final int F_DECLAIN_FREELANCER = 4;
    public static final int F_COMPLETE_FREELANCER = 5;

    // Stud quote status
    public static final int SEND_BY_ENDUSER = 1;
    public static final int ACCEPT_BY_STUDIO = 2;
    public static final int PROCESS_EVENT = 3;
    public static final int DECLAIN_EVENT = 4;
    public static final int COMPLETE_EVENT = 5;
    public static final int SEND_BY_STUDIO = 6;

    // RESULT CODE
    public static final int RESULT_DECLAIN_EVENT_BY_STUDIO = 11;
    public static final int RESULT_ACCEPT_EVENT_BY_STUDIO = 13;
    public static final int RESULT_SEND_EVENT_QUOTE = 12;

    public static final int RESULT_DECLAIN_FREELANCER_BY_STUDIO = 11;
    public static final int RESULT_ACCEPT_FREELANCER_BY_STUDIO = 12;

    public static final int RESULT_DECLAIN_QUOTE_BY_FREELANCER = 13;

}
