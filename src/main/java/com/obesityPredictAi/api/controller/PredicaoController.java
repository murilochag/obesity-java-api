package com.obesityPredictAi.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obesityPredictAi.api.DTO.PredicaoDto;
import com.obesityPredictAi.api.ENUM.ObesidadeLabel;
import com.obesityPredictAi.api.model.Predicao;
import com.obesityPredictAi.api.model.Usuario;
import com.obesityPredictAi.api.repository.PredicaoRepository;
import com.obesityPredictAi.api.repository.UsuarioRepository;
import com.obesityPredictAi.api.service.ApiPythonAuthService;
import com.obesityPredictAi.api.service.ApiPythonPredictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;


import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/predicao")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class PredicaoController {

    @Autowired
    private PredicaoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ApiPythonPredictService apiPythonPredictService;

    // passando o id do usuario na rota
    @GetMapping("/{id}")
    // PathVariable para pegar as predições por id
    public ResponseEntity<?> getPredicoes(@PathVariable Integer id) {
        List<Predicao> predicoes = repository.findByUsuarioIdOrderByDataDoResultadoAsc(id);
        return ResponseEntity.ok(predicoes);
    }

    @PostMapping
    public ResponseEntity<?> newPredicao(@RequestBody PredicaoDto predicaoDto) {
        try {
            List<Object> entrada = construirEntrada(predicaoDto);
            Map<String, Object> respostaPython = apiPythonPredictService.predict(entrada);

            Integer labelNumerica = (Integer) respostaPython.get("label");
            ObesidadeLabel labelDescricao = ObesidadeLabel.fromCode(labelNumerica);

            Optional<Usuario> usuarioOpt = usuarioRepository.findById(predicaoDto.usuario_id());
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuário não encontrado");
            }
            Usuario usuario = usuarioOpt.get();

            Predicao predicao = new Predicao();
            predicao.setUsuario(usuario);
            predicao.setLabel(labelNumerica);
            predicao.setResultado(labelDescricao.getDescricao());
            predicao.setDataDoResultado(LocalDate.now());
            repository.save(predicao);

            usuarioRepository.findById(predicaoDto.usuario_id())
                    .ifPresent(userExist -> {
                        userExist.setGenero(predicaoDto.gender());
                        userExist.setIdade(predicaoDto.age());
                        userExist.setAltura(predicaoDto.height());
                        userExist.setPeso(predicaoDto.weight());
                        usuarioRepository.save(userExist);
                    });

            return ResponseEntity.ok(Map.of(
                    "codigo", labelNumerica,
                    "descricao", labelDescricao.getDescricao()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar predição: " + e.getMessage());
        }
    }


    // Método auxiliar para montar a lista de entrada
    private List<Object> construirEntrada(PredicaoDto dto) {
        return List.of(
                dto.gender(),
                dto.age(),
                dto.height(),
                dto.weight(),
                dto.familyHistory(),
                dto.highCalorieFoods(),
                dto.eatVegetables(),
                dto.mainMeals(),
                dto.foodBetweenMeals(),
                dto.smoke(),
                dto.waterIntake(),
                dto.monitorCalories(),
                dto.physicalActivities(),
                dto.timeTechnologicalDevices(),
                dto.alcohol(),
                dto.transportation()
        );
    }

    // Método auxiliar para parsear JSON em Map
    private Map<String, Object> parseJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<>() {});
    }

}
