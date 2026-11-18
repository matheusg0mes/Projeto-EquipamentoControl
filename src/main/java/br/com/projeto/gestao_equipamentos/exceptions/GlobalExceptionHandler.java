package br.com.projeto.gestao_equipamentos.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ColaboradorNaoEncontradoException.class)
    public ResponseEntity<ErroPadrao> handleColaboradorNaoEncontrado(ColaboradorNaoEncontradoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErroPadrao body = new ErroPadrao(
                Instant.now(),
                status.value(),
                "Colaborador Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErroPadrao> handleEmailJaCadastrado(EmailJaCadastradoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErroPadrao body = new ErroPadrao(
                Instant.now(),
                status.value(),
                "E-mail Já Cadastrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(PatrimonioJaCadastradoException.class)
    public ResponseEntity<ErroPadrao> handlePatrimonioDuplicado(PatrimonioJaCadastradoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErroPadrao body = new ErroPadrao(
                Instant.now(),
                status.value(),
                "Patrimônio Já Cadastrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(EquipamentoNaoEncontradoException.class)
    public ResponseEntity<ErroPadrao> handleEquipamentoNaoEncontrado(EquipamentoNaoEncontradoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErroPadrao body = new ErroPadrao(
                Instant.now(),
                status.value(),
                "Equipamento não encontrado.",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(CepInvalidoException.class)
    public ResponseEntity<ErroPadrao> handleCepInvalido(CepInvalidoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErroPadrao body = new ErroPadrao(
                Instant.now(),
                status.value(),
                "CEP inválido.",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(IntegracaoCepException.class)
    public ResponseEntity<ErroPadrao> handleIntegracaoCep(IntegracaoCepException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        ErroPadrao body = new ErroPadrao(
                Instant.now(),
                status.value(),
                "Erro de integração com API.",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(EmprestimoNaoEncontradoException.class)
    public ResponseEntity<ErroPadrao> handleEmprestimoNaoEncontrado(EmprestimoNaoEncontradoException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErroPadrao body = new ErroPadrao(
                Instant.now(),
                status.value(),
                "Empréstimo não encontrado.",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(EquipamentoIndisponivelException.class)
    public ResponseEntity<ErroPadrao> handleEquipamentoIndisponivel(EquipamentoIndisponivelException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErroPadrao body = new ErroPadrao(
                Instant.now(),
                status.value(),
                "Equipamento Indisponível",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

}
