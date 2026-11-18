package br.com.projeto.gestao_equipamentos.repository;

import br.com.projeto.gestao_equipamentos.model.Equipamento;
import br.com.projeto.gestao_equipamentos.model.TipoEquipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

    boolean existsByPatrimonio(String patrimonio);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
    UPDATE Equipamento e
       SET e.nome = :nome,
           e.tipo = :tipo
     WHERE e.id = :id
         """)
    int atualizarEquipamento(@Param("id") Long id,
                             @Param("nome") String nome,
                             @Param("tipo") TipoEquipamento tipo);

    @Query("""
        SELECT e FROM Equipamento e
        WHERE (:tipo IS NULL OR e.tipo = :tipo)
          AND (:disponivel IS NULL OR e.disponivel = :disponivel)
    """)
    List<Equipamento> buscarComFiltros(
            @Param("tipo") TipoEquipamento tipo,
            @Param("disponivel") Boolean disponivel);
}
