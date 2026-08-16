package iwmvi.erp.financeiro;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TituloFinanceiroRepository extends JpaRepository<TituloFinanceiro, Long> {

    boolean existsByTipoAndOrigemTipoAndOrigemId(
            TipoTituloFinanceiro tipo, String origemTipo, Long origemId);

    List<TituloFinanceiro> findByStatusOrderByDataVencimentoAsc(StatusTituloFinanceiro status);

    List<TituloFinanceiro> findByStatusAndDataVencimentoBeforeOrderByDataVencimentoAsc(
            StatusTituloFinanceiro status, LocalDate data);

    List<TituloFinanceiro> findByOrigemTipoAndOrigemId(String origemTipo, Long origemId);
}
