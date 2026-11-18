package br.com.projeto.gestao_equipamentos.mapping;

import br.com.projeto.gestao_equipamentos.dtos.equipamento.EquipamentoRequest;
import br.com.projeto.gestao_equipamentos.dtos.equipamento.EquipamentoResponse;
import br.com.projeto.gestao_equipamentos.model.Equipamento;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EquipamentoMapper {

    Equipamento requestToEntity(EquipamentoRequest equipamentoRequest);

    EquipamentoResponse entityToResponse(Equipamento equipamento);

    List<EquipamentoResponse> entityToResponse(List<Equipamento> equipamentos);
}
