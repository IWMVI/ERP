package iwmvi.erp.fornecedor;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    boolean existsByDocumento(String documento);
    boolean existsByDocumentoAndIdNot(String documento, Long id);
    List<Fornecedor> findAllByOrderByNomeAsc();
}
