package com.obesityPredictAi.api.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.obesityPredictAi.api.model.Usuario;

import com.obesityPredictAi.api.DTO.RegisterRequestDTO;
import com.obesityPredictAi.api.DTO.LoginRequestDTO;
import com.obesityPredictAi.api.repository.UsuarioRepository;
import com.obesityPredictAi.api.service.TokenService;

@Service
public class AuthService{

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private TokenService tokenService;

    // criptografar senha
    @Autowired
    private PasswordEncoder passwordEncoder;

    // gerencia o processo de autenticação
    @Autowired
    private AuthenticationManager authenticationManager;
    

    public Usuario register(RegisterRequestDTO request){

        if (this.usuarioRepository.findByEmail(request.getEmail()) != null) {
            // interrompe a execução do registro e retorna uma mensagem
            throw new RuntimeException("Email já cadastrado");
        }

        // criando novo usuário
        Usuario newUser = new Usuario();
        newUser.setNome(request.getNome());
        newUser.setEmail(request.getEmail());

        ///criptografia da senha em hash
        newUser.setSenha(passwordEncoder.encode(request.getSenha())); 

        // guardando no banco
        return usuarioRepository.save(newUser);
    }

    public String login(LoginRequestDTO request){
        // objeto para organizar as credenciais do usuário 
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha());

        // autenticar o usuario de acordo com o banco, por meio do UserDetails
        // auth contem dados de autenticacao do usuario preenchidos
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        // pegar dados de email e senha
        // retorna o token de acesso
        return tokenService.generateToken((Usuario) auth.getPrincipal());
    }

}