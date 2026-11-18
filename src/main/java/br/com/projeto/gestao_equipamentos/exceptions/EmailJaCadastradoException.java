package br.com.projeto.gestao_equipamentos.exceptions;

public class EmailJaCadastradoException extends RuntimeException {

    public final static String message = "O e-mail '%s' já está cadastrado no sistema.";

    public EmailJaCadastradoException(String email) {
        super(String.format(message, email));
    }
}
