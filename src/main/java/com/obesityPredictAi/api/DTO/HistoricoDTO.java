package com.obesityPredictAi.api.DTO;

import java.time.LocalDate;

public record HistoricoDTO(
        LocalDate data_resultado,
        String resultado_descricao,
        Integer resultado_id
) {}