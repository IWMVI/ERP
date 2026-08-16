package iwmvi.erp.produto;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.shared.exception.CodigoProdutoJaCadastradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService{
    private final ProdutoRepository repository;private final AuditoriaService auditoria;
    public ProdutoService(ProdutoRepository repository,AuditoriaService auditoria){this.repository=repository;this.auditoria=auditoria;}
    @Transactional(readOnly=true) public List<Produto> listar(String termo){return termo==null||termo.isBlank()?repository.findAllByOrderByNomeAsc():repository.findByNomeContainingIgnoreCaseOrCodigoContainingIgnoreCaseOrderByNomeAsc(termo,termo);}
    @Transactional(readOnly=true) public Produto buscar(Long id){return repository.findById(id).orElseThrow(()->new IllegalArgumentException("Produto não encontrado."));}
    @Transactional public Produto criar(ProdutoRequest r){validar(r.codigo(),null);Produto p=repository.save(new Produto(r));auditoria.registrar("CRIAR","Produto",p.getId(),p.getCodigo()+" - "+p.getNome());return p;}
    @Transactional public Produto atualizar(Long id,ProdutoRequest r){validar(r.codigo(),id);Produto p=buscar(id);p.atualizar(r);auditoria.registrar("ATUALIZAR","Produto",id,p.getCodigo()+" - "+p.getNome());return p;}
    @Transactional public void alternarAtivo(Long id){Produto p=buscar(id);p.alternarAtivo();auditoria.registrar(p.isAtivo()?"ATIVAR":"INATIVAR","Produto",id,p.getNome());}
    private void validar(String codigo,Long id){boolean duplicado=id==null?repository.existsByCodigo(codigo):repository.existsByCodigoAndIdNot(codigo,id);if(duplicado)throw new CodigoProdutoJaCadastradoException(codigo);}
}
