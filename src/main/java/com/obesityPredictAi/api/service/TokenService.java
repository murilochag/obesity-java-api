package com.obesityPredictAi.api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.obesityPredictAi.api.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // pegando chave secreta
    @Value("${api.security.token.secret}")
    private String secret;

    // criando token a partir da JWT
    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("obesity-predict-ai")
                    .withSubject(usuario.getEmail())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    // validar Token
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("obesity-predict-ai")
                    .build()
                    .verify(token)
                    .getSubject(); // se valido retorna o e-mail do usuario
        } catch (JWTVerificationException exception){
            return "";
        }
    }

    // calcula validade de token
    private Instant genExpirationDate() {
        // valido por duas horas
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")); 
    }
}
