package br.com.projeto.gestao_equipamentos.service.usuario;

import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorRequest;
import br.com.projeto.gestao_equipamentos.model.Usuario;

public interface IUsuarioService {

    Usuario criarUsuario(ColaboradorRequest request);
}
