package com.nagpal.shivam.vtucslab.Activity;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nagpal.shivam.vtucslab.Adapter.ContentAdapter;
import com.nagpal.shivam.vtucslab.Adapter.NavigationAdapter;
import com.nagpal.shivam.vtucslab.Loader.InfoLoader;
import com.nagpal.shivam.vtucslab.Model.LabExperiment;
import com.nagpal.shivam.vtucslab.Model.LabResponse;
import com.nagpal.shivam.vtucslab.Model.Laboratory;
import com.nagpal.shivam.vtucslab.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<LabResponse>, NavigationAdapter.NavigationAdapterItemClickHandler, ContentAdapter.ItemClickHandler {

    private static final String ACTIVE_ITEM_KEY = "active_item_key";
    private static final String SUCCEEDED_KEY = "succeeded_key";
    private static final int NAV_LOADER_ID = 1;
    private static final int REPO_LOADER_ID = 2;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String URL = "https://raw.githubusercontent.com/vtucs/Index_v3/master/Index_v3.json";

    private int mActiveItem;

    private Boolean mSucceeded = false;
    private String mLinkToRepo;

    private ContentAdapter mProgramContentAdapter;

    private NavigationAdapter mNavigationAdapter;
    private RecyclerView mNavigationRecyclerView;
    private RecyclerView mProgramRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;
    private DrawerLayout mDrawerLayout;
    private SharedPreferences mSharedPreferences;
    private LoaderManager mLoaderManager;
    private String mLaboratoryBaseUrl;
    private String mProgramBaseUrl;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        if (mSucceeded) {
            MenuItem gitRepoMenuItem = menu.findItem(R.id.menu_item_git_repo_main_activity);
            gitRepoMenuItem.setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_item_refresh:
                recreate();
                return true;

            case R.id.menu_item_git_repo_main_activity:
                if (mLinkToRepo != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mLinkToRepo));
                    startActivity(intent);
                }
                return true;

            case R.id.menu_item_rate_app_main_activity:
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException e) {
                    Log.e(LOG_TAG, "Play Store Not Installed", e);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(LOG_TAG, "OnCreate Method Called.");

        if (savedInstanceState != null) {
            mSucceeded = savedInstanceState.getBoolean(SUCCEEDED_KEY, false);
        }

        mSharedPreferences = getPreferences(MODE_PRIVATE);
        mActiveItem = mSharedPreferences.getInt(ACTIVE_ITEM_KEY, 0);

        Toolbar toolbar = findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(toolbar);

        initViews();

        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(MainActivity.this,
                        mDrawerLayout,
                        toolbar,
                        R.string.drawer_open,
                        R.string.drawer_close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ImageButton navigationDrawerBackButton = findViewById(R.id.main_navigation_view_image_button_back);
        navigationDrawerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeNavigationDrawer();
            }
        });

        mNavigationAdapter = new NavigationAdapter(MainActivity.this, new ArrayList<Laboratory>());

        mNavigationRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        mNavigationRecyclerView.setHasFixedSize(true);

        mNavigationRecyclerView.setAdapter(mNavigationAdapter);

        mProgramContentAdapter = new ContentAdapter(MainActivity.this, new ArrayList<LabExperiment>());

        mProgramRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        mProgramRecyclerView.setHasFixedSize(true);
        mProgramRecyclerView.setAdapter(mProgramContentAdapter);


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        mLoaderManager = getLoaderManager();

        if (!mSucceeded) {
//            logd("Destroying Navigation Loader.");
            mLoaderManager.destroyLoader(NAV_LOADER_ID);
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            mLoaderManager.initLoader(NAV_LOADER_ID, null, MainActivity.this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mSucceeded = false;
            showErrorMessage(getString(R.string.no_internet_connection));
        }


        mNavigationAdapter.setNavigationAdapterItemClickHandler(this);

        mProgramContentAdapter.setItemClickHandler(this);

    }

    private void closeNavigationDrawer() {
        mDrawerLayout.closeDrawer(Gravity.START, true);
    }

    void initViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout_main_activity);
        mEmptyTextView = findViewById(R.id.empty_text_view_main);
        mNavigationRecyclerView = findViewById(R.id.main_recycler_view_navigation);
        mProgramRecyclerView = findViewById(R.id.main_recycler_view_content);
        mProgressBar = findViewById(R.id.progress_bar_main);
    }

    @Override
    public Loader<LabResponse> onCreateLoader(int i, Bundle bundle) {
//        logd("OnCreateLoader:  " + i);
        if (i == NAV_LOADER_ID) {
            return new InfoLoader(MainActivity.this, URL);
        } else {
            return new InfoLoader(MainActivity.this, bundle.getString("URL"));
        }
    }

    @Override
    public void onLoadFinished(Loader<LabResponse> loader, LabResponse labResponse) {

        mProgressBar.setVisibility(View.GONE);


        if (labResponse == null) {
//            logd("First error");
            mSucceeded = false;
            Toast.makeText(MainActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
            return;
        }

        if (labResponse.isValid()) {
//            if (labResponse.getInfoList().isEmpty()) {
////                logd("Second Error");
//                mSucceeded = false;
//                Toast.makeText(MainActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
//                return;
//            }
            int loaderId = loader.getId();
//            logd("On load finished: " + loaderId);
            if (loaderId == NAV_LOADER_ID) {
                mLaboratoryBaseUrl = labResponse.getGithub_raw_content() + "/" +
                        labResponse.getOrganization() + "/" +
                        labResponse.getRepository() + "/" +
                        labResponse.getBranch();
                mNavigationAdapter.clear();
                mNavigationAdapter.addAll(labResponse.getLaboratories());


//                logd("Selecting Item for first Time.");
                // TODO: Implement Tint Effect on Item Selected
//                mNavListView.performItemClick(mNavListView.getChildAt(mActiveItem), mActiveItem, mNavListView.getAdapter().getItemId(mActiveItem));
//                mNavListView.setItemChecked(mActiveItem, true);
            } else {
                mProgramBaseUrl = labResponse.getGithub_raw_content() + "/" +
                        labResponse.getOrganization() + "/" +
                        labResponse.getRepository() + "/" +
                        labResponse.getBranch();
                mSucceeded = true;
                mProgramContentAdapter.clear();
                mProgramContentAdapter.addAll(labResponse.getLabExperiments());
//                mLinkToRepo = labResponse.getLinkToRepo();
                invalidateOptionsMenu();
            }
        } else {
            mSucceeded = false;
            showErrorMessage(labResponse.getInvalidationMessage());
        }
    }

    @Override
    public void onLoaderReset(Loader<LabResponse> loader) {
        if (loader.getId() == NAV_LOADER_ID) {
            mNavigationAdapter.clear();
        } else {
            mProgramContentAdapter.clear();
        }
    }

    private void showErrorMessage(String error) {
        mEmptyTextView.setVisibility(View.VISIBLE);
        mEmptyTextView.setText(error);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SUCCEEDED_KEY, mSucceeded);
        super.onSaveInstanceState(outState);
    }

//    private void logd(String str) {
//        Log.d(LOG_TAG, str);
//    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            closeNavigationDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNavigationAdapterItemClick(Laboratory laboratory, int i) {
        if (mActiveItem != i) {
            mActiveItem = i;

            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(ACTIVE_ITEM_KEY, mActiveItem);
            editor.apply();

            mLoaderManager.destroyLoader(REPO_LOADER_ID);
        }

        if (!mSucceeded) {
            mLoaderManager.destroyLoader(REPO_LOADER_ID);
        }

        mEmptyTextView.setVisibility(View.GONE);

        setTitle(laboratory.getTitle());
        Bundle bundle = new Bundle();
        String url = mLaboratoryBaseUrl + "/" + laboratory.getFileName();

        bundle.putString("URL", url);
        mLoaderManager.initLoader(REPO_LOADER_ID, bundle, MainActivity.this);
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    @Override
    public void onContentFileClick(LabExperiment labExperiment, int position) {
        Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
//        intent.putExtra(ConstantVariables.title_intent_tag, info.getTitle());
//        intent.putExtra(ConstantVariables.url_intent_tag, info.getUrl());
        startActivity(intent);
    }
}
