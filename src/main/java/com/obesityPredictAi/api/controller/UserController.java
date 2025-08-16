package com.obesityPredictAi.api.controller;

import com.obesityPredictAi.api.DTO.UsuarioDto;
import com.obesityPredictAi.api.model.Usuario;
import com.obesityPredictAi.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "http://localhost:5500")
public class UserController {

    @Autowired
    private UsuarioRepository repository;

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> getUsuario(@PathVariable Integer id) {
        return repository.findById(id)
                .map(usuario -> ResponseEntity.ok(toDto(usuario)))
                .orElse(ResponseEntity.notFound().build());
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
