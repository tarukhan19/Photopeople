package com.mobiletemple.photopeople.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Gourav Dodia on 27/8/17.
 */


//http://eventdesire.com/event/webservice/Registration/signup
public class Endpoints {
    private static final String TAG = Endpoints.class.getSimpleName();
    public static final String BASE_URL ="http://eventdesire.com/event/webservice/";
    public static final String CAMERA_RENTALS= BASE_URL+"Camera_rental/register";

    public static final String HOMEPAGE_INPIRATIONAL_PHOTO = BASE_URL+"Home/inspiretion_photo/";
    public static final String HOMEPAGE_PHOTOTIPS = BASE_URL+"Home/photo_tips/";
    public static final String HOMEPAGE_FEATUREFREE = BASE_URL+"Home/fetured_freelancer_home/";
    public static final String HOMEPAGE_HOTDEALS= BASE_URL+"Home/hot_deal";
//    public static final String HOMEPAGE_UPCOMINGEVENT= BASE_URL+"Home/studio_event/";
    public static final String HOMEPAGE_LOADADS= BASE_URL+"Home/advertisements/";

    public static final String VIEWALL_INPIRATIONAL_PHOTO = BASE_URL+"Home/inspiretion_photo_view_all/";
    public static final String VIEWALL_PHOTOTIPS = BASE_URL+"Home/photo_tips_view_all/";
    public static final String VIEWALL_FEATUREFREE = BASE_URL+"Home/fetured_freelancer_view_all/";
    public static final String VIEWALL_HOTDEALS= BASE_URL+"Home/hot_deal_view_all/";
    public static final String VIEWALL_UPCOMINGEVENT= BASE_URL+"Home/studio_event_view_all/";


    public static final String CATEGORY_LIST = BASE_URL+"Product/category_list";
    public static final String ADD_PRODUCT = BASE_URL+"Product/addproduct_api";
    public static final String MY_PRODUCT = BASE_URL+"Product/user_product";
    public static final String OLD_PRODUCT_LIST = BASE_URL+"Product/product_list";
    public static final String PRODUCT_DETAILS= BASE_URL+"Product/product_detail";
    public static final String PRODUCT_DELETE= BASE_URL+"Product/delete_product";
    public static final String NEW_PRODUCT_LIST= BASE_URL+"Product/new_product_list";
    public static final String NEW_PRODUCT_DETAILS= BASE_URL+"Product/new_product_detail";
    public static final String GENERATE_PRODUCID= BASE_URL+"Product/genret_product_qouta/";
    public static final String PRODUCT_PAYMENT= BASE_URL+"Product/product_payto_admin/";
    public static final String NEWPRODUCT_ORDER_HISTORY= BASE_URL+"Product/product_history";

    public static final String PRINTER_LIST= BASE_URL+"Printer/Printer_listing";
    public static final String PAPER_LIST= BASE_URL+"Printer/printer_id";
    public static final String ALBUMPRINTER_JOB= BASE_URL+"Printer/add_printer";
    public static final String PRINTER_PAYMENT= BASE_URL+"Printer/printer_payto_admin";
    public static final String PRINTER_ORDER_HISTORY= BASE_URL+"Printer/printer_history";

    public static final String OTP_URL = BASE_URL+"Registration/getOtp";
    public static final String VERIFY_OTP_URL = BASE_URL+"Registration/verify_otp";
    public static final String SIGNUP_URL = BASE_URL+"Registration/signup";
    public static final String LOGIN_URL = BASE_URL+"Login/login";
    public static final String CAMERA_LISTING = BASE_URL+"Registration/listing";
    //http://eventdesire.com/event/webservice/Login/login
    public static final String CHANGE_IMAGE =BASE_URL+ "Registration/update_profile_image";
    public static final String FREELANCER_PERSONAL_PROFILE_URL = BASE_URL+"Registration/freelancer_personal_profile";
    public static final String STUDIO_PERSONAL_PROFILE_URL = BASE_URL+"Registration/studio_personal_profile";
    public static final String FREELANCE_PERSONAL_DETAIL = BASE_URL+"Freelancer_details/single_freelancer";

    public static final String EXPERIENCE_PROFILE_URL = BASE_URL+"Registration/exp_profile";
    public static final String SOCIAL_PROFILE_URL = BASE_URL+"Registration/social_profile";

