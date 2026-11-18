package br.com.projeto.gestao_equipamentos.controller;

import br.com.projeto.gestao_equipamentos.security.TokenService;
import br.com.projeto.gestao_equipamentos.security.UsuarioSeguranca;
import br.com.projeto.gestao_equipamentos.security.dtos.LoginRequest;
import br.com.projeto.gestao_equipamentos.security.dtos.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<TokenResponse> efetuarLogin(@RequestBody LoginRequest dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);

        var usuarioSeguranca = (UsuarioSeguranca) authentication.getPrincipal();
        var tokenJWT = tokenService.gerarToken(usuarioSeguranca.getUsuario());

        return ResponseEntity.ok(new TokenResponse(tokenJWT));
    }
}
