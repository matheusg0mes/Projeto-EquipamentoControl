package br.com.projeto.gestao_equipamentos.exceptions;

public class IntegracaoCepException extends RuntimeException {

    public final static String message = "Não foi possível consultar o CEP '%s' no momento. Tente novamente mais tarde.";

    public IntegracaoCepException(String cep, Throwable cause) {
        super(String.format(message, cep), cause);
    }
}
