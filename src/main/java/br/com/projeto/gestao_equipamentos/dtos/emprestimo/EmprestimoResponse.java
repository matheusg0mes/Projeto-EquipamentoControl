package br.com.projeto.gestao_equipamentos.dtos.emprestimo;

import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorResponse;
import br.com.projeto.gestao_equipamentos.dtos.equipamento.EquipamentoResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record EmprestimoResponse(
        Long id,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucao,
        EquipamentoResponse equipamento,
        ColaboradorResponse colaborador
){
}
