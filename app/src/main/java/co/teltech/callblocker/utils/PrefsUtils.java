package co.teltech.callblocker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import co.teltech.callblocker.constants.PrefsKeys;

/**
 * Created by tomislavtusek on 16/08/2018.
 */

public class PrefsUtils {

    private static final String SHARED_PREFS_FILENAME = "callblocker-shared-prefs";

    public static void putBoolean(Context context, String key, boolean value) {
        getSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key) {
        return getSharedPreferences(context).getBoolean(key, false);
    }

    public static void putString(Context context, String key, String value) {
        getSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key) {
        return getSharedPreferences(context).getString(key, "");
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILENAME, Context.MODE_PRIVATE);
        return prefs;
    }

    public static String getSelectedFilterType(Context context) {
        String selectedFilterType = PrefsUtils.getString(context, PrefsKeys.BLOCK_CALLS_TYPE);
        if (selectedFilterType.equalsIgnoreCase("")) {
            selectedFilterType = PrefsKeys.BLOCK_CALLS_TYPE_SPAM_ONLY;
        }
        return selectedFilterType;
    }

}
