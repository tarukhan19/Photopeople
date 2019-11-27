package com.mobiletemple.photopeople;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.mobiletemple.photopeople.userauth.OtpActivity;

public class IncomingSms extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null)
            {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj .length; i++)
                {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[])                                                                                                    pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber ;
                    String message = currentMessage .getDisplayMessageBody();
                    //Log.e("message1",message);
                    message= message.replaceAll("[^\\d]", "");
                    //Log.e("message2",message);

                    message= message.replaceAll("[\\s]","");
                    //Log.e("message3",message);

                    try
                    {
                        if (senderNum .equals("VK-PHPEOP"))
                        {
                            OtpActivity Sms = new OtpActivity();
                           // Sms.recivedSms(message );
                        }
                    }
                    catch(Exception e){}

                }
            }

        } catch (Exception e)
        {

        }
    }

}