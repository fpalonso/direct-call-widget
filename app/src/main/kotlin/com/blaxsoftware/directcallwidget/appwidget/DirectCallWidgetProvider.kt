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
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT
import android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Size
import android.view.View
import android.widget.RemoteViews
import androidx.core.os.bundleOf
import com.blaxsoftware.directcallwidget.Intents
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.WidgetClickReceiver
import com.blaxsoftware.directcallwidget.domain.DeleteOneContactWidgetUseCase
import com.blaxsoftware.directcallwidget.ui.xdpToPx
import com.blaxsoftware.directcallwidget.ui.ydpToPx
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.AppWidgetTarget
import com.bumptech.glide.request.target.Target
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.crashlytics.setCustomKeys
import com.google.firebase.ktx.Firebase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dev.ferp.dcw.core.analytics.Analytics
import dev.ferp.dcw.core.analytics.di.FirebaseEntryPoint
import dev.ferp.dcw.core.di.CoroutinesEntryPoint
import dev.ferp.dcw.data.contacts.OneContactWidget
import dev.ferp.dcw.data.contacts.OneContactWidgetRepository
import kotlinx.coroutines.launch

// TODO have only one static setWidgetData (call it updateWidget)
// TODO have it only have context, appWidgetManager and appWidgetId params.
// TODO start a JobService for the async operations:
//  https://developer.android.com/develop/background-work/background-tasks/broadcasts#effects-process-state
open class DirectCallWidgetProvider : AppWidgetProvider() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ProviderEntryPoint {
        fun widgetRepository(): OneContactWidgetRepository
        fun deleteWidgetUseCase(): DeleteOneContactWidgetUseCase
    }

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
        val firebaseEntryPoint = EntryPointAccessors.fromApplication<FirebaseEntryPoint>(context)
        val size = getAppWidgetSize(appWidgetManager, appWidgetId)
        if (size.width > 0 && size.height > 0) {
            firebaseEntryPoint.analytics().logEvent(
                Analytics.Event.WIDGET_RESIZE,
                bundleOf(
                    "width_dp" to size.width,
                    "height_dp" to size.height
                )
            )
        }
        updateWidget(context, appWidgetManager, appWidgetId)
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, id: Int) {
        // TODO launch a JobService
        val entryPoint = EntryPointAccessors.fromApplication<ProviderEntryPoint>(context)
        val widgetRepo = entryPoint.widgetRepository()
        val coroutinesEntryPoint = EntryPointAccessors
            .fromApplication<CoroutinesEntryPoint>(context)
        coroutinesEntryPoint.appScope().launch {
            widgetRepo.getWidget(id)?.let { widget ->
                setWidgetData(context, appWidgetManager, id, widget)
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        val entryPoint = EntryPointAccessors.fromApplication<ProviderEntryPoint>(context)
        val deleteWidgetUseCase = entryPoint.deleteWidgetUseCase()
        val coroutinesEntryPoint = EntryPointAccessors
            .fromApplication<CoroutinesEntryPoint>(context)
        coroutinesEntryPoint.appScope().launch {
            appWidgetIds.forEach { appWidgetId ->
                deleteWidgetUseCase(appWidgetId)
            }
        }
    }

    companion object {

        fun setWidgetData(
            context: Context,
            appWidgetManager: AppWidgetManager,
            widgetId: Int,
            widgetData: OneContactWidget
        ) {
            val firebase = EntryPointAccessors
                .fromApplication(context, FirebaseEntryPoint::class.java)
            val crashlytics = firebase.crashlytics()
            crashlytics.log("setWidgetData: widgetId=$widgetId")
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
                if (widgetData.hasPicture) {
                    crashlytics.log("setWidgetData: has a picture")
                    val size = getAppWidgetSize(appWidgetManager, widgetId)
                    val sizePx = Size(
                        context.xdpToPx(size.width),
                        context.ydpToPx(size.height)
                    )
                    if (size.width > 0 && size.height > 0) {
                        Firebase.crashlytics.log("setWidgetData: widget size in pixels: ${sizePx.width}x${sizePx.height} ")
                    }
                    setWidgetDataWithPic(
                        context,
                        it,
                        widgetId,
                        size.width,
                        size.height,
                        crashlytics
                    )
                } else {
                    crashlytics.log("setWidgetData: no picture")
                    appWidgetManager.updateAppWidget(widgetId, it)
                }
            }
        }

        // TODO extract to :core:androidutil
        /** Returns the upper bounds for the given [widgetId] size, in dp. */
        private fun getAppWidgetSize(
            appWidgetManager: AppWidgetManager,
            widgetId: Int
        ): Size {
            val options = appWidgetManager.getAppWidgetOptions(widgetId)
            val maxSize = Size(
                options.getInt(OPTION_APPWIDGET_MAX_WIDTH),
                options.getInt(OPTION_APPWIDGET_MAX_HEIGHT)
            )
            return maxSize
        }

        // TODO extract to :core:call
        private fun callContactIntent(context: Context, phoneNumber: String,
                                      widgetId: Int): PendingIntent {
            val callUri = Uri.parse("tel:" + Uri.encode(phoneNumber))
            val callIntent = Intent(Intents.ACTION_WIDGET_CLICK, callUri)
            callIntent.setClass(context, WidgetClickReceiver::class.java)
            return PendingIntent.getBroadcast(context, widgetId, callIntent,
                PendingIntent.FLAG_IMMUTABLE)
        }

        private fun setWidgetDataWithPic(
            context: Context,
            remoteViews: RemoteViews,
            appWidgetId: Int,
            widthDp: Int,
            heightDp: Int,
            crashlytics: FirebaseCrashlytics
        ) {
            if (widthDp == 0 || heightDp == 0) return
            crashlytics.setCustomKeys {
                key("scr_width_px", context.resources.displayMetrics.widthPixels)
                key("scr_height_px", context.resources.displayMetrics.heightPixels)
                key("scr_density", context.resources.displayMetrics.density)
                key("scr_xdpi", context.resources.displayMetrics.xdpi)
                key("scr_ydpi", context.resources.displayMetrics.ydpi)
            }
            val providerEntryPoint = EntryPointAccessors
                .fromApplication<ProviderEntryPoint>(context)
            val coroutinesEntryPoint = EntryPointAccessors
                .fromApplication<CoroutinesEntryPoint>(context)
            coroutinesEntryPoint.appScope().launch {
                providerEntryPoint.widgetRepository().getWidget(appWidgetId)?.let { widgetData ->
                    widgetData.pictureUri?.let { uriStr -> Uri.parse(uriStr) }?.let { picUri ->
                        AppWidgetTarget(context, R.id.picture, remoteViews, appWidgetId).also { target ->
                            val widthPx = context.xdpToPx(widthDp)
                            val heightPx = context.ydpToPx(heightDp)
                            crashlytics.log("setWidgetDataWithPic: Loading image. Required size (px): ${widthPx}x${heightPx}")
                            val options = RequestOptions().override(widthPx, heightPx)
                                .placeholder(R.drawable.ic_default_picture)
                            Glide.with(context.applicationContext)
                                .asBitmap()
                                .addListener(WidgetPictureRequestListener(crashlytics))
                                .load(picUri)
                                .centerCrop()
                                .apply(options)
                                .into(target)
                        }
                    }
                }
            }

        }
    }
}

private class WidgetPictureRequestListener(
    private val crashlytics: FirebaseCrashlytics
) : RequestListener<Bitmap> {

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Bitmap>?,
        isFirstResource: Boolean
    ): Boolean {
        crashlytics.log("Widget picture load failed with message: ${e?.message}")
        return false
    }

    override fun onResourceReady(
        resource: Bitmap?,
        model: Any?,
        target: Target<Bitmap>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        crashlytics.setCustomKeys {
            key("widget_pic_size_px", "${resource?.width}x${resource?.height}")
            key("widget_pic_allocation_byte_count", "${resource?.allocationByteCount}")
        }
        return false
    }
}