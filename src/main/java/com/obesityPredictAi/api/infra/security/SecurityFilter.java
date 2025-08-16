package com.obesityPredictAi.api.infra.security;

import com.obesityPredictAi.api.repository.TokenRepository;
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
import java.time.Instant;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TokenRepository tokenRepository;


    // responsavel por validar o token a cada requisicao
    // intercepta todas as requisicoes HTTP que chegam a sua API
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // extrai o token do cabecalho autorization da requisicao
        var token = this.recoverToken(request);

        if (token != null) {
            // valida JWT
            var email = tokenService.validateToken(token);

            if (!email.isEmpty()) {
                // verifica se o token está ativo e ainda não expirou no banco
                var tokenEntity = tokenRepository.findByTokenAndAtivoTrue(token).orElse(null);

                if (tokenEntity != null && tokenEntity.getDataExpiracao().isAfter(Instant.now())) {
                    // carrega detalhes do usuário
                    UserDetails user = usuarioRepository.findByEmail(email);

                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // token inválido ou expirado
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado");
                    return;
                }
            }
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
