package iwmvi.erp.auditoria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
        Specification<Auditoria> spec = Specification.unrestricted();

        String usuarioNormalizado = normalizar(usuario);
        if (usuarioNormalizado != null) {
            spec = spec.and((root, query, cb) -> cb.like(
                    cb.lower(root.get("usuario")), "%" + usuarioNormalizado.toLowerCase() + "%"));
        }

        String entidadeNormalizada = normalizar(entidade);
        if (entidadeNormalizada != null) {
            spec = spec.and((root, query, cb) -> cb.like(
                    cb.lower(root.get("entidade")), "%" + entidadeNormalizada.toLowerCase() + "%"));
        }

        if (inicio != null) {
            LocalDateTime inicioDataHora = inicio.atStartOfDay();
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("dataHora"), inicioDataHora));
        }

        if (fim != null) {
            LocalDateTime fimDataHora = fim.atTime(LocalTime.MAX);
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("dataHora"), fimDataHora));
        }

        return auditoriaRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "dataHora"));
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
