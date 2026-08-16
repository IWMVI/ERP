package iwmvi.erp.usuario;

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

    public CadastroUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
            usuarioService.criarPublico(cadastroUsuarioRequest);
        } catch (EmailJaCadastradoException exception) {
            bindingResult.rejectValue("email", "email.duplicado", exception.getMessage());
            return "cadastro";
        }

        return "redirect:/login?cadastro";
    }
}
