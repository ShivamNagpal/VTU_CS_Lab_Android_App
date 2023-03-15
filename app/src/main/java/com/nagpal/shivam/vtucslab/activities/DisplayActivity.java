package com.nagpal.shivam.vtucslab.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.databinding.ActivityDisplayBinding;
import com.nagpal.shivam.vtucslab.loaders.RawStreamLoader;
import com.nagpal.shivam.vtucslab.utilities.ConstantVariables;

public class DisplayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final int LOADER_ID = 1;
    private static final String SUCCEEDED_KEY = "succeeded_key";
    private static final String SCROLL_X_KEY = "scroll_x_key";
    private static final String SCROLL_Y_KEY = "scroll_y_key";

    private Boolean mSucceeded = false;
    private String mUrl;
    private String mCode;


    private ActivityDisplayBinding mBinding;
    private int mScrollX;
    private int mScrollY;

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
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(DisplayActivity.this);
            return true;
        } else if (itemId == R.id.display_menu_item_refresh) {
            recreate();
            return true;
        } else if (itemId == R.id.menu_item_copy_display_activity) {
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_display);
        ActionBar actionBar = getSupportActionBar();

        initViews();

        if (savedInstanceState != null) {
            mSucceeded = savedInstanceState.getBoolean(SUCCEEDED_KEY, false);
            mScrollX = savedInstanceState.getInt(SCROLL_X_KEY, 0);
            mScrollY = savedInstanceState.getInt(SCROLL_Y_KEY, 0);
        }

        Intent intent = getIntent();

        mUrl = intent.getStringExtra(ConstantVariables.url_intent_tag);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String title = intent.getStringExtra(ConstantVariables.title_intent_tag);
        DisplayActivity.this.setTitle(title);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        LoaderManager loaderManager = LoaderManager.getInstance(this);

        if (!mSucceeded) {
            loaderManager.destroyLoader(LOADER_ID);
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            loaderManager.initLoader(LOADER_ID, null, DisplayActivity.this);
        } else {
            mBinding.progressBarDisplay.setVisibility(View.GONE);
            mBinding.emptyTextViewDisplay.setVisibility(View.VISIBLE);
            mBinding.emptyTextViewDisplay.setText(R.string.no_internet_connection);
            mSucceeded = false;
        }
    }

    private void initViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBinding.displayTextView.setLetterSpacing(0.1f);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        return new RawStreamLoader(DisplayActivity.this, mUrl);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        mBinding.progressBarDisplay.setVisibility(View.GONE);
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(DisplayActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
            mSucceeded = false;
            return;
        }

        mSucceeded = true;
        mCode = s;
        s = s.replaceAll("\t", "\t\t");
        mBinding.displayTextView.setText(s);
        new Handler(getMainLooper()).postDelayed(() -> {
            mBinding.horizontalScroll.setScrollX(mScrollX);
            mBinding.verticalScroll.setScrollY(mScrollY);
        }, 500);
        invalidateOptionsMenu();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        mBinding.displayTextView.setText(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SUCCEEDED_KEY, mSucceeded);
        outState.putInt(SCROLL_X_KEY, mBinding.horizontalScroll.getScrollX());
        outState.putInt(SCROLL_Y_KEY, mBinding.verticalScroll.getScrollY());
        super.onSaveInstanceState(outState);
    }
}
