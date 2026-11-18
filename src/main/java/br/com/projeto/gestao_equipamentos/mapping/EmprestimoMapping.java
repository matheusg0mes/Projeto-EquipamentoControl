package br.com.projeto.gestao_equipamentos.mapping;

import br.com.projeto.gestao_equipamentos.dtos.emprestimo.EmprestimoRequest;
import br.com.projeto.gestao_equipamentos.dtos.emprestimo.EmprestimoResponse;
import br.com.projeto.gestao_equipamentos.model.Colaborador;
import br.com.projeto.gestao_equipamentos.model.Emprestimo;
import br.com.projeto.gestao_equipamentos.model.Equipamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EmprestimoMapping {

    EmprestimoResponse entityToResponse(Emprestimo emprestimo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "equipamento", source = "equipamento")
    @Mapping(target = "colaborador", source = "colaborador")
    @Mapping(target = "dataEmprestimo", source = "dataEmprestimo")
    @Mapping(target = "dataPrevistaDevolucao", source = "dataPrevistaDevolucao")
    Emprestimo toEntity(Equipamento equipamento, Colaborador colaborador, LocalDate dataEmprestimo, LocalDate dataPrevistaDevolucao);

    List<EmprestimoResponse> entityToResponse(List<Emprestimo> emprestimos);
}
