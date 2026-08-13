package iwmvi.erp.usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        boolean ativo
) {
}
