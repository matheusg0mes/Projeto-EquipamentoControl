package br.com.projeto.gestao_equipamentos.dtos.equipamento;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EquipamentoRequest(
        @NotBlank(message = "O patrimônio não pode ser nulo ou vazio")
        String patrimonio,

        @NotBlank (message = "O nome não pode ser nulo ou vazio")
        String nome,

        @NotBlank (message = "O tipo não pode ser nulo ou vazio")
        String tipo,

        boolean disponivel) {
}