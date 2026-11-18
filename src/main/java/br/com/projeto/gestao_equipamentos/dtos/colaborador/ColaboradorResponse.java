package br.com.projeto.gestao_equipamentos.dtos.colaborador;

import br.com.projeto.gestao_equipamentos.dtos.endereco.EnderecoResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
public record ColaboradorResponse(
        String id,
        String nome,
        String email,
        String cpf,
        @JsonIgnoreProperties("colaborador")
        EnderecoResponse endereco
) {
}
