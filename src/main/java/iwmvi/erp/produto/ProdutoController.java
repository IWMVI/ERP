package iwmvi.erp.produto;

import iwmvi.erp.shared.exception.CodigoProdutoJaCadastradoException;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("produtos", service.listar(q));
        model.addAttribute("q", q);
        return "produtos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        preparar(
                model,
                new ProdutoRequest(
                        "",
                        "",
                        "",
                        UnidadeMedida.UNIDADE,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO),
                null);
        return "produtos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Produto produto = service.buscar(id);
        preparar(
                model,
                new ProdutoRequest(
                        produto.getCodigo(),
                        produto.getNome(),
                        produto.getCategoria(),
                        produto.getUnidadeMedida(),
                        produto.getPrecoVenda(),
                        produto.getCusto(),
                        produto.getEstoqueMinimo()),
                id);
        return "produtos/form";
    }

    @PostMapping
    public String criar(
            @Valid @ModelAttribute("produtoRequest") ProdutoRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            preparar(model, request, null);
            return "produtos/form";
        }

        try {
            service.criar(request);
        } catch (CodigoProdutoJaCadastradoException exception) {
            result.rejectValue("codigo", "duplicado", exception.getMessage());
            preparar(model, request, null);
            return "produtos/form";
        }

        redirect.addFlashAttribute("sucesso", "Produto cadastrado com sucesso.");
        return "redirect:/produtos";
    }

    @PostMapping("/{id}")
    public String atualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("produtoRequest") ProdutoRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            preparar(model, request, id);
            return "produtos/form";
        }

        try {
            service.atualizar(id, request);
        } catch (CodigoProdutoJaCadastradoException exception) {
            result.rejectValue("codigo", "duplicado", exception.getMessage());
            preparar(model, request, id);
            return "produtos/form";
        }

        redirect.addFlashAttribute("sucesso", "Produto atualizado com sucesso.");
        return "redirect:/produtos";
    }

    @PostMapping("/{id}/status")
    public String status(@PathVariable Long id) {
        service.alternarAtivo(id);
        return "redirect:/produtos";
    }

    private void preparar(Model model, ProdutoRequest request, Long id) {
        model.addAttribute("produtoRequest", request);
        model.addAttribute("unidades", UnidadeMedida.values());
        model.addAttribute("id", id);
    }
}
