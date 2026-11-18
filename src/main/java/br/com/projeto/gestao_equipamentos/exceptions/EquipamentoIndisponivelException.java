package br.com.projeto.gestao_equipamentos.exceptions;

public class EquipamentoIndisponivelException extends RuntimeException {

    public final static String message = "O equipamento com id '%d' está indisponível para empréstimo.";

    public EquipamentoIndisponivelException(Long id) {
        super(String.format(message, id));
    }
}
