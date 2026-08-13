package iwmvi.erp.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class UsuarioMapperTest {

    @Test
    void deveMapearUsuarioParaResponseSemExporSenha() {
        Usuario usuario = new Usuario("Wallace", "wallace@gmail.com", "123456");

        UsuarioResponse response = UsuarioMapper.toResponse(usuario);

        assertNull(response.id());
        assertEquals("Wallace", response.nome());
        assertEquals("wallace@gmail.com", response.email());
        assertEquals(usuario.isAtivo(), response.ativo());
        assertFalse(response.toString().contains("123456"));
    }
}
