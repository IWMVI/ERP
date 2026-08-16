package iwmvi.erp.estoque;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.produto.Produto;
import iwmvi.erp.produto.ProdutoMapper;
import iwmvi.erp.produto.ProdutoRepository;
import iwmvi.erp.produto.ProdutoRequest;
import iwmvi.erp.produto.UnidadeMedida;
import iwmvi.erp.shared.exception.SaldoEstoqueInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private MovimentacaoEstoqueRepository movimentacaoRepository;
    @Mock
    private AuditoriaService auditoriaService;
    private EstoqueService service;
    private Produto produto;

    @BeforeEach
    void setUp() {
        service = new EstoqueService(produtoRepository, movimentacaoRepository, auditoriaService);
        produto =
            ProdutoMapper.toEntity(
                new ProdutoRequest(
                    "P1",
                    "Produto",
                    "Categoria",
                    UnidadeMedida.UNIDADE,
                    BigDecimal.TEN,
                    BigDecimal.ONE,
                    BigDecimal.ZERO));
    }

    @Test
    void deveRegistrarEntradaEAtualizarSaldo() {
        when(produtoRepository.buscarParaMovimentacao(1L)).thenReturn(Optional.of(produto));
        when(movimentacaoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.movimentar(
            new MovimentacaoEstoqueRequest(1L, TipoMovimentacao.ENTRADA, BigDecimal.TEN, "AJUSTE"));

        assertEquals(0, produto.getSaldoEstoque().compareTo(BigDecimal.TEN));
        verify(movimentacaoRepository).save(any());
    }

    @Test
    void deveImpedirSaidaSemSaldo() {
        when(produtoRepository.buscarParaMovimentacao(1L)).thenReturn(Optional.of(produto));

        assertThrows(
            SaldoEstoqueInsuficienteException.class,
            () ->
                service.movimentar(
                    new MovimentacaoEstoqueRequest(
                        1L, TipoMovimentacao.SAIDA, BigDecimal.ONE, "VENDA")));
    }
}
