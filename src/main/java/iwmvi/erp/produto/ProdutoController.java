package iwmvi.erp.produto;

import iwmvi.erp.shared.exception.CodigoProdutoJaCadastradoException;
import iwmvi.erp.shared.exception.DocumentoInvalidoException;
import iwmvi.erp.shared.storage.ImagemStorageService;
import iwmvi.erp.shared.web.PageView;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;
    private final ImagemStorageService imagemStorageService;

    public ProdutoController(ProdutoService service, ImagemStorageService imagemStorageService) {
        this.service = service;
        this.imagemStorageService = imagemStorageService;
    }

    @GetMapping
    public String listar(
        @RequestParam(required = false) String q,
        @RequestParam(defaultValue = "0") int page,
        Model model) {
        PageView<Produto> paginacao =
            PageView.of(service.listar(q), page, "/produtos", Map.of("q", q == null ? "" : q));
        model.addAttribute("produtos", paginacao.items());
        model.addAttribute("paginacao", paginacao);
        model.addAttribute("q", q);
        return "produtos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        preparar(model, vazio(), null);
        return "produtos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Produto produto = service.buscar(id);
        preparar(
            model,
            new ProdutoRequest(
                produto.getCodigo(),
                produto.getGtin(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getMarca(),
                produto.getCategoria(),
                produto.getSubcategoria(),
                produto.getTipo(),
                produto.getUnidadeMedida(),
                produto.getPrecoVenda(),
                produto.getCusto(),
                produto.getEstoqueMinimo(),
                produto.getEstoqueMaximo(),
                produto.getLocalizacao(),
                produto.isControlaEstoque(),
                produto.getNcm(),
                produto.getCest(),
                produto.getOrigem(),
                produto.getUnidadeTributavel(),
                produto.getFatorConversaoTributavel(),
                produto.getTipoItemSped(),
                produto.getPesoLiquido(),
                produto.getPesoBruto(),
                produto.getLargura(),
                produto.getAltura(),
                produto.getComprimento(),
                produto.getVolumes(),
                produto.getPrazoPreparacaoDias()),
            id);
        return "produtos/form";
    }

    @PostMapping
    public String criar(
        @Valid @ModelAttribute("produtoRequest") ProdutoRequest request,
        BindingResult result,
        @RequestParam(required = false) MultipartFile foto,
        Model model,
        RedirectAttributes redirect) {
        if (result.hasErrors()) {
            preparar(model, request, null);
            model.addAttribute("erroGlobal", "Revise os campos destacados antes de salvar o produto.");
            return "produtos/form";
        }
        try {
            Produto produto = service.criar(request);
            salvarFoto(produto, foto);
        } catch (CodigoProdutoJaCadastradoException exception) {
            result.rejectValue("codigo", "duplicado", exception.getMessage());
            preparar(model, request, null);
            model.addAttribute("erroGlobal", exception.getMessage());
            return "produtos/form";
        } catch (DocumentoInvalidoException exception) {
            result.rejectValue("gtin", "gtin.invalido", exception.getMessage());
            preparar(model, request, null);
            model.addAttribute("erroGlobal", exception.getMessage());
            return "produtos/form";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            result.reject("foto.invalida", exception.getMessage());
            preparar(model, request, null);
            model.addAttribute("erroGlobal", exception.getMessage());
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
        @RequestParam(required = false) MultipartFile foto,
        Model model,
        RedirectAttributes redirect) {
        if (result.hasErrors()) {
            preparar(model, request, id);
            model.addAttribute("erroGlobal", "Revise os campos destacados antes de salvar o produto.");
            return "produtos/form";
        }
        try {
            Produto atual = service.buscar(id);
            String fotoAnterior = atual.getFotoArquivo();
            Produto produto = service.atualizar(id, request);
            if (foto != null && !foto.isEmpty()) {
                String novaFoto = imagemStorageService.salvar(foto, "produtos");
                service.atualizarFoto(produto.getId(), novaFoto);
                imagemStorageService.remover(fotoAnterior);
            }
        } catch (CodigoProdutoJaCadastradoException exception) {
            result.rejectValue("codigo", "duplicado", exception.getMessage());
            preparar(model, request, id);
            model.addAttribute("erroGlobal", exception.getMessage());
            return "produtos/form";
        } catch (DocumentoInvalidoException exception) {
            result.rejectValue("gtin", "gtin.invalido", exception.getMessage());
            preparar(model, request, id);
            model.addAttribute("erroGlobal", exception.getMessage());
            return "produtos/form";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            result.reject("foto.invalida", exception.getMessage());
            preparar(model, request, id);
            model.addAttribute("erroGlobal", exception.getMessage());
            return "produtos/form";
        }
        redirect.addFlashAttribute("sucesso", "Produto atualizado com sucesso.");
        return "redirect:/produtos";
    }

    @PostMapping("/{id}/status")
    public String status(@PathVariable Long id, RedirectAttributes redirect) {
        service.alternarAtivo(id);
        redirect.addFlashAttribute("sucesso", "Status do produto atualizado.");
        return "redirect:/produtos";
    }

    private void salvarFoto(Produto produto, MultipartFile foto) {
        if (foto == null || foto.isEmpty()) return;
        String arquivo = imagemStorageService.salvar(foto, "produtos");
        service.atualizarFoto(produto.getId(), arquivo);
    }

    private void preparar(Model model, ProdutoRequest request, Long id) {
        model.addAttribute("produtoRequest", request);
        model.addAttribute("unidades", UnidadeMedida.values());
        model.addAttribute("tiposProduto", TipoProduto.values());
        model.addAttribute("origens", OrigemMercadoria.values());
        model.addAttribute("tiposItemSped", TipoItemSped.values());
        model.addAttribute("id", id);
        model.addAttribute("fotoAtual", id == null ? null : service.buscar(id).getFotoArquivo());
    }

    private ProdutoRequest vazio() {
        return new ProdutoRequest(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            TipoProduto.PRODUTO,
            UnidadeMedida.UNIDADE,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            "",
            true,
            "",
            "",
            null,
            UnidadeMedida.UNIDADE,
            BigDecimal.ONE,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    }
}
