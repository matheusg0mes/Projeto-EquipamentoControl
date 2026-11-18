package br.com.projeto.gestao_equipamentos.service.emprestimo;

import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorResponse;
import br.com.projeto.gestao_equipamentos.dtos.emprestimo.EmprestimoRequest;
import br.com.projeto.gestao_equipamentos.dtos.emprestimo.EmprestimoResponse;
import br.com.projeto.gestao_equipamentos.dtos.equipamento.EquipamentoResponse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private EmprestimoMapping emprestimoMapping;

    @Mock
    private ColaboradorRepository colaboradorRepository;

    @Mock
    private EquipamentoRepository equipamentoRepository;

    @InjectMocks
    private EmprestimoService emprestimoService;

    private EmprestimoRequest emprestimoRequest;
    private Equipamento equipamento;
    private Colaborador colaborador;
    private Emprestimo emprestimoEntity;
    private Emprestimo emprestimoSalvo;
    private EmprestimoResponse emprestimoResponse;

    @BeforeEach
    void setUp() {
        LocalDate dataEmprestimo = LocalDate.of(2026, 11, 16);
        LocalDate dataPrevista = LocalDate.of(2026, 11, 23);

        emprestimoRequest = EmprestimoRequest.builder()
                .equipamentoId(1L)
                .colaboradorId(2L)
                .build();

        equipamento = mock(Equipamento.class);
        colaborador = mock(Colaborador.class);

        emprestimoEntity = new Emprestimo();
        emprestimoEntity.setEquipamento(equipamento);
        emprestimoEntity.setColaborador(colaborador);
        emprestimoEntity.setDataEmprestimo(dataEmprestimo);
        emprestimoEntity.setDataPrevistaDevolucao(dataPrevista);

        emprestimoSalvo = mock(Emprestimo.class);

        EquipamentoResponse equipamentoResponse = EquipamentoResponse.builder()
                .id(1L)
                .patrimonio("PAT-001")
                .nome("Notebook Dell")
                .tipo("NOTEBOOK")
                .disponivel(false)
                .build();

        ColaboradorResponse colaboradorResponse = ColaboradorResponse.builder()
                .id("2")
                .nome("João Silva")
                .email("joao.silva@email.com")
                .cpf("12345678909")
                .build();

        emprestimoResponse = EmprestimoResponse.builder()
                .id(10L)
                .dataEmprestimo(dataEmprestimo)
                .dataPrevistaDevolucao(dataPrevista)
                .dataDevolucao(null)
                .equipamento(equipamentoResponse)
                .colaborador(colaboradorResponse)
                .build();
    }

    @Test
    @DisplayName("Deve criar um empréstimo com sucesso quando equipamento e colaborador existirem e equipamento estiver disponível")
    void deveCriarEmprestimoComSucesso() {
    }

    @Test
    @DisplayName("Deve lançar EquipamentoNaoEncontradoException quando o equipamento não existir")
    void deveLancarExcecaoQuandoEquipamentoNaoForEncontrado() {
        // Arrange
        Long equipamentoId = 1L;
        when(equipamentoRepository.findById(equipamentoId)).thenReturn(Optional.empty());

        // Act & Assert
        EquipamentoNaoEncontradoException exception = assertThrows(
                EquipamentoNaoEncontradoException.class,
                () -> emprestimoService.criarEmprestimo(emprestimoRequest)
        );

        assertEquals(
                String.format(EquipamentoNaoEncontradoException.message, equipamentoId),
                exception.getMessage()
        );

        verify(equipamentoRepository).findById(equipamentoId);
        verifyNoInteractions(colaboradorRepository, emprestimoRepository, emprestimoMapping);
    }

    @Test
    @DisplayName("Deve lançar ColaboradorNaoEncontradoException quando o colaborador não existir")
    void deveLancarExcecaoQuandoColaboradorNaoForEncontrado() {
        // Arrange
        Long equipamentoId = 1L;
        Long colaboradorId = 2L;

        when(equipamentoRepository.findById(equipamentoId)).thenReturn(Optional.of(equipamento));
        when(colaboradorRepository.findById(colaboradorId)).thenReturn(Optional.empty());

        // Act & Assert
        ColaboradorNaoEncontradoException exception = assertThrows(
                ColaboradorNaoEncontradoException.class,
                () -> emprestimoService.criarEmprestimo(emprestimoRequest)
        );

        assertEquals(
                String.format(ColaboradorNaoEncontradoException.message, colaboradorId),
                exception.getMessage()
        );

        verify(equipamentoRepository).findById(equipamentoId);
        verify(colaboradorRepository).findById(colaboradorId);
        verifyNoInteractions(emprestimoRepository, emprestimoMapping);
        verify(equipamento, never()).marcarEmprestado();
    }

    @Test
    @DisplayName("Deve lançar EquipamentoIndisponivelException quando o equipamento já possuir um empréstimo ativo")
    void deveLancarExcecaoQuandoEquipamentoEstiverIndisponivel() {
        // Arrange
        Long equipamentoId = 1L;
        Long colaboradorId = 2L;

        when(equipamento.getId()).thenReturn(equipamentoId);
        when(equipamentoRepository.findById(equipamentoId)).thenReturn(Optional.of(equipamento));
        when(colaboradorRepository.findById(colaboradorId)).thenReturn(Optional.of(colaborador));
        when(emprestimoRepository.existeEmprestimo(equipamentoId)).thenReturn(true);

        // Act & Assert
        EquipamentoIndisponivelException exception = assertThrows(
                EquipamentoIndisponivelException.class,
                () -> emprestimoService.criarEmprestimo(emprestimoRequest)
        );

        assertEquals(
                String.format(EquipamentoIndisponivelException.message, equipamentoId),
                exception.getMessage()
        );

        verify(equipamentoRepository).findById(equipamentoId);
        verify(colaboradorRepository).findById(colaboradorId);
        verify(emprestimoRepository).existeEmprestimo(equipamentoId);
        verify(equipamento, never()).marcarEmprestado();
        verifyNoInteractions(emprestimoMapping);
        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar uma lista de EmprestimoResponse quando existirem empréstimos cadastrados")
    void deveListarEmprestimosComSucesso() {
        // Arrange
        List<Emprestimo> listaEntidades = List.of(emprestimoSalvo);
        List<EmprestimoResponse> listaResponsesEsperada = List.of(emprestimoResponse);

        when(emprestimoSalvo.getId()).thenReturn(10L);
        when(emprestimoRepository.findAll()).thenReturn(listaEntidades);
        when(emprestimoMapping.entityToResponse(listaEntidades)).thenReturn(listaResponsesEsperada);

        // Act
        List<EmprestimoResponse> resultado = emprestimoService.buscarEmprestimos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(emprestimoSalvo.getId(), resultado.get(0).id());
        assertEquals(emprestimoResponse.dataEmprestimo(), resultado.get(0).dataEmprestimo());
        assertEquals(emprestimoResponse.dataPrevistaDevolucao(), resultado.get(0).dataPrevistaDevolucao());
        assertNull(resultado.get(0).dataDevolucao());

        verify(emprestimoRepository).findAll();
        verify(emprestimoMapping).entityToResponse(listaEntidades);
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver empréstimos cadastrados")
    void deveRetornarListaVaziaQuandoNaoExistiremEmprestimos() {
        // Arrange
        List<Emprestimo> listaVazia = Collections.emptyList();

        when(emprestimoRepository.findAll()).thenReturn(listaVazia);
        when(emprestimoMapping.entityToResponse(listaVazia)).thenReturn(Collections.emptyList());

        // Act
        List<EmprestimoResponse> resultado = emprestimoService.buscarEmprestimos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(emprestimoRepository).findAll();
        verify(emprestimoMapping).entityToResponse(listaVazia);
    }

    @Test
    @DisplayName("Deve registrar a devolução de um empréstimo com sucesso e tornar o equipamento disponível")
    void deveRegistrarDevolucaoComSucesso() {
        // Arrange
        Long emprestimoId = 10L;
        LocalDate hoje = LocalDate.now();

        EmprestimoResponse responseDevolvido = EmprestimoResponse.builder()
                .id(emprestimoId)
                .dataEmprestimo(emprestimoResponse.dataEmprestimo())
                .dataPrevistaDevolucao(emprestimoResponse.dataPrevistaDevolucao())
                .dataDevolucao(hoje)
                .equipamento(emprestimoResponse.equipamento())
                .colaborador(emprestimoResponse.colaborador())
                .build();

        when(emprestimoSalvo.getId()).thenReturn(emprestimoId);
        when(emprestimoSalvo.getEquipamento()).thenReturn(equipamento);
        when(emprestimoRepository.findById(emprestimoId)).thenReturn(Optional.of(emprestimoSalvo));
        when(emprestimoRepository.save(emprestimoSalvo)).thenReturn(emprestimoSalvo);
        when(emprestimoMapping.entityToResponse(emprestimoSalvo)).thenReturn(responseDevolvido);

        // Act
        EmprestimoResponse resultado = emprestimoService.registrarDevolucao(emprestimoId);

        // Assert
        assertNotNull(resultado);
        assertEquals(emprestimoSalvo.getId(), resultado.id());
        assertEquals(hoje, resultado.dataDevolucao());

        verify(emprestimoRepository).findById(emprestimoId);
        verify(emprestimoSalvo).registrarDevolucao();
        verify(equipamento).marcarDisponivel();
        verify(emprestimoRepository).save(emprestimoSalvo);
        verify(emprestimoMapping).entityToResponse(emprestimoSalvo);
    }


    @Test
    @DisplayName("Deve lançar EmprestimoNaoEncontradoException quando tentar devolver um empréstimo inexistente")
    void deveLancarExcecaoQuandoRegistrarDevolucaoDeEmprestimoInexistente() {
        // Arrange
        Long idInexistente = 99L;

        when(emprestimoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        EmprestimoNaoEncontradoException exception = assertThrows(
                EmprestimoNaoEncontradoException.class,
                () -> emprestimoService.registrarDevolucao(idInexistente)
        );

        assertEquals(
                String.format(EmprestimoNaoEncontradoException.message, idInexistente),
                exception.getMessage()
        );

        verify(emprestimoRepository).findById(idInexistente);
        verify(emprestimoRepository, never()).save(any());
        verifyNoInteractions(emprestimoMapping);
    }
}
