package com.nagpal.shivam.vtucslab.Loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.nagpal.shivam.vtucslab.Utility.FetchUtil;

public class RawStreamLoader extends AsyncTaskLoader<String> {
    private String mUrl;
    private String fetchedData;

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
