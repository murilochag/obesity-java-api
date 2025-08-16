package com.obesityPredictAi.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obesityPredictAi.api.DTO.HistoricoDTO;
import com.obesityPredictAi.api.DTO.PredicaoDto;
import com.obesityPredictAi.api.DTO.HistoricoRespostaDTO;
import com.obesityPredictAi.api.ENUM.ObesidadeLabel;
import com.obesityPredictAi.api.model.Predicao;
import com.obesityPredictAi.api.model.Usuario;
import com.obesityPredictAi.api.model.Usuario;
import com.obesityPredictAi.api.repository.PredicaoRepository;
import com.obesityPredictAi.api.repository.UsuarioRepository;
import com.obesityPredictAi.api.service.ApiPythonAuthService;
import com.obesityPredictAi.api.service.ApiPythonPredictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/me")
    public ResponseEntity<HistoricoRespostaDTO<List<HistoricoDTO>>> getPredicoes() {
        // Obtém o usuário autenticado do SecurityContextHolder
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Busca histórico do usuário
        List<Predicao> predicoes = repository.findByUsuarioIdOrderByDataDoResultadoAsc(usuario.getId());

        // Converte Predicao -> HistoricoDTO
        List<HistoricoDTO> dadosFormatados = predicoes.stream().map(p -> {
            String resultado;
            try {
                resultado = ObesidadeLabel.fromCode(p.getLabel()).getDescricao();
            } catch (IllegalArgumentException e) {
                resultado = "Código desconhecido (" + p.getLabel() + ")";
            }

            return new HistoricoDTO(
                    p.getDataDoResultado(),
                    resultado,
                    p.getLabel()
            );
        }).toList();

        // Retorna resposta (lista vazia se não houver histórico)
        HistoricoRespostaDTO<List<HistoricoDTO>> resposta =
                new HistoricoRespostaDTO<>(dadosFormatados, predicoes.isEmpty()
                        ? "Usuário sem histórico"
                        : "Dados retornados com sucesso");

        return ResponseEntity.ok(resposta);
    }


    @PostMapping
    public ResponseEntity<?> newPredicao(@RequestBody PredicaoDto predicaoDto) {
        try {
            // Obtém o usuário autenticado do SecurityContextHolder
            Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // Construir entrada para o serviço de predição
            List<Object> entrada = construirEntrada(predicaoDto);
            Map<String, Object> respostaPython = apiPythonPredictService.predict(entrada);

            // Extrair resultado da predição
            Integer labelNumerica = (Integer) respostaPython.get("label");
            ObesidadeLabel labelDescricao = ObesidadeLabel.fromCode(labelNumerica);

            // Criar e salvar a predição
            Predicao predicao = new Predicao();
            predicao.setUsuario(usuario);
            predicao.setLabel(labelNumerica);
            predicao.setResultado(labelDescricao.getDescricao());
            predicao.setDataDoResultado(LocalDate.now());
            repository.save(predicao);

            // Atualizar os dados do usuário
            usuario.setGenero(predicaoDto.gender());
            usuario.setIdade(predicaoDto.age());
            usuario.setAltura(predicaoDto.height());
            usuario.setPeso(predicaoDto.weight());
            usuarioRepository.save(usuario);

            // Retornar resposta
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
