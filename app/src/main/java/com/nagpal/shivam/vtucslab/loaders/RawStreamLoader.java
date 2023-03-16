package com.nagpal.shivam.vtucslab.loaders;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.nagpal.shivam.vtucslab.services.VtuCsLabService;

import java.io.IOException;

import retrofit2.Call;

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
        if (mUrl == null) {
            return null;
        }
        VtuCsLabService vtuCSLabService = VtuCsLabService.Companion.getInstance();
        Call<String> stringCall = vtuCSLabService.fetchRawResponse(mUrl);
        try {
            fetchedData = stringCall.execute().body();
            return fetchedData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
