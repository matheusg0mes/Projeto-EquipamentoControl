package br.com.projeto.gestao_equipamentos.service.equipamentos;

import br.com.projeto.gestao_equipamentos.dtos.equipamento.EquipamentoRequest;
import br.com.projeto.gestao_equipamentos.dtos.equipamento.EquipamentoResponse;

import java.util.List;

public interface IEquipamentoService {
    EquipamentoResponse criarEquipamento(EquipamentoRequest equipamentoRequest);

    EquipamentoResponse atualizarEquipamento(Long id, EquipamentoRequest equipamentoRequest);

    EquipamentoResponse buscarEquipamentosId(Long id);

    List<EquipamentoResponse> buscarEquipamentos();

    void deleteEquipamento(Long id);
}