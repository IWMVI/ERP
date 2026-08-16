package iwmvi.erp.compra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoCompraRepository extends JpaRepository<PedidoCompra, Long> {
    List<PedidoCompra> findAllByOrderByDataCriacaoDesc();
}
