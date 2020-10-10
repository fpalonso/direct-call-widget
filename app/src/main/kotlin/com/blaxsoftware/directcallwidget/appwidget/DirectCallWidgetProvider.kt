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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.AppWidgetTarget
import java.util.*

open class DirectCallWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager,
                          appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d(TAG, "onUpdate")
        appWidgetIds.forEach { id ->
            context.widgetRepository.getWidgetDataById(id)?.let { widgetData ->
                setWidgetData(context, appWidgetManager, id, widgetData)
            } ?: clearWidgetData(context, appWidgetManager, id)
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager,
                                           appWidgetId: Int, newOptions: Bundle) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        Log.d(TAG, "onAppWidgetOptionsChanged")
        val w = newOptions.getInt(OPTION_APPWIDGET_MAX_WIDTH)
        val h = newOptions.getInt(OPTION_APPWIDGET_MAX_HEIGHT)
        RemoteViews(context.packageName, R.layout.widget_2x2).also { rViews ->
            updatePhoto(context, rViews, appWidgetId, w, h)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        Log.d(TAG, "onDeleted: Deleting widget ids: ${appWidgetIds.contentToString()}")
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
            RemoteViews(context.packageName, R.layout.widget_2x2).apply {
                setViewVisibility(R.id.picture, View.VISIBLE)
                setTextViewText(R.id.contactName, widgetData.displayName)
                setViewVisibility(R.id.contactName,
                        if (widgetData.hasDisplayName) View.VISIBLE else View.INVISIBLE)
                setViewVisibility(R.id.addPerson, View.INVISIBLE)
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
            updatePhoto(context, remoteViews, appWidgetId, w, h)
        }

        private fun updatePhoto(context: Context, remoteViews: RemoteViews,
                                appWidgetId: Int, width: Int, height: Int) {
            context.widgetRepository.getWidgetDataById(appWidgetId)?.let { widgetData ->
                widgetData.pictureUri?.let { uriStr -> Uri.parse(uriStr) }?.let { picUri ->
                    AppWidgetTarget(context, R.id.picture, remoteViews, appWidgetId).also { target ->
                        val options = RequestOptions().override(width, height)
                                .placeholder(R.drawable.ic_default_picture)
                        Glide.with(context.applicationContext)
                                .asBitmap()
                                .load(picUri)
                                .apply(options)
                                .into(target)
                    }
                }
            }
        }

        fun clearWidgetData(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
            RemoteViews(context.packageName, R.layout.widget_2x2).apply {
                setViewVisibility(R.id.picture, View.INVISIBLE)
                setViewVisibility(R.id.contactName, View.INVISIBLE)
                setViewVisibility(R.id.addPerson, View.VISIBLE)
                setOnClickPendingIntent(R.id.widgetLayout,
                        startConfigActivityIntent(context, widgetId))
            }.also { appWidgetManager.updateAppWidget(widgetId, it) }
        }

        private fun startConfigActivityIntent(context: Context, widgetId: Int): PendingIntent {
            val intent = Intent(context, WidgetConfigActivity2::class.java)
                    .putExtra(EXTRA_APPWIDGET_ID, widgetId)
            return PendingIntent.getActivity(context, widgetId, intent, 0)
        }
    }
}