package com.startupk.umzzal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by skyisle on 3/18/14.
 */
public class UmzzalPreferenceUtil {
    private static final String LOG = "UmzzalPreferenceUtil";

    public UmzzalPreferenceUtil() {
    }

    private String getBasePath() {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Umzzal");
        if (!file.exists() && !file.mkdirs()) {
            Log.e(LOG, "Directory not created");
        }
        return file.toString();
    }

    public String getFrameFileName(int widgetId, int frame) {
        return getBasePath() + File.separator + String.format("%d-%d", widgetId, frame);
    }

    public Uri getFrameUri(int widgetId, int frame) {
        return Uri.fromFile(new File(getFrameFileName(widgetId, frame)));
    }

    public void saveBitmapToFileCache(Bitmap bitmap, String strFilePath) {
        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;

        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setWidgetFrameCount(Context ctx, int widgetId, int frameCount) {
        SharedPreferences widget = ctx.getSharedPreferences("WIDGET", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor edit = widget.edit();
        edit.putInt(getFrameCountKey(widgetId), frameCount);
        edit.commit();
    }

    public int getWidgetFrameCount(Context ctx, int widgetId) {
        SharedPreferences widget = ctx.getSharedPreferences("WIDGET", Context.MODE_MULTI_PROCESS);
        return widget.getInt(getFrameCountKey(widgetId), 1);
    }

    private String getFrameCountKey(int widgetId) {
        return "WIDGET_FRAME_COUNT" + widgetId;
    }
}
