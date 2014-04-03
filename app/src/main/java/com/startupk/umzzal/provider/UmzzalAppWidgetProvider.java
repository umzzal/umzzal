package com.startupk.umzzal.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.startupk.umzzal.R;
import com.startupk.umzzal.UmzzalPreferenceUtil;
import com.startupk.umzzal.service.UmzzalUpdateIntentService;

/**
 * Created by skyisle on 3/18/14.
 */
public class UmzzalAppWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "UmzzalAppWidgetProvider";

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, Uri imageUri) {
        UmzzalPreferenceUtil preferenceUtil = new UmzzalPreferenceUtil();

        // Create an Intent to launch ExampleActivity
        Intent intent = UmzzalUpdateIntentService.getWidgetAnimateIntent(context, appWidgetId,
                preferenceUtil.getWidgetFrameCount(context, appWidgetId));
        PendingIntent pendingIntent = PendingIntent.getService(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Construct the RemoteViews object.  It takes the package name (in our case, it's our
        // package, but it needs this because on the other side it's the widget host inflating
        // the layout from our package).
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_umzzal);
        views.setOnClickPendingIntent(R.id.imageView, pendingIntent);
        views.setImageViewUri(R.id.imageView, imageUri);

        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        UmzzalPreferenceUtil preferenceUtil = new UmzzalPreferenceUtil();
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId, preferenceUtil.getFrameUri(appWidgetId, 0));
            Log.d(TAG, "onUpdate widgetId = " + appWidgetId);
        }
    }
}
