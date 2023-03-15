package com.nagpal.shivam.vtucslab.models;

public class LabResponse {
    private String context;
    private boolean isValid = true;
    private String invalidationMessage;
    private Laboratory[] laboratories;
    private LabExperiment[] labExperiments;
    private String github_raw_content;
    private String organization;
    private String repository;
    private String branch;

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

    public String getGithub_raw_content() {
        return github_raw_content;
    }

    public void setGithub_raw_content(String github_raw_content) {
        this.github_raw_content = github_raw_content;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
