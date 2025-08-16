package com.obesityPredictAi.api.DTO;

public record HistoricoRespostaDTO<T>(
        T data,
        String message
) {}
