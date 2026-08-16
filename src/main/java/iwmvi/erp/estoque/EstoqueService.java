package iwmvi.erp.estoque;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.produto.Produto;
import iwmvi.erp.produto.ProdutoRepository;
import iwmvi.erp.shared.exception.SaldoEstoqueInsuficienteException;

@Service
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final AuditoriaService auditoriaService;

    public EstoqueService(
            ProdutoRepository produtoRepository,
            MovimentacaoEstoqueRepository movimentacaoRepository,
            AuditoriaService auditoriaService) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public MovimentacaoEstoque movimentar(MovimentacaoEstoqueRequest request) {
        Produto produto = produtoRepository
                .buscarParaMovimentacao(request.produtoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        if (!produto.isAtivo()) {
            throw new IllegalStateException("Não é possível movimentar um produto inativo.");
        }

        if (!produto.isControlaEstoque()) {
            throw new IllegalStateException("Este produto não utiliza controle de estoque.");
        }

        BigDecimal saldoAtual = produto.getSaldoEstoque();
        BigDecimal novoSaldo = request.tipo() == TipoMovimentacao.ENTRADA
                ? saldoAtual.add(request.quantidade())
                : saldoAtual.subtract(request.quantidade());

        if (novoSaldo.signum() < 0) {
            throw new SaldoEstoqueInsuficienteException(produto.getNome());
        }

        produto.definirSaldo(novoSaldo);
        MovimentacaoEstoque movimentacao = movimentacaoRepository.save(
                new MovimentacaoEstoque(
                        produto,
                        request.tipo(),
                        request.quantidade(),
                        LocalDateTime.now(),
                        request.origem().trim(),
                        usuarioAtual()));

        auditoriaService.registrar(
                request.tipo().name(),
                "Estoque",
                movimentacao.getId(),
                produto.getCodigo() + " - " + request.quantidade() + " - saldo: " + novoSaldo);
        return movimentacao;
    }

    @Transactional(readOnly = true)
    public List<Produto> produtos() {
        return produtoRepository.findByControlaEstoqueTrueOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public List<Produto> abaixoDoMinimo() {
        return produtoRepository.findAbaixoDoEstoqueMinimo();
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoque> historico(Long produtoId, LocalDate inicio, LocalDate fim) {
        Specification<MovimentacaoEstoque> spec = Specification.unrestricted();

        if (produtoId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("produto").get("id"), produtoId));
        }

        if (inicio != null) {
            LocalDateTime inicioDataHora = inicio.atStartOfDay();
            spec = spec.and(
                    (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dataHora"), inicioDataHora));
        }

        if (fim != null) {
            LocalDateTime fimDataHora = fim.atTime(LocalTime.MAX);
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("dataHora"), fimDataHora));
        }

        return movimentacaoRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "dataHora"));
    }

    private String usuarioAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "SISTEMA" : authentication.getName();
    }
}
