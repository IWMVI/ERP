package iwmvi.erp.auditoria;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    @Mock private AuditoriaRepository repository;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveRegistrarUsuarioAutenticado() {
        SecurityContextHolder.getContext()
                .setAuthentication(
                        UsernamePasswordAuthenticationToken.authenticated(
                                "admin@erp.local", "senha", java.util.List.of()));
        AuditoriaService service = new AuditoriaService(repository);

        service.registrar("CRIAR", "Cliente", 10L, "Cliente teste");

        verify(repository)
                .save(
                        argThat(
                                registro ->
                                        registro.getUsuario().equals("admin@erp.local")
                                                && registro.getOperacao().equals("CRIAR")
                                                && registro.getEntidade().equals("Cliente")
                                                && registro.getEntidadeId().equals("10")));
    }
}
