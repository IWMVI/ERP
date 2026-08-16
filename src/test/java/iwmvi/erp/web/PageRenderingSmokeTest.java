package iwmvi.erp.web;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.cliente.ClienteRepository;
import iwmvi.erp.cliente.ClienteService;
import iwmvi.erp.compra.PedidoCompraService;
import iwmvi.erp.estoque.EstoqueService;
import iwmvi.erp.financeiro.FinanceiroService;
import iwmvi.erp.fornecedor.FornecedorRepository;
import iwmvi.erp.fornecedor.FornecedorService;
import iwmvi.erp.funcionario.FuncionarioService;
import iwmvi.erp.integracao.BrasilApiClient;
import iwmvi.erp.integracao.PessoaCadastroLookupService;
import iwmvi.erp.produto.ProdutoRepository;
import iwmvi.erp.produto.ProdutoService;
import iwmvi.erp.security.SecurityConfig;
import iwmvi.erp.shared.storage.ImagemStorageService;
import iwmvi.erp.usuario.UsuarioRepository;
import iwmvi.erp.usuario.UsuarioService;
import iwmvi.erp.venda.PedidoVendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
class PageRenderingSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;
    @MockitoBean
    private ClienteRepository clienteRepository;
    @MockitoBean
    private FornecedorService fornecedorService;
    @MockitoBean
    private FornecedorRepository fornecedorRepository;
    @MockitoBean
    private ProdutoService produtoService;
    @MockitoBean
    private ProdutoRepository produtoRepository;
    @MockitoBean
    private EstoqueService estoqueService;
    @MockitoBean
    private PedidoCompraService pedidoCompraService;
    @MockitoBean
    private PedidoVendaService pedidoVendaService;
    @MockitoBean
    private FinanceiroService financeiroService;
    @MockitoBean
    private FuncionarioService funcionarioService;
    @MockitoBean
    private ImagemStorageService imagemStorageService;
    @MockitoBean
    private UsuarioService usuarioService;
    @MockitoBean
    private UsuarioRepository usuarioRepository;
    @MockitoBean
    private AuditoriaService auditoriaService;
    @MockitoBean
    private BrasilApiClient brasilApiClient;
    @MockitoBean
    private PessoaCadastroLookupService pessoaCadastroLookupService;

    @BeforeEach
    void setUp() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(clienteService.listar()).thenReturn(List.of());
        when(fornecedorService.listar()).thenReturn(List.of());
        when(produtoService.listar(any())).thenReturn(List.of());
        when(estoqueService.produtos()).thenReturn(List.of());
        when(estoqueService.abaixoDoMinimo()).thenReturn(List.of());
        when(estoqueService.historico(any(), any(), any())).thenReturn(List.of());
        when(usuarioService.listar()).thenReturn(List.of());
        when(auditoriaService.buscar(any(), any(), any(), any())).thenReturn(List.of());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/clientes", "/fornecedores", "/produtos", "/estoque"})
    void deveRenderizarPaginasInternas(String path) throws Exception {
        mockMvc
            .perform(get(path).with(user("usuario@erp.local").roles("USUARIO")))
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/usuarios", "/auditoria"})
    void deveRenderizarPaginasAdministrativas(String path) throws Exception {
        mockMvc
            .perform(get(path).with(user("admin@erp.local").roles("ADMIN")))
            .andExpect(status().isOk());
    }
}
