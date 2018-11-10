package com.nagpal.shivam.vtucslab.Model;

public class ContentFile {
    private String title;
    private String url;
    private String language;
    private String contentFileOrder;

    public String getTitle() {
        return title;
    }

    public ContentFile setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ContentFile setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public ContentFile setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getContentFileOrder() {
        return contentFileOrder;
    }

    public ContentFile setContentFileOrder(String contentFileOrder) {
        this.contentFileOrder = contentFileOrder;
        return this;
    }
}
