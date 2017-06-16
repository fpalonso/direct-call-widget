package com.blaxsoftware.directcallwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class WidgetClickReceiver extends BroadcastReceiver {

    public static final String PREF_ONTAP_KEY = "pref_onTap";
    public static final String PREF_ONTAP_DIAL_VALUE = "0";
    public static final String PREF_ONTAP_CALL_VALUE = "1";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String tapAction = pref.getString(PREF_ONTAP_KEY, PREF_ONTAP_CALL_VALUE);
        switch (tapAction) {
            case PREF_ONTAP_DIAL_VALUE:
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, intent.getData());
                dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dialIntent);
                break;
            case PREF_ONTAP_CALL_VALUE:
                Intent callIntent = new Intent(context, CallActivity.class);
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                callIntent.setData(intent.getData());
                context.startActivity(callIntent);
                break;
        }
    }
}
