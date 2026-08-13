package iwmvi.erp.usuario;

import iwmvi.erp.shared.exception.EmailJaCadastradoException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar().stream().map(UsuarioMapper::toResponse).toList());
        return "usuarios/lista";
    }

    @GetMapping("/usuarios/novo")
    public String novo(Model model) {
        model.addAttribute("usuarioRequest", new UsuarioRequest("", "", "", PerfilUsuario.USUARIO));
        model.addAttribute("perfis", PerfilUsuario.values());
        return "usuarios/form";
    }

    @PostMapping("/usuarios")
    public String criar(
            @Valid @ModelAttribute("usuarioRequest") UsuarioRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("perfis", PerfilUsuario.values());
            return "usuarios/form";
        }

        try {
            usuarioService.criar(request);
        } catch (EmailJaCadastradoException exception) {
            bindingResult.rejectValue("email", "email.duplicado", exception.getMessage());
            model.addAttribute("perfis", PerfilUsuario.values());
            return "usuarios/form";
        }

        redirectAttributes.addFlashAttribute("sucesso", "Usuário cadastrado com sucesso.");
        return "redirect:/usuarios";
    }
}
