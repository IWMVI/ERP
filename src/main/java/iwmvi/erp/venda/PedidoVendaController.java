package iwmvi.erp.venda;

import iwmvi.erp.cliente.ClienteRepository;
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
@RequestMapping("/vendas")
public class PedidoVendaController {

    private final PedidoVendaService service;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoVendaController(
            PedidoVendaService service,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository) {
        this.service = service;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", service.listar());
        model.addAttribute("activePage", "vendas");
        return "vendas/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("clientes", clienteRepository.findAllByOrderByNomeAsc());
        model.addAttribute("activePage", "vendas");
        return "vendas/novo";
    }

    @PostMapping
    public String criar(
            @RequestParam Long clienteId,
            @RequestParam(defaultValue = "0") BigDecimal desconto,
            RedirectAttributes redirectAttributes) {
        PedidoVenda pedido = service.criar(new CriarPedidoVendaRequest(clienteId, desconto));
        redirectAttributes.addFlashAttribute("sucesso", "Pedido de venda criado com sucesso.");
        return "redirect:/vendas/" + pedido.getId();
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("pedido", service.buscar(id));
        model.addAttribute("produtos", produtoRepository.findAllByOrderByNomeAsc());
        model.addAttribute("activePage", "vendas");
        return "vendas/detalhe";
    }

    @PostMapping("/{id}/itens")
    public String adicionarItem(
            @PathVariable Long id,
            @RequestParam Long produtoId,
            @RequestParam BigDecimal quantidade,
            RedirectAttributes redirectAttributes) {
        service.adicionarItem(id, new AdicionarItemVendaRequest(produtoId, quantidade));
        redirectAttributes.addFlashAttribute("sucesso", "Item adicionado ao pedido.");
        return "redirect:/vendas/" + id;
    }

    @PostMapping("/{id}/concluir")
    public String concluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.concluir(id);
        redirectAttributes.addFlashAttribute("sucesso", "Venda concluída e estoque atualizado.");
        return "redirect:/vendas/" + id;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.cancelar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Pedido cancelado.");
        return "redirect:/vendas/" + id;
    }
}
