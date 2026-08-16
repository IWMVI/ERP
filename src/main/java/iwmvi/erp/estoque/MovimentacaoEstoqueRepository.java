package iwmvi.erp.estoque;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    @Query(
            """
            SELECT m
            FROM MovimentacaoEstoque m
            JOIN FETCH m.produto p
            WHERE (:produtoId IS NULL OR p.id = :produtoId)
              AND (:inicio IS NULL OR m.dataHora >= :inicio)
              AND (:fim IS NULL OR m.dataHora <= :fim)
            ORDER BY m.dataHora DESC
            """)
    List<MovimentacaoEstoque> buscar(
            @Param("produtoId") Long produtoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}