    public static final String GET_EVENTS_URL = "http://eventdesire.com/webservice/end_user/event_list";
    public static final String UPCOMING_EVENTS_URL = "http://eventdesire.com/webservice/Quote/eventlist_quote_notsend";
    public static final String GET_FREELANCER_URL = "http://eventdesire.com/webservice/Freelancer/freelancer_bytype";

    public static final String FORGOT_OTP_URL = BASE_URL+"Password/forgotgetOtp";
    public static final String FORGOT_CHANGE_PASSWORD_URL = BASE_URL+"Password/forgot_password";

    public static final String SEND_EVENT_QUOTE = "http://eventdesire.com/webservice/Quote/quote_studio_to_enduser";
    public static final String ACCEPT_EVENT_QUOTE = BASE_URL+"Quote/studio_accept_quote";

    public static final String SEND_ENDUSER_CHAT = "http://eventdesire.com/webservice/Chat/studio_to_enduser";
    public static final String LOAD_ENDUSER_CHAT = "http://eventdesire.com/webservice/Chat/chat_history_enduser_studio";

    public static final String SEND_FREELANCER_QUOTE =BASE_URL+ "quote/quote_studio_to_freelancer";
    public static final String ACCEPT_FREELANCER_QUOTE = BASE_URL+"Quote/freelancer_accept_quote";
    public static final String DECLAIN_FREELANCER_QUOTE = BASE_URL+"Quote/cancel_quote_by_freelancer";
    public static final String ACCEPT_QUOTE_BY_STUDIO = BASE_URL+"Quote/studio_accept_freelnacer_quote";
    public static final String DECLAIN_FREELANCER_BY_STUDIO = "http://eventdesire.com/webservice/Quote/cancel_quote_by_studio";
    public static final String Freelancer_Filter = BASE_URL+"Freelancer/freelancer_bytype_filter";
    public static final String Freelancer_camera_Filter = BASE_URL+"Freelancer/freelancer_serch_camera";
    public static final String FREE_IMAVID_DETAILS = BASE_URL+"Freelancer_details/exp_img_vid_get";

    public static final String DECLAIN_FREELANCER_QUOTE_BY_STUDIO = "http://eventdesire.com/webservice/Quote/cancel_quote_by_studio";
    public static final String DECLAIN_EVENT_QUOTE_BY_STUDIO = "http://eventdesire.com/webservice/Quote/studio_cancel_event_quote";

    public static final String WAITING_EVENT =BASE_URL+ "studio/waiting_list_event";
    public static final String PROCESS_EVENT = BASE_URL+"studio/inprogress_list_event";
    public static final String DECLAIN_EVENT = BASE_URL+"studio/cancel_list_event";
    public static final String COMPLETE_EVENT =BASE_URL+ "studio/complete_list_event";

    public static final String WAITING_INBOX =  BASE_URL+"freelancer/waiting_list";
    public static final String PROCESS_INBOX =BASE_URL+ "freelancer/process_list2";
    public static final String DECLAIN_INBOX = BASE_URL+"freelancer/decline_list2";
    public static final String COMPLETE_INBOX = BASE_URL+"freelancer/completed_list2";

    public static final String WAITING_FREELANCER_JOB = BASE_URL+"freelancer/studio_list_quote_waiting2";
    public static final String PROCESS_FREELANCER_JOB = BASE_URL+"freelancer/studio_list_quote_process2";
    public static final String DECLAIN_FREELANCER_JOB = BASE_URL+"freelancer/studio_list_quote_cancel2";
    public static final String COMPLETE_FREELANCER_JOB =BASE_URL+ "freelancer/studio_list_quote_completed2";

    public static final String SEND_FREELANCER_CHAT = BASE_URL+"Chat/chat_studio_freelancer";
    public static final String SEND_STUDIO_CHAT = BASE_URL+"Chat/chat_freelancer_to_studio";
    public static final String LOAD_FREELANCER_CHAT = BASE_URL+"Chat/chat_history_freelancer_studio";
    public static final String LOAD_STUDIO_CHAT = BASE_URL+"Chat/chat_history_studio_freelancer";

    public static final String GET_PAYMENT_STUDIO = "http://eventdesire.com/webservice/Payment/studio_payment_history";
    public static final String GET_PAYMENT_FREELANCER = "http://eventdesire.com/webservice/Payment/freelancer_payment_history";

    public static final String CHANGE_PASSWORD = BASE_URL+"Password/change_password";

    public static final String GET_FREELANCER = "http://eventdesire.com/webservice/Freelancer/single_freelancer";

