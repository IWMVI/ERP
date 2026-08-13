package iwmvi.erp.security;

import iwmvi.erp.shared.exception.EmailJaCadastradoException;
import iwmvi.erp.usuario.PerfilUsuario;
import iwmvi.erp.usuario.UsuarioRequest;
import iwmvi.erp.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdministradorInicialInitializer implements ApplicationRunner {

    private final UsuarioService usuarioService;
    private final String nome;
    private final String email;
    private final String senha;

    public AdministradorInicialInitializer(
            UsuarioService usuarioService,
            @Value("${app.admin.nome:Administrador}") String nome,
            @Value("${app.admin.email:}") String email,
            @Value("${app.admin.senha:}") String senha) {
        this.usuarioService = usuarioService;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() || senha.isBlank()) {
            return;
        }

        try {
            usuarioService.criar(new UsuarioRequest(nome, email, senha, PerfilUsuario.ADMIN));
        } catch (EmailJaCadastradoException ignored) {
            // Administrador já cadastrado.
        }
    }
}
