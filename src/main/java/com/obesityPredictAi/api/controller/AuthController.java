package com.obesityPredictAi.api.controller;

import com.obesityPredictAi.api.model.Token;
import com.obesityPredictAi.api.model.Usuario;
import com.obesityPredictAi.api.repository.TokenRepository;
import com.obesityPredictAi.api.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.obesityPredictAi.api.DTO.LoginRequestDTO;
import com.obesityPredictAi.api.DTO.LoginResponseDTO;
import com.obesityPredictAi.api.DTO.RegisterRequestDTO;
import com.obesityPredictAi.api.service.AuthService;

import java.time.Instant;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO request){
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request){
        // autentica e pega o Usuario
        Authentication auth = authService.authenticate(request); // vamos criar esse método
        Usuario usuario = (Usuario) auth.getPrincipal();

        // gera token
        String token = tokenService.generateToken(usuario);

        // salva no banco
        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setUsuario(usuario);
        tokenEntity.setDataCriacao(Instant.now());
        tokenEntity.setDataExpiracao(Instant.now().plusSeconds(7200)); // 2 horas
        tokenEntity.setAtivo(true);
        tokenRepository.save(tokenEntity);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = recoverToken(request);

        if (token != null) {
            tokenRepository.findByTokenAndAtivoTrue(token).ifPresent(t -> {
                t.setAtivo(false); // marca como inativo
                tokenRepository.save(t);
            });
        }

        return ResponseEntity.ok().build();
    }

    // método para extrair token do header
    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
