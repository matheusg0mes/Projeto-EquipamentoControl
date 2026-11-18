package br.com.projeto.gestao_equipamentos.service.endereco;

import br.com.projeto.gestao_equipamentos.client.CepClient;
import br.com.projeto.gestao_equipamentos.dtos.endereco.CepResponse;
import br.com.projeto.gestao_equipamentos.exceptions.CepInvalidoException;
import br.com.projeto.gestao_equipamentos.exceptions.IntegracaoCepException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CepService {
    private final CepClient cepClient;

    public CepResponse buscarCep(String cep) {
        try {
            return cepClient.buscarCep(cep);
        } catch (FeignException.BadRequest e) {
            log.warn("Formato de CEP inválido enviado para o ViaCEP: {}", cep);
            throw new CepInvalidoException(cep);
        } catch (FeignException e) {
            log.error("Erro na comunicação com o ViaCEP. Status: {}, Detalhe: {}", e.status(), e.getMessage());
            throw new IntegracaoCepException(cep, e);
        }
    }
}