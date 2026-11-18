package br.com.projeto.gestao_equipamentos.exceptions;

public class EmprestimoNaoEncontradoException extends RuntimeException {

    public final static String message = "Empréstimo com id '%d' não existe no sistema.";

    public EmprestimoNaoEncontradoException(Long id) {
        super(String.format(message, id));
    }
}
