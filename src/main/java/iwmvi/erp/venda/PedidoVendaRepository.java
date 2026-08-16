package iwmvi.erp.venda;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoVendaRepository extends JpaRepository<PedidoVenda, Long> {
    List<PedidoVenda> findAllByOrderByDataCriacaoDesc();
}
