package com.obesityPredictAi.api.DTO;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String nome;
    private String email;
    private String senha;
}
