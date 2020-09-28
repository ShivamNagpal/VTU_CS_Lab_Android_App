package com.nagpal.shivam.vtucslab.activities;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.nagpal.shivam.vtucslab.adapters.NavigationAdapter;
import com.nagpal.shivam.vtucslab.loaders.InfoLoader;
import com.nagpal.shivam.vtucslab.models.LabResponse;
import com.nagpal.shivam.vtucslab.models.Laboratory;
import com.nagpal.shivam.vtucslab.R;

import java.util.ArrayList;

public class RepositoryActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<LabResponse>,
        NavigationAdapter.NavigationAdapterItemClickHandler {

    private static final int NAV_LOADER_ID = 1;
    private static final String SUCCEEDED_KEY = "succeeded_key";
    private static final String URL = "https://raw.githubusercontent.com/vtucs/Index_v3/master/Index_v3.json";
//    private static final String URL = "https://raw.githubusercontent.com/vtucs/Test_Index/master/TestIndex.json";

    private DrawerLayout mDrawerLayout;
    private LoaderManager mLoaderManager;
    private NavigationAdapter mRepositoryAdapter;
    private NavigationView mNavigationView;
    private ProgressBar mProgressBar;
    private RecyclerView mRepositoryRecyclerView;
    private TextView mEmptyTextView;
    private Toolbar mToolbar;
    private View mHeaderView;
    private boolean mSucceeded;
    private String mLaboratoryBaseUrl;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_item_privacy:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/ShivamNagpal/Privacy_Policies/blob/master/VTU_CS_LAB_MANUAL.md"));
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        boolean flag = false;
        switch (menuItem.getItemId()) {
            case R.id.menu_item_repository:
                flag = true;
                break;
            case R.id.menu_item_exit:
                exitApplication();
                flag = true;
                break;
        }
        if (flag) {
            closeNavigationDrawer();
        }
        return flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (savedInstanceState != null) {
            mSucceeded = savedInstanceState.getBoolean(SUCCEEDED_KEY, false);
        }

        initAndSetupViews();

        setupRepositoryAdapter();

        loadRepositories();
    }

    private void loadRepositories() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        mLoaderManager = LoaderManager.getInstance(this);

        if (!mSucceeded) {
            mLoaderManager.destroyLoader(NAV_LOADER_ID);
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            mLoaderManager.initLoader(NAV_LOADER_ID, null, RepositoryActivity.this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mSucceeded = false;
            showErrorMessage(getString(R.string.no_internet_connection));
        }

        mRepositoryAdapter.setNavigationAdapterItemClickHandler(this);
    }

    private void initAndSetupViews() {
        mDrawerLayout = findViewById(R.id.activity_repository_drawer_layout);
        setUpDrawerToggle();

        mNavigationView = findViewById(R.id.activity_repository_navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);

        mHeaderView = mNavigationView.getHeaderView(0);

        ImageButton navigationDrawerBackButton = mHeaderView.findViewById(R.id.activity_repository_image_button_close_drawer);
        navigationDrawerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeNavigationDrawer();
            }
        });

        mRepositoryRecyclerView = findViewById(R.id.activity_repository_recycler_view_repository);
        mRepositoryRecyclerView.setLayoutManager(new LinearLayoutManager(RepositoryActivity.this, LinearLayoutManager.VERTICAL, false));
        mRepositoryRecyclerView.setHasFixedSize(true);

        mEmptyTextView = findViewById(R.id.empty_text_view_main);

        mProgressBar = findViewById(R.id.progress_bar_main);
    }

    private void setUpDrawerToggle() {
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(RepositoryActivity.this,
                        mDrawerLayout,
                        mToolbar,
                        R.string.drawer_open,
                        R.string.drawer_close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void closeNavigationDrawer() {
        mDrawerLayout.closeDrawer(Gravity.START, true);
    }

    private void exitApplication() {
        this.finishAffinity();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, 1000);
    }

    private void setupRepositoryAdapter() {
        mRepositoryAdapter = new NavigationAdapter(RepositoryActivity.this, new ArrayList<Laboratory>());
        mRepositoryRecyclerView.setAdapter(mRepositoryAdapter);
    }

    @Override
    public Loader<LabResponse> onCreateLoader(int id, Bundle args) {
        return new InfoLoader(RepositoryActivity.this, URL);

    }

    @Override
    public void onLoadFinished(Loader<LabResponse> loader, LabResponse labResponse) {
        mProgressBar.setVisibility(View.GONE);

        if (labResponse == null) {
            mSucceeded = false;
            Toast.makeText(RepositoryActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
            return;
        }

        if (labResponse.isValid()) {
            mLaboratoryBaseUrl = labResponse.getGithub_raw_content() + "/" +
                    labResponse.getOrganization() + "/" +
                    labResponse.getRepository() + "/" +
                    labResponse.getBranch();

            mRepositoryAdapter.clear();
            mRepositoryAdapter.addAll(labResponse.getLaboratories());
        } else {
            mSucceeded = false;
            showErrorMessage(labResponse.getInvalidationMessage());
        }
    }

    @Override
    public void onLoaderReset(Loader<LabResponse> loader) {
        mRepositoryAdapter.clear();
    }

    @Override
    public void onNavigationAdapterItemClick(Laboratory laboratory, int i) {
        Intent intent = new Intent(RepositoryActivity.this, ProgramActivity.class);
        intent.putExtra(ProgramActivity.INTENT_LABORATORY, laboratory);
        intent.putExtra(ProgramActivity.INTENT_LABORATORY_BASE_URL, mLaboratoryBaseUrl);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SUCCEEDED_KEY, mSucceeded);
        super.onSaveInstanceState(outState);
    }

    private void showErrorMessage(String error) {
        mEmptyTextView.setVisibility(View.VISIBLE);
        mEmptyTextView.setText(error);
    }

}
