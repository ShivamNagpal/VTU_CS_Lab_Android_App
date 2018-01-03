package com.nagpal.shivam.vtudslab.Loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.nagpal.shivam.vtudslab.Utility.FetchUtil;

public class ProgramContentLoader extends AsyncTaskLoader<String> {
    private String mUrl;

    public ProgramContentLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        return FetchUtil.fetchData(mUrl);
    }
}
