package br.com.projeto.gestao_equipamentos.exceptions;

import java.time.Instant;

public record ErroPadrao(
        Instant timestamp,
        Integer status,
        String erro,
        String mensagem,
        String caminho
) {
}
