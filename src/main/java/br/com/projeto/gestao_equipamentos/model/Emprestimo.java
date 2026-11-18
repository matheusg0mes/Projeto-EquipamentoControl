package br.com.projeto.gestao_equipamentos.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "emprestimo")
@Getter
@Setter
public class Emprestimo {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private LocalDate dataEmprestimo;

    private LocalDate dataPrevistaDevolucao;

    private LocalDate dataDevolucao;

    @ManyToOne 
    @JoinColumn(name = "equipamentoId")
    @JsonIgnoreProperties("emprestimos")
    private Equipamento equipamento;

    @ManyToOne
    @JoinColumn(name = "colaboradorId")
    @JsonIgnoreProperties("emprestimos")
    private Colaborador colaborador;

    @PrePersist
    public void calcularDataEmprestimo() {
        if (this.dataEmprestimo == null) {
            this.dataEmprestimo = LocalDate.now();
        }
        this.setDataDevolucao(dataDevolucao);
    }

    public void registrarDevolucao() {
        if (this.dataDevolucao != null) {
            throw new IllegalStateException("Este empréstimo já possui devolução registrada.");
        }
        this.dataDevolucao = LocalDate.now();
    }

    public boolean isAtrasado() {
        if (this.dataDevolucao != null) {
            return this.dataDevolucao.isAfter(this.dataPrevistaDevolucao);
        }
        return LocalDate.now().isAfter(this.dataPrevistaDevolucao);
    }

    public long getDiasAtraso() {
        if (!isAtrasado()) {
            return 0;
        }
        LocalDate dataReferencia = (this.dataDevolucao != null) ? this.dataDevolucao : LocalDate.now();
        return ChronoUnit.DAYS.between(this.dataPrevistaDevolucao, dataReferencia);
    }
}