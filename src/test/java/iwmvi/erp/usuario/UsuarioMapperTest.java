package iwmvi.erp.usuario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    @Test
    void deveMapearUsuarioParaResponseSemExporSenha() {
        Usuario usuario = new Usuario("Wallace", "wallace@gmail.com", "123456", PerfilUsuario.ADMIN);

        UsuarioResponse response = UsuarioMapper.toResponse(usuario);

        assertNull(response.id());
        assertEquals("Wallace", response.nome());
        assertEquals("wallace@gmail.com", response.email());
        assertEquals(PerfilUsuario.ADMIN, response.perfil());
        assertEquals(usuario.isAtivo(), response.ativo());
        assertFalse(response.toString().contains("123456"));
    }
}
