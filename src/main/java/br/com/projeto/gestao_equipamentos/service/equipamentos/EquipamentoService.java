package br.com.projeto.gestao_equipamentos.service.equipamentos;

import br.com.projeto.gestao_equipamentos.dtos.equipamento.EquipamentoRequest;
import br.com.projeto.gestao_equipamentos.dtos.equipamento.EquipamentoResponse;
import br.com.projeto.gestao_equipamentos.exceptions.EquipamentoNaoEncontradoException;
import br.com.projeto.gestao_equipamentos.exceptions.PatrimonioJaCadastradoException;
import br.com.projeto.gestao_equipamentos.mapping.EquipamentoMapper;
import br.com.projeto.gestao_equipamentos.model.Equipamento;
import br.com.projeto.gestao_equipamentos.model.TipoEquipamento;
import br.com.projeto.gestao_equipamentos.repository.EquipamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EquipamentoService implements IEquipamentoService {

    private final EquipamentoRepository equipamentoRepository;
    private final EquipamentoMapper equipamentoMapper;

    @Override
    public EquipamentoResponse criarEquipamento(EquipamentoRequest equipamentoRequest) {
        log.info("Iniciando criação de equipamento. Patrimônio: {}", equipamentoRequest.patrimonio());

        if (equipamentoRepository.existsByPatrimonio(equipamentoRequest.patrimonio())) {
            log.warn("Tentativa de cadastro falhou: patrimônio já cadastrado. Patrimônio: {}", equipamentoRequest.patrimonio());
            throw new PatrimonioJaCadastradoException();
        }

        Equipamento equipamento = equipamentoMapper.requestToEntity(equipamentoRequest);

        log.debug("Salvando equipamento no banco de dados. Patrimônio: {}", equipamentoRequest.patrimonio());
        Equipamento equipamentoSalvo = equipamentoRepository.save(equipamento);

        EquipamentoResponse equipamentoResponse = equipamentoMapper.entityToResponse(equipamentoSalvo);

        log.info("Equipamento criado com sucesso. ID: {}, Patrimônio: {}", equipamentoSalvo.getId(), equipamentoSalvo.getPatrimonio());
        return equipamentoResponse;
    }

    @Override
    public EquipamentoResponse atualizarEquipamento(Long id, EquipamentoRequest equipamentoRequest) {
        log.info("Iniciando atualização do equipamento. ID: {}", id);
        Equipamento equipamento = equipamentoMapper.requestToEntity(equipamentoRequest);

        log.debug("Atualizando dados do equipamento no banco de dados. ID: {}, Nome: {}, Tipo: {}",
                id, equipamento.getNome(), equipamento.getTipo());
        int linhasAlteradas = equipamentoRepository.atualizarEquipamento(
                id,
                equipamento.getNome(),
                equipamento.getTipo());

        if (linhasAlteradas == 0) {
            log.warn("Equipamento não encontrado para atualização. ID: {}", id);
            throw new EquipamentoNaoEncontradoException(id);
        }

        log.debug("Buscando equipamento atualizado. ID: {}", id);
        EquipamentoResponse equipamentoResponse = this.buscarEquipamentosId(id);

        log.info("Equipamento atualizado com sucesso. ID: {}", id);
        return equipamentoResponse;
    }

    @Override
    public EquipamentoResponse buscarEquipamentosId(Long id) {
        log.info("Iniciando busca de equipamento por ID: {}", id);
        Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Equipamento não encontrado para o ID: {}", id);
                    return new EquipamentoNaoEncontradoException(id);
                });

        log.debug("Equipamento encontrado. Mapeando para response. ID: {}", id);
        EquipamentoResponse equipamentoResponse = equipamentoMapper.entityToResponse(equipamento);

        log.info("Busca de equipamento por ID finalizada com sucesso. ID: {}", id);
        return equipamentoResponse;
    }

    @Override
    public List<EquipamentoResponse> buscarEquipamentos() {
        log.info("Iniciando busca de todos os equipamentos.");
        List<Equipamento> equipamentos = equipamentoRepository.findAll();

        log.debug("Total de equipamentos encontrados: {}. Mapeando para response.", equipamentos.size());
        List<EquipamentoResponse> equipamentosResponse = equipamentoMapper.entityToResponse(equipamentos);

        log.info("Busca de equipamentos finalizada com sucesso. Total retornado: {}", equipamentosResponse.size());
        return equipamentosResponse;
    }

    public List<EquipamentoResponse> buscarComFiltros(TipoEquipamento tipo, Boolean disponivel) {
        log.info("Iniciando busca de equipamentos com filtros. Tipo: {}, Disponível: {}", tipo, disponivel);
        List<Equipamento> equipamentos = equipamentoRepository.buscarComFiltros(tipo, disponivel);

        log.debug("Total de equipamentos encontrados com os filtros: {}. Mapeando para response.", equipamentos.size());
        List<EquipamentoResponse> equipamentosResponse = equipamentos.stream()
                .map(equipamento -> equipamentoMapper.entityToResponse(equipamento))
                .toList();

        log.info("Busca de equipamentos com filtros finalizada com sucesso. Total retornado: {}", equipamentosResponse.size());
        return equipamentosResponse;
    }

    @Override
    public void deleteEquipamento(Long id) {
        log.info("Iniciando exclusão de equipamento. ID: {}", id);
        Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Equipamento não encontrado para exclusão. ID: {}", id);
                    return new EquipamentoNaoEncontradoException(id);
                });

        log.debug("Excluindo equipamento do banco de dados. ID: {}", id);
        equipamentoRepository.delete(equipamento);

        log.info("Equipamento excluído com sucesso. ID: {}", id);
    }
}