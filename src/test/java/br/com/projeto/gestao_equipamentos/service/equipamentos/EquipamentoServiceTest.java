package br.com.projeto.gestao_equipamentos.service.equipamentos;

import br.com.projeto.gestao_equipamentos.dtos.equipamento.EquipamentoRequest;
import br.com.projeto.gestao_equipamentos.dtos.equipamento.EquipamentoResponse;
import br.com.projeto.gestao_equipamentos.exceptions.EquipamentoNaoEncontradoException;
import br.com.projeto.gestao_equipamentos.exceptions.PatrimonioJaCadastradoException;
import br.com.projeto.gestao_equipamentos.mapping.EquipamentoMapper;
import br.com.projeto.gestao_equipamentos.model.Equipamento;
import br.com.projeto.gestao_equipamentos.model.TipoEquipamento;
import br.com.projeto.gestao_equipamentos.repository.EquipamentoRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipamentoServiceTest {

    @Mock
    private EquipamentoRepository equipamentoRepository;

    @Mock
    private EquipamentoMapper equipamentoMapper;

    @InjectMocks
    private EquipamentoService equipamentoService;

    private EquipamentoRequest equipamentoRequest;
    private Equipamento equipamentoEntity;
    private Equipamento equipamentoSalvo;
    private EquipamentoResponse equipamentoResponse;

    @BeforeEach
    void setUp() {
        equipamentoRequest = EquipamentoRequest.builder()
                .patrimonio("PAT-001")
                .nome("Notebook Dell Latitude")
                .tipo("NOTEBOOK")
                .disponivel(true)
                .build();

        equipamentoEntity = new Equipamento();
        equipamentoEntity.setPatrimonio("PAT-001");
        equipamentoEntity.setNome("Notebook Dell Latitude");
        equipamentoEntity.setTipo(TipoEquipamento.NOTEBOOK);
        equipamentoEntity.setDisponivel(true);

        equipamentoSalvo = mock(Equipamento.class);

        equipamentoResponse = EquipamentoResponse.builder()
                .id(1L)
                .patrimonio("PAT-001")
                .nome("Notebook Dell Latitude")
                .tipo("NOTEBOOK")
                .disponivel(true)
                .build();
    }

    @Test
    @DisplayName("Deve criar um equipamento com sucesso quando o patrimônio ainda não estiver cadastrado")
    void deveCriarEquipamentoComSucesso() {
        // Arrange
        when(equipamentoSalvo.getId()).thenReturn(1L);
        when(equipamentoRepository.existsByPatrimonio(equipamentoRequest.patrimonio())).thenReturn(false);
        when(equipamentoMapper.requestToEntity(equipamentoRequest)).thenReturn(equipamentoEntity);
        when(equipamentoRepository.save(equipamentoEntity)).thenReturn(equipamentoSalvo);
        when(equipamentoMapper.entityToResponse(equipamentoSalvo)).thenReturn(equipamentoResponse);

        // Act
        EquipamentoResponse resultado = equipamentoService.criarEquipamento(equipamentoRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals(equipamentoSalvo.getId(), resultado.id());
        assertEquals(equipamentoResponse.patrimonio(), resultado.patrimonio());
        assertEquals(equipamentoResponse.nome(), resultado.nome());
        assertEquals(equipamentoResponse.tipo(), resultado.tipo());
        assertTrue(resultado.disponivel());

        verify(equipamentoRepository).existsByPatrimonio(equipamentoRequest.patrimonio());
        verify(equipamentoMapper).requestToEntity(equipamentoRequest);
        verify(equipamentoRepository).save(equipamentoEntity);
        verify(equipamentoMapper).entityToResponse(equipamentoSalvo);
    }

    @Test
    @DisplayName("Deve lançar PatrimonioJaCadastradoException quando o patrimônio já existir no banco")
    void deveLancarExcecaoQuandoPatrimonioJaCadastrado() {
        // Arrange
        when(equipamentoRepository.existsByPatrimonio(equipamentoRequest.patrimonio())).thenReturn(true);

        // Act & Assert
        assertThrows(
                PatrimonioJaCadastradoException.class,
                () -> equipamentoService.criarEquipamento(equipamentoRequest)
        );

        verify(equipamentoRepository).existsByPatrimonio(equipamentoRequest.patrimonio());
        verifyNoInteractions(equipamentoMapper);
        verify(equipamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar um equipamento com sucesso quando o ID existir")
    void deveAtualizarEquipamentoComSucesso() {
        // Arrange
        Long idExistente = 1L;
        when(equipamentoSalvo.getId()).thenReturn(idExistente);
        when(equipamentoMapper.requestToEntity(equipamentoRequest)).thenReturn(equipamentoEntity);
        when(equipamentoRepository.atualizarEquipamento(
                idExistente,
                equipamentoEntity.getNome(),
                equipamentoEntity.getTipo()
        )).thenReturn(1);
        when(equipamentoRepository.findById(idExistente)).thenReturn(Optional.of(equipamentoSalvo));
        when(equipamentoMapper.entityToResponse(equipamentoSalvo)).thenReturn(equipamentoResponse);

        // Act
        EquipamentoResponse resultado = equipamentoService.atualizarEquipamento(idExistente, equipamentoRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals(equipamentoSalvo.getId(), resultado.id());
        assertEquals(equipamentoResponse.patrimonio(), resultado.patrimonio());
        assertEquals(equipamentoResponse.nome(), resultado.nome());
        assertEquals(equipamentoResponse.tipo(), resultado.tipo());

        verify(equipamentoMapper).requestToEntity(equipamentoRequest);
        verify(equipamentoRepository).atualizarEquipamento(
                idExistente,
                equipamentoEntity.getNome(),
                equipamentoEntity.getTipo()
        );
        verify(equipamentoRepository).findById(idExistente);
        verify(equipamentoMapper).entityToResponse(equipamentoSalvo);
    }

    @Test
    @DisplayName("Deve lançar EquipamentoNaoEncontradoException quando nenhuma linha for alterada na atualização")
    void deveLancarExcecaoQuandoAtualizarEquipamentoInexistente() {
        // Arrange
        Long idInexistente = 99L;
        when(equipamentoMapper.requestToEntity(equipamentoRequest)).thenReturn(equipamentoEntity);
        when(equipamentoRepository.atualizarEquipamento(
                idInexistente,
                equipamentoEntity.getNome(),
                equipamentoEntity.getTipo()
        )).thenReturn(0);

        // Act & Assert
        EquipamentoNaoEncontradoException exception = assertThrows(
                EquipamentoNaoEncontradoException.class,
                () -> equipamentoService.atualizarEquipamento(idInexistente, equipamentoRequest)
        );

        assertEquals(
                String.format(EquipamentoNaoEncontradoException.message, idInexistente),
                exception.getMessage()
        );

        verify(equipamentoMapper).requestToEntity(equipamentoRequest);
        verify(equipamentoRepository).atualizarEquipamento(
                idInexistente,
                equipamentoEntity.getNome(),
                equipamentoEntity.getTipo()
        );
        verify(equipamentoRepository, never()).findById(any());
        verify(equipamentoMapper, never()).entityToResponse(any(Equipamento.class));
    }

    @Test
    @DisplayName("Deve retornar EquipamentoResponse com sucesso quando o ID existir")
    void deveBuscarEquipamentoPorIdComSucesso() {
        // Arrange
        Long idExistente = 1L;
        when(equipamentoSalvo.getId()).thenReturn(idExistente);
        when(equipamentoRepository.findById(idExistente)).thenReturn(Optional.of(equipamentoSalvo));
        when(equipamentoMapper.entityToResponse(equipamentoSalvo)).thenReturn(equipamentoResponse);

        // Act
        EquipamentoResponse resultado = equipamentoService.buscarEquipamentosId(idExistente);

        // Assert
        assertNotNull(resultado);
        assertEquals(equipamentoSalvo.getId(), resultado.id());
        assertEquals(equipamentoResponse.patrimonio(), resultado.patrimonio());
        assertEquals(equipamentoResponse.nome(), resultado.nome());
        assertEquals(equipamentoResponse.tipo(), resultado.tipo());
        assertTrue(resultado.disponivel());

        verify(equipamentoRepository).findById(idExistente);
        verify(equipamentoMapper).entityToResponse(equipamentoSalvo);
    }

    @Test
    @DisplayName("Deve lançar EquipamentoNaoEncontradoException quando o ID não existir")
    void deveLancarExcecaoQuandoBuscarEquipamentoPorIdInexistente() {
        // Arrange
        Long idInexistente = 99L;
        when(equipamentoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        EquipamentoNaoEncontradoException exception = assertThrows(
                EquipamentoNaoEncontradoException.class,
                () -> equipamentoService.buscarEquipamentosId(idInexistente)
        );

        assertEquals(
                String.format(EquipamentoNaoEncontradoException.message, idInexistente),
                exception.getMessage()
        );

        verify(equipamentoRepository).findById(idInexistente);
        verifyNoInteractions(equipamentoMapper);
    }

    @Test
    @DisplayName("Deve retornar lista de EquipamentoResponse quando encontrar equipamentos com os filtros informados")
    void deveBuscarEquipamentosComFiltrosComSucesso() {
        // Arrange
        TipoEquipamento tipoFiltro = TipoEquipamento.NOTEBOOK;
        Boolean disponivelFiltro = true;

        when(equipamentoSalvo.getId()).thenReturn(1L);
        when(equipamentoRepository.buscarComFiltros(tipoFiltro, disponivelFiltro))
                .thenReturn(List.of(equipamentoSalvo));
        when(equipamentoMapper.entityToResponse(equipamentoSalvo))
                .thenReturn(equipamentoResponse);

        // Act
        List<EquipamentoResponse> resultado = equipamentoService.buscarComFiltros(tipoFiltro, disponivelFiltro);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(equipamentoSalvo.getId(), resultado.get(0).id());
        assertEquals(equipamentoResponse.patrimonio(), resultado.get(0).patrimonio());
        assertEquals(equipamentoResponse.nome(), resultado.get(0).nome());
        assertEquals(equipamentoResponse.tipo(), resultado.get(0).tipo());
        assertTrue(resultado.get(0).disponivel());

        verify(equipamentoRepository).buscarComFiltros(tipoFiltro, disponivelFiltro);
        verify(equipamentoMapper).entityToResponse(equipamentoSalvo);
    }

    @Test
    @DisplayName("Deve retornar lista de EquipamentoResponse quando os filtros forem nulos")
    void deveBuscarEquipamentosQuandoFiltrosForemNulos() {
        // Arrange
        when(equipamentoSalvo.getId()).thenReturn(1L);
        when(equipamentoRepository.buscarComFiltros(null, null))
                .thenReturn(List.of(equipamentoSalvo));
        when(equipamentoMapper.entityToResponse(equipamentoSalvo))
                .thenReturn(equipamentoResponse);

        // Act
        List<EquipamentoResponse> resultado = equipamentoService.buscarComFiltros(null, null);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(equipamentoSalvo.getId(), resultado.get(0).id());

        verify(equipamentoRepository).buscarComFiltros(null, null);
        verify(equipamentoMapper).entityToResponse(equipamentoSalvo);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum equipamento corresponder aos filtros")
    void deveRetornarListaVaziaQuandoNenhumEquipamentoForEncontradoNosFiltros() {
        // Arrange
        TipoEquipamento tipoFiltro = TipoEquipamento.NOTEBOOK;
        Boolean disponivelFiltro = false;

        when(equipamentoRepository.buscarComFiltros(tipoFiltro, disponivelFiltro))
                .thenReturn(Collections.emptyList());

        // Act
        List<EquipamentoResponse> resultado = equipamentoService.buscarComFiltros(tipoFiltro, disponivelFiltro);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(equipamentoRepository).buscarComFiltros(tipoFiltro, disponivelFiltro);
        verifyNoInteractions(equipamentoMapper);
    }

    @Test
    @DisplayName("Deve retornar uma lista de EquipamentoResponse quando existirem equipamentos cadastrados")
    void deveBuscarEquipamentosComSucesso() {
        // Arrange
        when(equipamentoSalvo.getId()).thenReturn(1L);
        List<Equipamento> listaEntidades = List.of(equipamentoSalvo);
        List<EquipamentoResponse> listaResponsesEsperada = List.of(equipamentoResponse);

        when(equipamentoRepository.findAll()).thenReturn(listaEntidades);
        when(equipamentoMapper.entityToResponse(listaEntidades)).thenReturn(listaResponsesEsperada);

        // Act
        List<EquipamentoResponse> resultado = equipamentoService.buscarEquipamentos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(equipamentoSalvo.getId(), resultado.get(0).id());
        assertEquals(equipamentoResponse.patrimonio(), resultado.get(0).patrimonio());
        assertEquals(equipamentoResponse.nome(), resultado.get(0).nome());
        assertEquals(equipamentoResponse.tipo(), resultado.get(0).tipo());
        assertTrue(resultado.get(0).disponivel());

        verify(equipamentoRepository).findAll();
        verify(equipamentoMapper).entityToResponse(listaEntidades);
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver equipamentos cadastrados")
    void deveRetornarListaVaziaQuandoNaoExistiremEquipamentos() {
        // Arrange
        List<Equipamento> listaVazia = Collections.emptyList();
        when(equipamentoRepository.findAll()).thenReturn(listaVazia);
        when(equipamentoMapper.entityToResponse(listaVazia)).thenReturn(Collections.emptyList());

        // Act
        List<EquipamentoResponse> resultado = equipamentoService.buscarEquipamentos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(equipamentoRepository).findAll();
        verify(equipamentoMapper).entityToResponse(listaVazia);
    }

    @Test
    @DisplayName("Deve deletar um equipamento com sucesso quando o ID existir")
    void deveDeletarEquipamentoComSucesso() {
        // Arrange
        Long idExistente = 1L;
        when(equipamentoRepository.findById(idExistente)).thenReturn(Optional.of(equipamentoSalvo));

        // Act
        assertDoesNotThrow(() -> equipamentoService.deleteEquipamento(idExistente));

        // Assert
        verify(equipamentoRepository).findById(idExistente);
        verify(equipamentoRepository).delete(equipamentoSalvo);
    }

    @Test
    @DisplayName("Deve lançar EquipamentoNaoEncontradoException quando tentar deletar um ID inexistente")
    void deveLancarExcecaoQuandoDeletarEquipamentoInexistente() {
        // Arrange
        Long idInexistente = 99L;
        when(equipamentoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        EquipamentoNaoEncontradoException exception = assertThrows(
                EquipamentoNaoEncontradoException.class,
                () -> equipamentoService.deleteEquipamento(idInexistente)
        );

        assertEquals(
                String.format(EquipamentoNaoEncontradoException.message, idInexistente),
                exception.getMessage()
        );

        verify(equipamentoRepository).findById(idInexistente);
        verify(equipamentoRepository, never()).delete(any());
    }
}
