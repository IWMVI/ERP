package iwmvi.erp.produto;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    List<Produto> findAllByOrderByNomeAsc();

    List<Produto> findByNomeContainingIgnoreCaseOrCodigoContainingIgnoreCaseOrderByNomeAsc(
            String nome, String codigo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Produto p WHERE p.id = :id")
    Optional<Produto> buscarParaMovimentacao(@Param("id") Long id);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.saldoEstoque < p.estoqueMinimo ORDER BY p.nome")
    List<Produto> findAbaixoDoEstoqueMinimo();
}
