package br.com.projeto.gestao_equipamentos.exceptions;

public class ColaboradorNaoEncontradoException extends RuntimeException {

    public final static String message = "Colaborador com id '%d' não existe no sistema.";

    public ColaboradorNaoEncontradoException(Long id) {
        super(String.format(message, id));
    }
}

