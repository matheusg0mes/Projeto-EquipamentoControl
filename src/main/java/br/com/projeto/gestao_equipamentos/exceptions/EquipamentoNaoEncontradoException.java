package br.com.projeto.gestao_equipamentos.exceptions;

public class EquipamentoNaoEncontradoException extends RuntimeException {

    public final static String message = "Equipamento com id '%d' não existe no sistema.";

    public EquipamentoNaoEncontradoException(Long id) {
        super(String.format(message, id));
    }
}
