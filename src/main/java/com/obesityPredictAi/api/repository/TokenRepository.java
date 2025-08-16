package com.obesityPredictAi.api.repository;

import com.obesityPredictAi.api.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenAndAtivoTrue(String token);
}
