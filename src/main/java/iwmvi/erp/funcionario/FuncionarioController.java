package iwmvi.erp.funcionario;

import iwmvi.erp.shared.exception.DocumentoJaCadastradoException;
import iwmvi.erp.shared.storage.ImagemStorageService;
import iwmvi.erp.shared.web.PageView;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

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
    public String listar(@RequestParam(defaultValue = "0") int page, Model model) {
        PageView<FuncionarioResponse> paginacao =
            PageView.of(
                service.listar().stream().map(FuncionarioMapper::toResponse).toList(),
                page,
                "/funcionarios");
        model.addAttribute("funcionarios", paginacao.items());
        model.addAttribute("paginacao", paginacao);
        return "funcionarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        preparar(model, vazio(), null);
        return "funcionarios/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        preparar(model, FuncionarioMapper.toRequest(service.buscar(id)), id);
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
            model.addAttribute(
                "erroGlobal", "Revise os campos destacados antes de salvar o funcionário.");
            return "funcionarios/form";
        }
        try {
            Funcionario funcionario = service.criar(request);
            salvarFoto(funcionario, foto);
        } catch (DocumentoJaCadastradoException exception) {
            result.rejectValue("cpf", "duplicado", exception.getMessage());
            preparar(model, request, null);
            model.addAttribute("erroGlobal", exception.getMessage());
            return "funcionarios/form";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            result.reject("funcionario.invalido", exception.getMessage());
            preparar(model, request, null);
            model.addAttribute("erroGlobal", exception.getMessage());
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
            model.addAttribute(
                "erroGlobal", "Revise os campos destacados antes de salvar o funcionário.");
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
            model.addAttribute("erroGlobal", exception.getMessage());
            return "funcionarios/form";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            result.reject("funcionario.invalido", exception.getMessage());
            preparar(model, request, id);
            model.addAttribute("erroGlobal", exception.getMessage());
            return "funcionarios/form";
        }
        redirect.addFlashAttribute("sucesso", "Funcionário atualizado com sucesso.");
        return "redirect:/funcionarios";
    }

    @PostMapping("/{id}/status")
    public String status(@PathVariable Long id, RedirectAttributes redirect) {
        service.alternarAtivo(id);
        redirect.addFlashAttribute("sucesso", "Status do funcionário atualizado.");
        return "redirect:/funcionarios";
    }

    private void salvarFoto(Funcionario funcionario, MultipartFile foto) {
        if (foto == null || foto.isEmpty()) return;
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
