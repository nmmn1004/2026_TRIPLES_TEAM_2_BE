package com.team2.fabackend.global.sms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NcpSmsClient {
    private final RestTemplate restTemplate;

    @Value("${ncp.sms.service-id}")
    private String serviceId;

    @Value("${ncp.sms.access-key}")
    private String accessKey;

    @Value("${ncp.sms.secret-key}")
    private String secretKey;

    @Value("${ncp.sms.sender}")
    private String senderPhone;

    /**
     * SMS 발송 메인 메서드
     */
    public void sendSms(String phoneNumber, String content) {
        String apiUrl = "https://sens.apigw.ntruss.com/sms/v2/services/" + serviceId + "/messages";
        String uriPath = "/sms/v2/services/" + serviceId + "/messages";

        String timestamp = String.valueOf(System.currentTimeMillis());

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("type", "SMS");
            body.put("from", senderPhone);
            body.put("content", content);
            body.put("messages", List.of(Map.of("to", phoneNumber)));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-ncp-apigw-timestamp", timestamp);
            headers.set("x-ncp-iam-access-key", accessKey);
            headers.set("x-ncp-apigw-signature-v2", makeSignature(uriPath, timestamp));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(apiUrl, request, String.class);

            log.info("SMS sent to {} successfully", phoneNumber);

        } catch (Exception e) {
            log.warn("[SMS ERROR] 발송 실패. phone={}, content={}, error={}",
                    phoneNumber, content, e.getMessage());
        }
    }

    private String makeSignature(String uri, String timestamp) throws Exception {
        String space = " ";
        String newLine = "\n";
        String method = "POST";

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(uri)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawHmac);
    }
}