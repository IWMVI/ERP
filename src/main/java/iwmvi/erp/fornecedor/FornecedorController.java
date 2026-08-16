package iwmvi.erp.fornecedor;

import iwmvi.erp.shared.exception.CepInvalidoException;
import iwmvi.erp.shared.exception.DocumentoInvalidoException;
import iwmvi.erp.shared.exception.DocumentoJaCadastradoException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/fornecedores")
public class FornecedorController {

    private final FornecedorService service;

    public FornecedorController(FornecedorService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("fornecedores", service.listar());
        return "fornecedores/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        preparar(model, new FornecedorRequest("", "", "", "", "", "", "", "", ""), null);
        return "fornecedores/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Fornecedor fornecedor = service.buscar(id);
        preparar(
                model,
                new FornecedorRequest(
                        fornecedor.getNome(),
                        fornecedor.getDocumento(),
                        fornecedor.getEmail(),
                        fornecedor.getTelefone(),
                        fornecedor.getLogradouro(),
                        fornecedor.getNumero(),
                        fornecedor.getCidade(),
                        fornecedor.getEstado(),
                        fornecedor.getCep()),
                id);
        return "fornecedores/form";
    }

    @PostMapping
    public String criar(
            @Valid @ModelAttribute("fornecedorRequest") FornecedorRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            preparar(model, request, null);
            return "fornecedores/form";
        }

        try {
            service.criar(request);
        } catch (DocumentoJaCadastradoException | DocumentoInvalidoException exception) {
            result.rejectValue("documento", "documento.invalido", exception.getMessage());
            preparar(model, request, null);
            return "fornecedores/form";
        } catch (CepInvalidoException exception) {
            result.rejectValue("cep", "cep.invalido", exception.getMessage());
            preparar(model, request, null);
            return "fornecedores/form";
        }

        redirect.addFlashAttribute("sucesso", "Fornecedor cadastrado com sucesso.");
        return "redirect:/fornecedores";
    }

    @PostMapping("/{id}")
    public String atualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("fornecedorRequest") FornecedorRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            preparar(model, request, id);
            return "fornecedores/form";
        }

        try {
            service.atualizar(id, request);
        } catch (DocumentoJaCadastradoException | DocumentoInvalidoException exception) {
            result.rejectValue("documento", "documento.invalido", exception.getMessage());
            preparar(model, request, id);
            return "fornecedores/form";
        } catch (CepInvalidoException exception) {
            result.rejectValue("cep", "cep.invalido", exception.getMessage());
            preparar(model, request, id);
            return "fornecedores/form";
        }

        redirect.addFlashAttribute("sucesso", "Fornecedor atualizado com sucesso.");
        return "redirect:/fornecedores";
    }

    @PostMapping("/{id}/status")
    public String status(@PathVariable Long id) {
        service.alternarAtivo(id);
        return "redirect:/fornecedores";
    }

    private void preparar(Model model, FornecedorRequest request, Long id) {
        model.addAttribute("fornecedorRequest", request);
        model.addAttribute("id", id);
    }
}
