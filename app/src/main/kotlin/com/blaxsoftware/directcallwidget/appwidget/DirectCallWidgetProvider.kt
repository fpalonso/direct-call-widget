/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2020 Fer P. A.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.blaxsoftware.directcallwidget.appwidget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.blaxsoftware.directcallwidget.*
import com.blaxsoftware.directcallwidget.data.model.WidgetData
import com.blaxsoftware.directcallwidget.ui.xdpToPx
import com.blaxsoftware.directcallwidget.ui.ydpToPx
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.AppWidgetTarget
import com.google.firebase.crashlytics.FirebaseCrashlytics

open class DirectCallWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager,
                          appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds.forEach { id ->
            updateWidget(context, appWidgetManager, id)
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager,
                                           appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateWidget(context, appWidgetManager, appWidgetId)
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, id: Int) {
        context.widgetRepository.getWidgetDataById(id)?.let { widgetData ->
            setWidgetData(context, appWidgetManager, id, widgetData)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds.forEach { id ->
            context.widgetRepository.getWidgetDataById(id)?.let { widgetData ->
                widgetData.pictureUri?.let { uriStr -> Uri.parse(uriStr) }?.let { uri ->
                    context.widgetPicRepository.delete(uri)
                }
                context.widgetRepository.deleteWidgetDataById(id)
            }
        }
    }

    companion object {

        fun setWidgetData(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int,
                          widgetData: WidgetData) {
            FirebaseCrashlytics.getInstance().log("setWidgetData: widgetId=$widgetId")
            RemoteViews(context.packageName, R.layout.widget_2x2).apply {
                setViewVisibility(R.id.picture, View.VISIBLE)
                setTextViewText(R.id.contactName, widgetData.displayName)
                setViewVisibility(R.id.contactName,
                        if (widgetData.hasDisplayName) View.VISIBLE else View.INVISIBLE)
                setViewVisibility(R.id.placeholder,
                        if (widgetData.hasPicture) View.INVISIBLE else View.VISIBLE)
                setOnClickPendingIntent(R.id.widgetLayout,
                        callContactIntent(context, widgetData.phoneNumber, widgetId, widgetData.selectedApp, widgetData.contactId))
            }.also {
                if (widgetData.hasPicture) {
                    setWidgetDataWithPic(context, appWidgetManager, it, widgetId)
                } else {
                    appWidgetManager.updateAppWidget(widgetId, it)
                }
            }
        }

//        @RequiresApi(Build.VERSION_CODES.M)
        private fun callContactIntent(context: Context, phoneNumber: String,
                                      widgetId: Int,
                                      desiredApp: String?,
                                      contactId: String?
): PendingIntent {
//            val callUri = Uri.parse("tel:" + Uri.encode(phoneNumber))
            val callIntent = Intent()
//            val callIntent = Intent(Intents.ACTION_WIDGET_CLICK, callUri)
    when (desiredApp){
        "com.whatsapp" -> {
            callIntent.action = Intents.ACTION_WIDGET_CLICK;
            Log.d("callMode", "WhatsApp")
//            callIntent.setPackage("com.whatsapp")
            if(contactId != null){
                val data = "$contactId"
                val type = "vnd.android.cursor.item/vnd.com.whatsapp.profile"
                callIntent.action = Intent.ACTION_VIEW
                callIntent.setDataAndType(Uri.parse(hasWhatsapp(contactId, context)), type)
                callIntent.setPackage("com.whatsapp")
            }else {
                callIntent.action = Intent.ACTION_CALL
                callIntent.data = Uri.parse("tel:" + Uri.encode(phoneNumber))
            }
        }
        else -> {
            callIntent.action = Intents.ACTION_WIDGET_CLICK;
            Log.d("callMode", "Dialer")
            callIntent.data = Uri.parse("tel:" + Uri.encode(phoneNumber))
        }
    }

            callIntent.setClass(context, WidgetClickReceiver::class.java)
            return PendingIntent.getBroadcast(context, widgetId, callIntent,
               if  (Build.VERSION.SDK_INT >= 23)
                FLAG_IMMUTABLE else 0)
        }



        @SuppressLint("Range")
        private fun hasWhatsapp(contactId:String, context: Context):String? {
            var whatsappId:String? = null
            val projection = arrayOf(
                ContactsContract.Data._ID,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.MIMETYPE,
                "account_type"
            )
            val selection =
                ContactsContract.RawContacts.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " =? and account_type=?"
            val selectionArgs = arrayOf(
                contactId,
                "vnd.android.cursor.item/vnd.com.whatsapp.voip.call",
                "com.whatsapp"
            )
            val cursor = context.contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                projection, selection, selectionArgs,
                ContactsContract.Contacts.DISPLAY_NAME
            )

            //ID QUERY SECTION
            while (cursor?.moveToNext() == true) {
                whatsappId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID)).toString()
            }
            return whatsappId
        }

        private fun setWidgetDataWithPic(context: Context, awm: AppWidgetManager,
                                         remoteViews: RemoteViews, appWidgetId: Int) {
            val options = awm.getAppWidgetOptions(appWidgetId)
            val w = options.getInt(OPTION_APPWIDGET_MAX_WIDTH)
            val h = options.getInt(OPTION_APPWIDGET_MAX_HEIGHT)
            FirebaseCrashlytics.getInstance().log("updatePhoto: Updating photo for widget with id $appWidgetId")
            setWidgetDataWithPic(context, remoteViews, appWidgetId, w, h)
        }

        private fun setWidgetDataWithPic(context: Context, remoteViews: RemoteViews,
                                         appWidgetId: Int, widthDp: Int, heightDp: Int) {
            FirebaseCrashlytics.getInstance().setCustomKey("scr_width_px", context.resources.displayMetrics.widthPixels)
            FirebaseCrashlytics.getInstance().setCustomKey("scr_height_px", context.resources.displayMetrics.heightPixels)
            FirebaseCrashlytics.getInstance().setCustomKey("scr_density", context.resources.displayMetrics.density)
            FirebaseCrashlytics.getInstance().setCustomKey("scr_xdpi", context.resources.displayMetrics.xdpi)
            FirebaseCrashlytics.getInstance().setCustomKey("scr_ydpi", context.resources.displayMetrics.ydpi)

            context.widgetRepository.getWidgetDataById(appWidgetId)?.let { widgetData ->
                widgetData.pictureUri?.let { uriStr -> Uri.parse(uriStr) }?.let { picUri ->
                    AppWidgetTarget(context, R.id.picture, remoteViews, appWidgetId).also { target ->
                        val widthPx = context.xdpToPx(widthDp)
                        val heightPx = context.ydpToPx(heightDp)
                        FirebaseCrashlytics.getInstance().log("updatePhoto: Loading image. Required size: ${widthPx}px x ${heightPx}px")
                        val options = RequestOptions().override(widthPx, heightPx)
                                .placeholder(R.drawable.ic_default_picture)
                        Glide.with(context.applicationContext)
                                .asBitmap()
                                .load(picUri)
                                .centerInside()
                                .apply(options)
                                .into(target)
                    }
                }
            }
        }
    }
}