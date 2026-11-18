package br.com.projeto.gestao_equipamentos.service.colaborador;

import java.util.List;

import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorRequest;
import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorResponse;

public interface IColaboradorService {
    List<ColaboradorResponse> buscarColaboradores();
    
    ColaboradorResponse criarColaborador(ColaboradorRequest colaboradorRequest);

    ColaboradorResponse buscarPorId(Long id);
}
