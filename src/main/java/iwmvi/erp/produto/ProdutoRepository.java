package iwmvi.erp.produto;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto,Long>{
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo,Long id);
    List<Produto> findAllByOrderByNomeAsc();
    List<Produto> findByNomeContainingIgnoreCaseOrCodigoContainingIgnoreCaseOrderByNomeAsc(String nome,String codigo);
}
