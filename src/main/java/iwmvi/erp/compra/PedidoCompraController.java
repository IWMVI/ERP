package iwmvi.erp.compra;

import iwmvi.erp.fornecedor.FornecedorRepository;
import iwmvi.erp.produto.ProdutoRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/compras")
public class PedidoCompraController {

    private final PedidoCompraService service;
    private final FornecedorRepository fornecedorRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoCompraController(
            PedidoCompraService service,
            FornecedorRepository fornecedorRepository,
            ProdutoRepository produtoRepository) {
        this.service = service;
        this.fornecedorRepository = fornecedorRepository;
        this.produtoRepository = produtoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", service.listar());
        model.addAttribute("activePage", "compras");
        return "compras/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("fornecedores", fornecedorRepository.findAllByOrderByNomeAsc());
        model.addAttribute("activePage", "compras");
        return "compras/novo";
    }

    @PostMapping
    public String criar(@RequestParam Long fornecedorId, RedirectAttributes redirectAttributes) {
        PedidoCompra pedido = service.criar(new CriarPedidoCompraRequest(fornecedorId));
        redirectAttributes.addFlashAttribute("sucesso", "Pedido de compra criado com sucesso.");
        return "redirect:/compras/" + pedido.getId();
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("pedido", service.buscar(id));
        model.addAttribute("produtos", produtoRepository.findAllByOrderByNomeAsc());
        model.addAttribute("activePage", "compras");
        return "compras/detalhe";
    }

    @PostMapping("/{id}/itens")
    public String adicionarItem(
            @PathVariable Long id,
            @RequestParam Long produtoId,
            @RequestParam BigDecimal quantidade,
            @RequestParam BigDecimal custoUnitario,
            RedirectAttributes redirectAttributes) {
        service.adicionarItem(id, new AdicionarItemCompraRequest(produtoId, quantidade, custoUnitario));
        redirectAttributes.addFlashAttribute("sucesso", "Item adicionado ao pedido.");
        return "redirect:/compras/" + id;
    }

    @PostMapping("/{id}/receber")
    public String receber(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.receber(id);
        redirectAttributes.addFlashAttribute("sucesso", "Compra recebida e estoque atualizado.");
        return "redirect:/compras/" + id;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.cancelar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Pedido cancelado.");
        return "redirect:/compras/" + id;
    }
}
