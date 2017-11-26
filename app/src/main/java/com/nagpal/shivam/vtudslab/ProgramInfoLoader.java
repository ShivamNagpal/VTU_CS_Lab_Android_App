package com.nagpal.shivam.vtudslab;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProgramInfoLoader extends AsyncTaskLoader<List<ProgramInfo>> {
    private String mUrl;

    public ProgramInfoLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<ProgramInfo> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        String jsonResponse = FetchUtil.fetchData(mUrl);
        return extractFeaturesFromJson(jsonResponse);
    }

    private List<ProgramInfo> extractFeaturesFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<ProgramInfo> programInfoList = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray jsonArray = root.getJSONArray("index");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject info = jsonArray.getJSONObject(i);
                String title = info.getString("title");
                String url = info.getString("url");
                programInfoList.add(new ProgramInfo(title, url));
            }
        } catch (JSONException e) {
            Log.e("Loader", "Error Parsing JSON", e);
        }
        return programInfoList;
    }
}
