package com.nagpal.shivam.vtucslab.Activity;


import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nagpal.shivam.vtucslab.Adapter.ContentAdapter;
import com.nagpal.shivam.vtucslab.Loader.InfoLoader;
import com.nagpal.shivam.vtucslab.Model.ContentFile;
import com.nagpal.shivam.vtucslab.Model.LabExperiment;
import com.nagpal.shivam.vtucslab.Model.LabResponse;
import com.nagpal.shivam.vtucslab.Model.Laboratory;
import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.Utility.ConstantVariables;

import java.util.ArrayList;

public class ProgramActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<LabResponse>,
        ContentAdapter.ItemClickHandler {

    public static final String INTENT_LABORATORY = "PROGRAM_INTENT_LABORATORY";
    public static final String INTENT_LABORATORY_BASE_URL = "PROGRAM_INTENT_LABORATORY_BASE_URL";

    private static final int REPO_LOADER_ID = 2;
    private static final String SUCCEEDED_KEY = "succeeded_key";

    private ContentAdapter mProgramAdapter;
    private ProgressBar mProgressBar;
    private RecyclerView mProgramRecyclerView;
    private TextView mEmptyTextView;
    private boolean mSucceeded;
    private String mProgramBaseUrl;
    private LoaderManager mLoaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        if (savedInstanceState != null) {
            mSucceeded = savedInstanceState.getBoolean(SUCCEEDED_KEY, false);
        }

        initAndSetupViews();

        setupProgramAdapter();

        loadPrograms();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(ProgramActivity.this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void initAndSetupViews() {
        mProgramRecyclerView = findViewById(R.id.activity_program_recycler_view_program);
        mProgramRecyclerView.setLayoutManager(new LinearLayoutManager(ProgramActivity.this, LinearLayoutManager.VERTICAL, false));
        mProgramRecyclerView.setHasFixedSize(true);

        mEmptyTextView = findViewById(R.id.activity_program_text_view_empty);

        mProgressBar = findViewById(R.id.activity_program_progress_bar);
    }

    private void setupProgramAdapter() {
        mProgramAdapter = new ContentAdapter(ProgramActivity.this, new ArrayList<LabExperiment>());
        mProgramRecyclerView.setAdapter(mProgramAdapter);
        mProgramAdapter.setItemClickHandler(this);
    }

    private void loadPrograms() {
        Intent intent = getIntent();

        Laboratory laboratory = (Laboratory) intent.getSerializableExtra(INTENT_LABORATORY);
        String laboratoryBaseUrl = intent.getStringExtra(INTENT_LABORATORY_BASE_URL);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        mLoaderManager = LoaderManager.getInstance(this);

        if (!mSucceeded) {
            mLoaderManager.destroyLoader(REPO_LOADER_ID);
        }

        mEmptyTextView.setVisibility(View.GONE);

        setTitle(laboratory.getTitle());
        if (networkInfo != null && networkInfo.isConnected()) {
            Bundle bundle = new Bundle();
            String url = laboratoryBaseUrl + "/" + laboratory.getFileName();

            bundle.putString("URL", url);
            mLoaderManager.initLoader(REPO_LOADER_ID, bundle, ProgramActivity.this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mSucceeded = false;
            showErrorMessage(getString(R.string.no_internet_connection));
        }
    }

    @Override
    public Loader<LabResponse> onCreateLoader(int id, Bundle args) {
        return new InfoLoader(ProgramActivity.this, args.getString("URL"));
    }

    @Override
    public void onLoadFinished(Loader<LabResponse> loader, LabResponse labResponse) {
        mProgressBar.setVisibility(View.GONE);

        if (labResponse == null) {
            mSucceeded = false;
            Toast.makeText(ProgramActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
            return;
        }

        if (labResponse.isValid()) {
            mProgramBaseUrl = labResponse.getGithub_raw_content() + "/" +
                    labResponse.getOrganization() + "/" +
                    labResponse.getRepository() + "/" +
                    labResponse.getBranch();
            mSucceeded = true;
            mProgramAdapter.clear();
            mProgramAdapter.addAll(labResponse.getLabExperiments());
//                mLinkToRepo = labResponse.getLinkToRepo();
//            invalidateOptionsMenu();
        } else {
            mSucceeded = false;
            showErrorMessage(labResponse.getInvalidationMessage());
        }
    }

    @Override
    public void onLoaderReset(Loader<LabResponse> loader) {
        mProgramAdapter.clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SUCCEEDED_KEY, mSucceeded);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onContentFileClick(ContentFile file) {
        Intent intent = new Intent(ProgramActivity.this, DisplayActivity.class);
        intent.putExtra(ConstantVariables.title_intent_tag, file.getFileName());
        intent.putExtra(ConstantVariables.url_intent_tag, mProgramBaseUrl + "/" + file.getFileName());
        startActivity(intent);
    }

    private void showErrorMessage(String error) {
        mEmptyTextView.setVisibility(View.VISIBLE);
        mEmptyTextView.setText(error);
    }
}
