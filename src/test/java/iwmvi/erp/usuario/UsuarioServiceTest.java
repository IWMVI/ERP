package iwmvi.erp.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import iwmvi.erp.Usuario.Usuario;
import iwmvi.erp.Usuario.UsuarioRepository;
import iwmvi.erp.Usuario.UsuarioRequest;
import iwmvi.erp.Usuario.UsuarioService;
import iwmvi.erp.exceptions.EmailJaCadastradoException;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository);
    }

    @Test
    @DisplayName("Deve cadastrar usuário.")
    void deveCadastrarUsuario() throws EmailJaCadastradoException {
        UsuarioRequest request = new UsuarioRequest(
                "Wallace",
                "wallace@gmail.com",
                "123456");

        when(usuarioRepository.existsByEmail(request.email())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuario = usuarioService.criar(request);

        assertNotNull(usuario);
        assertEquals("Wallace", usuario.getNome());
        assertEquals("wallace@gmail.com", usuario.getEmail());
        assertEquals("123456", usuario.getSenha());
        assertTrue(usuario.isAtivo());

        verify(usuarioRepository).existsByEmail("wallace@gmail.com");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarUsuarioComEmailJaCadastrado() {
        UsuarioRequest request = new UsuarioRequest(
                "Wallace",
                "wallace@gmail.com",
                "123456");

        when(usuarioRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> {
            usuarioService.criar(request);
        });
    }
}