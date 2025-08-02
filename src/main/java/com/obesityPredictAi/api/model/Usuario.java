package com.obesityPredictAi.api.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;

    private String nome;

    private String email;

    private String senha;

    // definindo relacao de um para muitos
    // falando que vai receber a predicao da tabela Predicao
    @OneToMany(mappedBy = "usuario")
    private List<Predicao> predicao;
}
