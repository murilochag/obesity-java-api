package com.obesityPredictAi.api.repository;

import com.obesityPredictAi.api.model.Predicao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredicaoRepository extends JpaRepository<Predicao, Integer> {
    // buscando pelo id do usuario na tabela Predicao 
    // List<Predicao> findByUsuarioId(Integer usuarioId);
     List<Predicao> findByUsuarioIdOrderByDataDoResultadoAsc(Integer usuarioId);
}
