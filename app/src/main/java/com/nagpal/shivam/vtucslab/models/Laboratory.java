package com.nagpal.shivam.vtucslab.models;

import java.io.Serializable;

public class Laboratory implements Serializable {
    private String title;
    private String fileName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
