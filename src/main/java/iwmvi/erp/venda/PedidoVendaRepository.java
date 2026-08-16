package iwmvi.erp.venda;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoVendaRepository extends JpaRepository<PedidoVenda, Long> {
    List<PedidoVenda> findAllByOrderByDataCriacaoDesc();
}
