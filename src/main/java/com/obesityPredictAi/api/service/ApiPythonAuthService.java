package com.obesityPredictAi.api.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ApiPythonAuthService {

    private final RestTemplate restTemplate = new RestTemplate();
    private String token;
    private LocalDateTime tokenExpiration;

    public String getToken() {
        if (token == null || isTokenExpired()) {
            authenticate();
        }
        return token;
    }

    private boolean isTokenExpired() {
        return tokenExpiration == null || LocalDateTime.now().isAfter(tokenExpiration);
    }

    private void authenticate() {
        String loginUrl = "http://72.60.7.65:5000/login";

        Map<String, String> loginBody = Map.of(
                "usuario", "murilochag",
                "senha", "senha123"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(loginBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(loginUrl, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || !response.getBody().containsKey("token")) {
            throw new RuntimeException("Falha ao autenticar na API Python");
        }

        this.token = response.getBody().get("token").toString();

        // Aqui você ajusta para o tempo real de expiração do seu token JWT
        // Se a API não te diz o tempo, você pode deixar algo conservador, tipo 50 minutos
        this.tokenExpiration = LocalDateTime.now().plusMinutes(50);
    }
}

