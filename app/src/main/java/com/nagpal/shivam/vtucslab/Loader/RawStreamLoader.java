package com.nagpal.shivam.vtucslab.Loader;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.nagpal.shivam.vtucslab.Utility.FetchUtil;

public class RawStreamLoader extends AsyncTaskLoader<String> {
    private final String mUrl;
    private String fetchedData;

    // TODO: Implement Local Data Saving.
    public RawStreamLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if (fetchedData != null) {
            deliverResult(fetchedData);
        } else {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        fetchedData = FetchUtil.fetchData(mUrl);
        return fetchedData;
    }
}
