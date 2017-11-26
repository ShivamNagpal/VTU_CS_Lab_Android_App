package com.nagpal.shivam.vtudslab;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class ProgramLoader extends AsyncTaskLoader<String> {
    private String mUrl;

    public ProgramLoader(Context context, String url) {
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
