package iwmvi.erp.estoque;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MovimentacaoEstoqueRepository
    extends JpaRepository<MovimentacaoEstoque, Long>,
    JpaSpecificationExecutor<MovimentacaoEstoque> {
}
