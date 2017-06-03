package com.blaxsoftware.directcallwidget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager

class WidgetClickReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val tapAction = pref.getString(PREF_ONTAP_KEY, PREF_ONTAP_CALL_VALUE)
        when (tapAction) {
            PREF_ONTAP_DIAL_VALUE -> {
                val dialIntent = Intent(Intent.ACTION_DIAL, intent.data)
                dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(dialIntent)
            }
            PREF_ONTAP_CALL_VALUE -> {
                val callIntent = Intent(context, CallActivity::class.java)
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                callIntent.data = intent.data
                context.startActivity(callIntent)
            }
        }
    }

    companion object {
        val PREF_ONTAP_KEY = "pref_onTap"
        val PREF_ONTAP_DIAL_VALUE = "0"
        val PREF_ONTAP_CALL_VALUE = "1"
    }
}
