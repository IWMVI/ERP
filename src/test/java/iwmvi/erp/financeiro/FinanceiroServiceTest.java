package iwmvi.erp.financeiro;

import iwmvi.erp.cliente.Cliente;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}
