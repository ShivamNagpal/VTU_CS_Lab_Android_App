package com.nagpal.shivam.vtucslab.Activity;

import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nagpal.shivam.pageview.PageView;
import com.nagpal.shivam.vtucslab.Loader.RawStreamLoader;
import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.Utility.ConstantVariables;

public class DisplayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final int LOADER_ID = 1;
    private static final String SUCCEEDED_KEY = "succeeded_key";
    private static final String LOG_TAG = DisplayActivity.class.getSimpleName();


    //    private TextView displayTextView;
    private PageView mDisplayPageView;
    private TextView mEmptyTextView;
    private ProgressBar mProgressBar;

    private Boolean mSucceeded = false;
    private String mUrl;
    private String mTitle;
    private String mCode;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display_activity, menu);
        if (mSucceeded) {
            MenuItem copyMenuItem = menu.findItem(R.id.menu_item_copy_display_activity);
            copyMenuItem.setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.display_menu_item_refresh:
                recreate();
                return true;

            case R.id.menu_item_copy_display_activity:
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = new ClipData(ClipData.newPlainText("Code", mCode));
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(DisplayActivity.this, "Code copied to Clipboard.", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
//        logd("OnCreate Called");
        ActionBar actionBar = getSupportActionBar();

        initViews();

        if (savedInstanceState != null) {
            mSucceeded = savedInstanceState.getBoolean(SUCCEEDED_KEY, false);
        }

        Intent intent = getIntent();
        mTitle = intent.getStringExtra(ConstantVariables.title_intent_tag);
        mUrl = intent.getStringExtra(ConstantVariables.url_intent_tag);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DisplayActivity.this.setTitle(mTitle);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        LoaderManager loaderManager = getLoaderManager();

        if (!mSucceeded) {
            loaderManager.destroyLoader(LOADER_ID);
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            loaderManager.initLoader(LOADER_ID, null, DisplayActivity.this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.VISIBLE);
            mEmptyTextView.setText(R.string.no_internet_connection);
            mSucceeded = false;
        }
    }

    private void initViews() {
//        displayTextView = findViewById(R.id.display_text_view);
        mDisplayPageView = findViewById(R.id.display_page_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDisplayPageView.setLetterSpacing(0.1f);
        }
        mEmptyTextView = findViewById(R.id.empty_text_view_display);
        mProgressBar = findViewById(R.id.progress_bar_display);
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        return new RawStreamLoader(DisplayActivity.this, mUrl);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
        mProgressBar.setVisibility(View.GONE);
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(DisplayActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
            mSucceeded = false;
            return;
        }

        mSucceeded = true;
        mCode = s;
        s = s.replaceAll("\t", "\t\t");
//        displayTextView.setText(s);
        mDisplayPageView.setText(s);
        invalidateOptionsMenu();
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
//        displayTextView.setText(null);
        mDisplayPageView.setText(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SUCCEEDED_KEY, mSucceeded);
        super.onSaveInstanceState(outState);
    }

//    private void logd(String str) {
//        Log.d(LOG_TAG, str);
//    }
}
