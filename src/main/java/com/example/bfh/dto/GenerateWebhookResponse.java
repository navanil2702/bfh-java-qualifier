package com.example.bfh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateWebhookResponse {
    @JsonProperty("webhook")
    private String webhookUrl;

    @JsonProperty("accessToken")
    private String accessToken;

    public String getWebhookUrl() { return webhookUrl; }
    public String getAccessToken() { return accessToken; }

    @Override
    public String toString() {
        return "GenerateWebhookResponse{" +
                "webhookUrl='" + webhookUrl + '\'' +
                ", accessToken='***'" +
                '}';
    }
}
