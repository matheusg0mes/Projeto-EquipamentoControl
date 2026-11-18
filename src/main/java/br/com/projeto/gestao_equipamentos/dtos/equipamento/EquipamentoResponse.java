package br.com.projeto.gestao_equipamentos.dtos.equipamento;

import lombok.Builder;

@Builder
public record EquipamentoResponse(
        Long id,
        String patrimonio,
        String nome,
        String tipo,
        boolean disponivel) {
}
