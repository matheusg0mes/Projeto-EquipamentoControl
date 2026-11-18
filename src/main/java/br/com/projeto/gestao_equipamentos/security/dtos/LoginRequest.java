package br.com.projeto.gestao_equipamentos.security.dtos;

public record LoginRequest(
        String email,
        String senha
) {}
