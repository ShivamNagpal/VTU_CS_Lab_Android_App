package com.nagpal.shivam.vtucslab.Model;

public class LabResponse {
    private String context;
    private boolean isValid = true;
    private String invalidationMessage;
    private Laboratory[] laboratories;
    private LabExperiment[] labExperiments;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getInvalidationMessage() {
        return invalidationMessage;
    }

    public void setInvalidationMessage(String invalidationMessage) {
        this.invalidationMessage = invalidationMessage;
    }

    public LabExperiment[] getLabExperiments() {
        return labExperiments;
    }

    public LabResponse setLabExperiments(LabExperiment[] labExperiments) {
        this.labExperiments = labExperiments;
        return this;
    }

    public Laboratory[] getLaboratories() {
        return laboratories;
    }

    public LabResponse setLaboratories(Laboratory[] laboratories) {
        this.laboratories = laboratories;
        return this;
    }
}
