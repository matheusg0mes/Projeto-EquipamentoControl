package br.com.projeto.gestao_equipamentos.controller;

import br.com.projeto.gestao_equipamentos.dtos.emprestimo.EmprestimoRequest;
import br.com.projeto.gestao_equipamentos.dtos.emprestimo.EmprestimoResponse;
import br.com.projeto.gestao_equipamentos.service.emprestimo.EmprestimoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/emprestimo")
    public EmprestimoResponse criarEmprestimo(@RequestBody EmprestimoRequest emprestimoRequest){
        EmprestimoResponse emprestimoResponse = emprestimoService.criarEmprestimo(emprestimoRequest);
        return emprestimoResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/emprestimo/{id}/devolucao")
    public EmprestimoResponse registrarDevolucao(@PathVariable Long id){
        EmprestimoResponse emprestimoResponse = emprestimoService.registrarDevolucao(id);
        return emprestimoResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/emprestimos/filtrar")
    public List<EmprestimoResponse> buscarEmprestimos(@RequestParam(required = false) Boolean atrasados) {
        List<EmprestimoResponse> emprestimosResponse = emprestimoService.buscarAtrasados(atrasados);
        return emprestimosResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/emprestimos")
    public List<EmprestimoResponse> buscarEmprestimos() {
        List<EmprestimoResponse> emprestimosResponse = emprestimoService.buscarEmprestimos();
        return emprestimosResponse;
    }
}
