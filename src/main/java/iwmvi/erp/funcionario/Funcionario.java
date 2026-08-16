package iwmvi.erp.funcionario;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
    name = "funcionarios",
    uniqueConstraints = @UniqueConstraint(name = "uk_funcionario_cpf", columnNames = "cpf"))
@Getter
@Setter(AccessLevel.PACKAGE)
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    private String email;
    private String telefone;

    @Column(nullable = false, length = 120)
    private String cargo;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;

    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(name = "foto_arquivo", length = 255)
    private String fotoArquivo;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private boolean ativo = true;

    protected Funcionario() {
    }

    public void definirFotoArquivo(String fotoArquivo) {
        this.fotoArquivo = fotoArquivo;
    }

    public void alternarAtivo() {
        this.ativo = !this.ativo;
    }
}
