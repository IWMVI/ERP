package iwmvi.erp.usuario;

public record UsuarioResponse(
    Long id, String nome, String email, PerfilUsuario perfil, boolean ativo) {
}
