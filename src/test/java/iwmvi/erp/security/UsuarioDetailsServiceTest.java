package iwmvi.erp.security;

import iwmvi.erp.usuario.PerfilUsuario;
import iwmvi.erp.usuario.Usuario;
import iwmvi.erp.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsuarioDetailsServiceTest {

    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final UsuarioDetailsService usuarioDetailsService = new UsuarioDetailsService(usuarioRepository);

    @Test
    void deveCarregarUsuarioAtivoComPerfilAdmin() {
        Usuario usuario = new Usuario("Administrador", "admin@erp.local", "senha-codificada", PerfilUsuario.ADMIN);
        when(usuarioRepository.findByEmail("admin@erp.local")).thenReturn(Optional.of(usuario));

        UserDetails details = usuarioDetailsService.loadUserByUsername("admin@erp.local");

        assertEquals("admin@erp.local", details.getUsername());
        assertEquals("senha-codificada", details.getPassword());
        assertTrue(details.isEnabled());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void deveFalharQuandoUsuarioNaoExistir() {
        when(usuarioRepository.findByEmail("inexistente@erp.local")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> usuarioDetailsService.loadUserByUsername("inexistente@erp.local"));
    }
}
