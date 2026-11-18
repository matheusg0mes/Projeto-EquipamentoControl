package br.com.projeto.gestao_equipamentos.exceptions;

public class CepInvalidoException extends RuntimeException {

    public final static String message = "O CEP '%s' possui formato inválido. Utilize 8 dígitos numéricos.";

    public CepInvalidoException(String cep) {
        super(String.format(message, cep));
    }
}
