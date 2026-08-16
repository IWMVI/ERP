package iwmvi.erp.auditoria;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String usuario;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false, length = 80)
    private String operacao;

    @Column(nullable = false, length = 120)
    private String entidade;

    @Column(name = "entidade_id", length = 80)
    private String entidadeId;

    @Column(length = 500)
    private String descricao;

    protected Auditoria() {
    }

    public Auditoria(
        String usuario,
        LocalDateTime dataHora,
        String operacao,
        String entidade,
        String entidadeId,
        String descricao) {
        this.usuario = usuario;
        this.dataHora = dataHora;
        this.operacao = operacao;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.descricao = descricao;
    }
}
