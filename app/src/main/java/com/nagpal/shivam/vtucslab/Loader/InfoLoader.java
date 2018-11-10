package com.nagpal.shivam.vtucslab.Loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.nagpal.shivam.vtucslab.Model.LabResponse;
import com.nagpal.shivam.vtucslab.Utility.FetchUtil;

public class InfoLoader extends AsyncTaskLoader<LabResponse> {
    private String mUrl;
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
        String jsonResponse = FetchUtil.fetchData(mUrl);
        mLabResponse = extractFeaturesFromJson(jsonResponse);
        return mLabResponse;
    }

    private LabResponse extractFeaturesFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        Gson gson = new Gson();
        LabResponse labResponse = gson.fromJson(jsonResponse, LabResponse.class);
        return labResponse;
    }
}
