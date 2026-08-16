package iwmvi.erp.funcionario;

import iwmvi.erp.shared.validation.DocumentoValidator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;

@Entity
@Table(
        name = "funcionarios",
        uniqueConstraints = @UniqueConstraint(name = "uk_funcionario_cpf", columnNames = "cpf"))
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private boolean ativo = true;

    protected Funcionario() {}

    public Funcionario(FuncionarioRequest request) {
        atualizar(request);
    }

    public void atualizar(FuncionarioRequest request) {
        this.nome = request.nome().trim();
        this.cpf = DocumentoValidator.somenteDigitos(request.cpf());
        this.email = request.email();
        this.telefone = request.telefone();
        this.cargo = request.cargo().trim();
        this.dataNascimento = request.dataNascimento();
        this.dataAdmissao = request.dataAdmissao();
        this.cep = DocumentoValidator.somenteDigitos(request.cep());
        this.logradouro = request.logradouro();
        this.numero = request.numero();
        this.complemento = request.complemento();
        this.bairro = request.bairro();
        this.cidade = request.cidade();
        this.estado = request.estado();
    }

    public void definirFotoArquivo(String fotoArquivo) {
        this.fotoArquivo = fotoArquivo;
    }

    public void alternarAtivo() {
        this.ativo = !this.ativo;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getCargo() { return cargo; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public LocalDate getDataAdmissao() { return dataAdmissao; }
    public String getCep() { return cep; }
    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getFotoArquivo() { return fotoArquivo; }
    public boolean isAtivo() { return ativo; }
}
