package br.com.projeto.gestao_equipamentos.service.emprestimo;

import br.com.projeto.gestao_equipamentos.dtos.emprestimo.EmprestimoRequest;
import br.com.projeto.gestao_equipamentos.dtos.emprestimo.EmprestimoResponse;

import java.util.List;

public interface IEmprestimoService {

    EmprestimoResponse criarEmprestimo(EmprestimoRequest emprestimoRequest);

    EmprestimoResponse registrarDevolucao(Long id);

    List<EmprestimoResponse> buscarAtrasados(Boolean atrasados);

    List<EmprestimoResponse> buscarEmprestimos();
}
