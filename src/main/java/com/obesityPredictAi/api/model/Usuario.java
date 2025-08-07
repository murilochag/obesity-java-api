package com.obesityPredictAi.api.model;

import java.util.List;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;

    private String nome;

    private String email;

    private String senha;

    // definindo relacao de um para muitos
    // falando que vai receber a predicao da tabela Predicao
    @OneToMany(mappedBy = "usuario")
    private List<Predicao> predicao;

    // métodos UserDetails

    //  retorna as autoridades concedidas aos usuarios
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // retorna a senha para autenticação
    @Override
    public String getPassword() {
        return senha;
    }

    // retorna o email para autenticação
    @Override
    public String getUsername() {
        return email;
    }

    // indica se as credenciais expiraram
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // indica se o usuario está bloqueado ou não
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    // indica se as credenciaiss (senha) expiraram
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // usuario habilitado ou não
    @Override
    public boolean isEnabled() {
        return true;
    }
}
