package iwmvi.erp.estoque;

import static org.junit.jupiter.api.Assertions.assertEquals;

import iwmvi.erp.produto.Produto;
import iwmvi.erp.produto.ProdutoRepository;
import iwmvi.erp.produto.ProdutoRequest;
import iwmvi.erp.produto.UnidadeMedida;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EstoqueConcorrenciaIntegrationTest {

    @Autowired private EstoqueService estoqueService;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private MovimentacaoEstoqueRepository movimentacaoRepository;

    @BeforeEach
    void limpar() {
        movimentacaoRepository.deleteAll();
        produtoRepository.deleteAll();
    }

    @Test
    void deveSerializarMovimentacoesConcorrentesDoMesmoProduto() throws Exception {
        Produto produto =
                produtoRepository.saveAndFlush(
                        new Produto(
                                new ProdutoRequest(
                                        "CONC-1",
                                        "Produto concorrente",
                                        "Teste",
                                        UnidadeMedida.UNIDADE,
                                        BigDecimal.TEN,
                                        BigDecimal.ONE,
                                        BigDecimal.ZERO)));

        CountDownLatch inicio = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<?> primeira =
                    executor.submit(
                            () -> movimentarAposSinal(inicio, produto.getId(), BigDecimal.TEN));
            Future<?> segunda =
                    executor.submit(
                            () -> movimentarAposSinal(inicio, produto.getId(), BigDecimal.TEN));

            inicio.countDown();
            primeira.get();
            segunda.get();
        } finally {
            executor.shutdownNow();
        }

        Produto atualizado = produtoRepository.findById(produto.getId()).orElseThrow();
        assertEquals(0, atualizado.getSaldoEstoque().compareTo(new BigDecimal("20.000")));
        assertEquals(2, movimentacaoRepository.count());
    }

    private void movimentarAposSinal(CountDownLatch inicio, Long produtoId, BigDecimal quantidade) {
        try {
            inicio.await();
            estoqueService.movimentar(
                    new MovimentacaoEstoqueRequest(
                            produtoId, TipoMovimentacao.ENTRADA, quantidade, "TESTE CONCORRENTE"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
