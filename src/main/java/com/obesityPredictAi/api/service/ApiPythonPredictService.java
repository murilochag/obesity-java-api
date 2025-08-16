package com.obesityPredictAi.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ApiPythonPredictService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ApiPythonAuthService authService;

    public ApiPythonPredictService(ApiPythonAuthService authService) {
        this.authService = authService;
    }

    public Map<String, Object> predict(List<Object> entrada) {
        String token = authService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(
                Map.of("entrada", entrada),
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://72.60.7.65:5000/predict",
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erro na chamada da API Python");
        }

        return parseJson(response.getBody());
    }

    private Map<String, Object> parseJson(String json) {
        try {
            return new ObjectMapper().readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao parsear JSON", e);
        }
    }
}


