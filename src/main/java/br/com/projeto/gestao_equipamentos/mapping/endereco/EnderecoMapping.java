package br.com.projeto.gestao_equipamentos.mapping.endereco;

import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorRequest;
import br.com.projeto.gestao_equipamentos.dtos.endereco.CepResponse;
import br.com.projeto.gestao_equipamentos.dtos.endereco.EnderecoRequest;
import br.com.projeto.gestao_equipamentos.dtos.endereco.EnderecoResponse;
import br.com.projeto.gestao_equipamentos.model.Endereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoMapping {
    public EnderecoResponse entityToResponse(Endereco endereco) {

        if (endereco == null) {
            return null;
        }

        return new EnderecoResponse(
                String.valueOf(endereco.getId()),
                endereco.getNumero(),
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getLocalidade(),
                endereco.getUf()
        );
    }

    public EnderecoRequest toRequest(ColaboradorRequest colaboradorRequest, CepResponse cepResponse) {
        return EnderecoRequest.builder()
                .numero(colaboradorRequest.numero())
                .cep(cepResponse.cep())
                .logradouro(cepResponse.logradouro())
                .complemento(cepResponse.complemento())
                .bairro(cepResponse.bairro())
                .localidade(cepResponse.localidade())
                .uf(cepResponse.uf())
                .build();

    }
}