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

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.*
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.blaxsoftware.directcallwidget.*
import com.blaxsoftware.directcallwidget.data.model.WidgetData
import com.blaxsoftware.directcallwidget.ui.WidgetConfigActivity2
import com.blaxsoftware.directcallwidget.ui.xdpToPx
import com.blaxsoftware.directcallwidget.ui.ydpToPx
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.AppWidgetTarget
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.lang.RuntimeException

open class DirectCallWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager,
                          appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds.forEach { id ->
            context.widgetRepository.getWidgetDataById(id)?.let { widgetData ->
                FirebaseCrashlytics.getInstance().log("onUpdate: hasPicture=${widgetData.hasPicture}, hasDisplayName=${widgetData.hasDisplayName}")
                setWidgetData(context, appWidgetManager, id, widgetData)
            } ?: FirebaseCrashlytics.getInstance().recordException(IllegalStateException("onUpdate: Widget data for widget id $id not found!"))
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager,
                                           appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        val w = newOptions.getInt(OPTION_APPWIDGET_MAX_WIDTH)
        val h = newOptions.getInt(OPTION_APPWIDGET_MAX_HEIGHT)
        FirebaseCrashlytics.getInstance().log("onAppWidgetOptionsChanged: Widget with id $appWidgetId resized to ${w}dp x ${h}dp")
        RemoteViews(context.packageName, R.layout.widget_2x2).also { rViews ->
            updatePhoto(context, rViews, appWidgetId, w, h)
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
        const val TAG = "WidgetProvider"

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
                        callContactIntent(context, widgetData.phoneNumber, widgetId))
            }.also {
                updatePhoto(context, appWidgetManager, it, widgetId)
                appWidgetManager.updateAppWidget(widgetId, it)
            }
        }

        private fun callContactIntent(context: Context, phoneNumber: String,
                                      widgetId: Int): PendingIntent {
            val callUri = Uri.parse("tel:" + Uri.encode(phoneNumber))
            val callIntent = Intent(Intents.ACTION_WIDGET_CLICK, callUri)
            callIntent.setClass(context, WidgetClickReceiver::class.java)
            return PendingIntent.getBroadcast(context, widgetId, callIntent, 0)
        }

        private fun updatePhoto(context: Context, awm: AppWidgetManager,
                                remoteViews: RemoteViews, appWidgetId: Int) {
            val options = awm.getAppWidgetOptions(appWidgetId)
            val w = options.getInt(OPTION_APPWIDGET_MAX_WIDTH)
            val h = options.getInt(OPTION_APPWIDGET_MAX_HEIGHT)
            FirebaseCrashlytics.getInstance().log("updatePhoto: Updating photo for widget with id $appWidgetId")
            updatePhoto(context, remoteViews, appWidgetId, w, h)
        }

        private fun updatePhoto(context: Context, remoteViews: RemoteViews,
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