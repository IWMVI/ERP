package iwmvi.erp.shared.web;

import iwmvi.erp.usuario.Usuario;
import iwmvi.erp.usuario.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class NavigationModelAdvice {

    private final UsuarioRepository usuarioRepository;

    public NavigationModelAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("admin")
    public boolean admin(Authentication authentication) {
        return authentication != null
            && authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    @ModelAttribute("usuarioAtualNome")
    public String usuarioAtualNome(Authentication authentication) {
        return usuarioAtual(authentication).map(Usuario::getNome).orElse("Usuário");
    }

    @ModelAttribute("usuarioAtualEmail")
    public String usuarioAtualEmail(Authentication authentication) {
        return authentication == null ? "" : authentication.getName();
    }

    private Optional<Usuario> usuarioAtual(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return usuarioRepository.findByEmail(authentication.getName());
    }
}
