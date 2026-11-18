package br.com.projeto.gestao_equipamentos.exceptions;

public class PatrimonioJaCadastradoException extends RuntimeException {

    public final static String message = "Patrimônio já cadastrado.";

    public PatrimonioJaCadastradoException() {
        super(message);
    }
}
