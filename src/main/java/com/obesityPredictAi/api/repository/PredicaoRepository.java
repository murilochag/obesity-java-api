package com.obesityPredictAi.api.repository;

import com.obesityPredictAi.api.model.Predicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredicaoRepository extends JpaRepository<Predicao, Integer> {

}
