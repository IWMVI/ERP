package iwmvi.erp.financeiro;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/financeiro")
public class FinanceiroController {

    private final FinanceiroService service;

    public FinanceiroController(FinanceiroService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "abertos") String filtro, Model model) {
        model.addAttribute(
            "titulos", "vencidos".equals(filtro) ? service.vencidos() : service.emAberto());
        model.addAttribute("filtro", filtro);
        model.addAttribute("activePage", "financeiro");
        return "financeiro/lista";
    }

    @PostMapping("/{id}/pagar")
    public String pagar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.pagar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Título baixado com sucesso.");
        return "redirect:/financeiro";
    }
}
