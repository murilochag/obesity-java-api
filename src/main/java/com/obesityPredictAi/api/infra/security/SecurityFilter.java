package com.obesityPredictAi.api.infra.security;

import com.obesityPredictAi.api.repository.UsuarioRepository;
import com.obesityPredictAi.api.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioRepository usuarioRepository;

    // responsavel por validar o token a cada requisicao
    // intercepta todas as requisicoes HTTP que chegam a sua API
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // extrai o token do cabecalho autorization da requisicao
        var token = this.recoverToken(request);

        if (token != null) {
            // valida o token e extrai o email
            var email = tokenService.validateToken(token);

            // carrega os detalhes do usuario do banco de dados
            UserDetails user = usuarioRepository.findByEmail(email);

            // cria um objeto de autenticacao
            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            // armazena os detalhes do usuario autenticado no SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // continua o fluxo da requisicao (passa para os controladores)
        filterChain.doFilter(request, response);
    }

    // metodo auxiliar para extrair o token do cabecalho autorization
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
