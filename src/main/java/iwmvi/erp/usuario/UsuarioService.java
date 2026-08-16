package iwmvi.erp.usuario;

import iwmvi.erp.shared.exception.EmailJaCadastradoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario criar(UsuarioRequest request) {
        return criar(request.nome(), request.email(), request.senha(), request.perfil());
    }

    public Usuario criarPublico(CadastroUsuarioRequest request) {
        return criar(request.nome(), request.email(), request.senha(), PerfilUsuario.USUARIO);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByNomeAsc();
    }

    private Usuario criar(String nome, String email, String senha, PerfilUsuario perfil) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException(email);
        }

        Usuario usuario = new Usuario(nome, email, passwordEncoder.encode(senha), perfil);
        return usuarioRepository.save(usuario);
    }
}
