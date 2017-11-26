package com.nagpal.shivam.vtudslab;

public class ProgramInfo {
    private String mTitle;
    private String mUrl;

    public ProgramInfo(String title, String url){
        mTitle = title;
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }
}
