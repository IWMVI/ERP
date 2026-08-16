package iwmvi.erp.fornecedor;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "fornecedores",
    uniqueConstraints =
    @UniqueConstraint(name = "uk_fornecedor_documento", columnNames = "documento"))
@Getter
@Setter(AccessLevel.PACKAGE)
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    private String email;
    private String telefone;
    private String celular;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(length = 1000)
    private String observacoes;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private boolean ativo = true;

    protected Fornecedor() {
    }

    public void alternarAtivo() {
        this.ativo = !this.ativo;
    }
}
