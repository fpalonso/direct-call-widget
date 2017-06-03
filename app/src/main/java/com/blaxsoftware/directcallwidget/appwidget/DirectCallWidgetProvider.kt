package com.blaxsoftware.directcallwidget.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.text.TextUtils
import android.view.View
import android.widget.RemoteViews
import com.blaxsoftware.directcallwidget.Constants
import com.blaxsoftware.directcallwidget.Intents
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.WidgetClickReceiver
import com.blaxsoftware.directcallwidget.image.LoadImageTask
import java.io.File
import java.lang.IllegalArgumentException
import java.net.URI
import java.util.*

class DirectCallWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager,
                          appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        val picturesToRemove = ArrayList<URI>()
        val preferences = context.getSharedPreferences(Constants.SHAREDPREF_WIDGET,
                Context.MODE_PRIVATE)
        for (id in appWidgetIds) {
            val picUriStr = preferences.getString(Constants.SHAREDPREF_WIDGET_PHOTO_URL + id, null)
            if (picUriStr != null) {
                try {
                    picturesToRemove.add(URI.create(picUriStr))
                } catch(ignore: IllegalArgumentException) {
                }
            }
            preferences.edit()
                    .remove(Constants.SHAREDPREF_WIDGET_DISPLAY_NAME + id)
                    .remove(Constants.SHAREDPREF_WIDGET_PHONE + id)
                    .remove(Constants.SHAREDPREF_WIDGET_PHONE_TYPE + id)
                    .remove(Constants.SHAREDPREF_WIDGET_PHOTO_URL + id)
                    .apply()
        }
        if (!picturesToRemove.isEmpty()) {
            val picsToRemoveArray = picturesToRemove.toTypedArray<URI>()
            RemovePictureTask().execute(*picsToRemoveArray)
        }
    }

    private inner class RemovePictureTask : AsyncTask<URI, Void, Void>() {

        override fun doInBackground(vararg uris: URI): Void? {
            for (fileUri in uris) {
                val pic = File(fileUri)
                if (pic.exists()) {
                    pic.delete()
                }
            }
            return null
        }
    }

    companion object {

        internal val DEFAULT_WIDTH = 110
        internal val DEFAULT_HEIGHT = 110

        fun updateWidget(context: Context, widgetMngr: AppWidgetManager, widgetId: Int) {
            // Get widget data
            val pref = context.getSharedPreferences(Constants.SHAREDPREF_WIDGET,
                    Context.MODE_PRIVATE)
            val displayName = pref.getString(Constants.SHAREDPREF_WIDGET_DISPLAY_NAME + widgetId,
                    null)
            val phoneNumber = pref.getString(Constants.SHAREDPREF_WIDGET_PHONE + widgetId, null)

            val rViews = RemoteViews(context.packageName, R.layout.widget_2x2)
            rViews.setViewVisibility(R.id.defaultPicture, View.VISIBLE)
            rViews.setTextViewText(R.id.contactName, displayName)
            rViews.setViewVisibility(R.id.contactName,
                    if (!TextUtils.isEmpty(displayName)) View.VISIBLE else View.GONE)
            if (phoneNumber != null) {
                val callUri = Uri.parse("tel:" + Uri.encode(phoneNumber))
                val callIntent = Intent(Intents.ACTION_WIDGET_CLICK, callUri)
                callIntent.setClass(context, WidgetClickReceiver::class.java)
                val callPendingIntent = PendingIntent.getBroadcast(context, widgetId, callIntent, 0)
                rViews.setOnClickPendingIntent(R.id.widgetLayout, callPendingIntent)
            }
            updatePhoto(context, rViews, widgetMngr, widgetId, DEFAULT_WIDTH, DEFAULT_HEIGHT)
            widgetMngr.updateAppWidget(widgetId, rViews)
        }

        private fun updatePhoto(context: Context, remoteViews: RemoteViews,
                                appWidgetManager: AppWidgetManager, appWidgetId: Int, width: Int,
                                height: Int) {
            val pref = context.getSharedPreferences(Constants.SHAREDPREF_WIDGET,
                    Context.MODE_PRIVATE)
            val photoUriString = pref.getString(Constants.SHAREDPREF_WIDGET_PHOTO_URL + appWidgetId,
                    null)
            val photoUri = if (photoUriString != null) Uri.parse(photoUriString)
            else null

            // Update the remote views
            if (photoUri != null) {
                val imgLoader = LoadImageTask(context, width, height, false)
                imgLoader.setOnImageLoadedListener { _, bitmap ->
                    remoteViews.setViewVisibility(R.id.defaultPicture, View.INVISIBLE)
                    remoteViews.setImageViewBitmap(R.id.picture, bitmap)
                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
                }
                imgLoader.execute(photoUri)
            }
        }
    }
}
