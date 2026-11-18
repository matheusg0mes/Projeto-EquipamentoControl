package br.com.projeto.gestao_equipamentos.controller;

import br.com.projeto.gestao_equipamentos.service.endereco.CepService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/cep")
@RequiredArgsConstructor
@RestController
public class CepController {

    private final CepService cepService;

    @GetMapping("/{cep}")
    public ResponseEntity<?> getCep(@PathVariable String cep) {
        return ResponseEntity.ok(cepService.buscarCep(cep));
    }
}