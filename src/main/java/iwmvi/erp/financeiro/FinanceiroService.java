package iwmvi.erp.financeiro;

import iwmvi.erp.cliente.Cliente;
import iwmvi.erp.fornecedor.Fornecedor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinanceiroService {

    private static final int MAX_PARCELAS = 24;
    private static final int VENCENDO_EM_DIAS = 5;

    public static final String CATEGORIA_ATRASO = "EM_ATRASO";
    public static final String CATEGORIA_VENCENDO = "VENCENDO";
    public static final String CATEGORIA_ABERTO = "ABERTO";
    public static final String CATEGORIA_PAGO = "PAGO";

    private final TituloFinanceiroRepository repository;

    public FinanceiroService(TituloFinanceiroRepository repository) {
        this.repository = repository;
    }

    public record GrupoFinanceiro(long quantidade, BigDecimal valor) {
    }

    public record ResumoFinanceiro(
        GrupoFinanceiro emAberto,
        GrupoFinanceiro vencendo,
        GrupoFinanceiro emAtraso,
        GrupoFinanceiro pago) {
    }

    public record TituloFinanceiroView(TituloFinanceiroResponse titulo, String categoria) {
    }

    public record LancamentoExtrato(TituloFinanceiroResponse titulo, String categoria, BigDecimal saldo) {
    }

    public record ExtratoFinanceiro(
        List<LancamentoExtrato> lancamentos,
        BigDecimal totalReceber,
        BigDecimal totalPagar,
        BigDecimal saldo) {
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
        TituloFinanceiro titulo =
            repository
                .findById(id)
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
    public List<TituloFinanceiro> emAberto(TipoTituloFinanceiro tipo) {
        return repository.findByTipoAndStatusOrderByDataVencimentoAsc(
            tipo, StatusTituloFinanceiro.ABERTO);
    }

    @Transactional(readOnly = true)
    public List<TituloFinanceiroView> listar(TipoTituloFinanceiro tipo, String filtro) {
        List<TituloFinanceiro> titulos =
            switch (filtro) {
                case CATEGORIA_ATRASO -> emAtraso(tipo);
                case CATEGORIA_VENCENDO -> vencendo(tipo);
                case CATEGORIA_PAGO -> pagos(tipo);
                default -> emAberto(tipo);
            };
        return titulos.stream()
            .map(t -> new TituloFinanceiroView(TituloFinanceiroMapper.toResponse(t), categoria(t)))
            .toList();
    }

    @Transactional(readOnly = true)
    public ResumoFinanceiro resumo(TipoTituloFinanceiro tipo) {
        List<TituloFinanceiro> abertos = emAberto(tipo);
        List<TituloFinanceiro> pagos = pagos(tipo);
        return new ResumoFinanceiro(
            grupo(abertos),
            grupo(vencendo(tipo)),
            grupo(emAtraso(tipo)),
            grupo(pagos));
    }

    @Transactional(readOnly = true)
    public ExtratoFinanceiro extrato() {
        List<TituloFinanceiro> titulos =
            repository.findByStatusNotOrderByDataVencimentoAsc(StatusTituloFinanceiro.CANCELADO);
        BigDecimal totalReceber = BigDecimal.ZERO;
        BigDecimal totalPagar = BigDecimal.ZERO;
        BigDecimal saldo = BigDecimal.ZERO;
        List<LancamentoExtrato> lancamentos = new ArrayList<>();
        for (TituloFinanceiro titulo : titulos) {
            if (titulo.getTipo() == TipoTituloFinanceiro.RECEBER) {
                totalReceber = totalReceber.add(titulo.getValor());
                saldo = saldo.add(titulo.getValor());
            } else {
                totalPagar = totalPagar.add(titulo.getValor());
                saldo = saldo.subtract(titulo.getValor());
            }
            lancamentos.add(
                new LancamentoExtrato(
                    TituloFinanceiroMapper.toResponse(titulo), categoria(titulo), saldo));
        }
        return new ExtratoFinanceiro(lancamentos, totalReceber, totalPagar, saldo);
    }

    public static String categoria(TituloFinanceiro titulo) {
        if (titulo.getStatus() == StatusTituloFinanceiro.PAGO) {
            return CATEGORIA_PAGO;
        }
        if (titulo.getStatus() == StatusTituloFinanceiro.CANCELADO) {
            return "CANCELADO";
        }
        LocalDate hoje = LocalDate.now();
        if (titulo.getDataVencimento().isBefore(hoje)) {
            return CATEGORIA_ATRASO;
        }
        if (!titulo.getDataVencimento().isAfter(hoje.plusDays(VENCENDO_EM_DIAS))) {
            return CATEGORIA_VENCENDO;
        }
        return CATEGORIA_ABERTO;
    }

    private List<TituloFinanceiro> emAtraso(TipoTituloFinanceiro tipo) {
        LocalDate hoje = LocalDate.now();
        return emAberto(tipo).stream()
            .filter(titulo -> titulo.getDataVencimento().isBefore(hoje))
            .toList();
    }

    private List<TituloFinanceiro> vencendo(TipoTituloFinanceiro tipo) {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(VENCENDO_EM_DIAS);
        return emAberto(tipo).stream()
            .filter(titulo -> !titulo.getDataVencimento().isBefore(hoje))
            .filter(titulo -> !titulo.getDataVencimento().isAfter(limite))
            .toList();
    }

    private List<TituloFinanceiro> pagos(TipoTituloFinanceiro tipo) {
        return repository.findByTipoAndStatusOrderByDataVencimentoAsc(
            tipo, StatusTituloFinanceiro.PAGO);
    }

    private GrupoFinanceiro grupo(List<TituloFinanceiro> titulos) {
        long quantidade = titulos.size();
        BigDecimal valor =
            titulos.stream()
                .map(TituloFinanceiro::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new GrupoFinanceiro(quantidade, valor);
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
            titulos.add(
                new TituloFinanceiro(
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
