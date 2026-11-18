package br.com.projeto.gestao_equipamentos.controller;

import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorRequest;
import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import br.com.projeto.gestao_equipamentos.service.colaborador.ColaboradorService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ColaboradorController {

    private final ColaboradorService colaboradorService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/colaborador/criar")
    public ColaboradorResponse criarColaborador(@Valid @RequestBody ColaboradorRequest colaboradorRequest) {
        ColaboradorResponse colaboradorResponse = colaboradorService.criarColaborador(colaboradorRequest);
        return colaboradorResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/colaborador/buscar/{id}")
    public ColaboradorResponse buscarPorId(@PathVariable Long id) {
        ColaboradorResponse colaboradorResponse = colaboradorService.buscarPorId(id);
        return colaboradorResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/colaboradores/buscar")
    public List<ColaboradorResponse> buscarColaboradores() {
        List<ColaboradorResponse> colaboradoresResponse = colaboradorService.buscarColaboradores();
        return colaboradoresResponse;
    }
}