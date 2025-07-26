package com.obesityPredictAi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.obesityPredictAi.api.DTO.PredicaoDto;
import com.obesityPredictAi.api.ENUM.ObesidadeLabel;
import com.obesityPredictAi.api.model.Predicao;
import com.obesityPredictAi.api.repository.PredicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/predicao")
public class PredicaoController {

    @Autowired
    private PredicaoRepository repository;

    private final String PYTHON_API_URL = "http://localhost:5000/predict";

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public ResponseEntity<List<Predicao>> getPredicoes() {
        List<Predicao> predicoes = repository.findAll();
        return ResponseEntity.ok(predicoes);
    }

    @PostMapping
    public ResponseEntity<?> newPredicao(@RequestBody PredicaoDto predicaoDto) {
        try {
            // Montando a lista de entrada
            List<Object> entrada = Arrays.asList(
                predicaoDto.gender(),
                predicaoDto.age(),
                predicaoDto.height(),
                predicaoDto.weight(),
                predicaoDto.familyHistory(),
                predicaoDto.highCalorieFoods(),
                predicaoDto.eatVegetables(),
                predicaoDto.mainMeals(),
                predicaoDto.foodBetweenMeals(),
                predicaoDto.smoke(),
                predicaoDto.waterIntake(),
                predicaoDto.monitorCalories(),
                predicaoDto.physicalActivities(),
                predicaoDto.timeTechnologicalDevices(),
                predicaoDto.alcohol(),
                predicaoDto.transportation()
            );

            // Criando corpo do JSON para a API Python
            Map<String, Object> body = new HashMap<>();
            body.put("entrada", entrada);

            // Configurando cabeçalhos
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Fazendo chamada para a API Python
            String pythonUrl = "http://localhost:5000/predict";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(pythonUrl, requestEntity, String.class);

            // Verificando retorno da API Python
            if (!response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Erro na chamada da API Python");
            }

            // Converte o body da resposta (String JSON) em um Map
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> respostaPython = mapper.readValue(response.getBody(), Map.class);

            // Obtém o número da label e transforma em enum
            Integer labelNumerica = (Integer) respostaPython.get("label");
            ObesidadeLabel labelDescricao = ObesidadeLabel.fromCode(labelNumerica);

            // Monta a resposta final
            Map<String, Object> respostaFinal = new HashMap<>();
            respostaFinal.put("codigo", labelNumerica);
            respostaFinal.put("descricao", labelDescricao.getDescricao());

            // Aqui você pode salvar no banco, se quiser
            // Por exemplo:
            Predicao predicao = new Predicao();
            predicao.setLabel(labelNumerica);
            predicao.setResultado(labelDescricao.getDescricao());
            predicao.setDataDoResultado(LocalDate.now());
            repository.save(predicao);

            return ResponseEntity.ok(respostaFinal);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar predição: " + e.getMessage());
        }
    }
 }
