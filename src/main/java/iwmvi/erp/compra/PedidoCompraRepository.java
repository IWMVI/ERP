package iwmvi.erp.compra;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoCompraRepository extends JpaRepository<PedidoCompra, Long> {
    List<PedidoCompra> findAllByOrderByDataCriacaoDesc();
}
