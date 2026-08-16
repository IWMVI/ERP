package iwmvi.erp.financeiro;

import iwmvi.erp.cliente.Cliente;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FinanceiroServiceTest {

    @Test
    void deveDividirValorEmParcelasPreservandoTotal() {
        TituloFinanceiroRepository repository = mock(TituloFinanceiroRepository.class);
        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        FinanceiroService service = new FinanceiroService(repository);

        List<TituloFinanceiro> titulos =
            service.gerarContasReceber(
                mock(Cliente.class),
                new BigDecimal("100.00"),
                3,
                LocalDate.now().plusDays(30),
                "VENDA",
                10L);

        assertEquals(3, titulos.size());
        assertEquals(new BigDecimal("33.33"), titulos.get(0).getValor());
        assertEquals(new BigDecimal("33.33"), titulos.get(1).getValor());
        assertEquals(new BigDecimal("33.34"), titulos.get(2).getValor());
        assertEquals(
            new BigDecimal("100.00"),
            titulos.stream().map(TituloFinanceiro::getValor).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void deveRejeitarMaisDeVinteEQuatroParcelas() {
        FinanceiroService service = new FinanceiroService(mock(TituloFinanceiroRepository.class));

        assertThrows(
            IllegalArgumentException.class,
            () ->
                service.gerarContasReceber(
                    mock(Cliente.class),
                    new BigDecimal("100.00"),
                    25,
                    LocalDate.now().plusDays(30),
                    "VENDA",
                    10L));
    }

    @Test
    void deveClassificarTitulosPorSituacao() {
        LocalDate hoje = LocalDate.now();
        TituloFinanceiro atrasado = titulo(hoje.minusDays(1));
        TituloFinanceiro vencendo = titulo(hoje.plusDays(2));
        TituloFinanceiro aberto = titulo(hoje.plusDays(10));
        TituloFinanceiro pago = titulo(hoje.plusDays(5));
        pago.pagar(hoje);

        assertEquals(FinanceiroService.CATEGORIA_ATRASO, FinanceiroService.categoria(atrasado));
        assertEquals(FinanceiroService.CATEGORIA_VENCENDO, FinanceiroService.categoria(vencendo));
        assertEquals(FinanceiroService.CATEGORIA_ABERTO, FinanceiroService.categoria(aberto));
        assertEquals(FinanceiroService.CATEGORIA_PAGO, FinanceiroService.categoria(pago));
    }

    @Test
    void deveFiltrarListaPorSituacao() {
        TituloFinanceiroRepository repository = mock(TituloFinanceiroRepository.class);
        LocalDate hoje = LocalDate.now();
        TituloFinanceiro atrasado = titulo(hoje.minusDays(1));
        TituloFinanceiro vencendo = titulo(hoje.plusDays(2));
        TituloFinanceiro aberto = titulo(hoje.plusDays(10));
        when(repository.findByTipoAndStatusOrderByDataVencimentoAsc(
                TipoTituloFinanceiro.RECEBER, StatusTituloFinanceiro.ABERTO))
            .thenReturn(List.of(atrasado, vencendo, aberto));
        FinanceiroService service = new FinanceiroService(repository);

        assertEquals(
            1, service.listar(TipoTituloFinanceiro.RECEBER, FinanceiroService.CATEGORIA_ATRASO).size());
        assertEquals(
            1, service.listar(TipoTituloFinanceiro.RECEBER, FinanceiroService.CATEGORIA_VENCENDO).size());
        assertEquals(
            3, service.listar(TipoTituloFinanceiro.RECEBER, FinanceiroService.CATEGORIA_ABERTO).size());
        assertEquals("EM_ATRASO",
            service.listar(TipoTituloFinanceiro.RECEBER, FinanceiroService.CATEGORIA_ATRASO)
                .get(0).categoria());
    }

    @Test
    void deveCalcularResumoPorSituacao() {
        TituloFinanceiroRepository repository = mock(TituloFinanceiroRepository.class);
        LocalDate hoje = LocalDate.now();
        TituloFinanceiro atrasado = titulo(hoje.minusDays(1));
        TituloFinanceiro vencendo = titulo(hoje.plusDays(2));
        TituloFinanceiro aberto = titulo(hoje.plusDays(10));
        TituloFinanceiro pago = titulo(hoje.plusDays(5));
        pago.pagar(hoje);
        when(repository.findByTipoAndStatusOrderByDataVencimentoAsc(
                TipoTituloFinanceiro.RECEBER, StatusTituloFinanceiro.ABERTO))
            .thenReturn(List.of(atrasado, vencendo, aberto));
        when(repository.findByTipoAndStatusOrderByDataVencimentoAsc(
                TipoTituloFinanceiro.RECEBER, StatusTituloFinanceiro.PAGO))
            .thenReturn(List.of(pago));
        FinanceiroService service = new FinanceiroService(repository);

        FinanceiroService.ResumoFinanceiro resumo =
            service.resumo(TipoTituloFinanceiro.RECEBER);

        assertEquals(3, resumo.emAberto().quantidade());
        assertEquals(new BigDecimal("300.00"), resumo.emAberto().valor());
        assertEquals(1, resumo.emAtraso().quantidade());
        assertEquals(1, resumo.vencendo().quantidade());
        assertEquals(1, resumo.pago().quantidade());
    }

    @Test
    void deveAcumularSaldoNoExtrato() {
        TituloFinanceiroRepository repository = mock(TituloFinanceiroRepository.class);
        LocalDate hoje = LocalDate.now();
        TituloFinanceiro receber = titulo(hoje);
        TituloFinanceiro pagar =
            new TituloFinanceiro(
                TipoTituloFinanceiro.PAGAR,
                null,
                null,
                new BigDecimal("40.00"),
                hoje.plusDays(1),
                1,
                1,
                "COMPRA",
                2L,
                "Pagamento COMPRA #2");
        when(repository.findByStatusNotOrderByDataVencimentoAsc(any()))
            .thenReturn(List.of(receber, pagar));
        FinanceiroService service = new FinanceiroService(repository);

        FinanceiroService.ExtratoFinanceiro extrato = service.extrato();

        assertEquals(2, extrato.lancamentos().size());
        assertEquals(new BigDecimal("100.00"), extrato.totalReceber());
        assertEquals(new BigDecimal("40.00"), extrato.totalPagar());
        assertEquals(new BigDecimal("60.00"), extrato.saldo());
        assertEquals(new BigDecimal("60.00"), extrato.lancamentos().get(1).saldo());
    }

    private TituloFinanceiro titulo(LocalDate vencimento) {
        return new TituloFinanceiro(
            TipoTituloFinanceiro.RECEBER,
            null,
            null,
            new BigDecimal("100.00"),
            vencimento,
            1,
            1,
            "VENDA",
            1L,
            "Recebimento VENDA #1");
    }
}
