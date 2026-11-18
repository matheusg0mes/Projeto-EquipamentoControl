package br.com.projeto.gestao_equipamentos.security;

import br.com.projeto.gestao_equipamentos.model.Usuario;
import br.com.projeto.gestao_equipamentos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${api.security.admin.email}")
    private String adminEmail;

    @Value("${api.security.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = new Usuario();
            admin.setEmail(adminEmail);
            admin.setSenha(passwordEncoder.encode(adminPassword));
            admin.setPerfil(Perfil.ADMIN);

            usuarioRepository.save(admin);
        }
    }
}
