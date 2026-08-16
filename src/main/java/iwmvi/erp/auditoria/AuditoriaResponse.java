package iwmvi.erp.auditoria;

import java.time.LocalDateTime;

public record AuditoriaResponse(
    Long id,
    String usuario,
    LocalDateTime dataHora,
    String operacao,
    String entidade,
    String entidadeId,
    String descricao) {
}
