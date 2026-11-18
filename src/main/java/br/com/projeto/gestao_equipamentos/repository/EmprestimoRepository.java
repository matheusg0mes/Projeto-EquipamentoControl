package br.com.projeto.gestao_equipamentos.repository;

import br.com.projeto.gestao_equipamentos.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo,Long> {

    @Query("""
    SELECT COUNT(e) > 0
      FROM Emprestimo e
     WHERE e.equipamento.id = :equipamentoId
       AND e.dataDevolucao IS NULL
           """)
    boolean existeEmprestimo(@Param("equipamentoId") Long equipamentoId);

    @Query("""
    SELECT e
      FROM Emprestimo e
     WHERE (:atrasados IS NULL)
        OR (:atrasados = true AND (
               (e.dataDevolucao IS NULL AND e.dataPrevistaDevolucao < :hoje)
            OR (e.dataDevolucao IS NOT NULL AND e.dataDevolucao > e.dataPrevistaDevolucao)
           ))
        OR (:atrasados = false AND (
               (e.dataDevolucao IS NULL AND e.dataPrevistaDevolucao >= :hoje)
            OR (e.dataDevolucao IS NOT NULL AND e.dataDevolucao <= e.dataPrevistaDevolucao)
           ))
""")
    List<Emprestimo> buscarAtrasados(@Param("atrasados") Boolean atrasados, @Param("hoje") LocalDate hoje);

}
