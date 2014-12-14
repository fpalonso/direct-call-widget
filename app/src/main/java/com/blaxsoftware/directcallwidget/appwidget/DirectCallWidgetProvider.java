package com.blaxsoftware.directcallwidget.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.blaxsoftware.directcallwidget.Constants;
import com.blaxsoftware.directcallwidget.R;
import com.blaxsoftware.directcallwidget.image.LoadImageTask;
import com.blaxsoftware.directcallwidget.image.LoadImageTask.OnImageLoadedListener;

public class DirectCallWidgetProvider extends AppWidgetProvider {

    static final int DEFAULT_WIDTH = 110;
    static final int DEFAULT_HEIGHT = 110;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
	    int[] appWidgetIds) {
	for (int widgetId : appWidgetIds) {
	    updateWidget(context, appWidgetManager, widgetId);
	}
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context,
	    final AppWidgetManager appWidgetManager, final int appWidgetId,
	    Bundle newOptions) {
	int widgetW = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(
		AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
	int widgetH = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(
		AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
	updatePhoto(context, appWidgetManager, appWidgetId, widgetW, widgetH);
    }

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
	rViews.setTextViewText(R.id.contactName, displayName);
	if (phoneNumber != null) {
	    Uri callUri = Uri.parse("tel:" + phoneNumber);
	    Intent callIntent = new Intent(Intent.ACTION_CALL, callUri);
	    PendingIntent callPendingIntent = PendingIntent.getActivity(
		    context, widgetId, callIntent, 0);
	    rViews.setOnClickPendingIntent(R.id.widgetLayout, callPendingIntent);
	}
	updatePhoto(context, widgetMngr, widgetId, DEFAULT_WIDTH,
		DEFAULT_HEIGHT);

	widgetMngr.updateAppWidget(widgetId, rViews);
    }

    private static void updatePhoto(Context context,
	    final AppWidgetManager appWidgetManager, final int appWidgetId,
	    int width, int height) {
	SharedPreferences pref = context.getSharedPreferences(
		Constants.SHAREDPREF_WIDGET, Context.MODE_PRIVATE);
	String photoUriString = pref.getString(
		Constants.SHAREDPREF_WIDGET_PHOTO_URL + appWidgetId, null);
	Uri photoUri = (photoUriString != null) ? Uri.parse(photoUriString)
		: null;

	// Update the remote views
	final RemoteViews rViews = new RemoteViews(context.getPackageName(),
		R.layout.widget_2x2);
	if (photoUri != null) {

	    LoadImageTask imgLoader = new LoadImageTask(context, width, height);
	    imgLoader.setOnImageLoadedListener(new OnImageLoadedListener() {

		@Override
		public void onImageLoaded(Bitmap bitmap) {
		    rViews.setImageViewBitmap(R.id.picture, bitmap);
		    appWidgetManager.updateAppWidget(appWidgetId, rViews);
		}
	    });
	    imgLoader.execute(photoUri);
	}
    }
}
