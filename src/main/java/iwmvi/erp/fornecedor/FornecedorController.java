package iwmvi.erp.fornecedor;

import iwmvi.erp.integracao.PessoaCadastroLookupService;
import iwmvi.erp.integracao.PessoaCadastroLookupService.PessoaCadastroLookupResult;
import iwmvi.erp.shared.exception.CepInvalidoException;
import iwmvi.erp.shared.exception.DocumentoInvalidoException;
import iwmvi.erp.shared.exception.DocumentoJaCadastradoException;
import iwmvi.erp.shared.validation.DocumentoValidator;
import jakarta.validation.Valid;
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
@RequestMapping("/fornecedores")
public class FornecedorController {

    private final FornecedorService service;
    private final PessoaCadastroLookupService pessoaCadastroLookupService;

    public FornecedorController(
            FornecedorService service, PessoaCadastroLookupService pessoaCadastroLookupService) {
        this.service = service;
        this.pessoaCadastroLookupService = pessoaCadastroLookupService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("fornecedores", service.listar());
        return "fornecedores/lista";
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
            FornecedorRequest request = new FornecedorRequest(
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
            return "fornecedores/form";
        } catch (DocumentoInvalidoException exception) {
            prepararIdentificacao(model, documento, exception.getMessage());
            return "cadastros/identificar-pessoa";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Fornecedor fornecedor = service.buscar(id);
        preparar(
                model,
                new FornecedorRequest(
                        fornecedor.getNome(),
                        fornecedor.getNomeFantasia(),
                        fornecedor.getDocumento(),
                        fornecedor.getEmail(),
                        fornecedor.getTelefone(),
                        fornecedor.getCelular(),
                        fornecedor.getCep(),
                        fornecedor.getLogradouro(),
                        fornecedor.getNumero(),
                        fornecedor.getComplemento(),
                        fornecedor.getBairro(),
                        fornecedor.getCidade(),
                        fornecedor.getEstado(),
                        fornecedor.getObservacoes()),
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
            model.addAttribute("erroGlobal", "Revise os campos destacados antes de salvar o fornecedor.");
            return "fornecedores/form";
        }

        try {
            service.criar(request);
        } catch (DocumentoJaCadastradoException | DocumentoInvalidoException exception) {
            result.rejectValue("documento", "documento.invalido", exception.getMessage());
            preparar(model, request, null);
            model.addAttribute("erroGlobal", exception.getMessage());
            return "fornecedores/form";
        } catch (CepInvalidoException exception) {
            result.rejectValue("cep", "cep.invalido", exception.getMessage());
            preparar(model, request, null);
            model.addAttribute("erroGlobal", exception.getMessage());
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
            model.addAttribute("erroGlobal", "Revise os campos destacados antes de salvar o fornecedor.");
            return "fornecedores/form";
        }

        try {
            service.atualizar(id, request);
        } catch (DocumentoJaCadastradoException | DocumentoInvalidoException exception) {
            result.rejectValue("documento", "documento.invalido", exception.getMessage());
            preparar(model, request, id);
            model.addAttribute("erroGlobal", exception.getMessage());
            return "fornecedores/form";
        } catch (CepInvalidoException exception) {
            result.rejectValue("cep", "cep.invalido", exception.getMessage());
            preparar(model, request, id);
            model.addAttribute("erroGlobal", exception.getMessage());
            return "fornecedores/form";
        }

        redirect.addFlashAttribute("sucesso", "Fornecedor atualizado com sucesso.");
        return "redirect:/fornecedores";
    }

    @PostMapping("/{id}/status")
    public String status(@PathVariable Long id, RedirectAttributes redirect) {
        service.alternarAtivo(id);
        redirect.addFlashAttribute("sucesso", "Status do fornecedor atualizado.");
        return "redirect:/fornecedores";
    }

    private void prepararIdentificacao(Model model, String documento, String erro) {
        model.addAttribute("titulo", "Novo fornecedor");
        model.addAttribute("breadcrumb", "Cadastros / Fornecedores / Novo");
        model.addAttribute("activePage", "fornecedores");
        model.addAttribute("formAction", "/fornecedores/novo/identificar");
        model.addAttribute("voltarUrl", "/fornecedores");
        model.addAttribute("documento", documento);
        model.addAttribute("erro", erro);
        if (erro != null && !erro.isBlank()) {
            model.addAttribute("erroGlobal", erro);
        }
    }

    private void preparar(Model model, FornecedorRequest request, Long id) {
        model.addAttribute("fornecedorRequest", request);
        model.addAttribute("id", id);
        String documento = DocumentoValidator.normalizarDocumento(request.documento());
        model.addAttribute(
                "tipoPessoa", DocumentoValidator.documentoEhCnpj(documento) ? "JURIDICA" : "FISICA");
    }

    private String valor(String valor) {
        return valor == null ? "" : valor;
    }
}
