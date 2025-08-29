package com.example.bfh.service;

import com.example.bfh.client.HiringClient;
import com.example.bfh.dto.GenerateWebhookRequest;
import com.example.bfh.dto.GenerateWebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QualifierService {
    private static final Logger log = LoggerFactory.getLogger(QualifierService.class);

    private final HiringClient client;

    @Value("${bfh.user.name}")
    private String name;

    @Value("${bfh.user.regNo}")
    private String regNo;

    @Value("${bfh.user.email}")
    private String email;

    public QualifierService(HiringClient client) {
        this.client = client;
    }

    public void execute() {
        GenerateWebhookResponse resp = client.generateWebhook(new GenerateWebhookRequest(name, regNo, email));
        String finalQuery = buildFinalQueryForAssignment(regNo);
        log.info("Determined final SQL query for regNo {}: {}", regNo, finalQuery);
        client.submitFinalQuery(resp.getWebhookUrl(), resp.getAccessToken(), finalQuery);
        log.info("Submission completed successfully.");
    }

    private String buildFinalQueryForAssignment(String regNo) {
        int lastTwo = extractLastTwoDigits(regNo);
        boolean isEven = lastTwo % 2 == 0;
        if (isEven) {
            return (
                "SELECT e1.EMP_ID,\n" +
                "       e1.FIRST_NAME,\n" +
                "       e1.LAST_NAME,\n" +
                "       d.DEPARTMENT_NAME,\n" +
                "       COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT\n" +
                "FROM EMPLOYEE e1\n" +
                "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID\n" +
                "LEFT JOIN EMPLOYEE e2\n" +
                "       ON e1.DEPARTMENT = e2.DEPARTMENT\n" +
                "      AND e2.DOB > e1.DOB\n" +
                "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME\n" +
                "ORDER BY e1.EMP_ID DESC;"
            );
        } else {
            return "SELECT 1 AS placeholder_for_question_1;";
        }
    }

    private int extractLastTwoDigits(String regNo) {
        if (regNo == null || regNo.isBlank()) return 0;
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < regNo.length(); i++) {
            char c = regNo.charAt(i);
            if (Character.isDigit(c)) digits.append(c);
        }
        String s = digits.toString();
        if (s.isEmpty()) return 0;
        String tail = s.length() >= 2 ? s.substring(s.length() - 2) : s;
        try { return Integer.parseInt(tail); } catch (NumberFormatException e) { return 0; }
    }
}
