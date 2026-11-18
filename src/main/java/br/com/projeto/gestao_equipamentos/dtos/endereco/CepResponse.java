package br.com.projeto.gestao_equipamentos.dtos.endereco;

import lombok.Builder;

@Builder
public record CepResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf
) {}