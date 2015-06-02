package com.tchip.carlauncher.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/5/6
 * * * * * * * * * * * * * * * * * * * * * * *
 */
public class TrafficPreferencesUtils {

        private TrafficPreferencesUtils() {
        }

        private static WeakReference<PreferenceManager> wrPM;

        public static PreferenceManager getInstance() {
                if (wrPM == null
                        || wrPM.get() == null) {
                        wrPM = new WeakReference<PreferenceManager>(new PreferenceManager());
                }
                return wrPM.get();
        }

        public static class PreferenceManager {
                public static final String FILE_NAME = "kaikaimonitor";

                private PreferenceManager() {
                }

                public boolean isChecked(Context ctx, String tag) {
                        return getSP(ctx).getBoolean(tag, false);
                }

                public boolean setChecked(Context ctx, String tag, boolean value) {
                        return getSP(ctx).edit().putBoolean(tag, value).commit();
                }

                private SharedPreferences getSP(Context ctx) {
                        return ctx.getSharedPreferences(FILE_NAME, 0);
                }

        }
}
