package com.nagpal.shivam.vtucslab.Loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.nagpal.shivam.vtucslab.Utility.FetchUtil;
import com.nagpal.shivam.vtucslab.Utility.IndexJsonResponse;
import com.nagpal.shivam.vtucslab.Utility.ProgramInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProgramInfoLoader extends AsyncTaskLoader<IndexJsonResponse> {
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
    public IndexJsonResponse loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        String jsonResponse = FetchUtil.fetchData(mUrl);
        return extractFeaturesFromJson(jsonResponse);
    }

    private IndexJsonResponse extractFeaturesFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        IndexJsonResponse indexJsonResponse = new IndexJsonResponse();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            indexJsonResponse.setValid(root.getBoolean("is_valid"));
            if (indexJsonResponse.getValid()) {
                JSONArray jsonArray = root.getJSONArray("index");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject info = jsonArray.getJSONObject(i);
                    String title = info.getString("title");
                    String url = info.getString("url");
                    indexJsonResponse.getProgramInfoList().add(new ProgramInfo(title, url));
                }
            } else {
                indexJsonResponse.setInvalidationMessage(root.getString("invalidation_message"));
            }
        } catch (JSONException e) {
            Log.e("Loader", "Error Parsing JSON", e);
        }
        return indexJsonResponse;
    }
}
