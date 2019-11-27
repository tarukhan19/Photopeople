package com.mobiletemple.photopeople.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobiletemple.photopeople.BottomNavigation.HomePage;
import com.mobiletemple.photopeople.Chat.ChatActivity;
import com.mobiletemple.photopeople.ChatNotification.OreoNotification;
import com.mobiletemple.photopeople.InboxActivity;
import com.mobiletemple.photopeople.MyJobActivity;
import com.mobiletemple.photopeople.R;

import com.mobiletemple.photopeople.TimeLine.TimelineChatActivity;
import com.mobiletemple.photopeople.constant.Constants;
import com.mobiletemple.photopeople.session.SessionManager;
import com.mobiletemple.photopeople.userauth.LoginActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    int MESSAGE_NOTIFICATION_ID = 0;
    Intent intent;
    String message, pushType;
    int studioID, freelancerId, quote_id;
    private SharedPreferences sp;
    NotificationCompat.Builder mBuilder;
    SessionManager sessionManager;
    String countS;
    private static final int BADGE_ICON_SMALL = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sessionManager = new SessionManager(getApplicationContext());

        Log.e("remotemsg",remoteMessage.getData().toString());

        pushType = remoteMessage.getData().get("type");

        if (pushType.equalsIgnoreCase("Text")) {
            String imageurl = remoteMessage.getData().get("picture");
            Bitmap bitmap = getBitmapFromURL(imageurl);

            message = remoteMessage.getData().get("message").replace("\"", "");
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));

            sendImageNotification(getResources().getString(R.string.app_name), message, bitmap);
        }

        else if (pushType.equalsIgnoreCase("chat"))
        {
            String sented=remoteMessage.getData().get("sented");
            String user=remoteMessage.getData().get("user");

            SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
            String currentUser=preferences.getString("currentuser","none");

            if (sented.equals(sessionManager.getLoginSession().get(SessionManager.KEY_USERID)))
            {
                if (!currentUser.equals(user)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        sendOreoNotification(remoteMessage,pushType,"jobchatapp");
                    } else {
                        sendChatNotification(remoteMessage,pushType);
                    }
                }
            }
        }


        else if (pushType.equalsIgnoreCase("timelinechat"))
        {
            String sented=remoteMessage.getData().get("sented");
            String user=remoteMessage.getData().get("user");

            SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
            String currentUser=preferences.getString("currentuser","none");

            FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
            if (sented.equals(sessionManager.getLoginSession().get(SessionManager.KEY_USERID)))
            {
                if (!currentUser.equals(user)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        sendOreoNotification(remoteMessage,pushType,"timelinechatapp");
                    } else {
                        sendChatNotification(remoteMessage,pushType);
                    }
                }
            }
        }

        else if (pushType.equalsIgnoreCase("quote_studio_to_freelancer"))
        {
            message = remoteMessage.getData().get("message").replace("\"", "");
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));

            sp = getSharedPreferences("photo", MODE_PRIVATE);
            try {
                HomePage.getInstance().runThread(Integer.parseInt(countS));
            } catch (NullPointerException e) {
                e.getLocalizedMessage();
            }



            sendNotification();
        }

        else if (pushType.equalsIgnoreCase("freelancer_accept_quote")) {

            message = remoteMessage.getData().get("message").replace("\"", "");
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));

            sp = getSharedPreferences("photo", MODE_PRIVATE);
            try {
                HomePage.getInstance().runThread(Integer.parseInt(countS));
            } catch (NullPointerException e) {
                e.getLocalizedMessage();
            }

            sendNotification();
        } else if (pushType.equalsIgnoreCase("cancel_quote_by_freelancerr")) {
            message = remoteMessage.getData().get("message").replace("\"", "");
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));

            sp = getSharedPreferences("photo", MODE_PRIVATE);
            try {
                HomePage.getInstance().runThread(Integer.parseInt(countS));
            } catch (NullPointerException e) {
                e.getLocalizedMessage();
            }


            sendNotification();
        } else if (pushType.equalsIgnoreCase("chat_studio_freelancer")) {
            message = remoteMessage.getData().get("message").replace("\"", "");
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));


            sp = getSharedPreferences("photo", MODE_PRIVATE);
            try {
                HomePage.getInstance().runThread(Integer.parseInt(countS));
            } catch (NullPointerException e) {
                e.getLocalizedMessage();
            }

            sendNotification();

        } else if (pushType.equalsIgnoreCase("chat_freelancer_to_studio")) {
            message = remoteMessage.getData().get("message").replace("\"", "");
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));


            sp = getSharedPreferences("photo", MODE_PRIVATE);
            try {
                HomePage.getInstance().runThread(Integer.parseInt(countS));
            } catch (NullPointerException e) {
                e.getLocalizedMessage();
            }


            sendNotification();
        } else if (pushType.equalsIgnoreCase("studio_payto_freelancer")) {
            message = remoteMessage.getData().get("message").replace("\"", "");
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));

            sp = getSharedPreferences("photo", MODE_PRIVATE);
            try {
                HomePage.getInstance().runThread(Integer.parseInt(countS));
            } catch (NullPointerException e) {
                e.getLocalizedMessage();
            }


            sendNotification();
        } else if (pushType.equalsIgnoreCase("studio_payto_freelancer1")) {
            message = remoteMessage.getData().get("message").replace("\"", "");
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));

            sp = getSharedPreferences("photo", MODE_PRIVATE);
            try {
                HomePage.getInstance().runThread(Integer.parseInt(countS));
            } catch (NullPointerException e) {
                e.getLocalizedMessage();
            }



            sendNotification();
        }

        else if (pushType.equalsIgnoreCase("post_like")) {
            message = remoteMessage.getData().get("message").replace("\"", "");
            countS = remoteMessage.getData().get("badge_count");
            sessionManager.setNotificationCount(Integer.parseInt(countS));

            sp = getSharedPreferences("photo", MODE_PRIVATE);
            try {
                HomePage.getInstance().runThread(Integer.parseInt(countS));
            } catch (NullPointerException e) {
                e.getLocalizedMessage();
            }



            sendNotification();
        }

    }

    public Bitmap getBitmapFromURL(String strURL)
    {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendImageNotification(String title, String message, Bitmap bitmap)
    {
        Context context = getBaseContext();
        String id = "my_channel_01";
        MESSAGE_NOTIFICATION_ID = (int) (System.currentTimeMillis() & 0xfffffff);

        sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.isLoggedIn()) {
            intent = new Intent(this, HomePage.class);
            intent.putExtra("from", "other");

        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        MESSAGE_NOTIFICATION_ID,
                        intent, MESSAGE_NOTIFICATION_ID);


        ///////////////////////////////////////////////////////////////////////////////////

        mBuilder = new NotificationCompat.Builder(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        }


        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Photo People")
                .setContentText(message)
                .setContentIntent(resultPendingIntent)
                // .setBadgeIconType(BADGE_ICON_SMALL)
                // .setNumber(Integer.parseInt(countS))
                .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
                .setChannelId(id);

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentText(message)
                .build();

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);//
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                mNotificationManager.createNotificationChannel(mChannel);
                mChannel.setShowBadge(true);

            }
        }
        mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, mBuilder.build());

        /////////////////////////////////////////////////////////////////////////////////////////


    }

    private void sendNotification() {
        Context context = getBaseContext();

        if (pushType.equalsIgnoreCase("quote_studio_to_freelancer"))
        {
            if (!sessionManager.isLoggedIn())
            {intent = new Intent(context, LoginActivity.class); }
            else
            {intent = new Intent(context, MyJobActivity.class).putExtra("tabIndex", 0);}
        }
        else if (pushType.equalsIgnoreCase("freelancer_accept_quote"))
        {
            if (!sessionManager.isLoggedIn())
            {intent = new Intent(context, LoginActivity.class); }
            else
            {intent = new Intent(context, InboxActivity.class).putExtra("tabIndex", 0);}
        } else if (pushType.equalsIgnoreCase("cancel_quote_by_freelancerr")) {
            if (!sessionManager.isLoggedIn())
            {intent = new Intent(context, LoginActivity.class); }
            else
            {intent = new Intent(context, InboxActivity.class).putExtra("tabIndex", 2);}
        } else if (pushType.equalsIgnoreCase("chat_studio_freelancer")) {
            if (!sessionManager.isLoggedIn())
            {intent = new Intent(context, LoginActivity.class); }
            else {
                intent = new Intent(context, ChatActivity.class);
                intent.putExtra("quoteId", quote_id);
                intent.putExtra("freelancerId", freelancerId);
                intent.putExtra("studioID", studioID);
                intent.putExtra("sender", Constants.STUDIO_TYPE);
            }
        } else if (pushType.equalsIgnoreCase("chat_freelancer_to_studio")) {
            if (!sessionManager.isLoggedIn())
            {intent = new Intent(context, LoginActivity.class); }
            else
            {
            intent = new Intent(context, ChatActivity.class);
            intent.putExtra("quoteId", quote_id);
            intent.putExtra("freelancerId", freelancerId);
            intent.putExtra("studioID", studioID);
            intent.putExtra("sender", Constants.FREELANCER_TYPE);}
        } else if (pushType.equalsIgnoreCase("studio_payto_freelancer")) {
            if (!sessionManager.isLoggedIn())
            {intent = new Intent(context, LoginActivity.class); }
            else
            {
            intent = new Intent(context, MyJobActivity.class);}
        } else if (pushType.equalsIgnoreCase("studio_payto_freelancer1")) {
            if (!sessionManager.isLoggedIn())
            {intent = new Intent(context, LoginActivity.class); }
            else
            { intent = new Intent(context, MyJobActivity.class);}
        }

        else if (pushType.equalsIgnoreCase("post_like")) {
            if (!sessionManager.isLoggedIn())
            {intent = new Intent(context, LoginActivity.class); }
            else
            { intent = new Intent(context, HomePage.class);
            intent.putExtra("from","timeline");}
        }


        String id = "my_channel_01";


        MESSAGE_NOTIFICATION_ID = (int) (System.currentTimeMillis() & 0xfffffff);

        PendingIntent pIntent = PendingIntent.getActivity(context, MESSAGE_NOTIFICATION_ID, intent, MESSAGE_NOTIFICATION_ID);

        mBuilder = new NotificationCompat.Builder(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        }


        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Photo People")
                .setContentText(message)
                .setContentIntent(pIntent)
                 .setBadgeIconType(BADGE_ICON_SMALL)
                 .setNumber(Integer.parseInt(countS))
                .setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true)
                .setChannelId(id);


        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);//
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                mNotificationManager.createNotificationChannel(mChannel);
                mChannel.setShowBadge(true);

            }
        }
        mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }







    private void sendOreoNotification(RemoteMessage remoteMessage, String pushType, String chattype)
    {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        String quoteid=remoteMessage.getData().get("quoteId");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int j= Integer.parseInt(user.replaceAll("[\\D]",""));
        if (pushType.equalsIgnoreCase("chat")) {
             intent = new Intent(this, ChatActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("RECIVERid",user);
            bundle.putString("quoteId",quoteid);
            bundle.putString("from","notification");

            intent.putExtras(bundle);
        }
        else
        {
             intent = new Intent(this, TimelineChatActivity.class);
            Bundle bundle=new Bundle();

            bundle.putString("userid",user);
            bundle.putString("from","other");
            intent.putExtras(bundle);

        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultsount=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoNotification oreoNotification=new OreoNotification(this,chattype);
        Notification.Builder builder=oreoNotification.getOreoNotification(title,body,
                pendingIntent,defaultsount,icon);
        int i=0;
        if (j>0)
        {
            i=j;
        }

        oreoNotification.getNotificationManager().notify(i,builder.build());


    }

    private void sendChatNotification(RemoteMessage remoteMessage, String pushType)
    {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        String quoteid=remoteMessage.getData().get("quoteId");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int j= Integer.parseInt(user.replaceAll("[\\D]",""));
        if (pushType.equalsIgnoreCase("chat")) {
            intent = new Intent(this, ChatActivity.class);
            Bundle bundle=new Bundle();

            bundle.putString("RECIVERid",user);
            bundle.putString("quoteId",quoteid);
            bundle.putString("from","notification");
            intent.putExtras(bundle);

        }
        else
        {
            intent = new Intent(this, TimelineChatActivity.class);
            Bundle bundle=new Bundle();

            bundle.putString("userid",user);
            bundle.putString("from","other");
            intent.putExtras(bundle);

        }


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultsount=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder =new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultsount)

                .setContentIntent(pendingIntent);

        NotificationManager noti= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int i=0;
        if (j>0)
        {
            i=j;
        }

        assert noti != null;
        noti.notify(i,builder.build());

    }
}