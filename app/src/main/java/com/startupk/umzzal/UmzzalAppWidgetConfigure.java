package com.startupk.umzzal;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.startupk.umzzal.provider.UmzzalAppWidgetProvider;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class UmzzalAppWidgetConfigure extends ActionBarActivity {

    private static final String LOG = "UmzzalAppWidgetConfigure";
    private static final int SELECT_PICTURE = 1000;
    private int mAppWidgetId;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            if(v.getId() == R.id.resize) {
                final Context context = UmzzalAppWidgetConfigure.this;

                // When the button is clicked, save the string in our prefs and return that they
                // Push widget update to surface with newly set prefix
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                UmzzalAppWidgetProvider.updateAppWidget(context, appWidgetManager,
                        mAppWidgetId, mFileUtil.getFrameUri(mAppWidgetId, 0));

                //UmzzalUpdateIntentService.startWidgetUpdate(context, mAppWidgetId, mFrameCount);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            } else if(v.getId() == R.id.play) {
                // in onCreate or any event where your want the user to
                // select a file
                Intent intent1 = new Intent();
                intent1.setType("image/*");
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent1,
                        "Select Picture"), SELECT_PICTURE);
            }
        }
    };
    private UmzzalPreferenceUtil mFileUtil;
    private int mFrameCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

          // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_umzzal_app_widget_configure);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        findViewById(R.id.resize).setOnClickListener(mOnClickListener);
        findViewById(R.id.play).setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE) {

            Uri selectedImageUri = data.getData();

            InputStream is = getInputStream(selectedImageUri);

//            InputStream is = getResources().openRawResource(R.drawable.img_1318);
            GifDecoder decoder = new GifDecoder();
            if (GifDecoder.STATUS_OK != decoder.read(is)) {
                //error
                return;
            }

            mFileUtil = new UmzzalPreferenceUtil();

            mFrameCount = decoder.getFrameCount();

            for (int i = 0; i < mFrameCount; i++) {
                mFileUtil.saveBitmapToFileCache(decoder.getFrame(i), mFileUtil.getFrameFileName(mAppWidgetId, i));
            }

            mFileUtil.setWidgetFrameCount(this, mAppWidgetId, mFrameCount);
        }
    }

    private InputStream getInputStream(Uri selectedImageUri) {
        try {
            return getContentResolver().openInputStream(selectedImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.umzzal_app_widget_configure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
