package com.obesityPredictAi.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "predicoes")
@Data
public class Predicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private String resultado;

    private LocalDate dataDoResultado;

    private Integer label;
}
