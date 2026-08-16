package iwmvi.erp.produto;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import iwmvi.erp.security.SecurityConfig;
import iwmvi.erp.shared.storage.ImagemStorageService;
import iwmvi.erp.usuario.UsuarioRepository;

@WebMvcTest(ProdutoController.class)
@Import(SecurityConfig.class)
class ProdutoFormRenderingTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;
    @MockitoBean
    private ImagemStorageService imagemStorageService;
    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void deveRenderizarCamposFiscaisLogisticosEAjudas() throws Exception {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/produtos/novo").with(user("usuario@erp.local").roles("USUARIO")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"ncm\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"pesoBruto\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("class=\"info-help\"")));
    }
}
