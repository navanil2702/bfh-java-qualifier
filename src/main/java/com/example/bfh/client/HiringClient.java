package com.example.bfh.client;

import com.example.bfh.dto.GenerateWebhookRequest;
import com.example.bfh.dto.GenerateWebhookResponse;
import com.example.bfh.dto.SubmissionPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HiringClient {
    private static final Logger log = LoggerFactory.getLogger(HiringClient.class);

    private final RestTemplate restTemplate;

    @Value("${bfh.endpoints.generate}")
    private String generateUrl;

    @Value("${bfh.endpoints.fallbackSubmit}")
    private String fallbackSubmitUrl;

    public HiringClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GenerateWebhookResponse generateWebhook(GenerateWebhookRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GenerateWebhookRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GenerateWebhookResponse> response = restTemplate.exchange(
                generateUrl, HttpMethod.POST, entity, GenerateWebhookResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Failed to generate webhook; status=" + response.getStatusCode());
        }
        log.info("Webhook generated: {}", response.getBody());
        return response.getBody();
    }

    public void submitFinalQuery(String webhookUrl, String accessToken, String finalQuery) {
        String submitUrl = (webhookUrl != null && !webhookUrl.isBlank()) ? webhookUrl : fallbackSubmitUrl;

        SubmissionPayload payload = new SubmissionPayload(finalQuery);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);

        HttpEntity<SubmissionPayload> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                submitUrl, HttpMethod.POST, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            headers.set("Authorization", "Bearer " + accessToken);
            entity = new HttpEntity<>(payload, headers);
            response = restTemplate.exchange(submitUrl, HttpMethod.POST, entity, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("Submission failed; status=" + response.getStatusCode());
            }
        }
    }
}
