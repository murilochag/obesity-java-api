package com.obesityPredictAi.api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // criacao metodos getters e setters
@AllArgsConstructor // criacao de um construtor
@NoArgsConstructor // criacao de um construtor sem argumentos
public class LoginResponseDTO {
    private String token;
}
