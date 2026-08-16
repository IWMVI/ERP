package iwmvi.erp.auditoria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public void registrar(String operacao, String entidade, Object entidadeId, String descricao) {
        Auditoria auditoria =
                new Auditoria(
                        usuarioAtual(),
                        LocalDateTime.now(),
                        operacao,
                        entidade,
                        entidadeId == null ? null : entidadeId.toString(),
                        descricao);
        auditoriaRepository.save(auditoria);
    }

    @Transactional(readOnly = true)
    public List<Auditoria> buscar(
            String usuario, String entidade, LocalDate inicio, LocalDate fim) {
        return auditoriaRepository.buscar(
                normalizar(usuario),
                normalizar(entidade),
                inicio == null ? null : inicio.atStartOfDay(),
                fim == null ? null : fim.atTime(LocalTime.MAX));
    }

    private String usuarioAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "SISTEMA";
        }
        return authentication.getName();
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
