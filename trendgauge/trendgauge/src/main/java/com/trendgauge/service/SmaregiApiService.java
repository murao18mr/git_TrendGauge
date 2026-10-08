package com.trendgauge.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;

import java.time.LocalDate;

@Service
public class SmaregiApiService {
    private final RestClient restClient;

    public SmaregiApiService() {
        this.restClient = RestClient.create();
    }


    public boolean testConnection(String contractId, String clientId, String clientSecret) {

        String url = "https://id.smaregi.dev/app/" + contractId + "/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", "pos.transactions:read");

        try {
            String response = restClient.post()
                    .uri(url)
                    .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return response != null && response.contains("access_token");

        } catch (RestClientResponseException e) {
            return false;
        }
    }

    private String getAccessToken(String contractId, String clientId, String clientSecret) {
        String url = "https://id.smaregi.dev/app/" + contractId + "/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", "pos.transactions:read");

        JsonNode response = restClient.post()
                .uri(url)
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.hasNonNull("access_token")) {
            throw new IllegalStateException("アクセストークンを取得できませんでした");
        }

        String accessToken = response.get("access_token").asText();

        return accessToken;
    }

    public JsonNode getTransactions(
            String contractId,
            String clientId,
            String clientSecret,
            String smaregiStoreId,
            LocalDate date
    ) {
        if (smaregiStoreId == null || smaregiStoreId.isBlank()) {
            throw new IllegalArgumentException("スマレジ店舗IDが未設定です");
        }

        String accessToken = getAccessToken(
                contractId, clientId, clientSecret
        );

        String from = date.toString();
        String to = date.toString();

        ArrayNode allTransactions = JsonNodeFactory.instance.arrayNode();

        int page = 1;
        int limit = 100;

        while (true) {

            int currentPage = page;

            JsonNode transactions = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.smaregi.dev")
                            .path("/" + contractId + "/pos/transactions")
                            .queryParam("store_id", smaregiStoreId)
                            .queryParam("sum_date-from", from)
                            .queryParam("sum_date-to", to)
                            .queryParam("limit", limit)
                            .queryParam("page", currentPage)
                            .build())
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .body(JsonNode.class);

            if (transactions == null || !transactions.isArray()) {
                throw new IllegalStateException("取引データの形式が不正です");
            }

            for (JsonNode transaction : transactions) {
                allTransactions.add(transaction);
            }

            if (transactions.size() < limit) {
                break;
            }

            page++;
        }

        return allTransactions;
    }
}
