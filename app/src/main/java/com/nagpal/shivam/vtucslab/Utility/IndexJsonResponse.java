package com.nagpal.shivam.vtucslab.Utility;

import java.util.ArrayList;
import java.util.List;

public class IndexJsonResponse {
    private ArrayList<Info> infoList = new ArrayList<>();
    private Boolean isValid;
    private String invalidationMessage;
    private String linkToRepo;

    public IndexJsonResponse() {
    }

    public ArrayList<Info> getInfoList() {
        return infoList;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public String getInvalidationMessage() {
        return invalidationMessage;
    }

    public void setInvalidationMessage(String invalidationMessage) {
        this.invalidationMessage = invalidationMessage;
    }

    public String getLinkToRepo() {
        return linkToRepo;
    }

    public void setLinkToRepo(String linkToRepo) {
        this.linkToRepo = linkToRepo;
    }
}
