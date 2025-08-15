package com.obesityPredictAi.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obesityPredictAi.api.DTO.PredicaoDto;
import com.obesityPredictAi.api.ENUM.ObesidadeLabel;
import com.obesityPredictAi.api.model.Predicao;
import com.obesityPredictAi.api.model.Usuario;
import com.obesityPredictAi.api.repository.PredicaoRepository;
import com.obesityPredictAi.api.repository.UsuarioRepository;
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


    private final String PYTHON_API_URL = "http://localhost:5000/predict";

    private final RestTemplate restTemplate = new RestTemplate();

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
            // Monta a entrada para a API Python
            List<Object> entrada = construirEntrada(predicaoDto);
            Map<String, Object> body = Map.of("entrada", entrada);

            // Configura cabeçalhos da requisição
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Chamada para a API Python
            String pythonUrl = "http://localhost:5000/predict";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(pythonUrl, requestEntity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Erro na chamada da API Python");
            }

            // Interpreta a resposta JSON da API Python
            Map<String, Object> respostaPython = parseJson(response.getBody());

            Integer labelNumerica = (Integer) respostaPython.get("label");
            ObesidadeLabel labelDescricao = ObesidadeLabel.fromCode(labelNumerica);

            // Busca o usuário pelo ID recebido no DTO
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(predicaoDto.usuario_id());
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuário não encontrado");
            }
            Usuario usuario = usuarioOpt.get();

            // Cria e salva a predição
            Predicao predicao = new Predicao();
            predicao.setUsuario(usuario);
            predicao.setLabel(labelNumerica);
            predicao.setResultado(labelDescricao.getDescricao());
            predicao.setDataDoResultado(LocalDate.now());

            repository.save(predicao);

            // Retorna a resposta final
            Map<String, Object> respostaFinal = Map.of(
                    "codigo", labelNumerica,
                    "descricao", labelDescricao.getDescricao()
            );

            return ResponseEntity.ok(respostaFinal);

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
