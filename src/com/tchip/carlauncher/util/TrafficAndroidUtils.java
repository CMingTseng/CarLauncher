package com.tchip.carlauncher.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/5/6
 * * * * * * * * * * * * * * * * * * * * * * *
 */
public class TrafficAndroidUtils {

        @SuppressWarnings("unchecked")
        public static <T extends View> T findViewById(Activity context, int id) {
                T view = null;
                View genericView = context.findViewById(id);
                try {
                        view = (T) (genericView);
                } catch (Exception ex) {
                        String message = "Can't cast view (" + id + ") to a " + view.getClass() + ".  Is actually a "
                                + genericView.getClass() + ".";
                        Log.e("PercolateAndroidUtils", message);
                        throw new ClassCastException(message);
                }

                return view;
        }

        @SuppressWarnings("unchecked")
        public static <T extends View> T findViewById(View parentView, int id) {
                T view = null;
                View genericView = parentView.findViewById(id);
                try {
                        view = (T) (genericView);
                } catch (Exception ex) {
                        String message = "Can't cast view (" + id + ") to a " + view.getClass() + ".  Is actually a "
                                + genericView.getClass() + ".";
                        Log.e("PercolateAndroidUtils", message);
                        throw new ClassCastException(message);
                }

                return view;
        }

        public static void startActivity(Context ctx, Class<?> cls) {
                startActivity(ctx, cls, null);
        }

        public static void startActivity(Context ctx, Class<?> cls, Bundle bundle) {
                Intent intent = new Intent();
                intent.setClass(ctx, cls);
                if (bundle != null) {
                        intent.putExtras(bundle);
                }
                ctx.startActivity(intent);
        }

        //        public static boolean hasPermission(Context ctx, String permission) {
        //                PackageManager pm = ctx.getPackageManager();
        //                return (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permission, ctx.getPackageName()));
        //        }
}
