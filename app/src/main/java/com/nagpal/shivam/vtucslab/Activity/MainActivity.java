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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nagpal.shivam.vtucslab.Adapter.ContentAdapter;
import com.nagpal.shivam.vtucslab.Adapter.InfoAdapter;
import com.nagpal.shivam.vtucslab.Loader.InfoLoader;
import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.Utility.ConstantVariables;
import com.nagpal.shivam.vtucslab.Utility.IndexJsonResponse;
import com.nagpal.shivam.vtucslab.Utility.Info;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<IndexJsonResponse> {

    private static final String ACTIVE_ITEM_KEY = "active_item_key";
    private static final String SUCCEEDED_KEY = "succeeded_key";
    private static final int NAV_LOADER_ID = 1;
    private static final int REPO_LOADER_ID = 2;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String URL = "https://raw.githubusercontent.com/VTU-CS-LAB/Init/master/root.json";

    private int mActiveItem;

    private Boolean mSucceeded = false;
    private String mLinkToRepo;

    private ContentAdapter mProgramContentAdapter;
    //    private InfoAdapter mProgramInfoAdapter;
    private InfoAdapter mNavInfoAdapter;
    private ListView mNavListView;
    //    private ListView mProgramListView;
    private RecyclerView mProgramRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mEmptyTextView;
    private DrawerLayout mDrawerLayout;
    private SharedPreferences mSharedPreferences;

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

        mNavInfoAdapter = new InfoAdapter(MainActivity.this,
                new ArrayList<Info>(),
                R.layout.text_view_layout);

        mNavListView.setAdapter(mNavInfoAdapter);

        mProgramContentAdapter = new ContentAdapter(MainActivity.this,
                new ArrayList<Info>());

        mProgramRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        mProgramRecyclerView.setHasFixedSize(true);
        mProgramRecyclerView.setAdapter(mProgramContentAdapter);


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        final LoaderManager loaderManager = getLoaderManager();

        if (!mSucceeded) {
//            logd("Destroying Navigation Loader.");
            loaderManager.destroyLoader(NAV_LOADER_ID);
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            loaderManager.initLoader(NAV_LOADER_ID, null, MainActivity.this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mSucceeded = false;
            showErrorMessage(getString(R.string.no_internet_connection));
        }


        mNavListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mActiveItem != i) {
                    mActiveItem = i;

                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putInt(ACTIVE_ITEM_KEY, mActiveItem);
                    editor.apply();

                    loaderManager.destroyLoader(REPO_LOADER_ID);
                }

                if (!mSucceeded) {
                    loaderManager.destroyLoader(REPO_LOADER_ID);
                }

                mEmptyTextView.setVisibility(View.GONE);

                Info info = (Info) adapterView.getItemAtPosition(i);
                setTitle(info.getTitle());
                Bundle bundle = new Bundle();
                bundle.putString("URL", info.getUrl());
                loaderManager.initLoader(REPO_LOADER_ID, bundle, MainActivity.this);
                mDrawerLayout.closeDrawer(Gravity.START);
            }
        });

        mProgramContentAdapter.setItemClickHandler(new ContentAdapter.ItemClickHandler() {
            @Override
            public void onClick(Info info, int position) {
                Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                intent.putExtra(ConstantVariables.title_intent_tag, info.getTitle());
                intent.putExtra(ConstantVariables.url_intent_tag, info.getUrl());
                startActivity(intent);
            }
        });

    }

    private void closeNavigationDrawer() {
        mDrawerLayout.closeDrawer(Gravity.START, true);
    }

    void initViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout_main_activity);
        mEmptyTextView = findViewById(R.id.empty_text_view_main);
        mNavListView = findViewById(R.id.nav_list_main_activity);
        mProgramRecyclerView = findViewById(R.id.main_recycler_view_content);
        mProgressBar = findViewById(R.id.progress_bar_main);
    }

    @Override
    public Loader<IndexJsonResponse> onCreateLoader(int i, Bundle bundle) {
//        logd("OnCreateLoader:  " + i);
        if (i == NAV_LOADER_ID) {
            return new InfoLoader(MainActivity.this, URL);
        } else {
            return new InfoLoader(MainActivity.this, bundle.getString("URL"));
        }
    }

    @Override
    public void onLoadFinished(Loader<IndexJsonResponse> loader, IndexJsonResponse indexJsonResponse) {

        mProgressBar.setVisibility(View.GONE);


        if (indexJsonResponse == null) {
//            logd("First error");
            mSucceeded = false;
            Toast.makeText(MainActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
            return;
        }

        if (indexJsonResponse.getValid()) {
            if (indexJsonResponse.getInfoList().isEmpty()) {
//                logd("Second Error");
                mSucceeded = false;
                Toast.makeText(MainActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                return;
            }
            int loaderId = loader.getId();
//            logd("On load finished: " + loaderId);
            if (loaderId == NAV_LOADER_ID) {
                mNavInfoAdapter.clear();
                mNavInfoAdapter.addAll(indexJsonResponse.getInfoList());

//                logd("Selecting Item for first Time.");
                mNavListView.performItemClick(mNavListView.getChildAt(mActiveItem), mActiveItem, mNavListView.getAdapter().getItemId(mActiveItem));
                mNavListView.setItemChecked(mActiveItem, true);
            } else {
                mSucceeded = true;
                mProgramContentAdapter.clear();
                mProgramContentAdapter.addAll(indexJsonResponse.getInfoList());
                mLinkToRepo = indexJsonResponse.getLinkToRepo();
                invalidateOptionsMenu();
            }
        } else {
            mSucceeded = false;
            showErrorMessage(indexJsonResponse.getInvalidationMessage());
        }
    }

    @Override
    public void onLoaderReset(Loader<IndexJsonResponse> loader) {
        if (loader.getId() == NAV_LOADER_ID) {
            mNavInfoAdapter.clear();
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
}
