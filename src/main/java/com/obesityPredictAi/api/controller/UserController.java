package com.obesityPredictAi.api.controller;

import com.obesityPredictAi.api.DTO.UsuarioDto;
import com.obesityPredictAi.api.model.Usuario;
import com.obesityPredictAi.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "http://localhost:5500")
public class UserController {

    @Autowired
    private UsuarioRepository repository;

    @GetMapping("/me")
    public ResponseEntity<UsuarioDto> getUsuario() {
        // Extrai o objeto Usuario do SecurityContextHolder
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Opcional: buscar no repositório para garantir dados atualizados
        return repository.findById(usuario.getId())
                .map(u -> ResponseEntity.ok(toDto(u)))
                .orElse(ResponseEntity.notFound().build());

        // Alternativa: retornar diretamente o usuario do contexto, sem consultar o banco
        // return ResponseEntity.ok(toDto(usuario));
    }

    private UsuarioDto toDto(Usuario usuario) {
        return new UsuarioDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getGenero(),
                usuario.getIdade(),
                usuario.getAltura(),
                usuario.getPeso()
        );
    }
}
