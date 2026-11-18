package br.com.projeto.gestao_equipamentos.service.emprestimo;

import br.com.projeto.gestao_equipamentos.dtos.emprestimo.EmprestimoRequest;
import br.com.projeto.gestao_equipamentos.dtos.emprestimo.EmprestimoResponse;
import br.com.projeto.gestao_equipamentos.exceptions.ColaboradorNaoEncontradoException;
import br.com.projeto.gestao_equipamentos.exceptions.EmprestimoNaoEncontradoException;
import br.com.projeto.gestao_equipamentos.exceptions.EquipamentoIndisponivelException;
import br.com.projeto.gestao_equipamentos.exceptions.EquipamentoNaoEncontradoException;
import br.com.projeto.gestao_equipamentos.mapping.EmprestimoMapping;
import br.com.projeto.gestao_equipamentos.model.Colaborador;
import br.com.projeto.gestao_equipamentos.model.Emprestimo;
import br.com.projeto.gestao_equipamentos.model.Equipamento;
import br.com.projeto.gestao_equipamentos.repository.ColaboradorRepository;
import br.com.projeto.gestao_equipamentos.repository.EmprestimoRepository;
import br.com.projeto.gestao_equipamentos.repository.EquipamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmprestimoService implements IEmprestimoService{

    private final EmprestimoRepository emprestimoRepository;
    private final EmprestimoMapping emprestimoMapping;
    private final ColaboradorRepository colaboradorRepository;
    private final EquipamentoRepository equipamentoRepository;

    @Transactional
    @Override
    public EmprestimoResponse criarEmprestimo(EmprestimoRequest emprestimoRequest) {
        log.info("Iniciando criação de empréstimo. Equipamento ID: {}, Colaborador ID: {}",
                emprestimoRequest.equipamentoId(), emprestimoRequest.colaboradorId());

        Equipamento equipamento = equipamentoRepository.findById(emprestimoRequest.equipamentoId())
                .orElseThrow(() -> {
                    log.warn("Equipamento não encontrado para o ID: {}", emprestimoRequest.equipamentoId());
                    return new EquipamentoNaoEncontradoException(emprestimoRequest.equipamentoId());
                });

        Colaborador colaborador = colaboradorRepository.findById(emprestimoRequest.colaboradorId())
                .orElseThrow(() -> {
                    log.warn("Colaborador não encontrado para o ID: {}", emprestimoRequest.colaboradorId());
                    return new ColaboradorNaoEncontradoException(emprestimoRequest.colaboradorId());
                });

        if (emprestimoRepository.existeEmprestimo(equipamento.getId())) {
            log.warn("Equipamento indisponível para empréstimo. ID: {}", equipamento.getId());
            throw new EquipamentoIndisponivelException(equipamento.getId());
        }

        log.debug("Marcando equipamento como emprestado. ID: {}", equipamento.getId());
        equipamento.marcarEmprestado();


        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setColaborador(colaborador);
        emprestimo.setEquipamento(equipamento);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataPrevistaDevolucao(emprestimoRequest.dataPrevistaDevolucao());

        log.debug("Salvando empréstimo no banco de dados.");
        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);
        System.out.println(emprestimoSalvo);

        log.info("Empréstimo criado com sucesso. ID: {}", emprestimoSalvo.getId());
        return emprestimoMapping.entityToResponse(emprestimoSalvo);
    }

    @Override
    @Transactional
    public EmprestimoResponse registrarDevolucao(Long id) {
        log.info("Iniciando registro de devolução do empréstimo. ID: {}", id);

        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Empréstimo não encontrado para o ID: {}", id);
                    return new EmprestimoNaoEncontradoException(id);
                });

        log.debug("Registrando devolução no empréstimo. ID: {}", id);
        emprestimo.registrarDevolucao();

        log.debug("Marcando equipamento como disponível. Equipamento ID: {}", emprestimo.getEquipamento().getId());
        emprestimo.getEquipamento().marcarDisponivel();

        log.debug("Salvando alterações do empréstimo no banco de dados. ID: {}", id);
        EmprestimoResponse response = emprestimoMapping.entityToResponse(emprestimoRepository.save(emprestimo));

        log.info("Devolução registrada com sucesso. Empréstimo ID: {}", id);
        return response;
    }

    @Override
    public List<EmprestimoResponse> buscarAtrasados(Boolean atrasados) {
        log.info("Iniciando busca de empréstimos. Filtro atrasados: {}", atrasados);
        List<Emprestimo> emprestimos = emprestimoRepository.buscarAtrasados(atrasados, LocalDate.now());

        log.debug("Total de empréstimos encontrados: {}. Mapeando para response.", emprestimos.size());
        List<EmprestimoResponse> emprestimosResponse = emprestimoMapping.entityToResponse(emprestimos);

        log.info("Busca de empréstimos finalizada com sucesso. Total retornado: {}", emprestimosResponse.size());
        return emprestimosResponse;
    }

    @Override
    public List<EmprestimoResponse> buscarEmprestimos() {
        log.info("Iniciando busca de todos os empréstimos.");
        List<Emprestimo> emprestimos = emprestimoRepository.findAll();

        log.debug("Total de empréstimos encontrados: {}. Mapeando para response.", emprestimos.size());
        List<EmprestimoResponse> emprestimosResponse = emprestimoMapping.entityToResponse(emprestimos);

        log.info("Busca de empréstimos finalizada com sucesso. Total retornado: {}", emprestimosResponse.size());
        return emprestimosResponse;
    }
}
