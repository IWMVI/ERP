package iwmvi.erp.venda;

import iwmvi.erp.cliente.Cliente;
import iwmvi.erp.cliente.ClienteRepository;
import iwmvi.erp.cliente.ClienteRequest;
import iwmvi.erp.cliente.TipoPessoa;
import iwmvi.erp.estoque.EstoqueService;
import iwmvi.erp.estoque.MovimentacaoEstoqueRepository;
import iwmvi.erp.estoque.MovimentacaoEstoqueRequest;
import iwmvi.erp.estoque.TipoMovimentacao;
import iwmvi.erp.financeiro.StatusTituloFinanceiro;
import iwmvi.erp.financeiro.TituloFinanceiroRepository;
import iwmvi.erp.produto.Produto;
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
class VendaFluxoIntegrationTest {

    @Autowired
    private PedidoVendaService vendaService;
    @Autowired
    private PedidoVendaRepository pedidoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private EstoqueService estoqueService;
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
        clienteRepository.deleteAll();
        produtoRepository.deleteAll();
    }

    @Test
    void deveConcluirEEstornarVendaMantendoEstoqueEFinanceiroConsistentes() {
        Cliente cliente =
            clienteRepository.save(
                new Cliente(
                    new ClienteRequest(
                        TipoPessoa.FISICA,
                        "Cliente teste",
                        "12345678901",
                        "cliente@teste.com",
                        "",
                        "Rua Teste",
                        "1",
                        "São Paulo",
                        "SP",
                        "01001000")));

        Produto produto =
            produtoRepository.save(
                new Produto(
                    new ProdutoRequest(
                        "VENDA-INT-1",
                        "Produto integração",
                        "Teste",
                        UnidadeMedida.UNIDADE,
                        new BigDecimal("25.00"),
                        new BigDecimal("10.00"),
                        BigDecimal.ZERO)));

        estoqueService.movimentar(
            new MovimentacaoEstoqueRequest(
                produto.getId(), TipoMovimentacao.ENTRADA, new BigDecimal("10.000"), "CARGA_TESTE"));

        PedidoVenda pedido =
            vendaService.criar(new CriarPedidoVendaRequest(cliente.getId(), BigDecimal.ZERO));
        vendaService.adicionarItem(
            pedido.getId(), new AdicionarItemVendaRequest(produto.getId(), new BigDecimal("2.000")));
        vendaService.concluir(pedido.getId(), 2, LocalDate.now().plusDays(30));

        Produto aposVenda = produtoRepository.findById(produto.getId()).orElseThrow();
        assertEquals(0, aposVenda.getSaldoEstoque().compareTo(new BigDecimal("8.000")));
        assertEquals(2, tituloRepository.findByOrigemTipoAndOrigemId("VENDA", pedido.getId()).size());

        vendaService.estornar(pedido.getId());

        Produto aposEstorno = produtoRepository.findById(produto.getId()).orElseThrow();
        assertEquals(0, aposEstorno.getSaldoEstoque().compareTo(new BigDecimal("10.000")));
        assertEquals(
            2,
            tituloRepository.findByOrigemTipoAndOrigemId("VENDA", pedido.getId()).stream()
                .filter(titulo -> titulo.getStatus() == StatusTituloFinanceiro.CANCELADO)
                .count());
        assertEquals(StatusPedidoVenda.ESTORNADO, vendaService.buscar(pedido.getId()).getStatus());
    }
}
