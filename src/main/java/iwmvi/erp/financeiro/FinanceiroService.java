package iwmvi.erp.financeiro;

import iwmvi.erp.cliente.Cliente;
import iwmvi.erp.fornecedor.Fornecedor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinanceiroService {

    private static final int MAX_PARCELAS = 24;

    private final TituloFinanceiroRepository repository;

    public FinanceiroService(TituloFinanceiroRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<TituloFinanceiro> gerarContasReceber(
            Cliente cliente,
            BigDecimal valorTotal,
            int parcelas,
            LocalDate primeiroVencimento,
            String origemTipo,
            Long origemId) {
        return gerarParcelas(
                TipoTituloFinanceiro.RECEBER,
                cliente,
                null,
                valorTotal,
                parcelas,
                primeiroVencimento,
                origemTipo,
                origemId,
                "Recebimento " + origemTipo + " #" + origemId);
    }

    @Transactional
    public List<TituloFinanceiro> gerarContasPagar(
            Fornecedor fornecedor,
            BigDecimal valorTotal,
            int parcelas,
            LocalDate primeiroVencimento,
            String origemTipo,
            Long origemId) {
        return gerarParcelas(
                TipoTituloFinanceiro.PAGAR,
                null,
                fornecedor,
                valorTotal,
                parcelas,
                primeiroVencimento,
                origemTipo,
                origemId,
                "Pagamento " + origemTipo + " #" + origemId);
    }

    @Transactional
    public TituloFinanceiro pagar(Long id) {
        TituloFinanceiro titulo = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Título financeiro não encontrado."));
        titulo.pagar(LocalDate.now());
        return titulo;
    }

    @Transactional
    public void cancelarPorOrigem(String origemTipo, Long origemId) {
        List<TituloFinanceiro> titulos = repository.findByOrigemTipoAndOrigemId(origemTipo, origemId);
        if (titulos.stream().anyMatch(titulo -> titulo.getStatus() == StatusTituloFinanceiro.PAGO)) {
            throw new IllegalStateException(
                    "Não é possível estornar a operação porque há título financeiro já pago.");
        }
        titulos.stream()
                .filter(titulo -> titulo.getStatus() == StatusTituloFinanceiro.ABERTO)
                .forEach(TituloFinanceiro::cancelar);
    }

    @Transactional(readOnly = true)
    public List<TituloFinanceiro> emAberto() {
        return repository.findByStatusOrderByDataVencimentoAsc(StatusTituloFinanceiro.ABERTO);
    }

    @Transactional(readOnly = true)
    public List<TituloFinanceiro> vencidos() {
        return repository.findByStatusAndDataVencimentoBeforeOrderByDataVencimentoAsc(
                StatusTituloFinanceiro.ABERTO, LocalDate.now());
    }

    private List<TituloFinanceiro> gerarParcelas(
            TipoTituloFinanceiro tipo,
            Cliente cliente,
            Fornecedor fornecedor,
            BigDecimal valorTotal,
            int parcelas,
            LocalDate primeiroVencimento,
            String origemTipo,
            Long origemId,
            String descricao) {
        if (parcelas <= 0 || parcelas > MAX_PARCELAS) {
            throw new IllegalArgumentException("A quantidade de parcelas deve estar entre 1 e 24.");
        }
        if (valorTotal == null || valorTotal.signum() <= 0) {
            throw new IllegalArgumentException("O valor total deve ser maior que zero.");
        }
        if (primeiroVencimento == null || primeiroVencimento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("O primeiro vencimento não pode estar no passado.");
        }
        if (repository.existsByTipoAndOrigemTipoAndOrigemId(tipo, origemTipo, origemId)) {
            throw new IllegalStateException("Já existem títulos financeiros para esta operação.");
        }

        BigDecimal valorBase = valorTotal.divide(BigDecimal.valueOf(parcelas), 2, RoundingMode.DOWN);
        BigDecimal acumulado = BigDecimal.ZERO;
        List<TituloFinanceiro> titulos = new ArrayList<>();

        for (int numero = 1; numero <= parcelas; numero++) {
            BigDecimal valor = numero == parcelas ? valorTotal.subtract(acumulado) : valorBase;
            acumulado = acumulado.add(valor);
            titulos.add(new TituloFinanceiro(
                    tipo,
                    cliente,
                    fornecedor,
                    valor,
                    primeiroVencimento.plusMonths(numero - 1L),
                    numero,
                    parcelas,
                    origemTipo,
                    origemId,
                    descricao));
        }
        return repository.saveAll(titulos);
    }
}
