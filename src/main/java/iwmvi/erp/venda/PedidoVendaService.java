package iwmvi.erp.venda;

import iwmvi.erp.cliente.Cliente;
import iwmvi.erp.cliente.ClienteRepository;
import iwmvi.erp.estoque.EstoqueService;
import iwmvi.erp.estoque.MovimentacaoEstoqueRequest;
import iwmvi.erp.estoque.TipoMovimentacao;
import iwmvi.erp.financeiro.FinanceiroService;
import iwmvi.erp.produto.Produto;
import iwmvi.erp.produto.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PedidoVendaService {

    private final PedidoVendaRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueService estoqueService;
    private final FinanceiroService financeiroService;

    public PedidoVendaService(
        PedidoVendaRepository pedidoRepository,
        ClienteRepository clienteRepository,
        ProdutoRepository produtoRepository,
        EstoqueService estoqueService,
        FinanceiroService financeiroService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueService = estoqueService;
        this.financeiroService = financeiroService;
    }

    @Transactional
    public PedidoVenda criar(CriarPedidoVendaRequest request) {
        Cliente cliente =
            clienteRepository
                .findById(request.clienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        if (!cliente.isAtivo()) {
            throw new IllegalStateException("Não é possível vender para um cliente inativo.");
        }
        return pedidoRepository.save(new PedidoVenda(cliente, request.desconto()));
    }

    @Transactional
    public PedidoVenda adicionarItem(Long pedidoId, AdicionarItemVendaRequest request) {
        PedidoVenda pedido = buscar(pedidoId);
        Produto produto =
            produtoRepository
                .findById(request.produtoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        if (!produto.isAtivo()) {
            throw new IllegalStateException("Não é possível vender um produto inativo.");
        }
        pedido.adicionarItem(produto, request.quantidade(), produto.getPrecoVenda());
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public PedidoVenda concluir(Long pedidoId, int parcelas, LocalDate primeiroVencimento) {
        PedidoVenda pedido = buscar(pedidoId);
        pedido.concluir();

        for (ItemPedidoVenda item : pedido.getItens()) {
            if (!item.getProduto().isControlaEstoque()) continue;
            estoqueService.movimentar(
                new MovimentacaoEstoqueRequest(
                    item.getProduto().getId(),
                    TipoMovimentacao.SAIDA,
                    item.getQuantidade(),
                    "VENDA:" + pedido.getId() + ":ITEM:" + item.getId()));
        }

        financeiroService.gerarContasReceber(
            pedido.getCliente(),
            pedido.getTotal(),
            parcelas,
            primeiroVencimento,
            "VENDA",
            pedido.getId());
        return pedido;
    }

    @Transactional
    public PedidoVenda cancelar(Long pedidoId) {
        PedidoVenda pedido = buscar(pedidoId);
        pedido.cancelar();
        return pedido;
    }

    @Transactional
    public PedidoVenda estornar(Long pedidoId) {
        PedidoVenda pedido = buscar(pedidoId);
        financeiroService.cancelarPorOrigem("VENDA", pedido.getId());
        for (ItemPedidoVenda item : pedido.getItens()) {
            if (!item.getProduto().isControlaEstoque()) continue;
            estoqueService.movimentar(
                new MovimentacaoEstoqueRequest(
                    item.getProduto().getId(),
                    TipoMovimentacao.ENTRADA,
                    item.getQuantidade(),
                    "ESTORNO_VENDA:" + pedido.getId() + ":ITEM:" + item.getId()));
        }
        pedido.estornar();
        return pedido;
    }

    @Transactional(readOnly = true)
    public List<PedidoVenda> listar() {
        return pedidoRepository.findAllByOrderByDataCriacaoDesc();
    }

    @Transactional(readOnly = true)
    public PedidoVenda buscar(Long id) {
        return pedidoRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pedido de venda não encontrado."));
    }
}