    public static final String ADD_PAYMENT_DETAIL = BASE_URL+"Registration/add_bank_detail";

    public static final String LOGOUT = BASE_URL+ "Login/logout";
    public static final String CALENDAR_LOAD =BASE_URL+ "Calendar/freelancer_calender_list";
    public static final String CALENDAR_CANCEL = BASE_URL+"Calendar/Cancel_Calender";
    public static final String CALENDAR_SET = BASE_URL+"Calendar/set_calendar";

    public static final String NOTI_STUDIO_ENDUSER = BASE_URL+"Quote/studio_to_enduser_by_quoteid";
    public static final String READ_NOTI = "http://eventdesire.com/webservice/Notification/message_read";
    public static final String WALLET_FREE = BASE_URL+"Wallet/wallet";
    public static final String WALLET_STUD = BASE_URL+"Payment/studio_wallet_amount";
    public static final String LOAD_FREELANCER_PROFILE = BASE_URL+"Freelancer/single_freelancer";
    public static final String LOAD_STUDIO_PROFILE = BASE_URL+"studio/single_studio";
    public static final String BANK_DETAIL = BASE_URL+"end_user/bank_detail";

    public static final String PAYMENT_STUDIO_TO_FREE = BASE_URL+"Payment/studio_payto_freelancer";
    public static final String CONTACT_US = BASE_URL+"Contact/contact_us";

    public static final String STUDIO_VIDIMAGE= BASE_URL+"Studio_details/exp_img_vid_get";

    public static final String SENDCHAT= BASE_URL+"Chate/send_msg";
    public static final String LOADCHAT= BASE_URL+"Chate/chat_load";

    public static final String LOADEQUIPMENTS= BASE_URL+"Freelancer_profile_first/profile_step1";
    public static final String UPDATE_FREELANCER_PERSONAL_PROFILE= BASE_URL+"Freelancer_profile_first/freelancer_Submit";
    public static final String UPDATE_FREELANCER_EQUIPMENTS= BASE_URL+"Freelancer_profile_first/equipment_data_Submit";

    public static final String TERMANDCONDITION= "http://www.eventdesire.com/event/web/privacy_policy";

    public static final String DELETEPIC= BASE_URL+"Freelancer_details/delete_exp_image";
    public static final String DELETEVIDEO= BASE_URL+"Freelancer_details/delete_exp_video";

    public static final String RATING =BASE_URL+ "Review_reting";
    public static final String RATING_LIST =BASE_URL+ "Review_reting/get_review_reting";


    public static final String COMPOSE_POST=BASE_URL+"Post/add_post";
    public static final String TIMELINEPOST_LIST=BASE_URL+"Post/post_list";
    public static final String TIMELINEPOST_LIKE=BASE_URL+"Post/post_like";
    public static final String TIMELINEPOST_DELETE=BASE_URL+"Post/delete_post";
    public static final String TIMELINEPOST_REPORTABUSE=BASE_URL+"Post/report_abuse";
    public static final String TRENDING_TIMELINE=BASE_URL+"Post/trending_post";
    public static final String STUDIO_NOTIFICATION_LIST =BASE_URL+ "notification/all_notification_history_studio";
    public static final String FREELANCER_NOTIFICATION_LIST =BASE_URL+ "notification/all_notification_history_freelancer";
    public static final String ONE_TIME_FEE_ACTIVATION =BASE_URL+ "registration/user_payment_status";
    public static final String ONE_TIME_FEE_ENTRY =BASE_URL+ "payment/do_payment";
    public static final String CAMERARENTAL_LIST =BASE_URL+ "Camera_rental/camera_rental_list";


    public static final String PROMOCODE_FREELANCER =BASE_URL+ "Product/promo_code_freelancer";
    //parameters: promo_code, user_type , user_id ,freelancer_id
    public static final String PROMOCODE_PRODUCT =BASE_URL+ "Product/promo_code_product";
    // parameters: promo_code ,user_type, user_id , product_id
    public static final String PROMOCODE_ALBUMPRINT =BASE_URL+ "Product/promo_code_album_print";
    //parameters : promo_code, user_type , user_id , price , printer_id


