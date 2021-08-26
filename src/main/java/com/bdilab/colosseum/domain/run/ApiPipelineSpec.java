package com.bdilab.colosseum.domain.run;

public class ApiPipelineSpec {


    /*Optional input field. The ID of the pipeline user uploaded before.*/
    private String pipelineId;

    /*Optional output field. The name of the pipeline. Not empty if the pipeline id is not empty.*/
    private String pipelineName;

    /*Optional input field. The marshalled raw argo JSON workflow. This will be deprecated when pipeline_manifest is in use.*/
    private String workflowManifest;

    /*Optional input field. The raw pipeline JSON spec.*/
    private String pipelineManifest;

    /*The parameter user provide to inject to the pipeline JSON. If a default value of a parameter exist in the JSON, the value user provided here will replace.*/
    /*ITEMS:apiParameter*/
    private Object[] parameters;

    public String getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public String getWorkflowManifest() {
        return workflowManifest;
    }

    public void setWorkflowManifest(String workflowManifest) {
        this.workflowManifest = workflowManifest;
    }

    public String getPipelineManifest() {
        return pipelineManifest;
    }

    public void setPipelineManifest(String pipelineManifest) {
        this.pipelineManifest = pipelineManifest;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
