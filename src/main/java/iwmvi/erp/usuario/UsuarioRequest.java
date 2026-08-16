package iwmvi.erp.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
    @NotBlank(message = "O nome é obrigatório.") String nome,
    @NotBlank(message = "O e-mail é obrigatório.") @Email(message = "E-mail inválido.") String email,
    @NotBlank(message = "A senha é obrigatória.") @Size(min = 12, max = 72, message = "A senha deve ter entre 12 e 72 caracteres.") String senha,
    @NotNull(message = "O perfil é obrigatório.") PerfilUsuario perfil) {}
