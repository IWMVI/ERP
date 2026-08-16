package iwmvi.erp.cliente;

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
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", service.listar());
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        preparar(
                model,
                new ClienteRequest(
                        TipoPessoa.FISICA, "", "", "", "", "", "", "", "", ""),
                null);
        return "clientes/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Cliente cliente = service.buscar(id);
        preparar(
                model,
                new ClienteRequest(
                        cliente.getTipoPessoa(),
                        cliente.getNome(),
                        cliente.getDocumento(),
                        cliente.getEmail(),
                        cliente.getTelefone(),
                        cliente.getLogradouro(),
                        cliente.getNumero(),
                        cliente.getCidade(),
                        cliente.getEstado(),
                        cliente.getCep()),
                id);
        return "clientes/form";
    }

    @PostMapping
    public String criar(
            @Valid @ModelAttribute("clienteRequest") ClienteRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            preparar(model, request, null);
            return "clientes/form";
        }

        try {
            service.criar(request);
        } catch (DocumentoJaCadastradoException exception) {
            result.rejectValue("documento", "duplicado", exception.getMessage());
            preparar(model, request, null);
            return "clientes/form";
        }

        redirect.addFlashAttribute("sucesso", "Cliente cadastrado com sucesso.");
        return "redirect:/clientes";
    }

    @PostMapping("/{id}")
    public String atualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("clienteRequest") ClienteRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            preparar(model, request, id);
            return "clientes/form";
        }

        try {
            service.atualizar(id, request);
        } catch (DocumentoJaCadastradoException exception) {
            result.rejectValue("documento", "duplicado", exception.getMessage());
            preparar(model, request, id);
            return "clientes/form";
        }

        redirect.addFlashAttribute("sucesso", "Cliente atualizado com sucesso.");
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/status")
    public String status(@PathVariable Long id) {
        service.alternarAtivo(id);
        return "redirect:/clientes";
    }

    private void preparar(Model model, ClienteRequest request, Long id) {
        model.addAttribute("clienteRequest", request);
        model.addAttribute("tiposPessoa", TipoPessoa.values());
        model.addAttribute("id", id);
    }
}
