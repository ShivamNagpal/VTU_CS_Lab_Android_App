package com.nagpal.shivam.vtucslab.Utility;

import java.util.ArrayList;
import java.util.List;

public class IndexJsonResponse {
    private List<ProgramInfo> programInfoList = new ArrayList<>();
    private Boolean isValid;
    private String invalidationMessage;

    public IndexJsonResponse() {
    }

    public List<ProgramInfo> getProgramInfoList() {
        return programInfoList;
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
}
