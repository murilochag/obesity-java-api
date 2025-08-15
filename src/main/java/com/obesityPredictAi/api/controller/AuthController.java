package com.obesityPredictAi.api.controller;

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

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDTO request){
        authService.register(request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request){
        String token = authService.login(request);
        
        // retorna um token com json
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
