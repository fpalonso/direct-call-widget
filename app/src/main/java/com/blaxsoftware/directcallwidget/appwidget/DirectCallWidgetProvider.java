package com.blaxsoftware.directcallwidget.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.blaxsoftware.directcallwidget.Constants;
import com.blaxsoftware.directcallwidget.R;
import com.blaxsoftware.directcallwidget.image.LoadImageTask;
import com.blaxsoftware.directcallwidget.image.LoadImageTask.OnImageLoadedListener;

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
        rViews.setViewVisibility(R.id.loadingPicText, View.VISIBLE);
        rViews.setTextViewText(R.id.contactName, displayName);
        rViews.setViewVisibility(R.id.contactName,
                !TextUtils.isEmpty(displayName) ? View.VISIBLE : View.GONE);
        if (phoneNumber != null) {
            Uri callUri = Uri.parse("tel:" + Uri.encode(phoneNumber));
            Intent callIntent = new Intent(Intent.ACTION_CALL, callUri);
            PendingIntent callPendingIntent = PendingIntent.getActivity(
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
                    remoteViews.setViewVisibility(R.id.loadingPicText, View.GONE);
                    remoteViews.setImageViewBitmap(R.id.picture, bitmap);
                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
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
}