    public String forProdPost(String urlString,  byte[] profilepic)
    {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";
        String filename=Long.toString(System.currentTimeMillis());
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        ////Log.e("videolist1",videoList+"");
        ////Log.e("photoArray1",photoArray+"");

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            System.setProperty("http.keepAlive", "false");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            if (profilepic != null) {
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"profile_image\"; filename=\"image.png\"" + lineEnd);
                //outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

                Log.e("profilepic",profilepic+"");

                outputStream.writeBytes(lineEnd);

                for (byte b : profilepic) {
                    outputStream.write(b);
                }

                outputStream.writeBytes(lineEnd);
            }




//            // Upload POST Data
//            Iterator<String> keys = params.keys();
//            while (keys.hasNext()) {
//                String key = keys.next();
//                String value = params.get(key).toString();
//
//                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
//                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
//                outputStream.writeBytes(lineEnd);
//                outputStream.writeBytes(value);
//                outputStream.writeBytes(lineEnd);
//            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("REg Error ", e.getMessage());
        }
        return result;
    }




    public String forPost(String urlString, JSONObject params, byte profilepic[] ) throws Exception
    {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";
        String filename=Long.toString(System.currentTimeMillis());
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        ////Log.e("videolist1",videoList+"");
        ////Log.e("photoArray1",photoArray+"");

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            System.setProperty("http.keepAlive", "false");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            if (profilepic != null) {
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"image.png\"" + lineEnd);
                //outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

                outputStream.writeBytes(lineEnd);

                for (byte b : profilepic) {
                    outputStream.write(b);
                }

                outputStream.writeBytes(lineEnd);
            }

            // Upload POST Data
            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key).toString();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            ////Log.e("REg Error ", e.getMessage());
        }
        return result;
    }


    public String getStringResponse(String urlString) throws Exception {
        String result = "";

        URL url = new URL(urlString);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

        con.connect();
        if (con.getResponseCode() == 200) //OK
        {
            InputStream is = con.getInputStream();
            //read one byte at a time
            while (true) {
                int data = is.read();
                if (data == -1)
                    break;
                else
                    result = result + (char) data;
            }
        } else {
            throw new RuntimeException("Server Not Respond; Response Code : " + con.getResponseCode());
        }
        con.disconnect();

        return result;
    }

    public String getStringResponse(String urlString, JSONObject params) throws Exception {
        String result = "";

        URL url = new URL(urlString);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        //   con.setReadTimeout(10000);
        //   con.setConnectTimeout(15000);
        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        //   con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.connect();

        if (params != null) {
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            writer.write(getParameterString(params));
            writer.close();
        }

        if (con.getResponseCode() == 200) //OK
        {
            InputStream is = con.getInputStream();

            StringBuilder buffer = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

            } finally {
                is.close();
                reader.close();
            }

            result = buffer.toString();

        } else {
            //Log.e(TAG, "Server Not Respond; Response Code : " + con.getResponseCode());
        }
        con.disconnect();

        return result;
    }

    // For Experience Profile upload
    public String forSignUp(String urlString, JSONObject params, byte profilepic[]) throws Exception {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());

            if (profilepic != null) {
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"profilepic\"; filename=\"profilepic.png\"" + lineEnd);
                //outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

                outputStream.writeBytes(lineEnd);

                for (byte b : profilepic) {
                    outputStream.write(b);
                }

                outputStream.writeBytes(lineEnd);
            }

            // Upload POST Data
            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key).toString();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            //Log.e("REg Error ", e.getMessage());
        }
        return result;
    }
    // For Experience Profile upload ENDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD

    public String forExperience(String urlString, JSONObject params, ArrayList<String> videoList, byte[][] photoArray) throws Exception
    {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";
        String filename=Long.toString(System.currentTimeMillis());
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        //Log.e("videolist1",videoList+"");
        //Log.e("photoArray1",photoArray+"");

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            System.setProperty("http.keepAlive", "false");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());

            for (String arr : videoList)
            {

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"video[]" +  "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(arr);
                outputStream.writeBytes(lineEnd);


            }

            if (photoArray != null)
            {
                for (byte arr[] : photoArray) {
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"ph[]" +  "\"; filename=\"ph" + filename + ".jpg\"" + lineEnd);
                    outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                    outputStream.writeBytes(lineEnd);

                    Log.e("photoarray",photoArray+" ");

                    for (byte b : arr) {
                        outputStream.write(b);
                    }
                    outputStream.writeBytes(lineEnd);
                }
            }

            // Upload POST Data
            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key).toString();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            //Log.e("REg Error ", e.getMessage());
        }
        return result;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public Bitmap getBitmap(String urlString) throws Exception {
        Bitmap bm = null;

        URL url = new URL(urlString);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

        con.connect();
        if (con.getResponseCode() == 200) {
            InputStream is = con.getInputStream();
            bm = BitmapFactory.decodeStream(is);
        } else {
            throw new RuntimeException("Server Not Respond; Response Code : " + con.getResponseCode());
        }
        con.disconnect();

        return bm;
    }

    private String getParameterString(JSONObject obj) throws JSONException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (first)
                first = false;
            else
                result.append("&");

            result.append(key);
            result.append("=");
            result.append(obj.getString(key));
        }

        return result.toString();
    }

    public String forImageChange(String urlString, JSONObject params, byte profilepic[]) throws Exception {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());

            if (profilepic != null) {
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"profilepic\"; filename=\"profilepic.png\"" + lineEnd);
                //outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

                outputStream.writeBytes(lineEnd);

                for (byte b : profilepic) {
                    outputStream.write(b);
                    /////Log.e("WEB_ profilePicbyte",">>>>"+ b);

                }

                outputStream.writeBytes(lineEnd);
            }

            // Upload POST Data
            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key).toString();
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);

                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            //Log.e("REg Error ", e.getMessage());
        }
        return result;
    }




    public String forFreelancerPersonal(String freelancerPersonalProfileUrl, JSONObject ob)
    {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";
        String filename=Long.toString(System.currentTimeMillis());
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;


        try {
            URL url = new URL(freelancerPersonalProfileUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());


            // Upload POST Data
            Iterator<String> keys = ob.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = ob.get(key).toString();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            //Log.e("REg Error ", e.getMessage());
        }
        return result;

    }

    public String forUpdateFreelancerEquipments(String freelancerPersonalProfileUrl, JSONObject ob, ArrayList<String> lenslistsentrray, ArrayList<String> lightningtsentrray, ArrayList<String> supportsentrray)
    {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";
        String filename=Long.toString(System.currentTimeMillis());
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;


        try {
            URL url = new URL(freelancerPersonalProfileUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            for (String arr : lenslistsentrray)
            {

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"lence_id[]" +  "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(arr);
                outputStream.writeBytes(lineEnd);


            }

            for (String arr : lightningtsentrray)
            {

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"lighting_id[]" +  "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(arr);
                outputStream.writeBytes(lineEnd);


            }

            for (String arr : supportsentrray)
            {

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"suport_id[]" +  "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(arr);
                outputStream.writeBytes(lineEnd);


            }

            // Upload POST Data
            Iterator<String> keys = ob.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = ob.get(key).toString();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            //Log.e("REg Error ", e.getMessage());
        }
        return result;

    }




    public String forFreelancerStepOne(String freelancerPersonalProfileUrl, JSONObject ob, ArrayList<String> lenslistsentrray, ArrayList<String> lightningtsentrray, ArrayList<String> supportsentrray)
    {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";
        String filename=Long.toString(System.currentTimeMillis());
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;


        try {
            URL url = new URL(freelancerPersonalProfileUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());

            for (String arr : lenslistsentrray)
            {

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"lence_id[]" +  "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(arr);
                outputStream.writeBytes(lineEnd);


            }

            for (String arr : lightningtsentrray)
            {

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"light_id[]" +  "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(arr);
                outputStream.writeBytes(lineEnd);


            }

            for (String arr : supportsentrray)
            {

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"suport_id[]" +  "\"" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(arr);
                outputStream.writeBytes(lineEnd);


            }

            // Upload POST Data
            Iterator<String> keys = ob.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = ob.get(key).toString();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            //Log.e("REg Error ", e.getMessage());
        }
        return result;

    }

    public String forProductPost(String addProduct, JSONObject params, byte[][] photoArray)
    {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try
        {
            URL url = new URL(addProduct);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());



            if (photoArray != null) {
                int index = 1;
                for (byte arr[] : photoArray) {
                    //Log.e("arr",""+arr);
                    String filename=Long.toString(System.currentTimeMillis());
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"product_image[]" +  "\"; filename=\"ph" + filename + ".jpg\"" + lineEnd);
                    outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                    outputStream.writeBytes(lineEnd);

                    for (byte b : arr) {
                        outputStream.write(b);
                    }
                    outputStream.writeBytes(lineEnd);
                    index++;
                }
            }

            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key).toString();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();


            return result;
        } catch (Exception e) {
            //Log.e("REg Error ", e.getMessage());
        }
        return result;
    }
}
