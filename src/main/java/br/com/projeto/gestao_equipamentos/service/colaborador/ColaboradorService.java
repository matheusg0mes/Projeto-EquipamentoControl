package br.com.projeto.gestao_equipamentos.service.colaborador;

import java.util.List;

import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorRequest;
import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorResponse;
import br.com.projeto.gestao_equipamentos.dtos.endereco.CepResponse;
import br.com.projeto.gestao_equipamentos.dtos.endereco.EnderecoRequest;
import br.com.projeto.gestao_equipamentos.exceptions.ColaboradorNaoEncontradoException;
import br.com.projeto.gestao_equipamentos.exceptions.EmailJaCadastradoException;
import br.com.projeto.gestao_equipamentos.mapping.colaborador.ColaboradorMapping;
import br.com.projeto.gestao_equipamentos.mapping.endereco.EnderecoMapping;
import br.com.projeto.gestao_equipamentos.model.Usuario;
import br.com.projeto.gestao_equipamentos.service.endereco.CepService;
import br.com.projeto.gestao_equipamentos.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import br.com.projeto.gestao_equipamentos.model.Colaborador;
import br.com.projeto.gestao_equipamentos.repository.ColaboradorRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ColaboradorService implements IColaboradorService {

    private final ColaboradorRepository colaboradorRepository;
    private final UsuarioService usuarioService;
    private final CepService cepService;
    private final EnderecoMapping enderecoMapping;
    private final ColaboradorMapping colaboradorMapping;

    @Override
    @Transactional
    public ColaboradorResponse criarColaborador(ColaboradorRequest colaboradorRequest) {
        log.info("Iniciando criação de colaborador. E-mail: {}", colaboradorRequest.email());

        if (colaboradorRepository.existsByEmail(colaboradorRequest.email())) {
            log.warn("E-mail já cadastrado: {}", colaboradorRequest.email());
            throw new EmailJaCadastradoException(colaboradorRequest.email());
        }

        Usuario usuario= usuarioService.criarUsuario(colaboradorRequest);

        log.debug("Buscando CEP: {}", colaboradorRequest.cep());
        CepResponse cepResponse = cepService.buscarCep(colaboradorRequest.cep());

        EnderecoRequest enderecoRequest = enderecoMapping.toRequest(colaboradorRequest, cepResponse);
        Colaborador colaborador = colaboradorMapping.requestToEntity(colaboradorRequest, enderecoRequest, usuario);

        log.debug("Salvando colaborador no banco de dados.");
        Colaborador colaboradorSalvo = colaboradorRepository.save(colaborador);

        log.info("Colaborador criado com sucesso. ID: {}", colaboradorSalvo.getId());
        return colaboradorMapping.entityToResponse(colaboradorSalvo);
    }

    @Override
    public ColaboradorResponse buscarPorId(Long id) {
        log.info("Iniciando busca de colaborador por ID: {}", id);
        Colaborador colaborador = colaboradorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Colaborador não encontrado para o ID: {}", id);
                    return new ColaboradorNaoEncontradoException(id);
                });

        log.debug("Colaborador encontrado. Mapeando para response. ID: {}", id);
        ColaboradorResponse colaboradorResponse = colaboradorMapping.entityToResponse(colaborador);
        log.info("Busca de colaborador por ID finalizada com sucesso. ID: {}", id);
        return colaboradorResponse;
    }

    @Override
    public List<ColaboradorResponse> buscarColaboradores() {
        log.info("Iniciando busca de todos os colaboradores.");
        List<Colaborador> colaboradores = colaboradorRepository.findAll();

        log.debug("Total de colaboradores encontrados: {}. Mapeando para response.", colaboradores.size());
        List<ColaboradorResponse> colaboradoresResponse = colaboradorMapping.entityToResponse(colaboradores);

        log.info("Busca de colaboradores finalizada com sucesso. Total retornado: {}", colaboradoresResponse.size());
        return colaboradoresResponse;
    }
}
