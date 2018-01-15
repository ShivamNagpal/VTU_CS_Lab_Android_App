package com.nagpal.shivam.vtucslab.Activity;

import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.nagpal.shivam.vtucslab.Loader.RawStreamLoader;
import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.Utility.ConstantVariables;

public class DisplayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private TextView displayTextView;
    private TextView emptyTextView;
    private ProgressBar progressBar;

    private Boolean succeeded = false;
    private String url;
    private String title;
    private String code;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_activity_menu, menu);
        if (succeeded) {
            MenuItem copyMenuItem = menu.findItem(R.id.copy_menu_item_display_activity);
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

            case R.id.copy_menu_item_display_activity:
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = new ClipData(ClipData.newPlainText("Code", code));
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

        ActionBar actionBar = getSupportActionBar();

        initViews();

        Intent intent = getIntent();
        title = intent.getStringExtra(ConstantVariables.title_intent_tag);
        url = intent.getStringExtra(ConstantVariables.url_intent_tag);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DisplayActivity.this.setTitle(title);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(2, null, DisplayActivity.this);
        } else {
            progressBar.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_internet_connection);
        }
    }

    private void initViews() {
        displayTextView = findViewById(R.id.display_text_view);
        emptyTextView = findViewById(R.id.empty_text_view_display);
        progressBar = findViewById(R.id.progress_bar_display);
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        return new RawStreamLoader(DisplayActivity.this, url);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
        progressBar.setVisibility(View.GONE);
        if (TextUtils.isEmpty(s)) {
            emptyTextView.setText(R.string.error_occurred);
            return;
        }
        succeeded = true;
        code = s;
        s = s.replaceAll("\t", "\t\t");
        displayTextView.setText(s);
        invalidateOptionsMenu();
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        displayTextView.setText(null);
    }
}
