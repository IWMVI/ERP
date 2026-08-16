package iwmvi.erp.fornecedor;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.shared.exception.DocumentoJaCadastradoException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FornecedorService {
    private final FornecedorRepository repository;
    private final AuditoriaService auditoria;
    public FornecedorService(FornecedorRepository repository, AuditoriaService auditoria){this.repository=repository;this.auditoria=auditoria;}
    @Transactional(readOnly=true) public List<Fornecedor> listar(){return repository.findAllByOrderByNomeAsc();}
    @Transactional(readOnly=true) public Fornecedor buscar(Long id){return repository.findById(id).orElseThrow(()->new IllegalArgumentException("Fornecedor não encontrado."));}
    @Transactional public Fornecedor criar(FornecedorRequest r){validar(r.documento(),null);Fornecedor f=repository.save(new Fornecedor(r));auditoria.registrar("CRIAR","Fornecedor",f.getId(),f.getNome());return f;}
    @Transactional public Fornecedor atualizar(Long id,FornecedorRequest r){validar(r.documento(),id);Fornecedor f=buscar(id);f.atualizar(r);auditoria.registrar("ATUALIZAR","Fornecedor",id,f.getNome());return f;}
    @Transactional public void alternarAtivo(Long id){Fornecedor f=buscar(id);f.alternarAtivo();auditoria.registrar(f.isAtivo()?"ATIVAR":"INATIVAR","Fornecedor",id,f.getNome());}
    private void validar(String documento,Long id){boolean duplicado=id==null?repository.existsByDocumento(documento):repository.existsByDocumentoAndIdNot(documento,id);if(duplicado)throw new DocumentoJaCadastradoException(documento);}
}
