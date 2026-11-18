package br.com.projeto.gestao_equipamentos.service.usuario;

import br.com.projeto.gestao_equipamentos.dtos.colaborador.ColaboradorRequest;
import br.com.projeto.gestao_equipamentos.exceptions.EmailJaCadastradoException;
import br.com.projeto.gestao_equipamentos.mapping.UsuarioMapping;
import br.com.projeto.gestao_equipamentos.model.Usuario;
import br.com.projeto.gestao_equipamentos.repository.UsuarioRepository;
import br.com.projeto.gestao_equipamentos.security.Perfil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService{

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapping usuarioMapping;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario criarUsuario(ColaboradorRequest request) {
        log.info("Iniciando criação de usuário. E-mail: {}", request.email());

        if (usuarioRepository.existsByEmail(request.email())) {
            log.warn("Tentativa de cadastro falhou: e-mail já cadastrado. E-mail: {}", request.email());
            throw new EmailJaCadastradoException(request.email());
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        String senha = "123456";
       String senha1 =  passwordEncoder.encode(senha);
        usuario.setSenha(senha1);
        usuario.setPerfil(Perfil.COLABORADOR);
        Usuario usuario1 = usuarioRepository.save(usuario);

        return usuario1;
    }
}
