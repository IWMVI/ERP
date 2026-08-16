package iwmvi.erp.fornecedor;

import iwmvi.erp.shared.exception.DocumentoJaCadastradoException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/fornecedores")
public class FornecedorController {
    private final FornecedorService service;
    public FornecedorController(FornecedorService service){this.service=service;}
    @GetMapping public String listar(Model model){model.addAttribute("fornecedores",service.listar());return "fornecedores/lista";}
    @GetMapping("/novo") public String novo(Model model){preparar(model,new FornecedorRequest("","","","","","","","",""),null);return "fornecedores/form";}
    @GetMapping("/{id}/editar") public String editar(@PathVariable Long id,Model model){Fornecedor f=service.buscar(id);preparar(model,new FornecedorRequest(f.getNome(),f.getDocumento(),f.getEmail(),f.getTelefone(),f.getLogradouro(),f.getNumero(),f.getCidade(),f.getEstado(),f.getCep()),id);return "fornecedores/form";}
    @PostMapping public String criar(@Valid @ModelAttribute("fornecedorRequest") FornecedorRequest r,BindingResult result,Model model,RedirectAttributes redirect){if(result.hasErrors()){preparar(model,r,null);return "fornecedores/form";}try{service.criar(r);}catch(DocumentoJaCadastradoException e){result.rejectValue("documento","duplicado",e.getMessage());preparar(model,r,null);return "fornecedores/form";}redirect.addFlashAttribute("sucesso","Fornecedor cadastrado com sucesso.");return "redirect:/fornecedores";}
    @PostMapping("/{id}") public String atualizar(@PathVariable Long id,@Valid @ModelAttribute("fornecedorRequest") FornecedorRequest r,BindingResult result,Model model,RedirectAttributes redirect){if(result.hasErrors()){preparar(model,r,id);return "fornecedores/form";}try{service.atualizar(id,r);}catch(DocumentoJaCadastradoException e){result.rejectValue("documento","duplicado",e.getMessage());preparar(model,r,id);return "fornecedores/form";}redirect.addFlashAttribute("sucesso","Fornecedor atualizado com sucesso.");return "redirect:/fornecedores";}
    @PostMapping("/{id}/status") public String status(@PathVariable Long id){service.alternarAtivo(id);return "redirect:/fornecedores";}
    private void preparar(Model model,FornecedorRequest r,Long id){model.addAttribute("fornecedorRequest",r);model.addAttribute("id",id);}
}
