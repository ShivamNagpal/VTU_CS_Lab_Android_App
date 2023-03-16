package com.nagpal.shivam.vtucslab.loaders;


import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.nagpal.shivam.vtucslab.models.LabResponse;
import com.nagpal.shivam.vtucslab.services.VtuCsLabService;

import java.io.IOException;

import retrofit2.Call;

public class InfoLoader extends AsyncTaskLoader<LabResponse> {
    private final String mUrl;
    private LabResponse mLabResponse;

    public InfoLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if (mLabResponse != null) {
            deliverResult(mLabResponse);
        } else {
            forceLoad();
        }

    }

    @Override
    public LabResponse loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        VtuCsLabService vtuCSLabService = VtuCsLabService.Companion.getInstance();
        Call<LabResponse> labResponseCall = vtuCSLabService.getLabResponse(mUrl);
        try {
            mLabResponse = labResponseCall.execute().body();
            return mLabResponse;
        } catch (IOException e) {
            return null;
        }
    }
}
