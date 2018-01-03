package com.nagpal.shivam.vtudslab.Activity;

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

import com.nagpal.shivam.vtudslab.Adapter.ProgramInfoAdapter;
import com.nagpal.shivam.vtudslab.Loader.ProgramInfoLoader;
import com.nagpal.shivam.vtudslab.R;
import com.nagpal.shivam.vtudslab.Utility.IndexJsonResponse;
import com.nagpal.shivam.vtudslab.Utility.ProgramInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<IndexJsonResponse> {

    private final String url = "https://raw.githubusercontent.com/ShivamNagpal/VTU-DS-Lab-Program/master/Index.json";
    private TextView emptyTextView;
    private ProgressBar progressBar;
    private ListView programListView;
    private ProgramInfoAdapter programInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        programInfoAdapter = new ProgramInfoAdapter(MainActivity.this, new ArrayList<ProgramInfo>());
        programListView.setAdapter(programInfoAdapter);

        programListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProgramInfo programInfo = (ProgramInfo) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                intent.putExtra(DisplayActivity.title_intent_tag, programInfo.getTitle());
                intent.putExtra(DisplayActivity.url_intent_tag, programInfo.getUrl());
                startActivity(intent);
            }
        });
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
    }

    private void initViews() {
        programListView = findViewById(R.id.program_list_view);
        emptyTextView = findViewById(R.id.empty_text_view_main);
        progressBar = findViewById(R.id.progress_bar_main);
    }


    @Override
    public Loader<IndexJsonResponse> onCreateLoader(int i, Bundle bundle) {
        return new ProgramInfoLoader(MainActivity.this, url);
    }

    @Override
    public void onLoadFinished(Loader<IndexJsonResponse> loader, IndexJsonResponse indexJsonResponse) {
        progressBar.setVisibility(View.GONE);

        if (indexJsonResponse == null) {
            emptyTextView.setText(R.string.error_occurred);
            return;
        }

        if (indexJsonResponse.getValid()) {
            if (indexJsonResponse.getProgramInfoList().isEmpty()) {
                emptyTextView.setText(R.string.error_occurred);
                return;
            }
            programInfoAdapter.clear();
            programInfoAdapter.addAll(indexJsonResponse.getProgramInfoList());
        } else {
            emptyTextView.setText(indexJsonResponse.getInvalidationMessage());
        }
    }

    @Override
    public void onLoaderReset(Loader<IndexJsonResponse> loader) {
        programInfoAdapter.clear();
    }
}