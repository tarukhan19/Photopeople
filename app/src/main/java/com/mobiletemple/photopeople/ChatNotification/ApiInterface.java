package com.mobiletemple.photopeople.ChatNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAhkLp_08:APA91bHXcRpveFxh10gj9dcO2OhMaaMprGYLiZUrLRzkUDC76wBu_Eg409L4E1l3w9quAKBR4uM_7Z798sYYVwPv80W6_2HSsAOcla8ihI6DeTl1xiITwMTGlWOqumJQkH8fv26SXN1t"

            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
