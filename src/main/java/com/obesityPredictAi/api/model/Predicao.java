package com.obesityPredictAi.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "predicoes")
@Data
@Getter
@Setter
public class Predicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    // definindo um atributo do tipo usuario para relacionar as tabelas Predicao e Usuario
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    private String resultado;

    private LocalDate dataDoResultado;

    private Integer label;
}
