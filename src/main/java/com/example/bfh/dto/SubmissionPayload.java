package com.example.bfh.dto;

public class SubmissionPayload {
    private String finalQuery;

    public SubmissionPayload() {}
    public SubmissionPayload(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    public String getFinalQuery() { return finalQuery; }
    public void setFinalQuery(String finalQuery) { this.finalQuery = finalQuery; }
}
