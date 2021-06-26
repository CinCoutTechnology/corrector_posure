package com.example.posture_corrector.view.viewConnect;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;

import com.example.posture_corrector.view.utilities.preferencesU;

public class ActivityHelper {

    public static void initialize(Activity activity) {
        @SuppressWarnings("deprecation") SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        String orientation = mPreferences.getString(preferencesU.prefOrientation, "Null");

        if (preferencesU.Landscape.equals(orientation)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (preferencesU.Portrait.equals(orientation)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }
}
