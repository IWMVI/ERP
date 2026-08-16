package iwmvi.erp.compra;

import iwmvi.erp.estoque.EstoqueService;
import iwmvi.erp.estoque.MovimentacaoEstoqueRequest;
import iwmvi.erp.estoque.TipoMovimentacao;
import iwmvi.erp.financeiro.FinanceiroService;
import iwmvi.erp.fornecedor.Fornecedor;
import iwmvi.erp.fornecedor.FornecedorRepository;
import iwmvi.erp.produto.Produto;
import iwmvi.erp.produto.ProdutoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoCompraService {

    private final PedidoCompraRepository pedidoRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueService estoqueService;
    private final FinanceiroService financeiroService;

    public PedidoCompraService(
            PedidoCompraRepository pedidoRepository,
            FornecedorRepository fornecedorRepository,
            ProdutoRepository produtoRepository,
            EstoqueService estoqueService,
            FinanceiroService financeiroService) {
        this.pedidoRepository = pedidoRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueService = estoqueService;
        this.financeiroService = financeiroService;
    }

    @Transactional
    public PedidoCompra criar(CriarPedidoCompraRequest request) {
        Fornecedor fornecedor = fornecedorRepository.findById(request.fornecedorId())
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado."));
        if (!fornecedor.isAtivo()) {
            throw new IllegalStateException("Não é possível comprar de um fornecedor inativo.");
        }
        return pedidoRepository.save(new PedidoCompra(fornecedor));
    }

    @Transactional
    public PedidoCompra adicionarItem(Long pedidoId, AdicionarItemCompraRequest request) {
        PedidoCompra pedido = buscar(pedidoId);
        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        if (!produto.isAtivo()) {
            throw new IllegalStateException("Não é possível comprar um produto inativo.");
        }
        pedido.adicionarItem(produto, request.quantidade(), request.custoUnitario());
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public PedidoCompra receber(Long pedidoId) {
        PedidoCompra pedido = buscar(pedidoId);
        pedido.receber();

        for (ItemPedidoCompra item : pedido.getItens()) {
            estoqueService.movimentar(new MovimentacaoEstoqueRequest(
                    item.getProduto().getId(),
                    TipoMovimentacao.ENTRADA,
                    item.getQuantidade(),
                    "COMPRA:" + pedido.getId() + ":ITEM:" + item.getId()));
        }

        financeiroService.gerarContasPagar(
                pedido.getFornecedor(),
                pedido.getTotal(),
                1,
                LocalDate.now().plusDays(30),
                "COMPRA",
                pedido.getId());
        return pedido;
    }

    @Transactional
    public PedidoCompra cancelar(Long pedidoId) {
        PedidoCompra pedido = buscar(pedidoId);
        pedido.cancelar();
        return pedido;
    }

    @Transactional(readOnly = true)
    public List<PedidoCompra> listar() {
        return pedidoRepository.findAllByOrderByDataCriacaoDesc();
    }

    @Transactional(readOnly = true)
    public PedidoCompra buscar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido de compra não encontrado."));
    }
}
