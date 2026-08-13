package iwmvi.erp.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        assertTrue(usuarioSemDados.isAtivo());
    }
}
