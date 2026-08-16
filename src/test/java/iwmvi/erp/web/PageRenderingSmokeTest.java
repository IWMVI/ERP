package iwmvi.erp.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.cliente.ClienteService;
import iwmvi.erp.estoque.EstoqueService;
import iwmvi.erp.fornecedor.FornecedorService;
import iwmvi.erp.produto.ProdutoService;
import iwmvi.erp.security.SecurityConfig;
import iwmvi.erp.usuario.UsuarioRepository;
import iwmvi.erp.usuario.UsuarioService;

@WebMvcTest
@Import(SecurityConfig.class)
class PageRenderingSmokeTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ClienteService clienteService;
    @MockitoBean private FornecedorService fornecedorService;
    @MockitoBean private ProdutoService produtoService;
    @MockitoBean private EstoqueService estoqueService;
    @MockitoBean private UsuarioService usuarioService;
    @MockitoBean private UsuarioRepository usuarioRepository;
    @MockitoBean private AuditoriaService auditoriaService;

    @ParameterizedTest
    @ValueSource(strings = {"/", "/clientes", "/fornecedores", "/produtos", "/estoque"})
    void deveRenderizarPaginasInternas(String path) throws Exception {
        mockMvc.perform(get(path).with(user("usuario@erp.local").roles("USUARIO")))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/usuarios", "/auditoria"})
    void deveRenderizarPaginasAdministrativas(String path) throws Exception {
        mockMvc.perform(get(path).with(user("admin@erp.local").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}
