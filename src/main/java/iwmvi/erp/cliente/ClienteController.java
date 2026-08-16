package iwmvi.erp.cliente;

import iwmvi.erp.integracao.PessoaCadastroLookupService;
import iwmvi.erp.integracao.PessoaCadastroLookupService.PessoaCadastroLookupResult;
import iwmvi.erp.shared.exception.CepInvalidoException;
import iwmvi.erp.shared.exception.DocumentoInvalidoException;
import iwmvi.erp.shared.exception.DocumentoJaCadastradoException;
import iwmvi.erp.shared.web.PageView;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;
    private final PessoaCadastroLookupService pessoaCadastroLookupService;

    public ClienteController(
        ClienteService service, PessoaCadastroLookupService pessoaCadastroLookupService) {
        this.service = service;
        this.pessoaCadastroLookupService = pessoaCadastroLookupService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page, Model model) {
        PageView<ClienteResponse> paginacao =
            PageView.of(
                service.listar().stream().map(ClienteMapper::toResponse).toList(), page, "/clientes");
        model.addAttribute("clientes", paginacao.items());
        model.addAttribute("paginacao", paginacao);
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        prepararIdentificacao(model, "", null);
        return "cadastros/identificar-pessoa";
    }

    @PostMapping("/novo/identificar")
    public String identificar(@RequestParam String documento, Model model) {
        try {
            PessoaCadastroLookupResult dados = pessoaCadastroLookupService.consultar(documento);
            ClienteRequest request =
                new ClienteRequest(
                    dados.pessoaJuridica() ? TipoPessoa.JURIDICA : TipoPessoa.FISICA,
                    valor(dados.nome()),
                    valor(dados.nomeFantasia()),
                    dados.documento(),
                    valor(dados.email()),
                    valor(dados.telefone()),
                    "",
                    valor(dados.cep()),
                    valor(dados.logradouro()),
                    valor(dados.numero()),
                    valor(dados.complemento()),
                    valor(dados.bairro()),
                    valor(dados.cidade()),
                    valor(dados.estado()),
                    "");
            preparar(model, request, null);
            model.addAttribute("cadastroNovo", true);
            model.addAttribute("avisoConsulta", dados.aviso());
            model.addAttribute("situacaoCadastral", dados.situacaoCadastral());
            if (dados.aviso() != null && !dados.aviso().isBlank()) {
                model.addAttribute("warning", dados.aviso());
            }
            return "clientes/form";
        } catch (DocumentoInvalidoException exception) {
            prepararIdentificacao(model, documento, exception.getMessage());
            return "cadastros/identificar-pessoa";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        preparar(model, ClienteMapper.toRequest(service.buscar(id)), id);
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
            model.addAttribute("erroGlobal", "Revise os campos destacados antes de salvar o cliente.");
            return "clientes/form";
        }

        try {
            service.criar(request);
        } catch (DocumentoJaCadastradoException | DocumentoInvalidoException exception) {
            result.rejectValue("documento", "documento.invalido", exception.getMessage());
            preparar(model, request, null);
            model.addAttribute("erroGlobal", exception.getMessage());
            return "clientes/form";
        } catch (CepInvalidoException exception) {
            result.rejectValue("cep", "cep.invalido", exception.getMessage());
            preparar(model, request, null);
            model.addAttribute("erroGlobal", exception.getMessage());
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
            model.addAttribute("erroGlobal", "Revise os campos destacados antes de salvar o cliente.");
            return "clientes/form";
        }

        try {
            service.atualizar(id, request);
        } catch (DocumentoJaCadastradoException | DocumentoInvalidoException exception) {
            result.rejectValue("documento", "documento.invalido", exception.getMessage());
            preparar(model, request, id);
            model.addAttribute("erroGlobal", exception.getMessage());
            return "clientes/form";
        } catch (CepInvalidoException exception) {
            result.rejectValue("cep", "cep.invalido", exception.getMessage());
            preparar(model, request, id);
            model.addAttribute("erroGlobal", exception.getMessage());
            return "clientes/form";
        }

        redirect.addFlashAttribute("sucesso", "Cliente atualizado com sucesso.");
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/status")
    public String status(@PathVariable Long id, RedirectAttributes redirect) {
        service.alternarAtivo(id);
        redirect.addFlashAttribute("sucesso", "Status do cliente atualizado.");
        return "redirect:/clientes";
    }

    private void prepararIdentificacao(Model model, String documento, String erro) {
        model.addAttribute("titulo", "Novo cliente");
        model.addAttribute("breadcrumb", "Cadastros / Clientes / Novo");
        model.addAttribute("activePage", "clientes");
        model.addAttribute("formAction", "/clientes/novo/identificar");
        model.addAttribute("voltarUrl", "/clientes");
        model.addAttribute("documento", documento);
        model.addAttribute("erro", erro);
        if (erro != null && !erro.isBlank()) {
            model.addAttribute("erroGlobal", erro);
        }
    }

    private void preparar(Model model, ClienteRequest request, Long id) {
        model.addAttribute("clienteRequest", request);
        model.addAttribute("tiposPessoa", TipoPessoa.values());
        model.addAttribute("id", id);
    }

    private String valor(String valor) {
        return valor == null ? "" : valor;
    }
}
