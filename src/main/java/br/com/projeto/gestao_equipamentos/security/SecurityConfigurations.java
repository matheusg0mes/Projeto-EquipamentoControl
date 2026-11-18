package br.com.projeto.gestao_equipamentos.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfigurations {

    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                // Permite que o navegador abra os frames do H2 Console
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    // Rotas públicas (Login e H2 Console)
                    req.requestMatchers("/error").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    req.requestMatchers("/h2-console/**").permitAll();

                    // Liberado para ADMIN e COLABORADOR (Consultar equipamentos e empréstimos)
                    req.requestMatchers(HttpMethod.GET, "/equipamento/**", "/emprestimo/**").hasAnyRole("ADMIN", "COLABORADOR");

                    // Restrito apenas ao ADMIN (Ver dados dos colaboradores)
                    req.requestMatchers(HttpMethod.GET, "/colaborador/**").hasRole("ADMIN");

                    // Restrito apenas ao ADMIN (Criar, atualizar, deletar, emprestar e devolver)
                    req.requestMatchers(HttpMethod.POST, "/colaborador/**", "/equipamento/**", "/emprestimo/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PUT, "/equipamento/**", "/emprestimo/**").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/equipamento/**").hasRole("ADMIN");

                    // Qualquer outra requisição precisa estar autenticada
                    req.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
