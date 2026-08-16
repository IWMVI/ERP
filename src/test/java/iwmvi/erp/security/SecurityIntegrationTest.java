package iwmvi.erp.security;

import iwmvi.erp.usuario.PerfilUsuario;
import iwmvi.erp.usuario.Usuario;
import iwmvi.erp.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void prepararUsuarios() {
        usuarioRepository.deleteAll();
        usuarioRepository.save(
            new Usuario(
                "Administrador",
                "admin@erp.local",
                passwordEncoder.encode("123456"),
                PerfilUsuario.ADMIN));
    }

    @Test
    void devePermitirAcessoPublicoAoLogin() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk());
    }

    @Test
    void devePermitirAcessoPublicoAoAvisoDePrivacidade() throws Exception {
        mockMvc.perform(get("/privacidade")).andExpect(status().isOk());
    }

    @Test
    void deveBloquearCadastroPublico() throws Exception {
        mockMvc.perform(get("/cadastro")).andExpect(status().is3xxRedirection());
    }

    @Test
    void deveExigirAutenticacaoParaTelaDeUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios")).andExpect(status().is3xxRedirection());
    }

    @Test
    void deveAutenticarComEmailESenhaValidos() throws Exception {
        mockMvc
            .perform(formLogin().user("admin@erp.local").password("123456"))
            .andExpect(authenticated().withUsername("admin@erp.local"));
    }

    @Test
    void deveRejeitarSenhaInvalida() throws Exception {
        mockMvc
            .perform(formLogin().user("admin@erp.local").password("senha-invalida"))
            .andExpect(unauthenticated());
    }

    @Test
    void deveBloquearTelaDeUsuariosParaPerfilComum() throws Exception {
        mockMvc
            .perform(get("/usuarios").with(user("usuario@erp.local").roles("USUARIO")))
            .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirTelaDeUsuariosParaAdministrador() throws Exception {
        mockMvc
            .perform(get("/usuarios").with(user("admin@erp.local").roles("ADMIN")))
            .andExpect(status().isOk());
    }

    @Test
    void deveBloquearFotoDeFuncionarioParaPerfilComum() throws Exception {
        mockMvc
            .perform(
                get("/uploads/funcionarios/00000000-0000-0000-0000-000000000000.jpg")
                    .with(user("usuario@erp.local").roles("USUARIO")))
            .andExpect(status().isForbidden());
    }
}
