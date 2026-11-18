package br.com.projeto.gestao_equipamentos.mapping;

import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorRequest;
import br.com.projeto.gestao_equipamentos.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapping {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "senha", source = "senha")
    Usuario toEntity(ColaboradorRequest request, String senha);
}

