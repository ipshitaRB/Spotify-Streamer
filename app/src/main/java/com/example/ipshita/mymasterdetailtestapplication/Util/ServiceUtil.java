package com.example.ipshita.mymasterdetailtestapplication.Util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by iroyb_000 on 10-09-2015.
 */
public class ServiceUtil {
    public static boolean isServiceRunning(Class<?> serviceClass, Activity activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
