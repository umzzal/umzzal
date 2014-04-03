package com.startupk.umzzal.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.Context;

import com.startupk.umzzal.UmzzalPreferenceUtil;
import com.startupk.umzzal.provider.UmzzalAppWidgetProvider;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class UmzzalUpdateIntentService extends IntentService {
    private static final String ACTION_ANIMATE = "com.startupk.umzzal.service.action.ANIMATE";

    private static final String EXTRA_WIDGET_ID = "com.startupk.umzzal.service.extra.WIDGET_ID";
    private static final String EXTRA_FRAME_COUNT = "com.startupk.umzzal.service.extra.FRAME_COUNT";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startWidgetUpdate(Context context, int widgetID, int frameCount) {
        Intent intent = getWidgetAnimateIntent(context, widgetID, frameCount);
        context.startService(intent);
    }

    public static Intent getWidgetAnimateIntent(Context context, int widgetID, int frameCount) {
        Intent intent = new Intent(context, UmzzalUpdateIntentService.class);
        intent.setAction(ACTION_ANIMATE);
        intent.putExtra(EXTRA_WIDGET_ID, widgetID);
        intent.putExtra(EXTRA_FRAME_COUNT, frameCount);
        return intent;
    }

    public UmzzalUpdateIntentService() {
        super("UmzzalUpdateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ANIMATE.equals(action)) {
                final int param1 = intent.getIntExtra(EXTRA_WIDGET_ID, 0);
                final int param2 = intent.getIntExtra(EXTRA_FRAME_COUNT, 1);
                handleUpdateWidget(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleUpdateWidget(int widgetId, int frameCount) {
        UmzzalPreferenceUtil fileUtil = new UmzzalPreferenceUtil();
        for (int idx = 0; idx < frameCount; idx++) {
            UmzzalAppWidgetProvider.updateAppWidget(getBaseContext(),
                    AppWidgetManager.getInstance(getBaseContext()),
                    widgetId,
                    fileUtil.getFrameUri(widgetId, idx));
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        UmzzalAppWidgetProvider.updateAppWidget(getBaseContext(),
                AppWidgetManager.getInstance(getBaseContext()),
                widgetId,
                fileUtil.getFrameUri(widgetId, 0));
    }
}
