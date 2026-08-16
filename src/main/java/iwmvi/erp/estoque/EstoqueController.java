package iwmvi.erp.estoque;

import iwmvi.erp.shared.exception.SaldoEstoqueInsuficienteException;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    private final EstoqueService service;

    public EstoqueController(EstoqueService service) {
        this.service = service;
    }

    @GetMapping
    public String painel(
            @RequestParam(required = false) Long produtoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate fim,
            Model model) {
        model.addAttribute("produtos", service.produtos());
        model.addAttribute("abaixoMinimo", service.abaixoDoMinimo());
        model.addAttribute("movimentacoes", service.historico(produtoId, inicio, fim));
        model.addAttribute("produtoId", produtoId);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        return "estoque/painel";
    }

    @GetMapping("/movimentar")
    public String movimentar(Model model) {
        prepararFormulario(
                model,
                new MovimentacaoEstoqueRequest(
                        null, TipoMovimentacao.ENTRADA, BigDecimal.ONE, "AJUSTE MANUAL"));
        return "estoque/form";
    }

    @PostMapping("/movimentacoes")
    public String registrar(
            @Valid @ModelAttribute("movimentacaoRequest") MovimentacaoEstoqueRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepararFormulario(model, request);
            return "estoque/form";
        }

        try {
            service.movimentar(request);
        } catch (SaldoEstoqueInsuficienteException | IllegalStateException exception) {
            result.reject("estoque.invalido", exception.getMessage());
            prepararFormulario(model, request);
            return "estoque/form";
        }

        redirect.addFlashAttribute("sucesso", "Movimentação registrada com sucesso.");
        return "redirect:/estoque";
    }

    private void prepararFormulario(Model model, MovimentacaoEstoqueRequest request) {
        model.addAttribute("movimentacaoRequest", request);
        model.addAttribute("produtos", service.produtos());
        model.addAttribute("tipos", TipoMovimentacao.values());
    }
}
