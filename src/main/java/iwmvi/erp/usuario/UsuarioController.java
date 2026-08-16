package iwmvi.erp.usuario;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.shared.exception.EmailJaCadastradoException;
import iwmvi.erp.shared.web.PageView;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;

    public UsuarioController(UsuarioService usuarioService, AuditoriaService auditoriaService) {
        this.usuarioService = usuarioService;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/usuarios")
    public String listar(@RequestParam(defaultValue = "0") int page, Model model) {
        var usuarios = usuarioService.listar().stream().map(UsuarioMapper::toResponse).toList();
        PageView<UsuarioResponse> paginacao = PageView.of(usuarios, page, "/usuarios");
        model.addAttribute("usuarios", paginacao.items());
        model.addAttribute("paginacao", paginacao);
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
            model.addAttribute("erroGlobal", "Revise os campos destacados antes de salvar o usuário.");
            return "usuarios/form";
        }

        try {
            Usuario usuario = usuarioService.criar(request);
            auditoriaService.registrar(
                "CRIAR", "Usuario", usuario.getId(), usuario.getEmail() + " - " + usuario.getPerfil());
        } catch (EmailJaCadastradoException exception) {
            bindingResult.rejectValue("email", "email.duplicado", exception.getMessage());
            model.addAttribute("perfis", PerfilUsuario.values());
            model.addAttribute("erroGlobal", exception.getMessage());
            return "usuarios/form";
        }

        redirectAttributes.addFlashAttribute("sucesso", "Usuário cadastrado com sucesso.");
        return "redirect:/usuarios";
    }
}
