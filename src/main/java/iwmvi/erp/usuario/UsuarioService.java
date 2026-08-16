package iwmvi.erp.usuario;

import iwmvi.erp.shared.exception.EmailJaCadastradoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByNomeAsc();
    }

    @Transactional
    public Usuario alternarAtivo(Long id, String usuarioAtual) {
        Usuario usuario =
            usuarioRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (usuario.getEmail().equalsIgnoreCase(usuarioAtual)) {
            throw new IllegalStateException("Não é permitido inativar o próprio acesso.");
        }
        if (usuario.isAtivo()
            && usuario.getPerfil() == PerfilUsuario.ADMIN
            && usuarioRepository.countByPerfilAndAtivoTrue(PerfilUsuario.ADMIN) <= 1) {
            throw new IllegalStateException("É necessário manter pelo menos um administrador ativo.");
        }

        usuario.alternarAtivo();
        return usuario;
    }

    private Usuario criar(String nome, String email, String senha, PerfilUsuario perfil) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException(email);
        }
        if (senha == null || senha.length() < 12 || senha.length() > 72) {
            throw new IllegalArgumentException("A senha deve ter entre 12 e 72 caracteres.");
        }

        Usuario usuario = new Usuario(nome, email, passwordEncoder.encode(senha), perfil);
        return usuarioRepository.save(usuario);
    }
}
