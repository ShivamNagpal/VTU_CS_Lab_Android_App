package com.nagpal.shivam.vtucslab.Model;

public class LabExperiment {

    private String serialOrder;
    private LabExperimentSubPart[] labExperimentSubParts;

    public String getSerialOrder() {
        return serialOrder;
    }

    public LabExperiment setSerialOrder(String serialOrder) {
        this.serialOrder = serialOrder;
        return this;
    }

    public LabExperimentSubPart[] getLabExperimentSubParts() {
        return labExperimentSubParts;
    }

    public LabExperiment setLabExperimentSubParts(LabExperimentSubPart[] labExperimentSubParts) {
        this.labExperimentSubParts = labExperimentSubParts;
        return this;
    }
}
