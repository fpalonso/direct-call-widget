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

package com.blaxsoftware.directcallwidget.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.blaxsoftware.directcallwidget.Constants;
import com.blaxsoftware.directcallwidget.Intents;
import com.blaxsoftware.directcallwidget.R;
import com.blaxsoftware.directcallwidget.WidgetClickReceiver;
import com.blaxsoftware.directcallwidget.image.LoadImageTask;
import com.blaxsoftware.directcallwidget.image.LoadImageTask.OnImageLoadedListener;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DirectCallWidgetProvider extends AppWidgetProvider {

    static final int DEFAULT_WIDTH = 110;
    static final int DEFAULT_HEIGHT = 110;

    public static void updateWidget(Context context,
                                    AppWidgetManager widgetMngr, int widgetId) {
        // Get widget data
        SharedPreferences pref = context.getSharedPreferences(
                Constants.SHAREDPREF_WIDGET, Context.MODE_PRIVATE);
        String displayName = pref.getString(
                Constants.SHAREDPREF_WIDGET_DISPLAY_NAME + widgetId, null);
        String phoneNumber = pref.getString(Constants.SHAREDPREF_WIDGET_PHONE
                + widgetId, null);

        RemoteViews rViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_2x2);
        rViews.setViewVisibility(R.id.defaultPicture, View.VISIBLE);
        rViews.setTextViewText(R.id.contactName, displayName);
        rViews.setViewVisibility(R.id.contactName,
                !TextUtils.isEmpty(displayName) ? View.VISIBLE : View.GONE);
        if (phoneNumber != null) {
            Uri callUri = Uri.parse("tel:" + Uri.encode(phoneNumber));
            Intent callIntent = new Intent(Intents.ACTION_WIDGET_CLICK, callUri);
            callIntent.setClass(context, WidgetClickReceiver.class);
            PendingIntent callPendingIntent = PendingIntent.getBroadcast(
                    context, widgetId, callIntent, 0);
            rViews.setOnClickPendingIntent(R.id.widgetLayout, callPendingIntent);
        }
        updatePhoto(context, rViews, widgetMngr, widgetId, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        widgetMngr.updateAppWidget(widgetId, rViews);
    }

    private static void updatePhoto(Context context, final RemoteViews remoteViews,
                                    final AppWidgetManager appWidgetManager, final int appWidgetId,
                                    int width, int height) {
        SharedPreferences pref = context.getSharedPreferences(
                Constants.SHAREDPREF_WIDGET, Context.MODE_PRIVATE);
        String photoUriString = pref.getString(
                Constants.SHAREDPREF_WIDGET_PHOTO_URL + appWidgetId, null);
        Uri photoUri = (photoUriString != null) ? Uri.parse(photoUriString)
                : null;

        // Update the remote views
        if (photoUri != null) {

            LoadImageTask imgLoader = new LoadImageTask(context, width, height, false);
            imgLoader.setOnImageLoadedListener(new OnImageLoadedListener() {

                @Override
                public void onImageLoaded(String uri, Bitmap bitmap) {
                    if (bitmap != null) {
                        remoteViews.setViewVisibility(R.id.defaultPicture, View.INVISIBLE);
                        remoteViews.setImageViewBitmap(R.id.picture, bitmap);
                        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                    }
                }
            });
            imgLoader.execute(photoUri);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        List<URI> picturesToRemove = new ArrayList<>();
        for (int id : appWidgetIds) {
            // Add the picture to the list of pictures to remove
            SharedPreferences sp = context
                    .getSharedPreferences(Constants.SHAREDPREF_WIDGET, Context.MODE_PRIVATE);
            String picUriStr = sp.getString(Constants.SHAREDPREF_WIDGET_PHOTO_URL + id, null);
            if (picUriStr != null) {
                try {
                    picturesToRemove.add(URI.create(picUriStr));
                } catch (IllegalArgumentException e) { /* Nothing to do here */ }
            }

            // Remove the widget data from preferences
            SharedPreferences.Editor spEditor = sp.edit();
            spEditor.remove(Constants.SHAREDPREF_WIDGET_DISPLAY_NAME + id);
            spEditor.remove(Constants.SHAREDPREF_WIDGET_PHONE + id);
            spEditor.remove(Constants.SHAREDPREF_WIDGET_PHONE_TYPE + id);
            spEditor.remove(Constants.SHAREDPREF_WIDGET_PHOTO_URL + id);
            spEditor.apply();
        }
        if (!picturesToRemove.isEmpty()) {
            URI[] picsToRemoveArray = picturesToRemove.toArray(new URI[picturesToRemove.size()]);
            new RemovePictureTask().execute(picsToRemoveArray);
        }
    }

    private class RemovePictureTask extends AsyncTask<URI, Void, Void> {

        @Override
        protected Void doInBackground(URI... uris) {
            for (URI fileUri : uris) {
                File pic = new File(fileUri);
                if (pic.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    pic.delete();
                }
            }
            return null;
        }
    }
}
