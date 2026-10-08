package com.cheque.online_cheque_book;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void sendEmail(
            String to,
            String subject,
            String message) {

        System.out.println("=================================");
        System.out.println("RESEND EMAIL SENDING STARTED");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);

        try {

            String json = """
                    {
                      "from": "Online Cheque Book System <onboarding@resend.dev>",
                      "to": ["%s"],
                      "subject": "%s",
                      "text": "%s"
                    }
                    """.formatted(
                            escapeJson(to),
                            escapeJson(subject),
                            escapeJson(message)
                    );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            System.out.println("Resend Response Status: "
                    + response.statusCode());

            System.out.println("Resend Response Body: "
                    + response.body());

            if (response.statusCode() >= 200
                    && response.statusCode() < 300) {

                System.out.println("EMAIL SENT SUCCESSFULLY");

            } else {

                System.out.println("EMAIL SENDING FAILED");
            }

            System.out.println("=================================");

        } catch (Exception e) {

            System.out.println("EMAIL SENDING FAILED");
            System.out.println("Error: " + e.getMessage());

            e.printStackTrace();

            System.out.println("=================================");
        }
    }

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}