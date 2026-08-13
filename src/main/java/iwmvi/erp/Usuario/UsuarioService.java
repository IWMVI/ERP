package iwmvi.erp.Usuario;

import iwmvi.erp.exceptions.EmailJaCadastradoException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario criar(UsuarioRequest request) throws EmailJaCadastradoException {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        Usuario usuario = new Usuario(request.nome(), request.email(), request.senha());
        return usuarioRepository.save(usuario);
    }
}
