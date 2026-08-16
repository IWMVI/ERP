package iwmvi.erp.financeiro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TituloFinanceiroRepository extends JpaRepository<TituloFinanceiro, Long> {

    boolean existsByTipoAndOrigemTipoAndOrigemId(
        TipoTituloFinanceiro tipo, String origemTipo, Long origemId);

    List<TituloFinanceiro> findByTipoAndStatusOrderByDataVencimentoAsc(
        TipoTituloFinanceiro tipo, StatusTituloFinanceiro status);

    List<TituloFinanceiro> findByStatusNotOrderByDataVencimentoAsc(StatusTituloFinanceiro status);

    List<TituloFinanceiro> findByOrigemTipoAndOrigemId(String origemTipo, Long origemId);
}
