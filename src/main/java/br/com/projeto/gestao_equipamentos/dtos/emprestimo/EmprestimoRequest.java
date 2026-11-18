package br.com.projeto.gestao_equipamentos.dtos.emprestimo;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record EmprestimoRequest(
        long equipamentoId,
        long colaboradorId,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao
) {
}
