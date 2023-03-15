package com.nagpal.shivam.vtucslab.models;

public class LabExperimentSubPart {

    private String subSerialOrder;
    private ContentFile[] contentFiles;

    public String getSubSerialOrder() {
        return subSerialOrder;
    }

    public LabExperimentSubPart setSubSerialOrder(String subSerialOrder) {
        this.subSerialOrder = subSerialOrder;
        return this;
    }

    public ContentFile[] getContentFiles() {
        return contentFiles;
    }

    public LabExperimentSubPart setContentFiles(ContentFile[] contentFiles) {
        this.contentFiles = contentFiles;
        return this;
    }
}
