package iwmvi.erp.financeiro;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/financeiro")
public class FinanceiroController {

    private static final Map<String, String> CATEGORIA_LABELS =
        Map.of(
            FinanceiroService.CATEGORIA_ATRASO, "Em atraso",
            FinanceiroService.CATEGORIA_VENCENDO, "Vencendo",
            FinanceiroService.CATEGORIA_ABERTO, "Em aberto",
            FinanceiroService.CATEGORIA_PAGO, "Pago",
            "CANCELADO", "Cancelado");

    private final FinanceiroService service;

    public FinanceiroController(FinanceiroService service) {
        this.service = service;
    }

    @GetMapping
    public String listar() {
        return "redirect:/financeiro/pagar";
    }

    @GetMapping("/pagar")
    public String contasPagar(
        @RequestParam(defaultValue = FinanceiroService.CATEGORIA_ABERTO) String filtro,
        Model model) {
        return listarPorTipo(TipoTituloFinanceiro.PAGAR, filtro, model, "financeiro-pagar");
    }

    @GetMapping("/receber")
    public String contasReceber(
        @RequestParam(defaultValue = FinanceiroService.CATEGORIA_ABERTO) String filtro,
        Model model) {
        return listarPorTipo(TipoTituloFinanceiro.RECEBER, filtro, model, "financeiro-receber");
    }

    @GetMapping("/extrato")
    public String extrato(Model model) {
        model.addAttribute("extrato", service.extrato());
        model.addAttribute("activePage", "financeiro-extrato");
        model.addAttribute("categorias", CATEGORIA_LABELS);
        return "financeiro/extrato";
    }

    @PostMapping("/{id}/pagar")
    public String pagar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        TituloFinanceiro titulo = service.pagar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Título baixado com sucesso.");
        return "redirect:/financeiro/"
            + (titulo.getTipo() == TipoTituloFinanceiro.PAGAR ? "pagar" : "receber");
    }

    private String listarPorTipo(
        TipoTituloFinanceiro tipo, String filtro, Model model, String activePage) {
        model.addAttribute("linhas", service.listar(tipo, filtro));
        model.addAttribute("resumo", service.resumo(tipo));
        model.addAttribute("filtro", filtro);
        model.addAttribute("tipo", tipo);
        model.addAttribute(
            "tituloPagina",
            tipo == TipoTituloFinanceiro.PAGAR ? "Contas a pagar" : "Contas a receber");
        model.addAttribute(
            "basePath",
            tipo == TipoTituloFinanceiro.PAGAR ? "/financeiro/pagar" : "/financeiro/receber");
        model.addAttribute("activePage", activePage);
        model.addAttribute("categorias", CATEGORIA_LABELS);
        return "financeiro/lista";
    }
}
