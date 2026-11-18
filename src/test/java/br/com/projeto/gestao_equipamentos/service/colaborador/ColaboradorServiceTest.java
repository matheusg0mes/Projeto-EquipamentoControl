package br.com.projeto.gestao_equipamentos.service.colaborador;

import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorRequest;
import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorResponse;
import br.com.projeto.gestao_equipamentos.dtos.endereco.CepResponse;
import br.com.projeto.gestao_equipamentos.dtos.endereco.EnderecoRequest;
import br.com.projeto.gestao_equipamentos.exceptions.CepInvalidoException;
import br.com.projeto.gestao_equipamentos.exceptions.ColaboradorNaoEncontradoException;
import br.com.projeto.gestao_equipamentos.exceptions.EmailJaCadastradoException;
import br.com.projeto.gestao_equipamentos.exceptions.IntegracaoCepException;
import br.com.projeto.gestao_equipamentos.mapping.colaborador.ColaboradorMapping;
import br.com.projeto.gestao_equipamentos.mapping.endereco.EnderecoMapping;
import br.com.projeto.gestao_equipamentos.model.Colaborador;
import br.com.projeto.gestao_equipamentos.model.Endereco;
import br.com.projeto.gestao_equipamentos.model.Usuario;
import br.com.projeto.gestao_equipamentos.repository.ColaboradorRepository;
import br.com.projeto.gestao_equipamentos.service.endereco.CepService;
import br.com.projeto.gestao_equipamentos.dtos.endereco.EnderecoResponse;
import br.com.projeto.gestao_equipamentos.service.usuario.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColaboradorServiceTest {

    @Mock
    private ColaboradorRepository colaboradorRepository;

    @Mock
    private CepService cepService;

    @Mock
    private EnderecoMapping enderecoMapping;

    @Mock
    private ColaboradorMapping colaboradorMapping;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ColaboradorService colaboradorService;

    private ColaboradorRequest colaboradorRequest;
    private CepResponse cepResponse;
    private EnderecoRequest enderecoRequest;
    private Colaborador colaboradorEntity;
    private Colaborador colaboradorSalvo;
    private ColaboradorResponse colaboradorResponse;


    @BeforeEach
    void setUp() {
        colaboradorRequest = ColaboradorRequest.builder()
                .nome("João Silva")
                .email("joao.silva@email.com")
                .cpf("12345678909")
                .cep("13454000")
                .numero("100")
                .build();

        cepResponse = CepResponse.builder()
                .cep("13454-000")
                .logradouro("Rua Exemplo")
                .complemento("Casa")
                .bairro("Centro")
                .localidade("Santa Bárbara d'Oeste")
                .uf("SP")
                .build();

        enderecoRequest = EnderecoRequest.builder()
                .numero("100")
                .cep("13454-000")
                .logradouro("Rua Exemplo")
                .complemento("Casa")
                .bairro("Centro")
                .localidade("Santa Bárbara d'Oeste")
                .uf("SP")
                .build();

        Endereco enderecoEntity = new Endereco();

        colaboradorEntity = new Colaborador();
        colaboradorEntity.setNome("João Silva");
        colaboradorEntity.setEmail("joao.silva@email.com");
        colaboradorEntity.setCpf("12345678909");
        colaboradorEntity.setEndereco(enderecoEntity);

        colaboradorSalvo = mock(Colaborador.class);

        EnderecoResponse enderecoResponse = mock(EnderecoResponse.class);

        colaboradorResponse = ColaboradorResponse.builder()
                .id("1")
                .nome("João Silva")
                .email("joao.silva@email.com")
                .cpf("12345678909")
                .endereco(enderecoResponse)
                .build();
    }

    @Test
    @DisplayName("Deve criar um colaborador com sucesso quando todos os dados forem válidos")
    void deveCriarColaboradorComSucesso() {
        // Arrange
        Usuario usuarioMock = new Usuario(); // Instancia um usuário para o mock retornar

        when(colaboradorSalvo.getId()).thenReturn(1L);
        when(colaboradorRepository.existsByEmail(colaboradorRequest.email())).thenReturn(false);
        when(usuarioService.criarUsuario(colaboradorRequest)).thenReturn(usuarioMock); // Novo mock
        when(cepService.buscarCep(colaboradorRequest.cep())).thenReturn(cepResponse);
        when(enderecoMapping.toRequest(colaboradorRequest, cepResponse)).thenReturn(enderecoRequest);
        when(colaboradorMapping.requestToEntity(colaboradorRequest, enderecoRequest, usuarioMock)).thenReturn(colaboradorEntity); // Atualizado com 3 parâmetros
        when(colaboradorRepository.save(colaboradorEntity)).thenReturn(colaboradorSalvo);
        when(colaboradorMapping.entityToResponse(colaboradorSalvo)).thenReturn(colaboradorResponse);

        // Act
        ColaboradorResponse resultado = colaboradorService.criarColaborador(colaboradorRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals(colaboradorSalvo.getId(), Long.parseLong(resultado.id()));
        assertEquals(colaboradorResponse.nome(), resultado.nome());
        assertEquals(colaboradorResponse.email(), resultado.email());
        assertEquals(colaboradorResponse.cpf(), resultado.cpf());

        verify(colaboradorRepository).existsByEmail(colaboradorRequest.email());
        verify(usuarioService).criarUsuario(colaboradorRequest); // Nova verificação
        verify(cepService).buscarCep(colaboradorRequest.cep());
        verify(enderecoMapping).toRequest(colaboradorRequest, cepResponse);
        verify(colaboradorMapping).requestToEntity(colaboradorRequest, enderecoRequest, usuarioMock); // Atualizado
        verify(colaboradorRepository).save(colaboradorEntity);
        verify(colaboradorMapping).entityToResponse(colaboradorSalvo);
    }

    @Test
    @DisplayName("Deve lançar EmailJaCadastradoException quando o e-mail já existir no banco")
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // Arrange
        when(colaboradorRepository.existsByEmail(colaboradorRequest.email())).thenReturn(true);

        // Act
        EmailJaCadastradoException exception = assertThrows(
                EmailJaCadastradoException.class,
                () -> colaboradorService.criarColaborador(colaboradorRequest)
        );

        // Assert
        assertEquals(
                String.format(EmailJaCadastradoException.message, colaboradorRequest.email()),
                exception.getMessage()
        );

        verify(colaboradorRepository).existsByEmail(colaboradorRequest.email());
        verifyNoInteractions(cepService, enderecoMapping, colaboradorMapping);
        verify(colaboradorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar CepInvalidoException quando o CepService informar que o CEP é inválido")
    void devePropagarExcecaoQuandoCepForInvalido() {
        // Arrange
        when(colaboradorRepository.existsByEmail(colaboradorRequest.email())).thenReturn(false);
        when(cepService.buscarCep(colaboradorRequest.cep()))
                .thenThrow(new CepInvalidoException(colaboradorRequest.cep()));

        // Act
        CepInvalidoException exception = assertThrows(
                CepInvalidoException.class,
                () -> colaboradorService.criarColaborador(colaboradorRequest)
        );

        assertEquals(
                String.format(CepInvalidoException.message, colaboradorRequest.cep()),
                exception.getMessage()
        );

        // Assert
        verify(colaboradorRepository).existsByEmail(colaboradorRequest.email());
        verify(cepService).buscarCep(colaboradorRequest.cep());
        verifyNoInteractions(enderecoMapping, colaboradorMapping);
        verify(colaboradorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar IntegracaoCepException quando ocorrer falha de comunicação com a API de CEP")
    void devePropagarExcecaoQuandoHouverErroDeIntegracaoNoCep() {
        // Arrange
        when(colaboradorRepository.existsByEmail(colaboradorRequest.email())).thenReturn(false);
        when(cepService.buscarCep(colaboradorRequest.cep()))
                .thenThrow(new IntegracaoCepException(colaboradorRequest.cep(), new RuntimeException("Timeout")));

        // Act
        IntegracaoCepException exception = assertThrows(
                IntegracaoCepException.class,
                () -> colaboradorService.criarColaborador(colaboradorRequest)
        );

        // Assert
        assertEquals(
                String.format(IntegracaoCepException.message, colaboradorRequest.cep()),
                exception.getMessage()
        );

        verify(colaboradorRepository).existsByEmail(colaboradorRequest.email());
        verify(cepService).buscarCep(colaboradorRequest.cep());
        verifyNoInteractions(enderecoMapping, colaboradorMapping);
        verify(colaboradorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar ColaboradorResponse com sucesso quando o ID existir")
    void deveBuscarColaboradorPorIdComSucesso() {
        // Arrange
        Long idExistente = 1L;
        when(colaboradorSalvo.getId()).thenReturn(idExistente);
        when(colaboradorRepository.findById(idExistente)).thenReturn(Optional.of(colaboradorSalvo));
        when(colaboradorMapping.entityToResponse(colaboradorSalvo)).thenReturn(colaboradorResponse);

        // Act
        ColaboradorResponse resultado = colaboradorService.buscarPorId(idExistente);

        // Assert
        assertNotNull(resultado);
        assertEquals(colaboradorSalvo.getId(), Long.parseLong(resultado.id()));
        assertEquals(colaboradorResponse.nome(), resultado.nome());
        assertEquals(colaboradorResponse.email(), resultado.email());
        assertEquals(colaboradorResponse.cpf(), resultado.cpf());

        verify(colaboradorRepository).findById(idExistente);
        verify(colaboradorMapping).entityToResponse(colaboradorSalvo);
    }

    @Test
    @DisplayName("Deve lançar ColaboradorNaoEncontradoException quando o ID não existir")
    void deveLancarExcecaoQuandoBuscarColaboradorPorIdInexistente() {
        // Arrange
        Long idInexistente = 99L;
        when(colaboradorRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act
        ColaboradorNaoEncontradoException exception = assertThrows(
                ColaboradorNaoEncontradoException.class,
                () -> colaboradorService.buscarPorId(idInexistente)
        );

        assertEquals(
                String.format(ColaboradorNaoEncontradoException.message, idInexistente),
                exception.getMessage()
        );

        // Assert
        verify(colaboradorRepository).findById(idInexistente);
        verifyNoInteractions(colaboradorMapping);
    }

    @Test
    @DisplayName("Deve retornar uma lista de ColaboradorResponse quando existirem colaboradores cadastrados")
    void deveBuscarColaboradoresComSucesso() {
        // Arrange
        when(colaboradorSalvo.getId()).thenReturn(1L);
        List<Colaborador> listaEntidades = List.of(colaboradorSalvo);
        List<ColaboradorResponse> listaResponsesEsperada = List.of(colaboradorResponse);

        when(colaboradorRepository.findAll()).thenReturn(listaEntidades);
        when(colaboradorMapping.entityToResponse(listaEntidades)).thenReturn(listaResponsesEsperada);

        // Act
        List<ColaboradorResponse> resultado = colaboradorService.buscarColaboradores();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(colaboradorSalvo.getId(), Long.parseLong(resultado.get(0).id()));
        assertEquals(colaboradorResponse.nome(), resultado.get(0).nome());
        assertEquals(colaboradorResponse.email(), resultado.get(0).email());

        verify(colaboradorRepository).findAll();
        verify(colaboradorMapping).entityToResponse(listaEntidades);
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver colaboradores cadastrados")
    void deveRetornarListaVaziaQuandoNaoExistiremColaboradores() {
        // Arrange
        List listaVazia = Collections.emptyList();
        when(colaboradorRepository.findAll()).thenReturn(listaVazia);
        when(colaboradorMapping.entityToResponse(listaVazia)).thenReturn(Collections.emptyList());

        // Act
        List resultado = colaboradorService.buscarColaboradores();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(colaboradorRepository).findAll();
        verify(colaboradorMapping).entityToResponse(listaVazia);
    }
}