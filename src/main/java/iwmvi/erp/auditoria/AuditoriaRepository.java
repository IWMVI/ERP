package iwmvi.erp.auditoria;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    @Query(
            """
            SELECT a
            FROM Auditoria a
            WHERE (:usuario IS NULL OR LOWER(a.usuario) LIKE LOWER(CONCAT('%', :usuario, '%')))
              AND (:entidade IS NULL OR LOWER(a.entidade) LIKE LOWER(CONCAT('%', :entidade, '%')))
              AND (:inicio IS NULL OR a.dataHora >= :inicio)
              AND (:fim IS NULL OR a.dataHora <= :fim)
            ORDER BY a.dataHora DESC
            """)
    List<Auditoria> buscar(
            @Param("usuario") String usuario,
            @Param("entidade") String entidade,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}
