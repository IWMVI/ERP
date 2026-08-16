package iwmvi.erp.auditoria;

import iwmvi.erp.shared.web.PageView;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

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
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
        @RequestParam(defaultValue = "0") int page,
        Model model) {
        PageView<AuditoriaResponse> paginacao =
            PageView.of(
                auditoriaService.buscar(usuario, entidade, inicio, fim).stream()
                    .map(AuditoriaMapper::toResponse)
                    .toList(),
                page,
                "/auditoria",
                Map.of(
                    "usuario", usuario == null ? "" : usuario,
                    "entidade", entidade == null ? "" : entidade,
                    "inicio", inicio == null ? "" : inicio,
                    "fim", fim == null ? "" : fim));

        model.addAttribute("registros", paginacao.items());
        model.addAttribute("paginacao", paginacao);
        model.addAttribute("usuario", usuario);
        model.addAttribute("entidade", entidade);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        return "auditoria/lista";
    }
}
