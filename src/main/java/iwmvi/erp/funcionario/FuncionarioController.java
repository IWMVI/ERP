package iwmvi.erp.funcionario;

import iwmvi.erp.shared.exception.DocumentoJaCadastradoException;
import iwmvi.erp.shared.storage.ImagemStorageService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService service;
    private final ImagemStorageService imagemStorageService;

    public FuncionarioController(
            FuncionarioService service, ImagemStorageService imagemStorageService) {
        this.service = service;
        this.imagemStorageService = imagemStorageService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("funcionarios", service.listar());
        return "funcionarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        preparar(model, vazio(), null);
        return "funcionarios/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Funcionario funcionario = service.buscar(id);
        preparar(
                model,
                new FuncionarioRequest(
                        funcionario.getNome(),
                        funcionario.getCpf(),
                        funcionario.getEmail(),
                        funcionario.getTelefone(),
                        funcionario.getCargo(),
                        funcionario.getDataNascimento(),
                        funcionario.getDataAdmissao(),
                        funcionario.getCep(),
                        funcionario.getLogradouro(),
                        funcionario.getNumero(),
                        funcionario.getComplemento(),
                        funcionario.getBairro(),
                        funcionario.getCidade(),
                        funcionario.getEstado()),
                id);
        return "funcionarios/form";
    }

    @PostMapping
    public String criar(
            @Valid @ModelAttribute("funcionarioRequest") FuncionarioRequest request,
            BindingResult result,
            @RequestParam(required = false) MultipartFile foto,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            preparar(model, request, null);
            return "funcionarios/form";
        }

        try {
            Funcionario funcionario = service.criar(request);
            salvarFoto(funcionario, foto);
        } catch (DocumentoJaCadastradoException exception) {
            result.rejectValue("cpf", "duplicado", exception.getMessage());
            preparar(model, request, null);
            return "funcionarios/form";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            result.reject("funcionario.invalido", exception.getMessage());
            preparar(model, request, null);
            return "funcionarios/form";
        }

        redirect.addFlashAttribute("sucesso", "Funcionário cadastrado com sucesso.");
        return "redirect:/funcionarios";
    }

    @PostMapping("/{id}")
    public String atualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("funcionarioRequest") FuncionarioRequest request,
            BindingResult result,
            @RequestParam(required = false) MultipartFile foto,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            preparar(model, request, id);
            return "funcionarios/form";
        }

        try {
            Funcionario atual = service.buscar(id);
            String fotoAnterior = atual.getFotoArquivo();
            Funcionario funcionario = service.atualizar(id, request);
            if (foto != null && !foto.isEmpty()) {
                String novaFoto = imagemStorageService.salvar(foto, "funcionarios");
                service.atualizarFoto(funcionario.getId(), novaFoto);
                imagemStorageService.remover(fotoAnterior);
            }
        } catch (DocumentoJaCadastradoException exception) {
            result.rejectValue("cpf", "duplicado", exception.getMessage());
            preparar(model, request, id);
            return "funcionarios/form";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            result.reject("funcionario.invalido", exception.getMessage());
            preparar(model, request, id);
            return "funcionarios/form";
        }

        redirect.addFlashAttribute("sucesso", "Funcionário atualizado com sucesso.");
        return "redirect:/funcionarios";
    }

    @PostMapping("/{id}/status")
    public String status(@PathVariable Long id) {
        service.alternarAtivo(id);
        return "redirect:/funcionarios";
    }

    private void salvarFoto(Funcionario funcionario, MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            return;
        }
        String arquivo = imagemStorageService.salvar(foto, "funcionarios");
        service.atualizarFoto(funcionario.getId(), arquivo);
    }

    private void preparar(Model model, FuncionarioRequest request, Long id) {
        model.addAttribute("funcionarioRequest", request);
        model.addAttribute("id", id);
        model.addAttribute("fotoAtual", id == null ? null : service.buscar(id).getFotoArquivo());
    }

    private FuncionarioRequest vazio() {
        return new FuncionarioRequest(
                "", "", "", "", "", null, LocalDate.now(), "", "", "", "", "", "", "");
    }
}
