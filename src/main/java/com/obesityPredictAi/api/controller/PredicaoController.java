package com.obesityPredictAi.api.controller;

import com.obesityPredictAi.api.model.Predicao;
import com.obesityPredictAi.api.repository.PredicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/predicao")
public class PredicaoController {

    @Autowired
    private PredicaoRepository repository;

    @GetMapping
    public ResponseEntity<List<Predicao>> getPredicoes() {
        List<Predicao> predicoes = repository.findAll();
        return ResponseEntity.ok(predicoes);
    }

    @PostMapping
    public ResponseEntity<Predicao> newPredicao(@RequestBody Predicao predicao) {
        Predicao salva = repository.save(predicao);
        return ResponseEntity.ok(salva);
    }
}
