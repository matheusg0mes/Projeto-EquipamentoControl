package br.com.projeto.gestao_equipamentos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.projeto.gestao_equipamentos.model.Colaborador;

import java.util.Optional;

@Repository 
public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

    boolean existsByEmail(String email);
}
