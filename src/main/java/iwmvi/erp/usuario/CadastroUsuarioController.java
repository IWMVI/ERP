package iwmvi.erp.usuario;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.shared.exception.EmailJaCadastradoException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CadastroUsuarioController {

    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;

    public CadastroUsuarioController(
            UsuarioService usuarioService, AuditoriaService auditoriaService) {
        this.usuarioService = usuarioService;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/cadastro")
    public String formulario(@ModelAttribute CadastroUsuarioRequest cadastroUsuarioRequest) {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @Valid @ModelAttribute CadastroUsuarioRequest cadastroUsuarioRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "cadastro";
        }

        try {
            Usuario usuario = usuarioService.criarPublico(cadastroUsuarioRequest);
            auditoriaService.registrar(
                    "CRIAR_PUBLICO", "Usuario", usuario.getId(), usuario.getEmail());
        } catch (EmailJaCadastradoException exception) {
            bindingResult.rejectValue("email", "email.duplicado", exception.getMessage());
            return "cadastro";
        }

        return "redirect:/login?cadastro";
    }
}
