package com.nagpal.shivam.vtucslab.Activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nagpal.shivam.vtucslab.Adapter.InfoAdapter;
import com.nagpal.shivam.vtucslab.Loader.InfoLoader;
import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.Utility.ConstantVariables;
import com.nagpal.shivam.vtucslab.Utility.IndexJsonResponse;
import com.nagpal.shivam.vtucslab.Utility.Info;

import java.util.ArrayList;

public class RepoDisplayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<IndexJsonResponse> {

    private Boolean succeeded = false;
    private String linkToRepo;
    private String title;
    private String url;

    private InfoAdapter programInfoAdapter;
    private ListView programListView;
    private ProgressBar progressBar;
    private TextView emptyTextView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repo_display_activity_menu, menu);
        if (succeeded) {
            MenuItem gitRepoMenuItem = menu.findItem(R.id.git_repo_menu_item_repo_display_activity);
            gitRepoMenuItem.setEnabled(true);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.git_repo_menu_item_repo_display_activity:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(linkToRepo));
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repodisplay);

        ActionBar actionBar = getSupportActionBar();

        initViews();

        programInfoAdapter = new InfoAdapter(RepoDisplayActivity.this, new ArrayList<Info>());
        programListView.setAdapter(programInfoAdapter);

        Intent intent = getIntent();
        title = intent.getStringExtra(ConstantVariables.title_intent_tag);
        url = intent.getStringExtra(ConstantVariables.url_intent_tag);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        RepoDisplayActivity.this.setTitle(title);


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(1, null, RepoDisplayActivity.this);
        } else {
            progressBar.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_internet_connection);
        }

        programListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Info info = (Info) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(RepoDisplayActivity.this, DisplayActivity.class);
                intent.putExtra(ConstantVariables.title_intent_tag, info.getTitle());
                intent.putExtra(ConstantVariables.url_intent_tag, info.getUrl());
                startActivity(intent);
            }
        });

    }

    private void initViews() {
        programListView = findViewById(R.id.program_list_view);
        emptyTextView = findViewById(R.id.empty_text_view_main);
        progressBar = findViewById(R.id.progress_bar_main);
    }


    @Override
    public Loader<IndexJsonResponse> onCreateLoader(int i, Bundle bundle) {
        return new InfoLoader(RepoDisplayActivity.this, url);
    }

    @Override
    public void onLoadFinished(Loader<IndexJsonResponse> loader, IndexJsonResponse indexJsonResponse) {
        progressBar.setVisibility(View.GONE);

        if (indexJsonResponse == null) {
            emptyTextView.setText(R.string.error_occurred);
            return;
        }

        if (indexJsonResponse.getValid()) {
            if (indexJsonResponse.getInfoList().isEmpty()) {
                emptyTextView.setText(R.string.error_occurred);
                return;
            }
            succeeded = true;
            programInfoAdapter.clear();
            programInfoAdapter.addAll(indexJsonResponse.getInfoList());
            invalidateOptionsMenu();
            linkToRepo = indexJsonResponse.getLinkToRepo();
        } else {
            emptyTextView.setText(indexJsonResponse.getInvalidationMessage());
        }
    }

    @Override
    public void onLoaderReset(Loader<IndexJsonResponse> loader) {
        programInfoAdapter.clear();
    }

    @Override
    protected void onPause() {
        supportInvalidateOptionsMenu();
        super.onPause();
    }
}