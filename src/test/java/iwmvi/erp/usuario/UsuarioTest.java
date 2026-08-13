package iwmvi.erp.usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void configurar() {
        usuario = new Usuario("Wallace", "wallace@gmail.com", "123456");
    }

    @Test
    void deveCriarUsuarioComDadosInformados() {
        assertNotNull(usuario);
        assertEquals("Wallace", usuario.getNome());
        assertEquals("wallace@gmail.com", usuario.getEmail());
        assertEquals("123456", usuario.getSenha());
        assertEquals(PerfilUsuario.USUARIO, usuario.getPerfil());
    }

    @Test
    void deveCriarUsuarioComAtivoPadrao() {
        assertTrue(usuario.isAtivo());
    }

    @Test
    void deveIniciarIdComoNulo() {
        assertNull(usuario.getId());
    }

    @Test
    void deveCriarUsuarioComConstrutorPadraoParaJpa() {
        Usuario usuarioSemDados = new Usuario();

        assertNotNull(usuarioSemDados);
        assertNull(usuarioSemDados.getNome());
        assertNull(usuarioSemDados.getEmail());
        assertNull(usuarioSemDados.getSenha());
        assertNull(usuarioSemDados.getPerfil());
        assertTrue(usuarioSemDados.isAtivo());
    }
}
