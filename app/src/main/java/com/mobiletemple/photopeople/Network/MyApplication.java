package com.mobiletemple.photopeople.Network;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.mobiletemple.photopeople.util.UpdateHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate()
    {
        super.onCreate();

//        final FirebaseRemoteConfig remoteConfig=FirebaseRemoteConfig.getInstance();
//
//        Map<String,Object> defaultvalue=new HashMap<>();
//        defaultvalue.put(UpdateHelper.KEY_UPDATE_ENABLE,false);
//        defaultvalue.put(UpdateHelper.KEY_UPDATE_VERSION,"17.8");
//        defaultvalue.put(UpdateHelper.KEY_UPDATE_URL,"https://play.google.com/store/apps/details?id=com.mobiletemple.photopeople&hl=en");
//
//        remoteConfig.setDefaults(defaultvalue);
//        remoteConfig.fetch(1000).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//            if (task.isSuccessful())
//            {
//                remoteConfig.activateFetched();
//
//            }
//            }
//        });

        initImageLoader(getApplicationContext());

        mInstance = this;
    }

    //Initiate Image Loader Configuration
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
                context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());

    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
