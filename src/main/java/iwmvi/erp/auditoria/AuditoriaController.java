package iwmvi.erp.auditoria;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/auditoria")
    public String listar(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String entidade,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate fim,
            Model model) {
        model.addAttribute("registros", auditoriaService.buscar(usuario, entidade, inicio, fim));
        model.addAttribute("usuario", usuario);
        model.addAttribute("entidade", entidade);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        return "auditoria/lista";
    }
}
