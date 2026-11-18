package br.com.projeto.gestao_equipamentos.mapping.colaborador;

import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorRequest;
import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorResponse;
import br.com.projeto.gestao_equipamentos.dtos.endereco.EnderecoRequest;
import br.com.projeto.gestao_equipamentos.dtos.endereco.EnderecoResponse;
import br.com.projeto.gestao_equipamentos.mapping.endereco.EnderecoMapping;
import br.com.projeto.gestao_equipamentos.model.Colaborador;
import br.com.projeto.gestao_equipamentos.model.Endereco;
import br.com.projeto.gestao_equipamentos.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ColaboradorMapping {

    private final EnderecoMapping enderecoMapping;

    public List<ColaboradorResponse> entityToResponse(List<Colaborador> colaboradores) {
        return colaboradores.stream()
                .map(colaborador -> entityToResponse(colaborador))
                .toList();
    }

    public Colaborador requestToEntity(ColaboradorRequest colaboradorRequest, EnderecoRequest enderecoRequest, Usuario usuario) {
        Endereco endereco = new Endereco();
        endereco.setNumero(enderecoRequest.numero());
        endereco.setCep(enderecoRequest.cep());
        endereco.setLogradouro(enderecoRequest.logradouro());
        endereco.setComplemento(enderecoRequest.complemento());
        endereco.setBairro(enderecoRequest.bairro());
        endereco.setLocalidade(enderecoRequest.localidade());
        endereco.setUf(enderecoRequest.uf());

        Colaborador colaborador = new Colaborador();
        colaborador.setNome(colaboradorRequest.nome());
        colaborador.setEmail(colaboradorRequest.email());
        colaborador.setCpf(colaboradorRequest.cpf());
        colaborador.setEndereco(endereco);
        colaborador.setUsuario(usuario);

        return colaborador;
    }

    public ColaboradorResponse entityToResponse(Colaborador colaborador) {
        if (colaborador == null) {
            return null;
        }

        EnderecoResponse enderecoResponse =
                enderecoMapping.entityToResponse(colaborador.getEndereco());

        return new ColaboradorResponse(
                String.valueOf(colaborador.getId()),
                colaborador.getNome(),
                colaborador.getEmail(),
                colaborador.getCpf(),
                enderecoResponse
        );
    }
}
