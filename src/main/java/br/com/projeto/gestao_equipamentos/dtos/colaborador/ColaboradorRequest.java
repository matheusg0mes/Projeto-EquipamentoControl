package br.com.projeto.gestao_equipamentos.dtos.colaborador;

import br.com.projeto.gestao_equipamentos.security.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CPF;

@Builder
public record ColaboradorRequest(
        @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
        @NotBlank (message = "O nome não pode ser nulo ou vazio")
        String nome,

        @NotBlank(message = "O email não pode ser nulo ou vazio")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank (message = "O CPF não pode ser nulo ou vazio")
        @CPF(message = "CPF inválido")
        String cpf,
        String cep,
        String numero

) {
}
