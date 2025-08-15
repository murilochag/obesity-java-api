package com.obesityPredictAi.api.repository;

import com.obesityPredictAi.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
