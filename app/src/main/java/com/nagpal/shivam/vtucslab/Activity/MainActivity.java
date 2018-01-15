package com.nagpal.shivam.vtucslab.Activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<IndexJsonResponse> {

    private static final String url = "https://raw.githubusercontent.com/VTU-CS-LAB/Init/master/root.json";

    private InfoAdapter repoInfoAdapter;
    private ListView repoListView;
    private ProgressBar progressBar;
    private TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        repoInfoAdapter = new InfoAdapter(MainActivity.this, new ArrayList<Info>());
        repoListView.setAdapter(repoInfoAdapter);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(1, null, MainActivity.this);
        } else {
            progressBar.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_internet_connection);
        }

        repoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Info info = (Info) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(MainActivity.this, RepoDisplayActivity.class);
                intent.putExtra(ConstantVariables.title_intent_tag, info.getTitle());
                intent.putExtra(ConstantVariables.url_intent_tag, info.getUrl());
                startActivity(intent);
            }
        });

    }

    private void initViews() {
        repoListView = findViewById(R.id.program_list_view);
        emptyTextView = findViewById(R.id.empty_text_view_main);
        progressBar = findViewById(R.id.progress_bar_main);
    }

    @Override
    public Loader<IndexJsonResponse> onCreateLoader(int i, Bundle bundle) {
        return new InfoLoader(MainActivity.this, url);
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
            repoInfoAdapter.clear();
            repoInfoAdapter.addAll(indexJsonResponse.getInfoList());
        } else {
            emptyTextView.setText(indexJsonResponse.getInvalidationMessage());
        }
    }

    @Override
    public void onLoaderReset(Loader<IndexJsonResponse> loader) {
        repoInfoAdapter.clear();
    }
}
