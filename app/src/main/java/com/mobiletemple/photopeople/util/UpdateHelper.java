package com.mobiletemple.photopeople.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class UpdateHelper {
    public static String KEY_UPDATE_ENABLE="is_update";
    public static String KEY_UPDATE_VERSION="version";
    public static String KEY_UPDATE_URL="update_url";

    public interface onUpdateCheckListener
    {
      void onUpdateCheckListener(String urlApp);
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    private Context context;
    private onUpdateCheckListener onUpdateCheckListener;


    public UpdateHelper(Context context, UpdateHelper.onUpdateCheckListener onUpdateCheckListener) {
        this.onUpdateCheckListener = onUpdateCheckListener;
        this.context = context;
    }

    public void check()
    {
        FirebaseRemoteConfig remoteConfig=FirebaseRemoteConfig.getInstance();
        if (remoteConfig.getBoolean(KEY_UPDATE_ENABLE))
        {
            String currentversion=remoteConfig.getString(KEY_UPDATE_VERSION);
            String appversion=getAppVersion(context);
            String updateUrl=remoteConfig.getString(KEY_UPDATE_URL);

            if (!TextUtils.equals(currentversion,appversion) && onUpdateCheckListener != null)
            {
                onUpdateCheckListener.onUpdateCheckListener(updateUrl);
            }
        }
    }

    private String getAppVersion(Context context)
    {
        String result="";

        try {
            result=context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
            result=result.replaceAll("[a-zA-Z]|-","");

        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static class Builder{
        private Context context;
        private onUpdateCheckListener onUpdateCheckListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateCheck(onUpdateCheckListener onUpdateCheckListener) {
            this.onUpdateCheckListener = onUpdateCheckListener;
            return this;
        }

        public UpdateHelper build()
        {
            return new UpdateHelper(context,onUpdateCheckListener);
        }

        public UpdateHelper check()
        {
            UpdateHelper updateHelper=build();
            updateHelper.check();
            return updateHelper;
        }
    }

}
