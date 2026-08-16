package iwmvi.erp.compra;

import iwmvi.erp.estoque.MovimentacaoEstoqueRepository;
import iwmvi.erp.financeiro.StatusTituloFinanceiro;
import iwmvi.erp.financeiro.TituloFinanceiroRepository;
import iwmvi.erp.fornecedor.Fornecedor;
import iwmvi.erp.fornecedor.FornecedorMapper;
import iwmvi.erp.fornecedor.FornecedorRepository;
import iwmvi.erp.fornecedor.FornecedorRequest;
import iwmvi.erp.produto.Produto;
import iwmvi.erp.produto.ProdutoMapper;
import iwmvi.erp.produto.ProdutoRepository;
import iwmvi.erp.produto.ProdutoRequest;
import iwmvi.erp.produto.UnidadeMedida;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CompraFluxoIntegrationTest {

    @Autowired
    private PedidoCompraService compraService;
    @Autowired
    private PedidoCompraRepository pedidoRepository;
    @Autowired
    private FornecedorRepository fornecedorRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoRepository;
    @Autowired
    private TituloFinanceiroRepository tituloRepository;

    @BeforeEach
    @AfterEach
    void limpar() {
        tituloRepository.deleteAll();
        pedidoRepository.deleteAll();
        movimentacaoRepository.deleteAll();
        fornecedorRepository.deleteAll();
        produtoRepository.deleteAll();
    }

    @Test
    void deveReceberEEstornarCompraMantendoEstoqueEFinanceiroConsistentes() {
        Fornecedor fornecedor =
                fornecedorRepository.save(
                        FornecedorMapper.toEntity(
                                new FornecedorRequest(
                                        "Fornecedor teste",
                                        "11222333000181",
                                        "fornecedor@teste.com",
                                        "",
                                        "Rua Teste",
                                        "1",
                                        "São Paulo",
                                        "SP",
                                        "01001000")));

        Produto produto =
                produtoRepository.save(
                        ProdutoMapper.toEntity(
                                new ProdutoRequest(
                                        "COMPRA-INT-1",
                                        "Produto integração compra",
                                        "Teste",
                                        UnidadeMedida.UNIDADE,
                                        new BigDecimal("30.00"),
                                        new BigDecimal("12.00"),
                                        BigDecimal.ZERO)));

        PedidoCompra pedido = compraService.criar(new CriarPedidoCompraRequest(fornecedor.getId()));
        compraService.adicionarItem(
                pedido.getId(),
                new AdicionarItemCompraRequest(
                        produto.getId(), new BigDecimal("5.000"), new BigDecimal("12.00")));
        compraService.receber(pedido.getId(), 3, LocalDate.now().plusDays(30));

        Produto aposCompra = produtoRepository.findById(produto.getId()).orElseThrow();
        assertEquals(0, aposCompra.getSaldoEstoque().compareTo(new BigDecimal("5.000")));
        assertEquals(3, tituloRepository.findByOrigemTipoAndOrigemId("COMPRA", pedido.getId()).size());

        compraService.estornar(pedido.getId());

        Produto aposEstorno = produtoRepository.findById(produto.getId()).orElseThrow();
        assertEquals(0, aposEstorno.getSaldoEstoque().compareTo(BigDecimal.ZERO));
        assertEquals(
                3,
                tituloRepository.findByOrigemTipoAndOrigemId("COMPRA", pedido.getId()).stream()
                        .filter(titulo -> titulo.getStatus() == StatusTituloFinanceiro.CANCELADO)
                        .count());
        assertEquals(StatusPedidoCompra.ESTORNADO, compraService.buscar(pedido.getId()).getStatus());
    }
}
