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

import com.nagpal.shivam.vtucslab.Adapter.InfoAdapter;
import com.nagpal.shivam.vtucslab.Loader.InfoLoader;
import com.nagpal.shivam.vtucslab.R;
import com.nagpal.shivam.vtucslab.Utility.ConstantVariables;
import com.nagpal.shivam.vtucslab.Utility.IndexJsonResponse;
import com.nagpal.shivam.vtucslab.Utility.Info;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<IndexJsonResponse> {

    private static final String ACTIVE_ITEM_KEY = "active_item_key";
    private static final String SUCCEEDED_KEY = "succeeded_key";
    private static final int NAV_LOADER_ID = 1;
    private static final int REPO_LOADER_ID = 2;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String URL = "https://raw.githubusercontent.com/VTU-CS-LAB/Init/master/root.json";

    private int activeItem;

    private Boolean succeeded = false;
    private String linkToRepo;

    private InfoAdapter programInfoAdapter;
    private InfoAdapter navInfoAdapter;
    private ListView navListView;
    private ListView programListView;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private DrawerLayout drawerLayout;
    private SharedPreferences sharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        if (succeeded) {
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
                if (linkToRepo != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(linkToRepo));
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
            succeeded = savedInstanceState.getBoolean(SUCCEEDED_KEY, false);
        }

        sharedPreferences = getPreferences(MODE_PRIVATE);
        activeItem = sharedPreferences.getInt(ACTIVE_ITEM_KEY, 0);

        Toolbar toolbar = findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(toolbar);

        initViews();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ImageButton navigationDrawerBackButton = findViewById(R.id.main_navigation_view_image_button_back);
        navigationDrawerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeNavigationDrawer();
            }
        });

        navInfoAdapter = new InfoAdapter(MainActivity.this, new ArrayList<Info>(), R.layout.text_view_layout);
        navListView.setAdapter(navInfoAdapter);

        programInfoAdapter = new InfoAdapter(MainActivity.this, new ArrayList<Info>(), R.layout.text_view_layout);
        programListView.setAdapter(programInfoAdapter);


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        final LoaderManager loaderManager = getLoaderManager();

        if (!succeeded) {
//            logd("Destroying Navigation Loader.");
            loaderManager.destroyLoader(NAV_LOADER_ID);
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            loaderManager.initLoader(NAV_LOADER_ID, null, MainActivity.this);
        } else {
            progressBar.setVisibility(View.GONE);
            succeeded = false;
            showErrorMessage(getString(R.string.no_internet_connection));
        }


        navListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (activeItem != i) {
                    activeItem = i;

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(ACTIVE_ITEM_KEY, activeItem);
                    editor.apply();

                    loaderManager.destroyLoader(REPO_LOADER_ID);
                }

                if (!succeeded) {
                    loaderManager.destroyLoader(REPO_LOADER_ID);
                }

                emptyTextView.setVisibility(View.GONE);

                Info info = (Info) adapterView.getItemAtPosition(i);
                setTitle(info.getTitle());
                Bundle bundle = new Bundle();
                bundle.putString("URL", info.getUrl());
                loaderManager.initLoader(REPO_LOADER_ID, bundle, MainActivity.this);
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        programListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Info info = (Info) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                intent.putExtra(ConstantVariables.title_intent_tag, info.getTitle());
                intent.putExtra(ConstantVariables.url_intent_tag, info.getUrl());
                startActivity(intent);
            }
        });

    }

    private void closeNavigationDrawer() {
        drawerLayout.closeDrawer(Gravity.START, true);
    }

    void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout_main_activity);
        emptyTextView = findViewById(R.id.empty_text_view_main);
        navListView = findViewById(R.id.nav_list_main_activity);
        programListView = findViewById(R.id.program_list_view);
        progressBar = findViewById(R.id.progress_bar_main);
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

        progressBar.setVisibility(View.GONE);


        if (indexJsonResponse == null) {
//            logd("First error");
            succeeded = false;
            Toast.makeText(MainActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
            return;
        }

        if (indexJsonResponse.getValid()) {
            if (indexJsonResponse.getInfoList().isEmpty()) {
//                logd("Second Error");
                succeeded = false;
                Toast.makeText(MainActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                return;
            }
            int loaderId = loader.getId();
//            logd("On load finished: " + loaderId);
            if (loaderId == NAV_LOADER_ID) {
                navInfoAdapter.clear();
                navInfoAdapter.addAll(indexJsonResponse.getInfoList());

//                logd("Selecting Item for first Time.");
                navListView.performItemClick(navListView.getChildAt(activeItem), activeItem, navListView.getAdapter().getItemId(activeItem));
                navListView.setItemChecked(activeItem, true);
            } else {
                succeeded = true;
                programInfoAdapter.clear();
                programInfoAdapter.addAll(indexJsonResponse.getInfoList());
                linkToRepo = indexJsonResponse.getLinkToRepo();
                invalidateOptionsMenu();
            }
        } else {
            succeeded = false;
            showErrorMessage(indexJsonResponse.getInvalidationMessage());
        }
    }

    @Override
    public void onLoaderReset(Loader<IndexJsonResponse> loader) {
        if (loader.getId() == NAV_LOADER_ID) {
            navInfoAdapter.clear();
        } else {
            programInfoAdapter.clear();
        }
    }

    private void showErrorMessage(String error) {
        emptyTextView.setVisibility(View.VISIBLE);
        emptyTextView.setText(error);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SUCCEEDED_KEY, succeeded);
        super.onSaveInstanceState(outState);
    }

//    private void logd(String str) {
//        Log.d(LOG_TAG, str);
//    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            closeNavigationDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
