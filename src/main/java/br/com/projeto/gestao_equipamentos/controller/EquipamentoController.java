package br.com.projeto.gestao_equipamentos.controller;


import br.com.projeto.gestao_equipamentos.dtos.equipamento.EquipamentoRequest;
import br.com.projeto.gestao_equipamentos.dtos.equipamento.EquipamentoResponse;
import br.com.projeto.gestao_equipamentos.model.TipoEquipamento;
import br.com.projeto.gestao_equipamentos.service.equipamentos.EquipamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EquipamentoController {

    private final EquipamentoService equipamentoService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/equipamento/criar")
    public EquipamentoResponse criarEquipamento(@Valid @RequestBody EquipamentoRequest equipamento) {
        EquipamentoResponse equipamentoResponse = equipamentoService.criarEquipamento(equipamento);
        return equipamentoResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/equipamento/atualizar/{id}")
    public EquipamentoResponse atualizarEquipamento(@PathVariable Long id, @Valid @RequestBody EquipamentoRequest equipamentoRequest) {
        EquipamentoResponse equipamentoResponse = equipamentoService.atualizarEquipamento(id, equipamentoRequest);
        return equipamentoResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/equipamento/buscar/{id}")
    public EquipamentoResponse buscarEquipamentoId(@PathVariable Long id) {
        EquipamentoResponse equipamentoResponse = equipamentoService.buscarEquipamentosId(id);
        return equipamentoResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/equipamentos")
    public List<EquipamentoResponse> buscarEquipamentos(
            @RequestParam(required = false) TipoEquipamento tipo,
            @RequestParam(required = false) Boolean disponivel) {

        return equipamentoService.buscarComFiltros(tipo, disponivel);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/equipamento/buscar")
    public List<EquipamentoResponse> buscarEquipamentos() {
        List<EquipamentoResponse> equipamentosResponse = equipamentoService.buscarEquipamentos();
        return equipamentosResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/equpamento/delete/{id}")
    public void deleteEquipamentoId(@PathVariable Long id) {
        equipamentoService.deleteEquipamento(id);
    }
}
