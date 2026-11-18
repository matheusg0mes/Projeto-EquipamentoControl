package br.com.projeto.gestao_equipamentos.client;

import br.com.projeto.gestao_equipamentos.dtos.endereco.CepResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "viaCep", url = "https://viacep.com.br/ws")
public interface CepClient {

    @GetMapping("/{cep}/json/")
    CepResponse buscarCep(@PathVariable String cep);
}
