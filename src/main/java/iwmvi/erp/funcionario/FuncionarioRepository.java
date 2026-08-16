package iwmvi.erp.funcionario;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    List<Funcionario> findAllByOrderByNomeAsc();

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);
}
