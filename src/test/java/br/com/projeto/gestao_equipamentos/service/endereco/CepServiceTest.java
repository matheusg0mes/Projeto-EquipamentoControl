package br.com.projeto.gestao_equipamentos.service.endereco;

import br.com.projeto.gestao_equipamentos.client.CepClient;
import br.com.projeto.gestao_equipamentos.dtos.endereco.CepResponse;
import br.com.projeto.gestao_equipamentos.exceptions.CepInvalidoException;
import br.com.projeto.gestao_equipamentos.exceptions.IntegracaoCepException;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CepServiceTest {

    @Mock
    private CepClient cepClient;

    @InjectMocks
    private CepService cepService;

    private CepResponse cepResponseEsperado;

    @BeforeEach
    void setUp() {
        cepResponseEsperado = CepResponse.builder()
                .cep("13454-000")
                .logradouro("Rua Exemplo")
                .complemento("Casa")
                .bairro("Centro")
                .localidade("Santa Bárbara d'Oeste")
                .uf("SP")
                .build();
    }

    @Test
    @DisplayName("Deve retornar CepResponse com sucesso quando o CEP for válido")
    void deveBuscarCepComSucesso() {
        // Arrange
        String cepValido = "13454000";
        when(cepClient.buscarCep(cepValido)).thenReturn(cepResponseEsperado);

        // Act
        CepResponse resultado = cepService.buscarCep(cepValido);

        // Assert
        assertNotNull(resultado);
        assertEquals(cepResponseEsperado.cep(), resultado.cep());
        assertEquals(cepResponseEsperado.logradouro(), resultado.logradouro());
        assertEquals(cepResponseEsperado.complemento(), resultado.complemento());
        assertEquals(cepResponseEsperado.bairro(), resultado.bairro());
        assertEquals(cepResponseEsperado.localidade(), resultado.localidade());
        assertEquals(cepResponseEsperado.uf(), resultado.uf());

        verify(cepClient).buscarCep(cepValido);
    }

    @Test
    @DisplayName("Deve lançar CepInvalidoException quando o Feign retornar BadRequest (400)")
    void deveLancarCepInvalidoExceptionQuandoFeignRetornarBadRequest() {
        // Arrange
        String cepInvalido = "134543";
        FeignException.BadRequest badRequestException = mock(FeignException.BadRequest.class);
        when(cepClient.buscarCep(cepInvalido)).thenThrow(badRequestException);

        // Act & Assert
        CepInvalidoException exception = assertThrows(
                CepInvalidoException.class,
                () -> cepService.buscarCep(cepInvalido)
        );

        assertEquals(
                String.format(CepInvalidoException.message, cepInvalido),
                exception.getMessage()
        );

        verify(cepClient).buscarCep(cepInvalido);
    }

    @Test
    @DisplayName("Deve lançar IntegracaoCepException quando ocorrer erro genérico do Feign (ex: 500 ou timeout)")
    void deveLancarIntegracaoCepExceptionQuandoOcorrerErroGenericoNoFeign() {
        // Arrange
        String cep = "13454000";
        FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(500);
        when(feignException.getMessage()).thenReturn("Internal Server Error");
        when(cepClient.buscarCep(cep)).thenThrow(feignException);

        // Act & Assert
        IntegracaoCepException exception = assertThrows(
                IntegracaoCepException.class,
                () -> cepService.buscarCep(cep)
        );

        assertEquals(
                String.format(IntegracaoCepException.message, cep),
                exception.getMessage()
        );
        assertEquals(feignException, exception.getCause());

        verify(cepClient).buscarCep(cep);
        verify(feignException).status();
    }
}
