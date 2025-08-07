package com.obesityPredictAi.api.repository;
import com.obesityPredictAi.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.security.core.userdetails.UserDetails;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    UserDetails findByEmail(String email);
}