package com.nagpal.shivam.vtucslab.Activity;

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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.nagpal.shivam.vtucslab.Loader.RawStreamLoader;
import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.Utility.ConstantVariables;
import com.nagpal.shivam.vtucslab.databinding.ActivityDisplayBinding;

public class DisplayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final int LOADER_ID = 1;
    private static final String SUCCEEDED_KEY = "succeeded_key";
    private static final String SCROLL_X_KEY = "scroll_x_key";
    private static final String SCROLL_Y_KEY = "scroll_y_key";

    private static final String LOG_TAG = DisplayActivity.class.getSimpleName();


    //    private TextView displayTextView;
//    private PageView mDisplayPageView;
//    private TextView mEmptyTextView;
//    private ProgressBar mProgressBar;

    private Boolean mSucceeded = false;
    private String mUrl;
    private String mTitle;
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
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(DisplayActivity.this);
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_display);
//        setContentView(R.layout.activity_display);
//        logd("OnCreate Called");
        ActionBar actionBar = getSupportActionBar();

        initViews();

        if (savedInstanceState != null) {
            mSucceeded = savedInstanceState.getBoolean(SUCCEEDED_KEY, false);
            mScrollX = savedInstanceState.getInt(SCROLL_X_KEY, 0);
            mScrollY = savedInstanceState.getInt(SCROLL_Y_KEY, 0);
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
        LoaderManager loaderManager = LoaderManager.getInstance(this);

        if (!mSucceeded) {
            loaderManager.destroyLoader(LOADER_ID);
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            loaderManager.initLoader(LOADER_ID, null, DisplayActivity.this);
        } else {
//            mProgressBar.setVisibility(View.GONE);
            mBinding.progressBarDisplay.setVisibility(View.GONE);
//            mEmptyTextView.setVisibility(View.VISIBLE);
            mBinding.emptyTextViewDisplay.setVisibility(View.VISIBLE);
//            mEmptyTextView.setText(R.string.no_internet_connection);
            mBinding.emptyTextViewDisplay.setText(R.string.no_internet_connection);
            mSucceeded = false;
        }
    }

    private void initViews() {
//        mDisplayPageView = findViewById(R.id.display_page_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mDisplayPageView.setLetterSpacing(0.1f);
            mBinding.displayTextView.setLetterSpacing(0.1f);
        }
//        mEmptyTextView = findViewById(R.id.empty_text_view_display);
//        mProgressBar = findViewById(R.id.progress_bar_display);

        //setupScrolling();
    }

//    private void setupScrolling() {
//        mBinding.horizontalScroll.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
//        mBinding.verticalScroll.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
//        mBinding.displayTextView.setOnTouchListener(new View.OnTouchListener() {
//            private float prevX, prevY, curX, curY;
//            private boolean handled = false;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.v("Ontouch Called", "");
//                mBinding.verticalScroll.requestDisallowInterceptTouchEvent(true);
//                mBinding.horizontalScroll.requestDisallowInterceptTouchEvent(true);
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        handled = false;
//                        prevX = event.getX();
//                        prevY = event.getY();
//                        Log.v("Action Down", prevX + " " + prevY);
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        handled = true;
//                                                curX = event.getX();
//                        curY = event.getY();
//                        int dy = (int) (prevY - curY);
//                        int dx = (int) (prevX - curX);
//
//                        prevX = curX;
//                        prevY = curY;
//
//
//                        Log.v("Action Move Cur", curX + " " + curY);
////                        Log.v("Action Move Delta", dx + " " + dy);
//
//                        mBinding.verticalScroll.scrollBy(0, dy);
//                        mBinding.horizontalScroll.scrollBy(dx, 0);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        Log.v("Action Up", event.getX() + " " + event.getY());
//                        break;
//                }
//                return true;
//            }
//        });
//    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        return new RawStreamLoader(DisplayActivity.this, mUrl);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
//        mProgressBar.setVisibility(View.GONE);
        mBinding.progressBarDisplay.setVisibility(View.GONE);
        if (TextUtils.isEmpty(s)) {
            Toast.makeText(DisplayActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
            mSucceeded = false;
            return;
        }

        mSucceeded = true;
        mCode = s;
        s = s.replaceAll("\t", "\t\t");
//        displayTextView.setText(s);
//        mDisplayPageView.setText(s);
        mBinding.displayTextView.setText(s);
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.horizontalScroll.setScrollX(mScrollX);
                mBinding.verticalScroll.setScrollY(mScrollY);
            }
        }, 500);
        invalidateOptionsMenu();
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
//        displayTextView.setText(null);
//        mDisplayPageView.setText(null);
        mBinding.displayTextView.setText(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SUCCEEDED_KEY, mSucceeded);
        outState.putInt(SCROLL_X_KEY, mBinding.horizontalScroll.getScrollX());
        outState.putInt(SCROLL_Y_KEY, mBinding.verticalScroll.getScrollY());
        super.onSaveInstanceState(outState);
    }

//    private void logd(String str) {
//        Log.d(LOG_TAG, str);
//    }
}
