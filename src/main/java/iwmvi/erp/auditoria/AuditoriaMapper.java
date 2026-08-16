package iwmvi.erp.auditoria;

public final class AuditoriaMapper {

    private AuditoriaMapper() {
    }

    public static AuditoriaResponse toResponse(Auditoria auditoria) {
        return new AuditoriaResponse(
            auditoria.getId(),
            auditoria.getUsuario(),
            auditoria.getDataHora(),
            auditoria.getOperacao(),
            auditoria.getEntidade(),
            auditoria.getEntidadeId(),
            auditoria.getDescricao());
    }
}
